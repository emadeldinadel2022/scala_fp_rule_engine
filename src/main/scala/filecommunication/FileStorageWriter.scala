package filecommunication

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
