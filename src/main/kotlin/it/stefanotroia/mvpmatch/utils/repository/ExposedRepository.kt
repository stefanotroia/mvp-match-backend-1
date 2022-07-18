package it.stefanotroia.mvpmatch.utils.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Field
import java.time.LocalDateTime
import java.util.*

class ExposedRepository<T, M>(private val t: T, private val m: M) where T : BaseTable, M : BaseModel {

    private val log: Logger = LoggerFactory.getLogger(this::class.java.simpleName)

    fun findById(id: UUID, forUpdate: Boolean = false): M? {
        var select = t.select { t.id eq id }
        if (forUpdate)
            select = select.forUpdate()
        return select.map { transform(it) }.firstOrNull()
    }

    fun findFirstBy(op: Op<Boolean>, forUpdate: Boolean = false): M? {
        var select = t.select { op }
        if (forUpdate)
            select = select.forUpdate()
        return select.map { transform(it) }.firstOrNull()
    }

    fun findByQuery(
        query: Query,
        offset: Int,
        limit: Int
    ): PageResponse<M> {
        val total = query.count()
        val result = query.limit(limit, offset.toLong())
            .map { transform(it) }
        return PageResponse(result, offset, limit, total.toInt())
    }


    fun update(id: UUID, model: M, t: Transaction, returnUpdatedRow: Boolean = false, patch: Boolean = false): M {
        val statement = transformUpdate(id, model, patch)

        model.updatedAt = LocalDateTime.now()
        statement[this.t.updatedAt] = model.updatedAt

        statement.execute(t)
        return if (returnUpdatedRow) findById(id)!! else model
    }

    fun update(op: Op<Boolean>, model: M, t: Transaction, patch: Boolean = false): Int {
        val statement = createStatement(UpdateStatement(this.t, null, op), model, patch)
        model.updatedAt = LocalDateTime.now()
        statement[this.t.updatedAt] = model.updatedAt
        val result = statement.execute(t)
        return result ?: 0
    }

    fun save(model: M, t: Transaction): M {
        val statement = transform(model)
        statement.execute(t)
        return transform(statement.resultedValues!!.firstOrNull()!!)
    }

    fun saveAll(models: List<M>, t: Transaction) {
        val statement = BatchInsertStatement(this.t)
        models.forEach {
            statement.addBatch()
            transformBatch(it, statement)
        }
        statement.execute(t)
    }

    fun delete(id: UUID): Int {
        return t.deleteWhere { t.id eq id }
    }

    fun delete(op: Op<Boolean>): Int {
        return t.deleteWhere { op }
    }

    private fun transform(row: ResultRow): M {
        val model = m.clone() as M
        val cols = (t::class.java).declaredFields.toList()
        model::class.java.declaredFields.forEach {
            it.isAccessible = true
            findColumn(it.name, cols)?.let { c1 ->
                try {
                    var value: Any? = null
                    try{
                        value = row[c1]
                    }catch (_: Exception) {}

                    value?.let { _ ->
                            value.let { v ->  it.set(model, v) }
                    }

                } catch (e: Exception) {
                    log.warn("Unable to transform result-row for field: ${it.name} with cause: ${e.message}")
                }
            }
        }
        return model
    }

    private fun transform(model: M): InsertStatement<Number> {
        return createStatement(InsertStatement(t), model)
    }

    private fun transformBatch(model: M, statement: BatchInsertStatement): BatchInsertStatement {
        return createStatement(statement, model)
    }

    private fun transformUpdate(id: UUID, model: M, patch: Boolean = false): UpdateStatement {
        return createStatement(UpdateStatement(t, null, Op.build { t.id eq id }), model, patch)
    }

    private fun <S : UpdateBuilder<Int>> createStatement(statement: S, model: M, patchMode: Boolean = false): S {
        val cols = (t::class.java).declaredFields.toList()
        m::class.java.declaredFields.filter { it.name != "updatedAt" }
            .forEach {
                it.isAccessible = true
                findColumn(it.name, cols)?.let { c1 ->
                    try {
                        it.get(model)?.let { v ->
                                statement[c1] = v
                        }
                            ?: run {
                                if (c1.columnType.nullable && !patchMode) {
                                    val column = c1 as Column<Any?>
                                    statement[column] = null
                                }
                            }
                    } catch (e: Exception) {
                        print(e)
                    }
                }
            }
        return statement
    }

    private fun findColumn(name: String, cols: List<Field>? = null): Column<Any>? {
        return (cols ?: (t::class.java).declaredFields.toList()).firstOrNull { it.name == name }
            ?.let {
                it.isAccessible = true
                it.get(null) as Column<Any>
            }
    }
}