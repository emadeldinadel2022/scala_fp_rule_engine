package extractionlayer

import java.io.{File, FileOutputStream, PrintWriter}
import scala.io.{BufferedSource, Source}


object FileReader {
  val filePath = "src/main/resources/Orders.csv"

  def getFileLength(path: String): Int = Source.fromFile(path).getLines().length

  def readFile(path: String, batch: Int): List[String] = {
    if (batch <= getFileLength(path)) Source.fromFile(path).getLines().toList.tail.slice(0, batch)
    else List.empty
  }

  

}
