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

package controller

import controllers.Controller
import model.EnrolmentKeys.{mtdVatEnrolmentKey, vatDecEnrolmentKey, vatVarEnrolmentKey}
import model._
import model.des.RiskingStatus.SENT_FOR_RISKING
import play.api.http.Status
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import repository.VrtRepo
import support.AuthStub._
import support.DesData.repaymentDetail
import support._
import uk.gov.hmrc.auth.core.SessionRecordNotFound
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate.now

class ControllerSpec extends ItSpec with Status {
  implicit val emptyHC: HeaderCarrier = HeaderCarrier()

  private val vrn = Vrn("2345678890")
  private val vrn2 = Vrn("2345678891")
  private val vrn3 = Vrn("2345678892")
  private val periodKey = PeriodKey("18AC")
  private val vrtData = VrtRepaymentDetailData(now(), vrn, repaymentDetail)
  private val vrtData2 = VrtRepaymentDetailData(now(), vrn2, repaymentDetail)
  private val vrtData3 = VrtRepaymentDetailData(now(), vrn, repaymentDetail.copy(riskingStatus = SENT_FOR_RISKING))

  private lazy val testConnector = injector.instanceOf[TestConnector]
  private lazy val repo = injector.instanceOf[VrtRepo]
  private lazy val controller = injector.instanceOf[Controller]

  override def beforeEach(): Unit = {
    repo.collection.drop().toFuture().futureValue
    ()
  }

  def fakeRequest(method: String = "", url: String = ""): FakeRequest[AnyContentAsEmpty.type] = FakeRequest(method, url).withHeaders(
    uk.gov.hmrc.http.HeaderNames.authorisation -> "Bearer 123"
  )

  import play.api.test.Helpers._

  "store data" in {
    givenTheUserIsAuthenticatedAndAuthorised(vrn       = vrn, enrolment = mtdVatEnrolmentKey)
    val result = testConnector.store(vrtData)
    status(result) shouldBe OK
    contentAsString(result) shouldBe "updated 1 record"
  }

  "store data for class VRN's" in {
    List(vatDecEnrolmentKey, vatVarEnrolmentKey).foreach{ enrolmentKey =>
      withClue(s"For enrolment key '$enrolmentKey': "){
        givenTheUserIsAuthenticatedAndAuthorised(vrn       = vrn, enrolment = enrolmentKey)
        val result = testConnector.store(vrtData)
        status(result) shouldBe OK
        contentAsString(result) shouldBe "updated 1 record"
      }
    }
  }

  "store data testOnly" in {
    val result = testConnector.storeTestOnly(vrtData)
    status(result) shouldBe OK
    contentAsString(result) shouldBe "updated 1 record"
  }

  "find data" in {
    givenTheUserIsAuthenticatedAndAuthorised(vrn       = vrn, enrolment = mtdVatEnrolmentKey)
    val result = testConnector.store(vrtData)
    status(result) shouldBe OK
    val findResult = testConnector.find(vrn, periodKey)
    contentAsJson(findResult).as[List[VrtRepaymentDetailData]].size shouldBe 1
  }

  "store two records then find 1" in {
    givenTheUserIsAuthenticatedAndAuthorised(vrn       = vrn2, enrolment = mtdVatEnrolmentKey)
    val result = testConnector.store(vrtData)
    status(result) shouldBe OK
    val result2 = testConnector.store(vrtData2)
    status(result2) shouldBe OK
    val findResult = testConnector.find(vrn2, periodKey)
    contentAsJson(findResult).as[List[VrtRepaymentDetailData]].size shouldBe 1
  }

  "store record twice with an update, should find most recent version" in {
    givenTheUserIsAuthenticatedAndAuthorised(vrn       = vrn, enrolment = mtdVatEnrolmentKey)
    val result = testConnector.store(vrtData)
    status(result) shouldBe OK
    val result2 = testConnector.store(vrtData.copy(creationDate = now().plusDays(1)))
    status(result2) shouldBe OK
    val findResult = testConnector.find(vrn, periodKey)
    val lst = contentAsJson(findResult).as[List[VrtRepaymentDetailData]]
    lst.size shouldBe 1
    lst.head.creationDate shouldBe now.plusDays(1)
  }

  "store two records for the same VRN and Period Key bu different status should find 2" in {
    givenTheUserIsAuthenticatedAndAuthorised(vrn       = vrn, enrolment = mtdVatEnrolmentKey)
    val result = testConnector.store(vrtData)
    status(result) shouldBe OK
    val result2 = testConnector.store(vrtData3)
    status(result2) shouldBe OK
    val findResult = testConnector.find(vrn, periodKey)
    contentAsJson(findResult).as[List[VrtRepaymentDetailData]].size shouldBe 2
  }

  "return an empty list if record vrn and periodKey combination not found" in {
    givenTheUserIsAuthenticatedAndAuthorised(vrn       = vrn, enrolment = mtdVatEnrolmentKey)
    val findResult = testConnector.find(vrn, periodKey)
    contentAsJson(findResult).as[List[VrtRepaymentDetailData]].size shouldBe 0
  }

  "store data, not authorised should result in 401" in {
    givenTheUserIsNotAuthenticated()
    givenTheUserIsNotAuthenticated()
    the[SessionRecordNotFound] thrownBy {
      status(testConnector.find(vrn, periodKey)) shouldBe 401
    } should have message "Session record not found"
  }

  "get data, not authorised should result in 401" in {
    givenTheUserIsNotAuthenticated()
    the[SessionRecordNotFound] thrownBy {
      status(testConnector.find(vrn, periodKey)) shouldBe 401
    } should have message "Session record not found"
  }

  "get data, logged in but no access to VRN" in {
    givenTheUserIsAuthenticatedAndAuthorised(vrn       = vrn2, enrolment = mtdVatEnrolmentKey)
    val result = testConnector.find(vrn, periodKey)
    contentAsString(result) should include("You do not have access to this vrn: 2345678890")
  }

  "get data, logged in but no enrolments" in {
    givenTheUserIsAuthenticatedButNotAuthorised()
    val result = testConnector.find(vrn, periodKey)
    contentAsString(result) should include("You do not have access to this service")
  }

  "findRepaymentData" in {
    givenTheUserIsAuthenticatedAndAuthorisedWithSeveralEnrolments(vrnList = List(vrn2 -> vatVarEnrolmentKey, vrn -> mtdVatEnrolmentKey, vrn3 -> vatDecEnrolmentKey))
    val response = controller.findRepaymentData(vrn, periodKey)(fakeRequest())
    status(response) shouldBe 200
  }
}
