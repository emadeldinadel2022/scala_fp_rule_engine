package businesslogic

import businessmodels.{Order, OrderIdGenerator, ProcessedOrder}
import businesslogic.RefactorRuleEngine.{calculateOrderDiscount, getDiscountRules, calculateFinalPrice}
import com.typesafe.scalalogging.Logger

/**
 *
 * Act as The Coordinator between the RuleEngine Module that implement the business logic for qualifiers,
 * and discount calculators, and the parsed orders, and also between the Query Handler and Slick Data Model.
 *
 * */
object OrderProcessor {

  val logger: Logger = Logger(getClass.getName)

  /**
   * Converts a line of text into an Order object.
   *
   * @param line     The input line of text.
   * @param splitter The character used to split the fields in the line.
   * @return The Order object parsed from the input line.
   */
  def toOrder(line: String, splitter: Char): Order = {
    val fields = line.split(splitter)

    logger.info("Parse line and convert into order object")
    
    Order(OrderIdGenerator.nextId(), fields(0), fields(1), fields(2),
      fields(3).toInt, fields(4).toFloat, fields(5), fields(6))
  }

  /**
   * Processes an order to calculate discounts and create a ProcessedOrder object.
   *
   * @param order The Order object to process.
   * @param limit The discount limit to apply.
   * @return The ProcessedOrder object with calculated discounts.
   */
  def processOrderDiscounts(order: Order, limit: Int): ProcessedOrder = {
    // Calculate discount for the order based on discount rules
    val discount = calculateOrderDiscount(order, limit, getDiscountRules)

    // Calculate the final price after applying the discount
    val finalPrice = calculateFinalPrice(order, discount)

    logger.info("add discount to order object and convert to processedObject")

    ProcessedOrder(order.id, OrderIdGenerator.nextId(), order.timestamp, order.productName, order.expiryDate, order.quantity,
      order.unitPrice, order.channel, order.paymentMethod, discount, finalPrice)
  }

}
