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

package controller

import java.time.LocalDate

import model._
import play.api.Logger
import play.api.http.Status
import reactivemongo.bson.BSONObjectID
import repository.VrtRepo
import support._

class ContollerSpec extends ItSpec {

  val testConnector = injector.instanceOf[TestConnector]
  val vrn: Vrn = Vrn("2345678890")
  val vrn2: Vrn = Vrn("2345678891")
  val id = VrtId(BSONObjectID.generate.stringify)
  val id2 = VrtId(BSONObjectID.generate.stringify)
  val periodKey: PeriodKey = PeriodKey("18AC")
  val vrtData = VrtRepaymentDetailData(Some(id), LocalDate.now(), vrn, DesData.repaymentDetail)
  val vrtData2 = VrtRepaymentDetailData(Some(id), LocalDate.now(), vrn2, DesData.repaymentDetail)
  val vrtData3 = VrtRepaymentDetailData(Some(id2), LocalDate.now(), vrn, DesData.repaymentDetail.copy(riskingStatus = "SENT_FOR_RISKING"))

  val repo = injector.instanceOf[VrtRepo]

  override def beforeEach(): Unit = {
    val remove = repo.removeAll().futureValue
  }

  "store data " in {
    AuthWireMockResponses.authOkWithEnrolments(wireMockBaseUrlAsString = wireMockBaseUrlAsString, vrn = vrn, enrolment = EnrolmentKeys.mtdVatEnrolmentKey)
    val result = testConnector.store(vrtData).futureValue
    result.status shouldBe Status.OK
    result.body shouldBe "updated 1 records"
  }

  "store data testOnly" in {
    val result = testConnector.storeTestOnly(vrtData).futureValue
    result.status shouldBe Status.OK
    result.body shouldBe "updated 1 records"
  }

  "find data " in {
    AuthWireMockResponses.authOkWithEnrolments(wireMockBaseUrlAsString = wireMockBaseUrlAsString, vrn = vrn, enrolment = EnrolmentKeys.mtdVatEnrolmentKey)
    val result = testConnector.store(vrtData).futureValue
    result.status shouldBe Status.OK
    val findResult: List[VrtRepaymentDetailData] = testConnector.find(vrn, periodKey).futureValue
    findResult.size shouldBe 1

  }

  "Store two records then find 1" in {
    AuthWireMockResponses.authOkWithEnrolments(wireMockBaseUrlAsString = wireMockBaseUrlAsString, vrn = vrn2, enrolment = EnrolmentKeys.mtdVatEnrolmentKey)
    val result = testConnector.store(vrtData).futureValue
    result.status shouldBe Status.OK
    val result2 = testConnector.store(vrtData2).futureValue
    result2.status shouldBe Status.OK
    val findResult: List[VrtRepaymentDetailData] = testConnector.find(vrn2, periodKey).futureValue
    findResult.size shouldBe 1

  }

  "Store record twice with an update, should find most recent version" in {
    AuthWireMockResponses.authOkWithEnrolments(wireMockBaseUrlAsString = wireMockBaseUrlAsString, vrn = vrn, enrolment = EnrolmentKeys.mtdVatEnrolmentKey)
    val result = testConnector.store(vrtData).futureValue
    result.status shouldBe Status.OK
    val result2 = testConnector.store(vrtData.copy(creationDate = LocalDate.now().plusDays(1))).futureValue
    result2.status shouldBe Status.OK
    val findResult: List[VrtRepaymentDetailData] = testConnector.find(vrn, periodKey).futureValue
    findResult.size shouldBe 1
    findResult(0).creationDate shouldBe LocalDate.now.plusDays(1)
  }

  "Store two records for the same VRN and Period Key bu different status should find 2" in {
    AuthWireMockResponses.authOkWithEnrolments(wireMockBaseUrlAsString = wireMockBaseUrlAsString, vrn = vrn, enrolment = EnrolmentKeys.mtdVatEnrolmentKey)
    val result = testConnector.store(vrtData).futureValue
    result.status shouldBe Status.OK
    val result2 = testConnector.store(vrtData3).futureValue
    result2.status shouldBe Status.OK
    val findResult: List[VrtRepaymentDetailData] = testConnector.find(vrn, periodKey).futureValue
    findResult.size shouldBe 2

  }

  "return an empty list if record vrn and periodKey combination not found" in {
    AuthWireMockResponses.authOkWithEnrolments(wireMockBaseUrlAsString = wireMockBaseUrlAsString, vrn = vrn, enrolment = EnrolmentKeys.mtdVatEnrolmentKey)
    val findResult: List[VrtRepaymentDetailData] = testConnector.find(vrn, periodKey).futureValue
    findResult.size shouldBe 0
  }

  "store data , not authorised should result in 401" in {
    AuthWireMockResponses.authFailed
    val result = testConnector.store(vrtData).failed.futureValue
    result.getMessage should include("Session record not found")
  }

  "Get  data, not authorised should result in 401" in {
    AuthWireMockResponses.authFailed
    val result = testConnector.find(vrn, periodKey).failed.futureValue
    result.getMessage should include("Session record not found")
  }

  "Get  data, logged in but no access to VRN" in {
    AuthWireMockResponses.authOkWithEnrolments(wireMockBaseUrlAsString = wireMockBaseUrlAsString, vrn = vrn2, enrolment = EnrolmentKeys.mtdVatEnrolmentKey)
    val result = testConnector.find(vrn, periodKey).failed.futureValue
    result.getMessage should include("You do not have access to this vrn: 2345678890")
  }

  "Get  data, logged in but no enrolments" in {
    AuthWireMockResponses.authOkNoEnrolments(wireMockBaseUrlAsString = wireMockBaseUrlAsString)
    val result = testConnector.find(vrn, periodKey).failed.futureValue
    result.getMessage should include("You do not have access to this service")
  }
}
