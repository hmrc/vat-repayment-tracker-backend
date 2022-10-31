/*
 * Copyright 2022 HM Revenue & Customs
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

import model.des.RiskingStatus.{INITIAL, REPAYMENT_APPROVED}

import java.time.{LocalDate, ZoneOffset}
import java.time.LocalDate.now
import model.{Vrn, VrtId, VrtRepaymentDetailData}
import model.des._
import org.bson.types.ObjectId
import play.api.libs.json.{JsValue, Json}

object DesData {

  val repaymentDetail: RepaymentDetailData = RepaymentDetailData(
    returnCreationDate     = LocalDate.parse("2001-01-01"),
    sentForRiskingDate     = Option(LocalDate.parse("2001-01-01")),
    lastUpdateReceivedDate = Option(LocalDate.parse("2001-01-01")),
    periodKey              = "18AC",
    riskingStatus          = INITIAL,
    vatToPay_BOX5          = 1000,
    supplementDelayDays    = Option(1),
    originalPostingAmount  = 100.02
  )

  val repaymentDetail2: RepaymentDetailData = RepaymentDetailData(
    returnCreationDate     = LocalDate.parse("2001-01-01"),
    sentForRiskingDate     = Option(LocalDate.parse("2001-01-01")),
    lastUpdateReceivedDate = Option(LocalDate.parse("2001-01-01")),
    periodKey              = "18AC",
    riskingStatus          = REPAYMENT_APPROVED,
    vatToPay_BOX5          = 1000,
    supplementDelayDays    = Option(1),
    originalPostingAmount  = 100.02
  )

  val repaymentsDetail: Seq[RepaymentDetailData] = Seq(repaymentDetail)

  //language=JSON
  val repaymentDetailJson: JsValue = Json.parse(
    s"""[
          {
          "returnCreationDate": "2001-01-01",
          "sentForRiskingDate": "2001-01-01",
          "lastUpdateReceivedDate": "2001-01-01",
          "periodKey": "18AC",
          "riskingStatus": "INITIAL",
          "vatToPay_BOX5": 1000,
          "supplementDelayDays": 1,
          "originalPostingAmount": 100.02
      }
    ]""".stripMargin
  )

  private val vrn: Vrn = Vrn("2345678891")
  private val id: VrtId = VrtId(ObjectId.get.toString)
  val vrtRepaymentDetailData: VrtRepaymentDetailData = VrtRepaymentDetailData(Some(id), now(), vrn, repaymentDetail)

  //language=JSON
  val vrtRepaymentDetailDataJson: JsValue = Json.parse(
    s"""{
          "_id" : "${id.value}",
          "creationDate": {"$$date":{"$$numberLong":"${now().atStartOfDay(ZoneOffset.UTC).toInstant.toEpochMilli}"}},
          "vrn": "${vrn.value}",
          "repaymentDetailsData": {
          "returnCreationDate": "2001-01-01",
          "sentForRiskingDate": "2001-01-01",
          "lastUpdateReceivedDate": "2001-01-01",
          "periodKey": "18AC",
          "riskingStatus": "INITIAL",
          "vatToPay_BOX5": 1000,
          "supplementDelayDays": 1,
          "originalPostingAmount": 100.02
      }
    }""".stripMargin
  )
}

