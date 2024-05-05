package dblayer

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

object TimeConvertor{
  
  def stringToTimestamp(input: String): Timestamp = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val localDateTime = LocalDateTime.parse(input, formatter)
    val timestamp = Timestamp.valueOf(localDateTime)
    timestamp
  }

  def stringToDate(input: String): LocalDate = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val localDate = LocalDate.parse(input, formatter)
    localDate
  }

}

