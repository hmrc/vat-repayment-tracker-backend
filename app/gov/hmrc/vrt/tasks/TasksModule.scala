package gov.hmrc.vrt.tasks

import org.mongodb.scala.MongoNamespace
import play.api.Logging
import play.api.inject._
import uk.gov.hmrc.mongo.MongoComponent

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

class TasksModule extends SimpleModule(bind[RenameCollectionTask].toSelf.eagerly())

@Singleton
class RenameCollectionTask @Inject()(mongoComponent: MongoComponent)(implicit ec: ExecutionContext) extends Logging {
  logger.info("**************** Start collection rename task...")

  mongoComponent.client
    .getDatabase("vat-repayment-tracker-backend")
    .getCollection("repayment-details-new-mongo")
    .renameCollection(MongoNamespace("vat-repayment-tracker-backend", "repayment-details-new-mongo"))
    .map { _ => logger.info("**************** collection rename task done.") }
}
