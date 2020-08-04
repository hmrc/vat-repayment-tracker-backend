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

package support

import javax.inject.{Inject, Singleton}
import model.{PeriodKey, Vrn, VrtRepaymentDetailData}
import uk.gov.hmrc.http.HttpReads.Implicits.{readRaw, readFromJson}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TestConnector @Inject() (httpClient: HttpClient)(implicit executionContext: ExecutionContext) {

  val port = 19001
  val headers: Seq[(String, String)] = Seq(("Content-Type", "application/json"))

  def store(vrtRepaymentDetailData: VrtRepaymentDetailData)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    httpClient.POST(s"http://localhost:$port/vat-repayment-tracker-backend/store", vrtRepaymentDetailData, headers)

  def storeTestOnly(vrtRepaymentDetailData: VrtRepaymentDetailData)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    httpClient.POST(s"http://localhost:$port/vat-repayment-tracker-backend/test-only/store", vrtRepaymentDetailData, headers)

  def find(vrn: Vrn, periodKey: PeriodKey)(implicit hc: HeaderCarrier): Future[List[VrtRepaymentDetailData]] =
    httpClient.GET[List[VrtRepaymentDetailData]](s"http://localhost:$port/vat-repayment-tracker-backend/find/vrn/${vrn.value}/${periodKey.value}")
}
