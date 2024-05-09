package datarepository

import slick.jdbc.PostgresProfile.api.*
import businessmodels.{OrderWithDiscount, ProcessedOrder}
import java.time.LocalDate
import java.sql.Timestamp


object SlickTables {
  
  class OrderTable(tag: Tag) extends Table[OrderWithDiscount](tag, Some("destination"), "Order") {
    def id = column[Long]("order_id", O.PrimaryKey, O.AutoInc)
    def transactionTimestamp = column[java.sql.Timestamp]("transaction_timestamp")
    def productName = column[String]("product_name")
    def expiryDate = column[LocalDate]("expiry_date")
    def quantity = column[Int]("quantity")
    def unitPrice = column[Float]("unit_price")
    def channel = column[String]("channel")
    def paymentMethod = column[String]("payment_method")
    def discount = column[Double]("discount")

    private def toOrder: ((Long, Timestamp, String, LocalDate, Int, Float, String, String, Double)) => OrderWithDiscount = {
      case (id, transactionDate, productName, expiryDate, quantity, unitPrice, channel, paymentMethod, discount) =>
        OrderWithDiscount(id, transactionDate, productName, expiryDate, quantity, unitPrice, channel, paymentMethod, discount)
    }
    //mapping function to the case class
    override def * = (id, transactionTimestamp, productName, expiryDate, quantity, unitPrice, channel, paymentMethod, discount) <> (toOrder, OrderWithDiscount.unapply)
  }


  def toOrderTable(processedOrder: ProcessedOrder): OrderWithDiscount = {
    OrderWithDiscount(processedOrder.id, TimeConvertor.stringToTimestamp(processedOrder.timestamp),
      processedOrder.productName, TimeConvertor.stringToDate(processedOrder.expiryDate), processedOrder.quantity,
      processedOrder.unitPrice, processedOrder.channel, processedOrder.paymentMethod, processedOrder.discount)
  }
  
  //API entry point, fetch and add records to the db
  lazy val orderTable = TableQuery[OrderTable]
}




