package filecommunication

import java.io.{BufferedWriter, FileWriter}
import com.typesafe.scalalogging.Logger
import scala.util.{Failure, Success, Try}

/**
 * Represent a FileStorageWriter implement the file writer to the staging areas.
 *
 * @param String path for file to write on it.
 * */

class FileStorageWriter(path: String) {

  val logger: Logger = Logger(getClass.getName)

  /**
   * Writes the specified lines to the file represented by this FileStorageWriter instance.
   *
   * @param lines The lines to write to the file.
   * @return Unit => because it write to file
   */
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

//companion object with apply function
object FileWriter {
  def apply(path: String): FileWriter = new FileWriter(path)
}

