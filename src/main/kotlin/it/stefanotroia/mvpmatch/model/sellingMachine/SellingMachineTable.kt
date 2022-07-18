package it.stefanotroia.mvpmatch.model.sellingMachine

import it.stefanotroia.mvpmatch.utils.repository.BaseTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.*

object SellingMachineTable : BaseTable("selling_machine") {

    override var id: Column<UUID> = uuid("id").autoGenerate()
    override var createdAt: Column<LocalDateTime> = datetime("created_at").clientDefault { LocalDateTime.now() }
    override var updatedAt: Column<LocalDateTime?> = datetime("updated_at").nullable()

    var coin = integer("coin").uniqueIndex("selling_machine_coin")
    var amount = integer("amount").clientDefault { 0 }

    override val primaryKey = PrimaryKey(id, name = "pk_selling_machine_id")

}