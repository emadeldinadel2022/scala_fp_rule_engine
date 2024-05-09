package datarepository

import slick.jdbc.PostgresProfile.api.*
import businessmodels.{OrderWithDiscount, ProcessedOrder}
import java.time.LocalDate
import java.sql.Timestamp

/**
 * 
 * The Data Model Object to store and interact with the db.
 * 
 */

object SlickTables {

  /**
   * Represents a Slick table mapping for the `Order` table in the database.
   *
   * @param tag The tag representing the table, the tag is like alias.
   */
  class OrderTable(tag: Tag) extends Table[OrderWithDiscount](tag, Some("destination"), "Order") {
    def id = column[Long]("order_id", O.PrimaryKey, O.AutoInc)
    def transactionTimestamp = column[java.sql.Timestamp]("transaction_timestamp")
    def productName = column[String]("product_name")
    def expiryDate = column[LocalDate]("expiry_date")
    def quantity = column[Int]("quantity")
    def totalPrice = column[Float]("total_price")
    def channel = column[String]("channel")
    def paymentMethod = column[String]("payment_method")
    def discount = column[Double]("discount")
    def finalPrice = column[Double]("final_price")

    // Mapping function to convert tuple to `OrderWithDiscount`, because i tried many time to use .tupled method and didn't work......
    private def toOrder: ((Long, Timestamp, String, LocalDate, Int, Float, String, String, Double, Double)) => OrderWithDiscount = {
      case (id, transactionDate, productName, expiryDate, quantity, totalPrice, channel, paymentMethod, discount, finalPrice) =>
        OrderWithDiscount(id, transactionDate, productName, expiryDate, quantity, totalPrice, channel, paymentMethod, discount, finalPrice)
    }

    //mapping function to the case class of the OrderWithDiscount
    override def * = (id, transactionTimestamp, productName, expiryDate, quantity, totalPrice, channel, paymentMethod, discount, finalPrice) <> (toOrder, OrderWithDiscount.unapply)
  }

  /**
   * Converts a processed order to an `OrderWithDiscount` instance.
   *
   * @param processedOrder The processed order to convert.
   * @return An `OrderWithDiscount` instance representing the processed order after apply some time/date conversion to be suitable for stroing in db.
   */
  def toOrderTable(processedOrder: ProcessedOrder): OrderWithDiscount = {
    val timestamp = TimeDateConvertor.stringToTimestamp(processedOrder.timestamp)
    val expiryDate = TimeDateConvertor.stringToDate(processedOrder.expiryDate)
    OrderWithDiscount(processedOrder.id, timestamp, processedOrder.productName, expiryDate, processedOrder.quantity,
      processedOrder.unitPrice, processedOrder.channel, processedOrder.paymentMethod, processedOrder.discount,
      processedOrder.finalPrice)
  }
  
  //API entry point, fetch and add records to the db, slick TableQuery for the OrderTable
  lazy val orderTable = TableQuery[OrderTable]
}




