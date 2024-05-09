package filecommunication

import java.nio.file.*
import businesslogic.OrderProcessor
import businessmodels.OrderIdGenerator
import datarepository.{SlickTables, QueryHandler}
import scala.io.Source
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import scala.util.{Failure, Success, Try}
import com.typesafe.scalalogging.Logger

/**
 * Represents an FileWatcher class that implement the logic for the directory monitor actor.
 *
 * @constructor Create a new FileWatcher with input directory path, and splitter to the appended files.
 * @param inputDir The directory to monitor.
 * @param splitter specify the delimiter of the appended file ot the file system or directory.
 */
class FileWatcher(inputDir: String, splitter: Char) {
  val logger: Logger = Logger(getClass.getName)

  ///creation of new watcher service the responsible for the directory monitoring
  private val watchService: WatchService = FileSystems.getDefault.newWatchService()
  logger.info("open watch service to listen to the directory")
  //create a path object from the inputDir parameter
  private val path: Path = FileSystems.getDefault.getPath(inputDir)

  /**
   * Registers the specified directory with the provided WatchService for monitoring file creation events.
   *
   * @param watchService The WatchService to register with.
   * @param eventKind    The WatchEvent.Kind representing the type of event to monitor (e.g., ENTRY_CREATE)(observer for the create or append an new file).
   */
  path.register(watchService, ENTRY_CREATE)

  logger.info("starting to listen to events")

  /**
   * Starts listening for file system events in the watched directory and processes them.
   * This method runs indefinitely until explicitly stopped, interruption.
   * 
   * @return Unit => Void
   */
  def startListening(): Unit = {
    while (true) {
      val listener = watchService.take()
      val events = listener.pollEvents()
      logger.info("start to process events occurred in directory")
      //Send each observed events to the processEvent function for observing the events.
      events.forEach(processEvent)
      //Resets the WatchKey and allowing it to continue to receive events.
      listener.reset() 
    }
  }

  /**
   * Processes the specified file system event.
   *
   * @param event The WatchEvent to process.
   * @return Unit => Void
   */
  private def processEvent(event: WatchEvent[_]): Unit = {
    val kind = event.kind()

    if (kind == ENTRY_CREATE) {
      //Starting by read file name the have been created or appended to the monitored directory.
      val fileName = event.context().asInstanceOf[Path].getFileName.toString
      val filePath = s"$inputDir/$fileName"
      
      logger.info("starting reading file lines")
      
      //Calling the readLinesFromFile Function to start read file and parse it into list of lines as strings.
      readLinesFromFile(filePath) match {
        case Success(lines) =>
          //case of success while parsing file then convert each line into a order object using method toOrder from the OrderProcessor(coordinator).
          logger.info("converting lines to order object")
          val orders = lines.map(OrderProcessor.toOrder(_, splitter))

          //Calling the newCounter method from the OrderIdGenerator to the loading_zone file store it with indicator as number.
          //Store the orders into a raw form without applying any qualifiers or operation, that the loading zone act as a bronze layer.
          //for data validation, or cold storage in case of low archiving in the monitored dir.
          OrderIdGenerator.newCounter()
          val fileStorageWriterLZ = FileStorageWriter("src/main/localstorage/loading_zone/rawOrders_"+s"${OrderIdGenerator.nextId()}"+".csv")
          fileStorageWriterLZ.writeFile(orders.map(_.toString))
          println("Store data in th loading zone successfully")
          logger.info("Store data in th loading zone successfully")

          
          //Start to process order applying hte qualifiers and calculate the discount for each order
          OrderIdGenerator.newCounter()
          logger.info("pass orders to calculate discount")
          val processedOrders = orders.map(OrderProcessor.processOrderDiscounts(_, 2))

          //After processing the orders, then store into the save_zone act as the sliver layer, for data consistency check.
          OrderIdGenerator.newCounter()
          val fileStorageWriterSZ = FileStorageWriter("src/main/localstorage/save_zone/processedOrders_"+s"${OrderIdGenerator.nextId()}"+".csv")
          fileStorageWriterSZ.writeFile(processedOrders.map(_.toString))
          println("Store data in th save zone successfully")
          logger.info("Store data in th save zone successfully")

          //Start connection to the postgres db to save data into it.
          logger.info("Connecting to Database....")
          val ordersWithDiscounts = processedOrders.map(SlickTables.toOrderTable)
          
          /**
          * Provide insertion the orders as bulk into a database
           * 
           * @param List[ordersWithDiscounts] ready in suitable format to store in the db.
          */
          QueryHandler.insertBulkOrders(ordersWithDiscounts)
          println("Store data in th database successfully")
          logger.info("Store data in th database successfully")

          //close the connection with the db.
          QueryHandler.dbClose()
          println("Database closed successfully")
          logger.info("Database closed successfully")

        case Failure(exception) =>
          logger.error(s"Failed to read file: $filePath, Error: ${exception.getMessage}")
      }

      /**
       * Checks if the newly created file is a directory, and if so, registers it for further monitoring.
       */
      if (Files.isDirectory(Paths.get(filePath))) { 
        val subPath = Paths.get(filePath)
        subPath.register(watchService, ENTRY_CREATE)
      }
    }
  }

  /** 
   * Provide function to read and parse the file lines into list of lines as strings.
   * using Try for handling i/o exception in functional programming manner.
   * @param String file path from the monitored dir.
   * @return Try[List[String]] => using match(pattern matching) for handling the return either success or failuer. 
   * */
  private def readLinesFromFile(filePath: String): Try[List[String]] = {
    Try {
      logger.info("buffered source opened and read in lazy evaluation way")
      val source = Source.fromFile(filePath)
      try {
        logger.info("ignore the header line from the file")
        source.getLines().toList.tail
      } finally {
        source.close()
      }
    }
  }
}

