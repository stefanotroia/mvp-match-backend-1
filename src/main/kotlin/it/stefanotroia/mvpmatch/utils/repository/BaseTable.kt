package it.stefanotroia.mvpmatch.utils.repository

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.time.LocalDateTime
import java.util.*

open class BaseTable(tableName: String): Table(tableName) {
     open lateinit var id: Column<UUID>
     open lateinit var createdAt: Column<LocalDateTime>
     open lateinit var updatedAt: Column<LocalDateTime?>
}
