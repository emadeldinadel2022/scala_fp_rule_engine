import Models.{Order, OrderIdGenerator}
import rulesengine.OrderProcessor
import extractionlayer.FileReader
import dblayer.QueryHandler


object Main {

  def main(args: Array[String]): Unit = {
    val sourcePath: String = "src/main/resources/Orders.csv"
    val sourceReader = new FileReader(sourcePath)
    val sourceFile = sourceReader.apply()
    val lines = sourceReader.readFile(30)

    OrderIdGenerator.newCounter()

    val orders = lines.map(OrderProcessor.toOrder(_,','))
    orders.foreach(println)

    OrderIdGenerator.newCounter()

    val processedOrders = orders.map(OrderProcessor.processOrderDiscounts(_, 2))
    processedOrders.foreach(println)

    val ordersWithDiscounts = processedOrders.map(OrderProcessor.toOrderWithDiscount)
    ordersWithDiscounts.foreach(println)

    QueryHandler.insertOrder(ordersWithDiscounts(0))


  }

}
