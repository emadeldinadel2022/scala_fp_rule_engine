import filecommunication.FileWatcher

object Main {

  def main(args: Array[String]): Unit = {
    val directoryPath = "src/main/generation_data_source"
    val fileWatcher = FileWatcher(directoryPath)
    fileWatcher.startListening()


  }

}
