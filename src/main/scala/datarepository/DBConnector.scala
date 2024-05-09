package datarepository

import slick.jdbc.PostgresProfile.api._

object DBConnector {
  
  /**
   * 
   * Represent the database connection object that get the configuration form the application.conf in src/main/resources dir
   *
   * */
  
  val db = Database.forConfig("postgres")
  
}
