/*
 * Copyright 2022 HM Revenue & Customs
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

package repository

import model.des.RiskingStatus

import javax.inject.{Inject, Singleton}
import model.{PeriodKey, Vrn, VrtId, VrtRepaymentDetailData}
import org.mongodb.scala.model.{IndexModel, IndexOptions, Indexes}
import play.api.libs.json.Json
import repository.Repo.{Id, IdExtractor}
import uk.gov.hmrc.mongo.MongoComponent
import VrtRepo._
import java.util.concurrent.TimeUnit
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Sorts._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
final class VrtRepo @Inject()(mongoComponent: MongoComponent, config: VrtRepoConfig)(implicit ec: ExecutionContext)
  extends Repo[VrtId, VrtRepaymentDetailData](
    collectionName = "repayment-details-new-mongo",
    mongoComponent = mongoComponent,
    indexes = VrtRepo.indexes(config.expireMongoPayments.toSeconds),
    replaceIndexes = true) {


  def findByVrnAndPeriodKey(vrn: Vrn, periodKey: PeriodKey): Future[Seq[VrtRepaymentDetailData]] =
    collection.find(and(equal("vrn", vrn.value), equal("repaymentDetailsData.periodKey", periodKey.value)))
      .toFuture()

  def findByVrnAndPeriodKeyAndRiskingStatus(vrn: Vrn, periodKey: PeriodKey, riskingStatus: RiskingStatus): Future[Seq[VrtRepaymentDetailData]] =
    collection.find(
      and(
        equal("vrn", vrn.value),
        equal("repaymentDetailsData.periodKey", periodKey.value),
        equal("repaymentDetailsData.riskingStatus", riskingStatus.toString),
      )
    ).toFuture()

//  // TODO this should not exist
//  def removeByPeriodKeyForTest(periodKeys: List[PeriodKey]): Future[WriteResult] = {
//    collection.dele
//    remove("repaymentDetailsData.periodKey" -> Json.obj("$in" -> Json.toJson(periodKeys)))
//  }
}

object VrtRepo {

  def indexes(ttl: Long): Seq[IndexModel] = Seq(
    IndexModel(keys = Indexes.ascending("vrn")),
    IndexModel(keys = Indexes.ascending("repaymentDetailsData.periodKey")),
    IndexModel(keys = Indexes.ascending("repaymentDetailsData.riskingStatus")),
    IndexModel(
      keys = Indexes.ascending("createdOn"),
      indexOptions = IndexOptions().expireAfter(ttl, TimeUnit.SECONDS)
    )
  )

  implicit val journeyId: Id[VrtId] = (i: VrtId) => i.value

  implicit val journeyIdExtractor: IdExtractor[VrtRepaymentDetailData, VrtId] = (v: VrtRepaymentDetailData) => v._id.getOrElse(throw new RuntimeException(s"No id for $v (should be impossible)"))
}