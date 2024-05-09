package datarepository

import businessmodels.OrderWithDiscount
import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


object PrivateExecutionContext{
  // Create a private executor with a fixed thread pool of size 2, object manages the execution context used for asynchronous operations.
  private val executor = Executors.newFixedThreadPool(2)
  //Implicit execution context created from the executor, allowing it to be implicitly used in methods that require an execution context.
  implicit  val ec: ExecutionContext = ExecutionContext.fromExecutor(executor)
}

/**
 * 
 * Query Handler is responsible as the API for the DB operations.
 * 
 * */
object QueryHandler {
    import slick.jdbc.PostgresProfile.api._
    import PrivateExecutionContext._

  /**
   * Inserts an order into the database.
   *
   * @param order The order to insert.
   * @return Unit.
   */
    def insertOrder(order: OrderWithDiscount): Unit = {
      val query = SlickTables.orderTable += order
      val futureId: Future[Int] = DBConnector.db.run(query)

      futureId.onComplete {
        case Success(newOrderId) => println(s"Query was successful, new id is $newOrderId")
        case Failure(ex) => println(s"Query failed, reason $ex")
      }

      Thread.sleep(10000)
    }

  /**
   * Inserts multiple orders into the database in bulk.
   *
   * @param orders The orders to insert.
   * @return Unit.
   */
  def insertBulkOrders(orders: Seq[OrderWithDiscount]): Unit = {
    val actions = DBIO.seq(orders.map(SlickTables.orderTable += _): _*)
    val futureResult: Future[Unit] = DBConnector.db.run(actions)

    futureResult.onComplete {
      case Success(_) => println("Bulk insert successful")
      case Failure(ex) => println(s"Bulk insert failed, reason: $ex")
    }
    Thread.sleep(100000)
  }

  /**
   * Closes the database connection.
   */
  def dbClose(): Unit =  DBConnector.db.close()
}
