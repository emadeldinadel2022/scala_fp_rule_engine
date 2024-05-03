package ordersrulesengine

import Models.Order
import java.time.LocalDate
import scala.math.BigDecimal.RoundingMode
import java.util.concurrent.atomic.AtomicLong


object OrderIdGenerator {
  private val counter = new AtomicLong(0)

  def nextId(): Long = counter.incrementAndGet()
}


object RuleEngine{

  def toOrder(line: String): Order = {
    val fields = line.split(",")

    val orderId = OrderIdGenerator.nextId()

    Order( orderId, fields(0), fields(1), fields(2), fields(3).toInt, fields(4).toFloat, fields(5), fields(6))
  }

  private def getDate(timestamp: String): String = timestamp.substring(0, 10)

  private def getProductCategory(product_name: String): String = product_name match {
    case product_name if product_name.contains('-') => product_name.split('-')(0).trim.toLowerCase
    case _ => " "
  }

  private def extractMMDD(timestamp: String): String = timestamp.substring(6, 10)

  private def subtractDate(date1: String, date2: String): Long = LocalDate.parse(getDate(date1)).toEpochDay - LocalDate.parse(getDate(date2)).toEpochDay

  def qualifyExpirationDiscount(order: Order): Boolean = subtractDate(order.expiryDate, getDate(order.timestamp)) < 30

  def calculateExpirationDiscount(order: Order): Double = (30 - subtractDate(order.expiryDate, getDate(order.timestamp))).toDouble / 100

  def qualifyCategoryDiscount(order: Order): Boolean = getProductCategory(order.productName) match {
    case "wine" | "cheese" => true
    case _ => false
  }

  def calculateCategoryDiscount(order: Order): Double = getProductCategory(order.productName) match {
    case "wine" => 0.05
    case "cheese" => 0.10
  }

  def qualifySpecialDiscount(order: Order): Boolean = extractMMDD(order.timestamp) == "3-23"

  def calculateSpecialDiscount(order: Order): Double = 0.5

  def qualifyQuantityDiscount(order: Order): Boolean = if (order.quantity > 5) true else false

  def calculateQuantityDiscount(order: Order): Double = if (order.quantity > 5  & order.quantity <= 9) 0.05
  else if (order.quantity > 9 & order.quantity <= 14) 0.07
  else 0.1

  //val path = "src/main/resources/Orders.csv"
  //val lines: List[String] = readFile(path, 1000)
  //val orders: List[Order] =  lines.map(toOrder)

  val qualifierList = List(qualifyExpirationDiscount:Function[Order, Boolean],
    qualifyCategoryDiscount:Function[Order, Boolean], qualifySpecialDiscount:Function[Order, Boolean],
    qualifyQuantityDiscount:Function[Order, Boolean])

  val calculatorList = List(calculateExpirationDiscount: Function[Order, Double],
    calculateCategoryDiscount: Function[Order, Double], calculateSpecialDiscount: Function[Order, Double],
    calculateQuantityDiscount: Function[Order, Double])

  val rules = qualifierList.zip(calculatorList)

  def getOrderWithDiscount(order: Order, limit: Int, rules: List[(Function[Order, Boolean], Function[Order, Double])]): Double = {
    rules.filter(a => a._1(order)).map(b => b._2(order)).sortBy(+_).take(limit).sum / limit
  }

  def roundDiscount(discount: Double, place: Int): Double = BigDecimal(discount).setScale(4, BigDecimal.RoundingMode.HALF_UP).toDouble

  //orders.map(getOrderWithDiscount(_, rules)).map(roundDiscount(_, 4)).foreach(println)
}