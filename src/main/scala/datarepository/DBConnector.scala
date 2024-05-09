package datarepository

import slick.jdbc.PostgresProfile.api._

object DBConnector {
  
  val db = Database.forConfig("postgres")
  
  def closeConnection(): Unit = db.close()
  
}
