package com.avast.sst.flyway

import cats.effect.Sync
import org.flywaydb.core.Flyway

import javax.sql.DataSource
import scala.jdk.CollectionConverters._

object FlywayModule {

  /** Makes [[org.flywaydb.core.Flyway]] from the given `javax.sql.DataSource` and config. */
  def make[F[_]: Sync](dataSource: DataSource, config: FlywayConfig): F[Flyway] = {
    Sync[F].delay {
      val builder = Flyway.configure
        .dataSource(dataSource)
        .baselineOnMigrate(config.baselineOnMigrate)
        .cleanDisabled(config.cleanDisabled)
        .cleanOnValidationError(config.cleanOnValidationError)
        .connectRetries(config.connectRetries)
        .encoding(config.encoding)
        .group(config.group)
        .ignoreMigrationPatterns(config.ignoreMigrationPatterns: _*)
        .mixed(config.mixed)
        .outOfOrder(config.outOfOrder)
        .validateOnMigrate(config.validateOnMigrate)
        .placeholderReplacement(config.placeholderReplacement)
        .placeholders(config.placeholders.asJava)

      config.baselineVersion.foreach(builder.baselineVersion)
      config.targetVersion.foreach(builder.target)
      config.baselineDescription.foreach(builder.baselineDescription)
      config.installedBy.foreach(builder.installedBy)
      if (config.locations.nonEmpty) builder.locations(config.locations: _*)

      builder.load()
    }
  }

}
