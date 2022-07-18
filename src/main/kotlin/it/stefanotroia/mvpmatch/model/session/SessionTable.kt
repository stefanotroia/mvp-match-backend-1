package it.stefanotroia.mvpmatch.model.session

import it.stefanotroia.mvpmatch.utils.repository.BaseTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.*

object SessionTable : BaseTable("session") {

    override var id: Column<UUID> = uuid("id").autoGenerate()
    override var createdAt: Column<LocalDateTime> = datetime("created_at").clientDefault { LocalDateTime.now() }
    override var updatedAt: Column<LocalDateTime?> = datetime("updated_at").nullable()

    var userId = uuid("user_id")
    var jit = uuid("jit")
    var revoked = bool("evicted")
    var exp = long("exp")


    override val primaryKey = PrimaryKey(id, name = "pk_session_id")

}