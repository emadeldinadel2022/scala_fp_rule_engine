package extractionlayer

import scala.io.{BufferedSource, Source}


class FileReader(path: String) {
  private val bufferedSource: BufferedSource = Source.fromFile(path)

  def apply(): BufferedSource = bufferedSource

  def getFileLength: Int = bufferedSource.getLines().size
  
  def readFile(batch: Int): List[String] = {
    val lines = bufferedSource.getLines().toList
    bufferedSource.close()
    if (batch <= lines.size) lines.tail.take(batch)
    else List.empty
  }
}

