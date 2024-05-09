package filecommunication

import businesslogic.OrderProcessor

import java.io.{BufferedWriter, FileWriter}
import com.typesafe.scalalogging.Logger

import scala.util.{Failure, Success, Try}

class FileStorageWriter(path: String) {

  val logger: Logger = Logger(getClass.getName)

  def writeFile(lines: List[String]): Unit = {
    Try {
      val bufferedWriter = new BufferedWriter(new FileWriter(path))
      lines.foreach { line =>
        bufferedWriter.write(line)
        bufferedWriter.newLine()
      }
      bufferedWriter.close()
      logger.info("File successfully written")
    } match {
      case Success(_) =>
        logger.info("File write operation completed successfully")
      case Failure(exception) =>
        logger.error("Error occurred while writing to file", exception)
    }
  }
}

object FileWriter {
  def apply(path: String): FileWriter = new FileWriter(path)
}

object Main{
  def main(args: Array[String]): Unit = {
    val reader = FileReader("src/main/resources/Orders.csv")
    val lines = reader.readFile(10)
    val orders = lines.map(OrderProcessor.toOrder(_, ','))
    val fileStorageWriterLZ = FileStorageWriter("src/main/localstorage/loading_zone/orders_1.csv")
    fileStorageWriterLZ.writeFile(orders.map(_.toString))
    val processedOrder  = orders.map(OrderProcessor.processOrderDiscounts(_, 2))
    val fileStorageWriterSZ = FileStorageWriter("src/main/localstorage/save_zone/orders_1.csv")
    fileStorageWriterSZ.writeFile(processedOrder.map(_.toString))
  }
}
