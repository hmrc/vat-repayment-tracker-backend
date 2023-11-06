package uk.gov.hmrc.vrt.tasks

import play.api.Logging
import play.api.inject._
import uk.gov.hmrc.mongo.MongoComponent

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

class TasksModule extends SimpleModule(bind[CleanupTask].toSelf.eagerly())

@Singleton
class CleanupTask @Inject() (mongoComponent: MongoComponent)(implicit ec: ExecutionContext) extends Logging {
  logger.info("**************** Start cleanup tasks...")

  mongoComponent.client
    .getDatabase("vat-repayment-tracker-backend") // update
    .getCollection("repayment-details") // update
    .drop()
    .toFuture()
    .map { _ => logger.info("**************** cleanup done.") }
}
