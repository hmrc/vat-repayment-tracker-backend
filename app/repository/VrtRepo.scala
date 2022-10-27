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

import model.des.{RepaymentDetailData, RiskingStatus}

import javax.inject.{Inject, Singleton}
import model.{PeriodKey, Vrn, VrtId, VrtRepaymentDetailData}
import org.mongodb.scala.model.Filters.{and, equal}
import org.mongodb.scala.model.{IndexModel, IndexOptions, Indexes}
import org.mongodb.scala.result
import play.api.libs.json.Json
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.Codecs
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.util.concurrent.TimeUnit
import scala.concurrent.{ExecutionContext, Future}

object VrtRepo {
  def indexes(cacheTtlInSeconds: Long): Seq[IndexModel] = Seq(
    IndexModel(
      keys         = Indexes.ascending("creationDate"),
      indexOptions = IndexOptions().expireAfter(cacheTtlInSeconds, TimeUnit.SECONDS)
    )
  )
}

@Singleton
final class VrtRepo @Inject() (
    mongoComponent: MongoComponent,
    config:         ServicesConfig
)(implicit ec: ExecutionContext)
  extends Repo[VrtId, VrtRepaymentDetailData](
    collectionName = "repayment-details",
    mongoComponent = mongoComponent,
    indexes        = VrtRepo.indexes(config.getDuration("vrt.ttl").toSeconds),
    replaceIndexes = true) {

  //  override def indexes: Seq[Index] = Seq(
  //    Index(
  //      key  = Seq("vrn" -> IndexType.Ascending),
  //      name = Some("vrnIdx")
  //    ),
  //    Index(
  //      key  = Seq("repaymentDetailsData.periodKey" -> IndexType.Ascending),
  //      name = Some("periodkeyIdx")
  //    ),
  //    Index(
  //      key  = Seq("repaymentDetailsData.riskingStatus" -> IndexType.Ascending),
  //      name = Some("riskingStatusIdx")
  //    ),
  //    Index(
  //      key     = Seq("createdOn" -> IndexType.Ascending),
  //      name    = Some("createdOnIdx"),
  //      options = BSONDocument("expireAfterSeconds" -> config.expireMongoPayments.toSeconds)
  //    )
  //  )

  def findByVrnAndPeriodKey(vrn: Vrn, periodKey: PeriodKey): Future[Seq[VrtRepaymentDetailData]] = {
    collection.find(and(equal("vrn", vrn.value), equal("repaymentDetailsData.periodKey", periodKey.value))).toFuture()
  }

  def findByVrnAndPeriodKeyAndRiskingStatus(vrn: Vrn, periodKey: PeriodKey, riskingStatus: RiskingStatus): Future[Seq[VrtRepaymentDetailData]] = {
    collection.find(and(equal("vrn", vrn.value), equal("repaymentDetailsData.periodKey", periodKey.value),
                        equal("repaymentDetailsData.riskingStatus", riskingStatus))).toFuture()
  }

  def removeByPeriodKeyForTest(periodKeys: List[PeriodKey]): Future[result.DeleteResult] = {
    collection.deleteOne(equal("repaymentDetailsData.periodKey", Json.obj("$in" -> Json.toJson(periodKeys)))).toFuture()
  }
}
