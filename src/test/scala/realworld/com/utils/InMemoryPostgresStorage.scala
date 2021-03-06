package realworld.com.utils

import de.flapdoodle.embed.process.runtime.Network.getLocalHost
import ru.yandex.qatools.embed.postgresql.PostgresStarter
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig.{ Credentials, Net, Storage, Timeout }
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig
import ru.yandex.qatools.embed.postgresql.distribution.Version

object InMemoryPostgresStorage {
  val dbHost = "127.0.0.1"
  val dbPort = 5999
  val dbName = "real_world_dev_test"
  val dbUser = "postgres"
  val dbPassword = "postgres"
  val jdbcUrl = s"jdbc:postgresql://$dbHost:$dbPort/$dbName"

  val psqlConfig = new PostgresConfig(
    Version.V9_6_11, new Net(dbHost, dbPort),
    new Storage(dbName), new Timeout(),
    new Credentials(dbUser, dbPassword))
  val psqlInstance = PostgresStarter.getDefaultInstance
  val flywayService = new DatabaseMigrationManager(jdbcUrl, dbUser, dbPassword)

  val process = psqlInstance.prepare(psqlConfig).start()

  flywayService.dropDatabase()
  flywayService.migrateDatabaseSchema()

  val databaseConnector = new DatabaseConnector(
    InMemoryPostgresStorage.jdbcUrl,
    InMemoryPostgresStorage.dbUser,
    InMemoryPostgresStorage.dbPassword)
}
