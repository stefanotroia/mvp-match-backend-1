package it.stefanotroia.mvpmatch.model.product

import it.stefanotroia.mvpmatch.model.user.UserTable
import it.stefanotroia.mvpmatch.utils.repository.BaseTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.*

object ProductTable : BaseTable("product") {

    override var id: Column<UUID> = uuid("id").autoGenerate()
    override var createdAt: Column<LocalDateTime> = datetime("created_at").clientDefault { LocalDateTime.now() }
    override var updatedAt: Column<LocalDateTime?> = datetime("updated_at").nullable()

    var name = varchar("name", 255).uniqueIndex("product_name_uidx")
    var inStock = integer("in_stock").clientDefault { 0 }
    var cost = integer("cost")
    var sellerId = uuid("seller_id").index().references(UserTable.id)


    override val primaryKey = PrimaryKey(id, name = "pk_product_id")

}