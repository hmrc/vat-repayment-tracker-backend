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

import java.time.LocalDate.now

import model.EnrolmentKeys.mtdVatEnrolmentKey
import model._
import play.api.http.Status
import reactivemongo.bson.BSONObjectID
import repository.VrtRepo
import support.AuthStub._
import support.DesData.repaymentDetail
import support._
import uk.gov.hmrc.http.HeaderCarrier

class ControllerSpec extends ItSpec with Status {
  implicit val emptyHC: HeaderCarrier = HeaderCarrier()

  private val vrn = Vrn("2345678890")
  private val vrn2 = Vrn("2345678891")
  private val id = VrtId(BSONObjectID.generate.stringify)
  private val id2 = VrtId(BSONObjectID.generate.stringify)
  private val periodKey = PeriodKey("18AC")
  private val vrtData = VrtRepaymentDetailData(Some(id), now(), vrn, repaymentDetail)
  private val vrtData2 = VrtRepaymentDetailData(Some(id), now(), vrn2, repaymentDetail)
  private val vrtData3 = VrtRepaymentDetailData(Some(id2), now(), vrn, repaymentDetail.copy(riskingStatus = "SENT_FOR_RISKING"))

  private lazy val testConnector = injector.instanceOf[TestConnector]
  private lazy val repo = injector.instanceOf[VrtRepo]

  override def beforeEach(): Unit = {
    repo.removeAll().futureValue
    ()
  }

  "store data " in {
    givenTheUserIsAuthenticatedAndAuthorised(vrn       = vrn, enrolment = mtdVatEnrolmentKey)
    val result = testConnector.store(vrtData).futureValue
    result.status shouldBe OK
    result.body shouldBe "updated 1 records"
  }

  "store data testOnly" in {
    val result = testConnector.storeTestOnly(vrtData).futureValue
    result.status shouldBe OK
    result.body shouldBe "updated 1 records"
  }

  "find data " in {
    givenTheUserIsAuthenticatedAndAuthorised(vrn       = vrn, enrolment = mtdVatEnrolmentKey)
    val result = testConnector.store(vrtData).futureValue
    result.status shouldBe OK
    val findResult: List[VrtRepaymentDetailData] = testConnector.find(vrn, periodKey).futureValue
    findResult.size shouldBe 1
  }

  "Store two records then find 1" in {
    givenTheUserIsAuthenticatedAndAuthorised(vrn       = vrn2, enrolment = mtdVatEnrolmentKey)
    val result = testConnector.store(vrtData).futureValue
    result.status shouldBe OK
    val result2 = testConnector.store(vrtData2).futureValue
    result2.status shouldBe OK
    val findResult: List[VrtRepaymentDetailData] = testConnector.find(vrn2, periodKey).futureValue
    findResult.size shouldBe 1
  }

  "Store record twice with an update, should find most recent version" in {
    givenTheUserIsAuthenticatedAndAuthorised(vrn       = vrn, enrolment = mtdVatEnrolmentKey)
    val result = testConnector.store(vrtData).futureValue
    result.status shouldBe OK
    val result2 = testConnector.store(vrtData.copy(creationDate = now().plusDays(1))).futureValue
    result2.status shouldBe OK
    val findResult: List[VrtRepaymentDetailData] = testConnector.find(vrn, periodKey).futureValue
    findResult.size shouldBe 1
    findResult.head.creationDate shouldBe now.plusDays(1)
  }

  "Store two records for the same VRN and Period Key bu different status should find 2" in {
    givenTheUserIsAuthenticatedAndAuthorised(vrn       = vrn, enrolment = mtdVatEnrolmentKey)
    val result = testConnector.store(vrtData).futureValue
    result.status shouldBe OK
    val result2 = testConnector.store(vrtData3).futureValue
    result2.status shouldBe OK
    val findResult: List[VrtRepaymentDetailData] = testConnector.find(vrn, periodKey).futureValue
    findResult.size shouldBe 2
  }

  "return an empty list if record vrn and periodKey combination not found" in {
    givenTheUserIsAuthenticatedAndAuthorised(vrn       = vrn, enrolment = mtdVatEnrolmentKey)
    val findResult: List[VrtRepaymentDetailData] = testConnector.find(vrn, periodKey).futureValue
    findResult.size shouldBe 0
  }

  "store data , not authorised should result in 401" in {
    givenTheUserIsNotAuthenticated
    val result = testConnector.store(vrtData).failed.futureValue
    result.getMessage should include("Session record not found")
  }

  "Get data, not authorised should result in 401" in {
    givenTheUserIsNotAuthenticated
    val result = testConnector.find(vrn, periodKey).failed.futureValue
    result.getMessage should include("Session record not found")
  }

  "Get data, logged in but no access to VRN" in {
    givenTheUserIsAuthenticatedAndAuthorised(vrn       = vrn2, enrolment = mtdVatEnrolmentKey)
    val result = testConnector.find(vrn, periodKey).failed.futureValue
    result.getMessage should include("You do not have access to this vrn: 2345678890")
  }

  "Get data, logged in but no enrolments" in {
    givenTheUserIsAuthenticatedButNotAuthorised()
    val result = testConnector.find(vrn, periodKey).failed.futureValue
    result.getMessage should include("You do not have access to this service")
  }
}
