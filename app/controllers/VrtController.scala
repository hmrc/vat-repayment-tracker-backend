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

package controllers

import model.{PeriodKey, VrtId, VrtRepaymentDetailData, VrtRepaymentDetailDataMongo}
import play.api.Logging
import play.api.mvc.{ControllerComponents, Request, Result}
import repository.VrtRepo
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

abstract class VrtController @Inject() (cc: ControllerComponents, repo: VrtRepo)
  (implicit executionContext: ExecutionContext) extends BackendController(cc) with Logging {

  def store()(implicit request: Request[VrtRepaymentDetailData]): Future[Result] = {
    val repaymentData: VrtRepaymentDetailData = request.body
    val periodKey = PeriodKey(repaymentData.repaymentDetailsData.periodKey)
    val riskingStatus = repaymentData.repaymentDetailsData.riskingStatus

    logger.debug(s"received ${repaymentData.toString}")

    for {
      data <- repo.findByVrnAndPeriodKeyAndRiskingStatus(repaymentData.vrn, periodKey, riskingStatus)
      vrtId = data.headOption.fold(VrtId.generate)(_._id)
      _ <- repo.upsert(VrtRepaymentDetailDataMongo(repaymentData, vrtId))
    } yield {
      Ok(s"updated 1 record")
    }
  }
}
