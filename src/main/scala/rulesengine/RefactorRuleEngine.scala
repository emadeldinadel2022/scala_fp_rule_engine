package rulesengine

import Models.Order

import java.time.LocalDate
import scala.util.Try

object RefactorRuleEngine {

  private def extractDate(timestamp: String): Option[String] = {
    val regex = """(\d{4})-(\d{2})-(\d{2}).*""".r
    timestamp match {
      case regex(year, month, day) => Some(s"$year-$month-$day")
      case _ => None
    }
  }

  private def extractProductCategory(productName: String): Option[String] = {
    productName.split('-').headOption.map(_.trim.toLowerCase)
  }

  private def extractMMDD(timestamp: String): Option[String] = {
    val regex = """(\d{4})-(\d{2})-(\d{2}).*""".r
    timestamp match {
      case regex(_, month, day) => Some(s"$month-$day")
      case _ => None
    }
  }

  private def toLocalDate(date: String): Option[LocalDate] = {
    Try(LocalDate.parse(date)).toOption
  }

  private def subtractDate(date1: LocalDate, date2: LocalDate): Long = {
    val dateDiff = date1.toEpochDay - date2.toEpochDay
    Math.abs(dateDiff)
  }

  private def qualifyExpirationDiscount(order: Order): Boolean = {
    val expirationDate = toLocalDate(order.expiryDate)
    val transactionDate = toLocalDate(extractDate(order.timestamp) match {
      case Some(yyyymmdd) => yyyymmdd
      case None => " "
    })

    (expirationDate, transactionDate) match {
      case (Some(expDate), Some(curDate)) => subtractDate(expDate, curDate) < 30
      case _ => false
    }
  }

  private def calculateExpirationDiscount(order: Order): Double = {
    val expirationDate = toLocalDate(order.expiryDate)
    val transactionDate = toLocalDate(extractDate(order.timestamp) match {
      case Some(yyyymmdd) => yyyymmdd
      case None => " "
    })

    (expirationDate, transactionDate) match {
      case (Some(expDate), Some(curDate)) =>
        val daysRemaining = subtractDate(expDate, curDate)
        if (daysRemaining < 30) (30 - daysRemaining).toDouble / 100
        else 0.0
      case _ => 0.0
    }
  }

  private def qualifyCategoryDiscount(order: Order): Boolean =
    extractProductCategory(order.productName) match {
      case Some("wine") | Some("cheese") => true
      case _ => false
    }

  private def calculateCategoryDiscount(order: Order): Double =
    extractProductCategory(order.productName) match {
      case Some("wine") => 0.05
      case Some("cheese") => 0.10
      case None => 0.0
      case Some(_) => 0.0
    }

  private def qualifySpecialDiscount(order: Order): Boolean =
    extractMMDD(order.timestamp) match {
      case Some("03-23") => true
      case _ => false
    }

  private def calculateSpecialDiscount(order: Order): Double = 0.5

  private def qualifyQuantityDiscount(order: Order): Boolean = order.quantity > 5

  private def calculateQuantityDiscount(order: Order): Double =
    order.quantity match {
      case quantity if quantity <= 5 => 0.0
      case quantity if quantity <= 9 => 0.05
      case quantity if quantity <= 14 => 0.07
      case _ => 0.1
    }

  private def qualifyPaymentMethodDiscount(order: Order): Boolean = order.paymentMethod.equalsIgnoreCase("visa")

  private def calculatePaymentMethodDiscount(order: Order): Double = 0.15

  private def qualifyAppUserDiscount(order: Order): Boolean = order.channel.equalsIgnoreCase("app")

  private def calculateAppUserDiscount(order: Order): Double = (Math.ceil(order.quantity / 5.0) * 5) / 100

  def roundDiscount(discount: Double, place: Int): Double = BigDecimal(discount).setScale(place, BigDecimal.RoundingMode.HALF_UP).toDouble

  def getDiscountRules: List[(Order => Boolean, Order => Double)] = {
    List(
      (qualifyExpirationDiscount, calculateExpirationDiscount),
      (qualifyCategoryDiscount, calculateCategoryDiscount),
      (qualifySpecialDiscount, calculateSpecialDiscount),
      (qualifyQuantityDiscount, calculateQuantityDiscount),
      (qualifyPaymentMethodDiscount, calculatePaymentMethodDiscount),
      (qualifyAppUserDiscount, calculateAppUserDiscount)
    )
  }

  def calculateOrderDiscount(order: Order, limit: Int, rules: List[(Order => Boolean, Order => Double)]): Double = {
    val applicableDiscounts = rules.collect {
      case (qualifier, calculator) if qualifier(order) => calculator(order)
    }.sorted.reverse.take(limit)

    applicableDiscounts.sum / limit
  }

  def main(args: Array[String]): Unit = {


  }

}
