package datarepository

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

/**
 * Provide a helper object in date/time conversion.
 * */
object TimeDateConvertor{

  /**
   * Converts a string representation of a timestamp to a `Timestamp` object.
   *
   * @param input The input string representing the timestamp.
   * @return The `Timestamp` object parsed from the input string.
   */
  def stringToTimestamp(input: String): Timestamp = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val localDateTime = LocalDateTime.parse(input, formatter)
    val timestamp = Timestamp.valueOf(localDateTime)
    timestamp
  }

  /**
   * Converts a string representation of a date to a `LocalDate` object.
   *
   * @param input The input string representing the date.
   * @return The `LocalDate` object parsed from the input string.
   */
  def stringToDate(input: String): LocalDate = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val localDate = LocalDate.parse(input, formatter)
    localDate
  }

}

