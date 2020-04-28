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

import controllers.action.Actions
import javax.inject.{Inject, Singleton}
import model.{PeriodKey, Vrn, VrtId, VrtRepaymentDetailData}
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import repository.VrtRepo
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext

@Singleton
class Controller @Inject() (cc: ControllerComponents, vrtRepo: VrtRepo, actions: Actions)(implicit executionContext: ExecutionContext) extends BackendController(cc) {

  def storeRepaymentData(): Action[VrtRepaymentDetailData] = actions.securedActionStore().async(parse.json[VrtRepaymentDetailData]) { implicit request =>

    Logger.debug(s"received ${request.body.toString}")
    for {

      data <- vrtRepo.findByVrnAndPeriodKeyAndRiskingStatus(request.body.vrn,
                                                            PeriodKey(request.body.repaymentDetailsData.periodKey),
                                                            request.body.repaymentDetailsData.riskingStatus)
      vrtId: VrtId = if (data.isEmpty) VrtId.fresh else data(0)._id.getOrElse(throw new RuntimeException("No id"))
      result <- vrtRepo.upsert(vrtId, request.body.copy(_id = Some(vrtId)))

    } yield {
      Ok(s"updated ${result.n.toString} records")
    }
  }

  def findRepaymentData(vrn: Vrn, periodKey: PeriodKey): Action[AnyContent] = actions.securedAction(vrn).async { implicit request =>
    Logger.debug(s"received vrn ${vrn.value}, periodKey : ${periodKey.value}")

    for {
      data <- vrtRepo.findByVrnAndPeriodKey(vrn, periodKey)

    } yield {
      Ok(Json.toJson(data))
    }
  }

}
