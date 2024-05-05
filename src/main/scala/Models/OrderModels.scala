package Models

import java.time.LocalDate
import java.sql.Timestamp
import java.util.concurrent.atomic.AtomicLong

case class Order(id: Long, timestamp: String, productName: String, expiryDate: String,
                 quantity: Int, unitPrice: Float, channel: String, paymentMethod: String)

case class ProcessedOrder(id: Long, seqNum: Long, timestamp: String, productName: String, expiryDate: String,
                 quantity: Int, unitPrice: Float, channel: String, paymentMethod: String, discount: Double)

case class OrderWithDiscount(id: Long, transactionTimestamp: Timestamp, productName: String, expiryDate: LocalDate,
                             quantity: Int, unitPrice: Float, channel: String, paymentMethod: String, discount: Double)

object OrderIdGenerator {
  private val counter = new AtomicLong(0)

  def nextId(): Long = counter.incrementAndGet()

  def newCounter(): Unit = counter.set(0)
}