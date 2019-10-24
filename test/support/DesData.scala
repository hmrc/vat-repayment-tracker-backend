/*
 * Copyright 2019 HM Revenue & Customs
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

import java.time.LocalDate

import model.{Vrn, VrtId, VrtRepaymentDetailData}
import model.des._
import play.api.libs.json.{JsValue, Json}
import reactivemongo.bson.BSONObjectID

object DesData {

  val repaymentDetail: RepaymentDetailData = RepaymentDetailData(
    LocalDate.parse("2001-01-01"),
    LocalDate.parse("2001-01-01"),
    Option(LocalDate.parse("2001-01-01")),
    "18AC",
    "INITIAL",
    1000,
    Option(1),
    100.02
  )

  val repaymentDetail2: RepaymentDetailData = RepaymentDetailData(
    LocalDate.parse("2001-01-01"),
    LocalDate.parse("2001-01-01"),
    Option(LocalDate.parse("2001-01-01")),
    "18AC",
    "REPAYMENT_APPROVED",
    1000,
    Option(1),
    100.02
  )

  val repaymentsDetail: Seq[RepaymentDetailData] = Seq(repaymentDetail)

  //language=JSON
  val repaymentDetailJson: JsValue = Json.parse(
    s"""[{
        "returnCreationDate": "2001-01-01",
        "sentForRiskingDate": "2001-01-01",
        "lastUpdateReceivedDate": "2001-01-01",
        "periodKey": "18AC",
        "riskingStatus": "INITIAL",
        "vatToPay_BOX5": 1000,
        "supplementDelayDays": 1,
        "originalPostingAmount": 100.02
    }]
""".stripMargin
  )

  val vrn: Vrn = Vrn("2345678891")
  val id = VrtId(BSONObjectID.generate.stringify)
  val vrtRepaymentDetailData: VrtRepaymentDetailData = VrtRepaymentDetailData(Some(id), LocalDate.now(), vrn, repaymentDetail)

  //language=JSON
  val vrtRepaymentDetailDataJson: JsValue = Json.parse(
    s"""{
        "_id" : "${id.value}",
        "creationDate": "${LocalDate.now()}",
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

