package Models

import java.time.LocalDate
import java.sql.Timestamp


case class Order(id: Long, timestamp: String, productName: String, expiryDate: String,
                 quantity: Int, unitPrice: Float, channel: String, paymentMethod: String)

case class ProcessedOrder(id: Long, seqNum: Long, timestamp: String, productName: String, expiryDate: String,
                 quantity: Int, unitPrice: Float, channel: String, paymentMethod: String)

case class OrderWithDiscount(id: Long, transactionTimestamp: Timestamp, productName: String, expiryDate: LocalDate,
                             quantity: Int, unitPrice: Float, channel: String, paymentMethod: String, discount: Double)
