package businessmodels

import java.time.LocalDate
import java.sql.Timestamp
import java.util.concurrent.atomic.AtomicLong

/**
 * Represents an order, after parsing from the file in the raw form.
 *
 * @param id            The unique identifier for the order.
 * @param timestamp     The timestamp when the order was placed.
 * @param productName   The name of the product in the order.
 * @param expiryDate    The expiry date of the product.
 * @param quantity      The quantity of the product in the order.
 * @param unitPrice     The unit price of the product(but is the total price in the real data).
 * @param channel       The channel through which the order was placed.
 * @param paymentMethod The payment method used for the order.
 */
case class Order(id: Long, timestamp: String, productName: String, expiryDate: String,
                 quantity: Int, unitPrice: Float, channel: String, paymentMethod: String) {
  override def toString: String = s"$id,$timestamp,$productName,$expiryDate,$quantity,$unitPrice,$channel,$paymentMethod"
}

/**
 * Represents a processed order with discount information.
 *
 * @param id            The unique identifier for the order.
 * @param seqNum        The sequence number of the order.
 * @param timestamp     The timestamp when the order was processed.
 * @param productName   The name of the product in the order.
 * @param expiryDate    The expiry date of the product.
 * @param quantity      The quantity of the product in the order.
 * @param unitPrice     The unit price of the product.
 * @param channel       The channel through which the order was placed.
 * @param paymentMethod The payment method used for the order.
 * @param discount      The discount applied to the order.
 * @param finalPrice    The final price of the order after discount.
 */
case class ProcessedOrder(id: Long, seqNum: Long, timestamp: String, productName: String, expiryDate: String,
                          quantity: Int, unitPrice: Float, channel: String, paymentMethod: String, discount: Double, finalPrice: Double) {
  override def toString: String = s"$id,$seqNum,$timestamp,$productName,$expiryDate,$quantity,$unitPrice,$channel,$paymentMethod,$discount,$finalPrice"
}

/**
 * Represents an order with discount information using timestamps and dates, ready as data model for interact with db.
 *
 * @param id                   The unique identifier for the order.
 * @param transactionTimestamp The timestamp when the order transaction occurred.
 * @param productName          The name of the product in the order.
 * @param expiryDate           The expiry date of the product.
 * @param quantity             The quantity of the product in the order.
 * @param unitPrice            The unit price of the product.
 * @param channel              The channel through which the order was placed.
 * @param paymentMethod        The payment method used for the order.
 * @param discount             The discount applied to the order.
 * @param finalPrice           The final price of the order after discount.
 */
case class OrderWithDiscount(id: Long, transactionTimestamp: Timestamp, productName: String, expiryDate: LocalDate,
                             quantity: Int, unitPrice: Float, channel: String, paymentMethod: String, discount: Double, finalPrice: Double) {
  override def toString: String = s"$id,$transactionTimestamp,$productName,$expiryDate,$quantity,$unitPrice,$channel,$paymentMethod,$discount,$finalPrice"
}

case class DataLineage(operationName: String, description: String, returnOperation: String) {
  override def toString: String = s"Operation Name: $operationName, Description: $description, Return Operation: $returnOperation"
}

/**
 * Provides functionality to generate unique order IDs.
 */
object OrderIdGenerator {
  private val counter = new AtomicLong(0)

  /**
   * Generates the next unique order ID.
   *
   * @return The next unique order ID.
   */
  def nextId(): Long = counter.incrementAndGet()

  /**
   * Resets the order ID counter to zero.
   */
  def newCounter(): Unit = counter.set(0)
}