/*
 * Copyright 2020 HM Revenue & Customs
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

import javax.inject.Inject
import model.{PeriodKey, VrtId, VrtRepaymentDetailData}
import play.api.Logger
import play.api.mvc.{ControllerComponents, Request}
import repository.VrtRepo
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext

abstract class VrtController @Inject() (cc: ControllerComponents, repo: VrtRepo)
  (implicit executionContext: ExecutionContext) extends BackendController(cc) {
  private[controllers] def store()(implicit request: Request[VrtRepaymentDetailData]) = {
    val repaymentData: VrtRepaymentDetailData = request.body
    val periodKey = PeriodKey(repaymentData.repaymentDetailsData.periodKey)
    val riskingStatus = repaymentData.repaymentDetailsData.riskingStatus

    Logger.debug(s"received ${repaymentData.toString}")

    for {
      data <- repo.findByVrnAndPeriodKeyAndRiskingStatus(repaymentData.vrn, periodKey, riskingStatus)
      vrtId = data.headOption.fold(VrtId.fresh)(_._id.getOrElse(throw new RuntimeException("No id")))
      result <- repo.upsert(vrtId, repaymentData.copy(_id = Some(vrtId)))
    } yield {
      Ok(s"updated ${result.n.toString} records")
    }
  }
}
