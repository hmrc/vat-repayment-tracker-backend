package uk.gov.hmrc.vrt.tasks

import model.{VrtId, VrtRepaymentDetailDataMongo}
import org.mongodb.scala.model.{IndexModel, IndexOptions, Indexes}
import repository.Repo.{Id, IdExtractor}
import repository.VrtRepo._
import repository.{Repo, VrtRepoConfig}
import uk.gov.hmrc.mongo.MongoComponent

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
final class TempVrtNewMongoRepo @Inject() (mongoComponent: MongoComponent, config: VrtRepoConfig)(implicit ec: ExecutionContext)
  extends Repo[VrtId, VrtRepaymentDetailDataMongo](
    collectionName = "repayment-details-new-mongo",
    mongoComponent = mongoComponent,
    indexes        = VrtRepo.indexes(config.expireMongoPayments.toSeconds),
    replaceIndexes = true) {

  val allDocumentsInNewMongo: Future[Seq[VrtRepaymentDetailDataMongo]] = collection.find().toFuture()

}

object VrtRepo {

  def indexes(ttl: Long): Seq[IndexModel] = Seq(
    IndexModel(keys = Indexes.ascending("vrn")),
    IndexModel(keys = Indexes.ascending("repaymentDetailsData.periodKey")),
    IndexModel(keys = Indexes.ascending("repaymentDetailsData.riskingStatus")),
    IndexModel(
      keys         = Indexes.ascending("creationDate"),
      indexOptions = IndexOptions().expireAfter(ttl, TimeUnit.SECONDS)
    )
  )

  implicit val vrtId: Id[VrtId] = (i: VrtId) => i.value

  implicit val vrtIdExtractor: IdExtractor[VrtRepaymentDetailDataMongo, VrtId] = (v: VrtRepaymentDetailDataMongo) => v._id
}
