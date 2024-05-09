import filecommunication.FileWatcher

/**
* Main Object is the Entry point to the Application.
*/

object Main {

  def main(args: Array[String]): Unit = {
    //the directory path represent the path to the source directory for monitoring.
    val directoryPath = "src/main/generation_data_source"
    //create new object from the file watcher that represent the watcher service to monitor the directory.
    val fileWatcher = FileWatcher(directoryPath, ',')
    //start the watcher service to listen to the directory path
    fileWatcher.startListening()
  }

}
