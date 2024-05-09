package filecommunication

import java.nio.file.*
import businesslogic.OrderProcessor
import scala.io.Source
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import scala.util.{Failure, Success, Try}
import com.typesafe.scalalogging.Logger


class FileWatcher(inputDir: String) {
  val logger: Logger = Logger(getClass.getName)

  private val watchService: WatchService = FileSystems.getDefault.newWatchService()
  logger.info("open watch service to listen to the directory")
  private val path: Path = FileSystems.getDefault.getPath(inputDir)
  path.register(watchService, ENTRY_CREATE)

  logger.info("starting to listen to events")
  def startListening(): Unit = {
    while (true) {
      val listener = watchService.take()
      val events = listener.pollEvents()
      logger.info("start to process events occurred in directory")
      events.forEach(processEvent)
      listener.reset() 
    }
  }

  private def processEvent(event: WatchEvent[_]): Unit = {
    val kind = event.kind()

    if (kind == ENTRY_CREATE) {
      val fileName = event.context().asInstanceOf[Path].getFileName.toString
      val filePath = s"$inputDir/$fileName"

      logger.info("starting reading file lines")
      readLinesFromFile(filePath) match {
        case Success(lines) =>
          logger.info("converting lines to order object")
          val orders = lines.map(OrderProcessor.toOrder(_, ','))
          logger.info("pass orders to calculate discount")
          val processedOrders = orders.map(OrderProcessor.processOrderDiscounts(_, 2))
          processedOrders.foreach(println)
          
        case Failure(exception) =>
          logger.error(s"Failed to read file: $filePath, Error: ${exception.getMessage}")
      }

      if (Files.isDirectory(Paths.get(filePath))) { 
        val subPath = Paths.get(filePath)
        subPath.register(watchService, ENTRY_CREATE)
      }
    }
  }

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

