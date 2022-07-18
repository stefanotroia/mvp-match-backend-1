package it.stefanotroia.mvpmatch.service

import io.micronaut.http.HttpStatus
import it.stefanotroia.mvpmatch.model.session.SessionModel
import it.stefanotroia.mvpmatch.model.session.SessionTable
import it.stefanotroia.mvpmatch.utils.exceptions.ExceptionResponse
import it.stefanotroia.mvpmatch.utils.repository.ExposedRepository
import jakarta.inject.Singleton
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Singleton
class SessionService {

    private val exp: ExposedRepository<SessionTable, SessionModel> = ExposedRepository(SessionTable, SessionModel())

    fun findByJit(jit: UUID): SessionModel {
        return transaction {
            exp.findFirstBy(Op.build { SessionTable.jit eq jit })
                ?: throw ExceptionResponse(HttpStatus.UNAUTHORIZED, "Session not found", "session_not_found")
        }
    }


    //find and evict active sessions
    fun logout(userId: UUID) {
        val time = System.currentTimeMillis()
        return transaction {
            exp.update(Op.build { SessionTable.revoked eq false }
                .and { SessionTable.exp greater time }
                .and { SessionTable.userId eq userId }, SessionModel().apply { revoked = true }, this, patch = true)
        }
    }

    //Create a session if no session is active for the user
    fun createSession(userId: UUID, jit: UUID, expiration: Long) {
        val time = System.currentTimeMillis()
        transaction {
            exp.findFirstBy(
                Op.build { SessionTable.revoked eq false }
                    .and { SessionTable.exp greater time }
                    .and { SessionTable.userId eq userId }
            )?.let {
                throw ExceptionResponse(
                    HttpStatus.UNAUTHORIZED,
                    "Session already active, logout needed ",
                    "session_conflict"
                )
            }


            exp.save(SessionModel().apply {
                this.userId = userId
                this.jit = jit
                this.exp = expiration
            }, this)
        }
    }
}