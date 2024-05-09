package filecommunication

import scala.io.{BufferedSource, Source}
import scala.util.{Try, Success, Failure}
import com.typesafe.scalalogging.Logger

class FileReader(path: String) {

  val logger: Logger = Logger(getClass.getName)

  def readFile(batch: Int): List[String] = {
    var lines: List[String] = List.empty[String]

    Try {
      val bufferedSource: BufferedSource = Source.fromFile(path)
      logger.info("File start buffering")
      lines = bufferedSource.getLines().toList
      bufferedSource.close()
      logger.info("file buffered successfully and closed")
      lines
    } match {
      case Success(fileLines) =>
        if (batch <= fileLines.size) {
          logger.info("File successfully read")
          fileLines.take(batch).tail
        } else {
          logger.error("Batch size is greater than the file lines size")
          List.empty[String]
        }
      case Failure(exception) =>
        logger.error("Error occurred while reading file", exception)
        List.empty[String]
    }
  }
}

object FileReader{
  def apply(path: String): FileReader = new FileReader(path)
}
