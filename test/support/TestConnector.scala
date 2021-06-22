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

package support

import javax.inject.{Inject, Singleton}
import model.{PeriodKey, Vrn, VrtRepaymentDetailData}
import play.api.Application
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, POST, route, writeableOf_AnyContentAsEmpty, writeableOf_AnyContentAsJson}
import scala.concurrent.Future

@Singleton
class TestConnector @Inject() (app: Application) {

  def store(vrtRepaymentDetailData: VrtRepaymentDetailData): Future[Result] = {
    val req = FakeRequest(POST, "/vat-repayment-tracker-backend/store").withJsonBody(Json.toJson(vrtRepaymentDetailData))

    route(app, req).getOrElse(Future.failed(new Exception))
  }

  def storeTestOnly(vrtRepaymentDetailData: VrtRepaymentDetailData): Future[Result] = {
    val req = FakeRequest(POST, "/vat-repayment-tracker-backend/test-only/store").withJsonBody(Json.toJson(vrtRepaymentDetailData))

    route(app, req).getOrElse(Future.failed(new Exception))
  }

  def find(vrn: Vrn, periodKey: PeriodKey): Future[Result] = {
    val req = FakeRequest(GET, s"/vat-repayment-tracker-backend/find/vrn/${vrn.value}/${periodKey.value}")

    route(app, req).getOrElse(Future.failed(new Exception))
  }

}
