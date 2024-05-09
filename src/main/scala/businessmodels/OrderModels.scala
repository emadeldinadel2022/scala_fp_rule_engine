package businessmodels

import java.time.LocalDate
import java.sql.Timestamp
import java.util.concurrent.atomic.AtomicLong

case class Order(id: Long, timestamp: String, productName: String, expiryDate: String,
                 quantity: Int, unitPrice: Float, channel: String, paymentMethod: String) {
  override def toString: String = s"$id,$timestamp,$productName,$expiryDate,$quantity,$unitPrice,$channel,$paymentMethod"
}

case class ProcessedOrder(id: Long, seqNum: Long, timestamp: String, productName: String, expiryDate: String,
                          quantity: Int, unitPrice: Float, channel: String, paymentMethod: String, discount: Double, finalPrice: Double) {
  override def toString: String = s"$id,$seqNum,$timestamp,$productName,$expiryDate,$quantity,$unitPrice,$channel,$paymentMethod,$discount,$finalPrice"
}

case class OrderWithDiscount(id: Long, transactionTimestamp: Timestamp, productName: String, expiryDate: LocalDate,
                             quantity: Int, unitPrice: Float, channel: String, paymentMethod: String, discount: Double, finalPrice: Double) {
  override def toString: String = s"$id,$transactionTimestamp,$productName,$expiryDate,$quantity,$unitPrice,$channel,$paymentMethod,$discount,$finalPrice"
}
case class DataLineage(operationName: String, description: String, returnOperation: String) {
  override def toString: String = s"Operation Name: $operationName, Description: $description, Return Operation: $returnOperation"
}
object OrderIdGenerator {
  private val counter = new AtomicLong(0)

  def nextId(): Long = counter.incrementAndGet()

  def newCounter(): Unit = counter.set(0)
}