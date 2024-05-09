package businesslogic

import businessmodels.{Order, OrderIdGenerator, OrderWithDiscount, ProcessedOrder}
import businesslogic.RefactorRuleEngine.{calculateOrderDiscount, getDiscountRules, calculateFinalPrice}
import com.typesafe.scalalogging.Logger


object OrderProcessor {

  val logger = Logger(getClass.getName)

  def toOrder(line: String, splitter: Char): Order = {
    val fields = line.split(splitter)

    logger.info("Parse line and convert into order object")
    
    Order(OrderIdGenerator.nextId(), fields(0), fields(1), fields(2),
      fields(3).toInt, fields(4).toFloat, fields(5), fields(6))
  }

  def processOrderDiscounts(order: Order, limit: Int): ProcessedOrder = {
    val discount = calculateOrderDiscount(order, limit, getDiscountRules)
    val finalPrice = calculateFinalPrice(order, discount)
    logger.info("add discount to order object and convert to processedObject")
    ProcessedOrder(order.id, OrderIdGenerator.nextId(), order.timestamp, order.productName, order.expiryDate, order.quantity,
      order.unitPrice, order.channel, order.paymentMethod, discount, finalPrice)
  }

}
