/*
 * Copyright 2019 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}
import model.{PeriodKey, Vrn, VrtId, VrtRepaymentDetailData}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.indexes._
import reactivemongo.bson.BSONDocument

import scala.concurrent.{ExecutionContext, Future}

@Singleton
final class VrtRepo @Inject() (reactiveMongoComponent: ReactiveMongoComponent, config: VrtRepoConfig)(implicit ec: ExecutionContext)
  extends Repo[VrtRepaymentDetailData, VrtId]("repayment-details", reactiveMongoComponent) {

  override def indexes: Seq[Index] = Seq(
    Index(
      key  = Seq("vrn" -> IndexType.Ascending),
      name = Some("vrnIdx")
    ),
    Index(
      key  = Seq("repaymentDetailsData.periodKey" -> IndexType.Ascending),
      name = Some("periodkeyIdx")
    ),
    Index(
      key  = Seq("repaymentDetailsData.riskingStatus" -> IndexType.Ascending),
      name = Some("riskingStatusIdx")
    ),
    Index(
      key     = Seq("createdOn" -> IndexType.Ascending),
      name    = Some("createdOnIdx"),
      options = BSONDocument("expireAfterSeconds" -> config.expireMongoPayments.toSeconds)
    )
  )

  def findByVrnAndPeriodKey(vrn: Vrn, periodKey: PeriodKey): Future[List[VrtRepaymentDetailData]] = {
    find("vrn" -> vrn.value, "repaymentDetailsData.periodKey" -> periodKey.value)

    this.find()
  }

  def findByVrnAndPeriodKeyAndRiskingStatus(vrn: Vrn, periodKey: PeriodKey, riskingStatus: String): Future[List[VrtRepaymentDetailData]] = {
    find("vrn" -> vrn.value, "repaymentDetailsData.periodKey" -> periodKey.value,
      "repaymentDetailsData.riskingStatus" -> riskingStatus)
  }

}
