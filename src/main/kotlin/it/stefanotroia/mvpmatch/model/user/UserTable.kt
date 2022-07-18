package it.stefanotroia.mvpmatch.model.user

import it.stefanotroia.mvpmatch.utils.repository.BaseTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.*

object UserTable : BaseTable("user") {

    override var id: Column<UUID> = uuid("id").autoGenerate()
    override var createdAt: Column<LocalDateTime> = datetime("created_at").clientDefault { LocalDateTime.now() }
    override var updatedAt: Column<LocalDateTime?> = datetime("updated_at").nullable()

    var role = enumerationByName("role", 50, UserRole::class)
    var username = varchar("username", 100).uniqueIndex("user_username_idx")
    var password = varchar("password", 255)
    var deposit = integer("deposit").clientDefault { 0 }

    override val primaryKey = PrimaryKey(id, name = "pk_user_id")

}