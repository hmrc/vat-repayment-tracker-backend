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

package model

import model.des.RepaymentDetailData
import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

/***
 * VrtRepaymentDetailData is the model to be used to
 * POST from frontend to backend and to GET from backend
 */
final case class VrtRepaymentDetailData(creationDate: LocalDate, vrn: Vrn, repaymentDetailsData: RepaymentDetailData)

object VrtRepaymentDetailData {
  implicit val format: OFormat[VrtRepaymentDetailData] = Json.format[VrtRepaymentDetailData]

  def apply(mongo: VrtRepaymentDetailDataMongo): VrtRepaymentDetailData = {
    VrtRepaymentDetailData(
      creationDate         = mongo.creationDate,
      vrn                  = mongo.vrn,
      repaymentDetailsData = mongo.repaymentDetailsData
    )
  }
}
