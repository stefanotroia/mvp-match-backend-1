package it.stefanotroia.mvpmatch.service

import io.micronaut.http.HttpStatus
import it.stefanotroia.mvpmatch.model.user.UserModel
import it.stefanotroia.mvpmatch.model.user.UserRole
import it.stefanotroia.mvpmatch.model.user.UserTable
import it.stefanotroia.mvpmatch.utils.exceptions.ExceptionResponse
import it.stefanotroia.mvpmatch.utils.isAllNull
import it.stefanotroia.mvpmatch.utils.repository.ExposedRepository
import it.stefanotroia.mvpmatch.utils.security.BCryptPasswordEncoderService
import jakarta.inject.Singleton
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Singleton
class UserService(
    private val encoderService: BCryptPasswordEncoderService,
    private val productService: ProductService,
    private val sessionService: SessionService
) {

    private val exp: ExposedRepository<UserTable, UserModel> = ExposedRepository(UserTable, UserModel())

    fun createUser(user: UserModel): UserModel {
        return transaction {
            findUserByUsername(user.username!!)?.let {
                throw ExceptionResponse(
                    HttpStatus.CONFLICT,
                    "User already exists with username: ${user.username}",
                    "username_conflict"
                )
            }
            user.password = encoderService.encode(user.password)
            exp.save(user, this)
        }
    }

    fun findUserById(id: UUID, forUpdate: Boolean = false): UserModel {
        return transaction {
            exp.findFirstBy(Op.build { UserTable.id eq id }, forUpdate = forUpdate) ?: throw ExceptionResponse(
                HttpStatus.NOT_FOUND,
                "User not found",
                "not_found"
            )
        }
    }

    fun findUserByUsername(username: String): UserModel? {
        return transaction {
            exp.findFirstBy(Op.build { UserTable.username eq username })
        }
    }

    fun updateUser(id: UUID, user: UserModel, confirmPassword: Boolean?): UserModel {
        return transaction {
            val patch = UserModel()
            val persisted = findUserById(id)

            user.username?.takeIf { it != persisted.username }?.let {
                findUserByUsername(it)?.let {
                    throw ExceptionResponse(
                        HttpStatus.CONFLICT,
                        "User already exists with username: ${user.username}",
                        "username_conflict"
                    )
                }
                patch.username = it
            }

            user.role?.takeIf { it != persisted.role }?.let {
                throw ExceptionResponse(
                    HttpStatus.BAD_REQUEST,
                    "Unable to change role, create another account",
                    "bad_request"
                )
            }

            user.password?.let {
                if (confirmPassword != true)
                    throw ExceptionResponse(
                        HttpStatus.BAD_REQUEST,
                        "Confirm to change password",
                        "change_password_confirmation_nedeed"
                    )

                patch.password = encoderService.encode(user.password)
            }

            if (patch.isAllNull()) {
                persisted
            } else {
                exp.update(id, patch, this, patch = true, returnUpdatedRow = true)
            }

        }
    }

    fun updateAmount(userId: UUID, newAmount: Int, transaction: Transaction) {
        exp.update(userId, UserModel().apply { deposit = newAmount }, transaction, patch = true)
    }

    fun deleteUser(userId: UUID) {
        return transaction {
            val user = findUserById(userId)
            when (user.role) {
                UserRole.BUYER -> {
                    if ((user.deposit ?: 0) > 0) {
                        throw ExceptionResponse(
                            HttpStatus.BAD_REQUEST,
                            "Perform a reset, before deleting account",
                            "reset_needed"
                        )
                    }
                }
                UserRole.SELLER -> {
                    productService.deleteProductsBySellerId(userId)
                }
            }
            exp.delete(userId)
            sessionService.logout(userId)
        }
    }


}
