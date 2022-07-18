package it.stefanotroia.mvpmatch.configuration

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.micronaut.context.annotation.Value
import it.stefanotroia.mvpmatch.acceptedCoins
import it.stefanotroia.mvpmatch.model.product.ProductTable
import it.stefanotroia.mvpmatch.model.sellingMachine.SellingMachineModel
import it.stefanotroia.mvpmatch.model.sellingMachine.SellingMachineTable
import it.stefanotroia.mvpmatch.model.session.SessionTable
import it.stefanotroia.mvpmatch.model.user.UserTable
import it.stefanotroia.mvpmatch.utils.annotations.EagerInit
import it.stefanotroia.mvpmatch.utils.repository.ExposedRepository
import jakarta.inject.Singleton
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import javax.annotation.PostConstruct

@Singleton
@EagerInit
open class DataSourceConfiguration {

    private var db: Database? = null
    @Value("\${mvp.database.jdbcUrl}")
    private var _jdbcUrl: String? = null
    @Value("\${mvp.database.driverClassName}")
    private var _driverClassName: String? = null
    @Value("\${mvp.database.username}")
    private var _username: String? = null
    @Value("\${mvp.database.password}")
    private var _password: String? = null
    @Value("\${mvp.database.maximumPoolSize}")
    private var _maximumPoolSize: Int = 5

    @PostConstruct
    open fun init() {

        val config = HikariConfig().apply {
            this.jdbcUrl =_jdbcUrl
            this.driverClassName = _driverClassName
            this.username = _username
            this.password = _password
            this.maximumPoolSize = _maximumPoolSize
        }
        val dataSource = HikariDataSource(config)
        db = Database.connect(dataSource)


        val exp: ExposedRepository<SellingMachineTable, SellingMachineModel> =
            ExposedRepository(SellingMachineTable, SellingMachineModel())

        transaction {
            SchemaUtils.create(UserTable, ProductTable, SellingMachineTable, SessionTable)

            if (exp.findByQuery(SellingMachineTable.selectAll(), 0, 10).total == 0) {
                val rows = acceptedCoins.map {
                    SellingMachineModel().apply {
                        coin = it
                        amount = 0
                    }
                }
                exp.saveAll(rows, this)
            }
        }
    }

}