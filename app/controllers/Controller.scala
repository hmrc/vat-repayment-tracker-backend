/*
 * Copyright 2021 HM Revenue & Customs
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
import model.{PeriodKey, Vrn, VrtRepaymentDetailData}
import play.api.Logger
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import repository.VrtRepo

import scala.concurrent.ExecutionContext

@Singleton
class Controller @Inject() (cc: ControllerComponents, repo: VrtRepo, actions: Actions)
  (implicit executionContext: ExecutionContext) extends VrtController(cc, repo) {
  def storeRepaymentData(): Action[VrtRepaymentDetailData] = actions.authorised.async(parse.json[VrtRepaymentDetailData]) { implicit request =>
    store()
  }

  def findRepaymentData(vrn: Vrn, periodKey: PeriodKey): Action[AnyContent] = actions.authorised(vrn).async {
    Logger("application").debug(s"received vrn ${vrn.value}, periodKey : ${periodKey.value}")

    repo.findByVrnAndPeriodKey(vrn, periodKey).map { data =>
      Ok(toJson(data))
    }
  }
}
