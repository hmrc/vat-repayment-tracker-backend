package gov.hmrc.vrt.tasks

import com.mongodb.client.model.RenameCollectionOptions
import org.mongodb.scala.MongoNamespace
import org.mongodb.scala.model.RenameCollectionOptions
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
    .renameCollection(MongoNamespace("vat-repayment-tracker-backend", "repayment-details"), RenameCollectionOptions().dropTarget(true))
    .map { _ => logger.info("**************** collection rename task done.") }
}
