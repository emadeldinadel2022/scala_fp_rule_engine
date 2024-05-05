import Models.{Order, OrderIdGenerator}
import rulesengine.RuleEngine

import scala.io.{BufferedSource, Source}


object Main {

  def main(args: Array[String]): Unit = {
    val sourcePath: String = "src/main/resources/Orders.csv"
    val source: BufferedSource = Source.fromFile(sourcePath)
    val lines: List[String] = source.getLines().toList.tail.slice(0, 30)
    
    OrderIdGenerator.newCounter()

    val orders: List[Order] = lines.map(RuleEngine.toOrder)
    
    OrderIdGenerator.newCounter()

    orders.map(RuleEngine.processOrderDiscounts(_, 3)).foreach(println)
    
  }

}
