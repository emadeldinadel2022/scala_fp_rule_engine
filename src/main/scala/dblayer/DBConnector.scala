package dblayer

import slick.jdbc.PostgresProfile.api._

object DBConnector {
  
  val db = Database.forConfig("postgres")

}
