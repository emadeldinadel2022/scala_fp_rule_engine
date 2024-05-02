import Models.OrderWithDiscount
import dblayer.{DBConnector, SlickTables}

import java.time.LocalDate
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object PrivateExecutionContext{
  val executor = Executors.newFixedThreadPool(2)
  implicit  val ec: ExecutionContext = ExecutionContext.fromExecutor(executor)
}

object Main {
  import slick.jdbc.PostgresProfile.api._
  import PrivateExecutionContext._

  val newOrder = OrderWithDiscount(
    id = 0,
    transactionTimestamp = Timestamp.valueOf(LocalDateTime.now()),
    productName = "Product 1",
    expiryDate = LocalDate.now(),
    quantity = 10,
    unitPrice = 100.0f,
    channel = "Online",
    paymentMethod = "Credit Card",
    discount = 0.05
  )

  def demoInsertOrder(): Unit = {
    val query = SlickTables.orderTable += newOrder
    val futureId: Future[Int] = DBConnector.db.run(query)

    futureId.onComplete {
      case Success(newOrderId) => println(s"Query was successful, new id is $newOrderId")
      case Failure(ex) => println(s"Query failed, reason $ex")
    }

    Thread.sleep(50000)
  }

  def main(args: Array[String]): Unit = {
    demoInsertOrder()
  }

}
