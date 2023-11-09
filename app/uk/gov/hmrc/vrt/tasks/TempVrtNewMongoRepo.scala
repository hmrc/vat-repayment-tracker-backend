/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
