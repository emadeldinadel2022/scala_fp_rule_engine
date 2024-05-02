package ordersrulesengine

import java.io.{File, FileOutputStream, PrintWriter}
import java.time.LocalDate
import scala.io.{BufferedSource, Source}
import scala.math.BigDecimal.RoundingMode


object OrderDiscountProcessing extends App{
  case class Order(timestamp: String, product_name: String, expiry_date: String, quantity: Int, unit_price: Float,
                   channel: String, payment_method: String)

  def getFileLength(path: String): Int = Source.fromFile(path).getLines().length
  def readFile(path: String, batch: Int): List[String] = {
    if (batch <= getFileLength(path))  Source.fromFile(path).getLines().toList.tail.slice(0, batch)
    else List.empty
  }
  private def toOrder(line: String): Order = Order(line.split(",")(0), line.split(",")(1), line.split(",")(2), line.split(",")(3).toInt,
      line.split(",")(4).toFloat, line.split(",")(5), line.split(",")(6))

  //private def writeOrder(order: Order): String =
  private def getDate(timestamp: String): String = timestamp.substring(0, 10)

  private def getProductCategory(product_name: String): String = product_name match {
    case product_name if product_name.contains('-') => product_name.split('-')(0).trim.toLowerCase
    case _ => " "
  }

  private def extractMMDD(timestamp: String): String = timestamp.substring(6, 10)

  private def subtractDate(date1: String, date2: String): Long = LocalDate.parse(getDate(date1)).toEpochDay - LocalDate.parse(getDate(date2)).toEpochDay

  def qualifyExpirationDiscount(order: Order): Boolean = subtractDate(order.expiry_date, getDate(order.timestamp)) < 30

  def calculateExpirationDiscount(order: Order): Double = (30 - subtractDate(order.expiry_date, getDate(order.timestamp))).toDouble / 100

  def qualifyCategoryDiscount(order: Order): Boolean = getProductCategory(order.product_name) match {
    case "wine" | "cheese" => true
    case _ => false
  }

  def calculateCategoryDiscount(order: Order): Double = getProductCategory(order.product_name) match {
    case "wine" => 0.05
    case "cheese" => 0.10
  }

  def qualifySpecialDiscount(order: Order): Boolean = extractMMDD(order.timestamp) == "3-23"

  def calculateSpecialDiscount(order: Order): Double = 0.5

  def qualifyQuantityDiscount(order: Order): Boolean = if (order.quantity > 5) true else false

  def calculateQuantityDiscount(order: Order): Double = if (order.quantity > 5  & order.quantity <= 9) 0.05
  else if (order.quantity > 9 & order.quantity <= 14) 0.07
  else 0.1


  val path = "src/main/resources/Orders.csv"
  val lines: List[String] = readFile(path, 1000)
  val orders: List[Order] =  lines.map(toOrder)

  val qualifierList = List(qualifyExpirationDiscount:Function[Order, Boolean],
    qualifyCategoryDiscount:Function[Order, Boolean], qualifySpecialDiscount:Function[Order, Boolean],
    qualifyQuantityDiscount:Function[Order, Boolean])

  val calculatorList = List(calculateExpirationDiscount: Function[Order, Double],
    calculateCategoryDiscount: Function[Order, Double], calculateSpecialDiscount: Function[Order, Double],
    calculateQuantityDiscount: Function[Order, Double])

  val rules = qualifierList.zip(calculatorList)

  case class ProcessedOrder(timestamp: String, product_name: String, expiry_date: String, quantity: Int, unit_price: Float,
                   channel: String, payment_method: String, discount: Double)




  def getOrderWithDiscount(order: Order, rules: List[(Function[Order, Boolean], Function[Order, Double])]): Double = {
    rules.filter(a => a._1(order)).map(b => b._2(order)).sortBy(+_).take(3).sum / 3
  }

  def roundDiscount(discount: Double, place: Int): Double = BigDecimal(discount).setScale(4, BigDecimal.RoundingMode.HALF_UP).toDouble

  orders.map(getOrderWithDiscount(_, rules)).map(roundDiscount(_, 4)).foreach(println)
}