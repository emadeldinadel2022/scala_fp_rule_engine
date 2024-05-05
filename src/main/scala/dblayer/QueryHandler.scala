package dblayer

import Models.OrderWithDiscount

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


object PrivateExecutionContext{
  private val executor = Executors.newFixedThreadPool(2)
  implicit  val ec: ExecutionContext = ExecutionContext.fromExecutor(executor)
}

object QueryHandler {
    import slick.jdbc.PostgresProfile.api._
    import PrivateExecutionContext._


    def InsertOrder(order: OrderWithDiscount): Unit = {
      val query = SlickTables.orderTable += order
      val futureId: Future[Int] = DBConnector.db.run(query)

      futureId.onComplete {
        case Success(newOrderId) => println(s"Query was successful, new id is $newOrderId")
        case Failure(ex) => println(s"Query failed, reason $ex")
      }

      Thread.sleep(50000)
    }

  def insertBulkOrders(orders: Seq[OrderWithDiscount]): Unit = {
    val actions = DBIO.seq(orders.map(SlickTables.orderTable += _): _*)
    val futureResult: Future[Unit] = DBConnector.db.run(actions)

    futureResult.onComplete {
      case Success(_) => println("Bulk insert successful")
      case Failure(ex) => println(s"Bulk insert failed, reason: $ex")
    }

    Thread.sleep(100000)
  }

}
