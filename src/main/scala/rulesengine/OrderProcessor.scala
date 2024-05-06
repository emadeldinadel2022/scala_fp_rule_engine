package rulesengine

import Models.{Order, OrderIdGenerator, ProcessedOrder, OrderWithDiscount}
import RuleEngine.{getDiscountRules, calcOrderDiscount}
import dblayer.TimeConvertor

object OrderProcessor {

  def toOrder(line: String, splitter: Char): Order = {
    val fields = line.split(splitter)

    Order(OrderIdGenerator.nextId(), fields(0), fields(1), fields(2), fields(3).toInt, fields(4).toFloat, fields(5), fields(6))
  }

  def processOrderDiscounts(order: Order, limit: Int): ProcessedOrder = {
    ProcessedOrder(order.id, OrderIdGenerator.nextId(), order.timestamp, order.productName, order.expiryDate, order.quantity,
                    order.unitPrice, order.channel, order.paymentMethod, calcOrderDiscount(order, limit,getDiscountRules))
  }

  def toOrderWithDiscount(processedOrder: ProcessedOrder): OrderWithDiscount = {
    OrderWithDiscount(processedOrder.id, TimeConvertor.stringToTimestamp(processedOrder.timestamp),
      processedOrder.productName, TimeConvertor.stringToDate(processedOrder.expiryDate), processedOrder.quantity,
      processedOrder.unitPrice, processedOrder.channel, processedOrder.paymentMethod, processedOrder.discount)
  }

}
