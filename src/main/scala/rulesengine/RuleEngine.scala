package rulesengine

import Models.{Order, OrderIdGenerator, ProcessedOrder}
import OrderProcessor.{getDiscountRules, calcOrderDiscount}

object RuleEngine {

  def toOrder(line: String): Order = {
    val fields = line.split(",")

    Order(OrderIdGenerator.nextId(), fields(0), fields(1), fields(2), fields(3).toInt, fields(4).toFloat, fields(5), fields(6))
  }
  
  def processOrderDiscounts(order: Order, limit: Int): ProcessedOrder = {
    ProcessedOrder(order.id, OrderIdGenerator.nextId(), order.timestamp, order.productName, order.expiryDate, order.quantity, 
                    order.unitPrice, order.channel, order.paymentMethod, calcOrderDiscount(order, limit,getDiscountRules))
  }

}
