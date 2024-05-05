package rulesengine

import Models.Order
import java.time.LocalDate
import scala.math.BigDecimal.RoundingMode

object OrderProcessor{

  private def getDate(timestamp: String): String = timestamp.substring(0, 10)

  private def getProductCategory(product_name: String): String = product_name match {
    case product_name if product_name.contains('-') => product_name.split('-')(0).trim.toLowerCase
    case _ => " "
  }

  private def extractMMDD(timestamp: String): String = timestamp.substring(6, 10)

  private def subtractDate(date1: String, date2: String): Long = LocalDate.parse(getDate(date1)).toEpochDay - LocalDate.parse(getDate(date2)).toEpochDay

  private def qualifyExpirationDiscount(order: Order): Boolean = subtractDate(order.expiryDate, getDate(order.timestamp)) < 30

  private def calculateExpirationDiscount(order: Order): Double = (30 - subtractDate(order.expiryDate, getDate(order.timestamp))).toDouble / 100

  private def qualifyCategoryDiscount(order: Order): Boolean = getProductCategory(order.productName) match {
    case "wine" | "cheese" => true
    case _ => false
  }

  private def calculateCategoryDiscount(order: Order): Double = getProductCategory(order.productName) match {
    case "wine" => 0.05
    case "cheese" => 0.10
  }

  private def qualifySpecialDiscount(order: Order): Boolean = extractMMDD(order.timestamp) == "3-23"

  private def calculateSpecialDiscount(order: Order): Double = 0.5

  private def qualifyQuantityDiscount(order: Order): Boolean = order.quantity > 5

  private def calculateQuantityDiscount(order: Order): Double = if (order.quantity > 5  & order.quantity <= 9) 0.05
  else if (order.quantity > 9 & order.quantity <= 14) 0.07
  else 0.1

  private def qualifyPaymentMethodDiscount(order :Order): Boolean = order.paymentMethod.toLowerCase == "visa"

  private def calculatePaymentMethodDiscount(order: Order): Double = 0.15

  private def qualifyAppUserDiscount(order: Order): Boolean = order.channel.toLowerCase == "app"

  //private def calcAppUserDiscount

  def roundDiscount(discount: Double, place: Int): Double = BigDecimal(discount).setScale(place, BigDecimal.RoundingMode.HALF_UP).toDouble

  def getDiscountRules: List[(Function[Order, Boolean], Function[Order, Double])] = {
    val qualifierList = List(qualifyExpirationDiscount: Function[Order, Boolean],
      qualifyCategoryDiscount: Function[Order, Boolean], qualifySpecialDiscount: Function[Order, Boolean],
      qualifyQuantityDiscount: Function[Order, Boolean], qualifyPaymentMethodDiscount: Function[Order, Boolean])

    val calculatorList = List(calculateExpirationDiscount: Function[Order, Double],
      calculateCategoryDiscount: Function[Order, Double], calculateSpecialDiscount: Function[Order, Double],
      calculateQuantityDiscount: Function[Order, Double], calculatePaymentMethodDiscount: Function[Order, Double])

    qualifierList.zip(calculatorList)
  }

  def calcOrderDiscount(order: Order, limit: Int, rules: List[(Function[Order, Boolean], Function[Order, Double])]): Double = {
    rules.filter(a => a._1(order)).map(b => b._2(order)).sortBy(+_).take(limit).sum / limit
  }



  /*
  def getDiscountRules: List[Order => (Boolean, Double)] = List(
  order => qualifyExpirationDiscount(order) -> calculateExpirationDiscount(order),
  order => qualifyCategoryDiscount(order) -> calculateCategoryDiscount(order),
  order => qualifySpecialDiscount(order) -> calculateSpecialDiscount(order),
  order => qualifyQuantityDiscount(order) -> calculateQuantityDiscount(order),
  order => qualifyPaymentMethodDiscount(order) -> calculatePaymentMethodDiscount(order)
)

def calcOrderDiscount(order: Order, limit: Int, rules: List[Order => (Boolean, Double)]): Double = {
  rules
    .collect { case (qualify, calculate) if qualify(order) => calculate(order) }
    .sortBy(identity)
    .take(limit)
    .sum / limit
}
*/

}