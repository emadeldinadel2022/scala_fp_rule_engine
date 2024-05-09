package businesslogic

import businessmodels.{DataLineage, Order, OrderIdGenerator, ProcessedOrder}

import java.time.LocalDate
import scala.util.Try
import com.typesafe.scalalogging.Logger
import filecommunication.FileReader


object RefactorRuleEngine {

  val logger: Logger = Logger(getClass.getName)

  private def extractDate(timestamp: String): Option[String] = {
    val regex = """(\d{4})-(\d{2})-(\d{2}).*""".r
    timestamp match {
      case regex(year, month, day) => 
        logger.info("Operation: extractDate" +" executed successfully, Result: " + s"$year-$month-$day")
        Some(s"$year-$month-$day")
      case _ => 
        logger.warn("Operation: extractDate" +" failed during execution, Result: (none) failed to extract date")
        None
    }
  }

  private def extractProductCategory(productName: String): Option[String] = {
    val prodCat = productName.split('-').headOption.map(_.trim.toLowerCase)
    prodCat match {
      case Some(cat) => logger.info("Operation: extractProductCategory" +" executed successfully, Result: "+prodCat.get)
      case None => logger.warn("Operation: extractProductCategory" +" failed during execution, "+ s"(none) product:$productName not have category")
    }
    prodCat
  }

  private def extractMMDD(timestamp: String): Option[String] = {
    val regex = """(\d{4})-(\d{2})-(\d{2}).*""".r
    val operation = DataLineage("extractMMDD", "extract the month and day from timestamp or date", "MMDD: ")
    timestamp match {
      case regex(_, month, day) => 
        logger.info(operation.operationName +" executed successfully, "+ operation.toString+s"$month-$day")
        Some(s"$month-$day")
      case _ => 
        logger.warn(operation.operationName +" failed during execution, "+ operation.toString+"(none) failed to parse timestamp")
        None
    }
  }

  private def toLocalDate(date: String): Option[LocalDate] = {
    val localDate = Try(LocalDate.parse(date)).toOption
    localDate match {
      case Some(date) => logger.info("Operation: tolocalDate" +" executed successfully, "+s"Result: $localDate.get.toString")
      case None => logger.warn("Operation: tolocalDate" +" failed during execution, "+ s"(none) failed to convert $date to localdate")
    }
    localDate
  }

  private def subtractDate(date1: LocalDate, date2: LocalDate): Long = {
    val dateDiff = date1.toEpochDay - date2.toEpochDay
    logger.info("Operation: subtractDate" +" executed successfully, "+"Result: "+(-1*dateDiff).toString + s"from $date1, $date2")
    Math.abs(dateDiff)
  }

  private def qualifyExpirationDiscount(order: Order): Boolean = {
    val expirationDate = toLocalDate(order.expiryDate)
    val transactionDate = toLocalDate(extractDate(order.timestamp) match {
      case Some(yyyymmdd) => yyyymmdd
      case None => " "
    })

    (expirationDate, transactionDate) match {
      case (Some(expDate), Some(curDate)) => 
        logger.info("Operation: qualifyExpirationDiscount" + s" executed successfully on Order: ${order.id}, Result: true")
        subtractDate(expDate, curDate) < 30
      case _ =>
        logger.info("Operation: qualifyExpirationDiscount" + s" check failed on Order: ${order.id}, Result: False")
        false
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
        if (daysRemaining < 30)
          logger.info("Operation: calculateExpirationDiscount" + s" executed successfully on Order: ${order.id}, Result: True")
          (30 - daysRemaining).toDouble / 100
        else
          logger.info("Operation: calculateExpirationDiscount" + s" check failed on Order: ${order.id}, Result: False")
          0.0
      case _ => 
        0.0
    }
  }

  private def qualifyCategoryDiscount(order: Order): Boolean =
    extractProductCategory(order.productName) match {
      case Some("wine") | Some("cheese") => 
        logger.info("Operation: qualifyCategoryDiscount" + s" executed successfully on Order: ${order.id}, Result: True")
        true
      case _ =>
        logger.info("Operation: qualifyCategoryDiscount" + s" check failed on Order: ${order.id}, Result: False")
        false
    }

  private def calculateCategoryDiscount(order: Order): Double =
    extractProductCategory(order.productName) match {
      case Some("wine") =>
        logger.info("Operation: calculateCategoryDiscount" + s" executed successfully on Order: ${order.id}, Result: wine --> 5%")
        0.05
      case Some("cheese") =>
        logger.info("Operation: calculateCategoryDiscount" + s" executed successfully on Order: ${order.id}, Result: cheese --> 10%")
        0.10
      case None =>
        logger.warn("Operation: calculateCategoryDiscount" + s" check failed on Order: ${order.id} Result: (none)")
        0.0
      case Some(_) =>
        logger.info("Operation: calculateCategoryDiscount" + s" check failed on Order: ${order.id} Result: is not in discount categories")
        0.0
    }

  private def qualifySpecialDiscount(order: Order): Boolean =
    extractMMDD(order.timestamp) match {
      case Some("03-23") =>
        logger.info("Operation: qualifySpecialDiscount" + s" executed successfully on Order: ${order.id}, Result: (True) happy birthday java, give 5% discount")
        true
      case _ =>
        logger.info("Operation: qualifySpecialDiscount" + s" check failed on Order: ${order.id}, Result: (False) waiting.... for event")
        false
    }

  private def calculateSpecialDiscount(order: Order): Double = 0.5

  private def qualifyQuantityDiscount(order: Order): Boolean =
    logger.info("Operation: qualifyQuantityDiscount"+ s" executed successfully on Order: ${order.id}, Result: True")
    order.quantity > 5

  private def calculateQuantityDiscount(order: Order): Double =
    order.quantity match {
      case quantity if quantity <= 5 =>
        logger.info("Operation: calculateQuantityDiscount"+ s" rule qualified on Order: ${order.id}, Result: 0%")
        0.0
      case quantity if quantity <= 9 =>
        logger.info("Operation: calculateQuantityDiscount"+ s" rule qualified on Order: ${order.id}, Result: 5%")
        0.05
      case quantity if quantity <= 14 =>
        logger.info("Operation: calculateQuantityDiscount"+ s" rule qualified on Order: ${order.id}, Result: 7%")
        0.07
      case _ =>
        logger.info("Operation: calculateQuantityDiscount"+ s" rule qualified on Order: ${order.id}, Result: 10%")
        0.1
    }

  private def qualifyPaymentMethodDiscount(order: Order): Boolean =
    logger.info("Operation: qualifyPaymentMethodDiscount"+ s" rule qualified on Order: ${order.id}, Result: "+s"${order.paymentMethod} --> 15%")
    order.paymentMethod.equalsIgnoreCase("visa")

  private def calculatePaymentMethodDiscount(order: Order): Double = 0.05

  private def qualifyAppUserDiscount(order: Order): Boolean =
    logger.info("Operation: qualifyAppUserDiscount"+ s" rule qualified on Order: ${order.id}, Result: "+s"${order.channel}")
    order.channel.equalsIgnoreCase("app")

  private def calculateAppUserDiscount(order: Order): Double = (Math.ceil(order.quantity / 5.0) * 5) / 100


  def calculateFinalPrice(processedOrder: ProcessedOrder): Double =
    logger.info("Operation: calculateFinalPrice"+ s" calculate final price for Order: ${processedOrder.id}")
    processedOrder.unitPrice - (processedOrder.discount * processedOrder.unitPrice)

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
    logger.info("Operation: calculateOrderDiscount" + s" calculate final discount for Order: ${order.id}")
    val applicableDiscounts = rules.collect {
      case (qualifier, calculator) if qualifier(order) => calculator(order)
    }.sorted.reverse.take(limit)

    applicableDiscounts.sum / limit
  }
  

}
