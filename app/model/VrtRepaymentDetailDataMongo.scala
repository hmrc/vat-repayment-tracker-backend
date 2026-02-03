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
import play.api.libs.json.{Format, Json, OFormat}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.LocalDate

/** * VrtRepaymentDetailDataMongo is the model to be stored in mongo
  */
final case class VrtRepaymentDetailDataMongo(
  _id:                  VrtId,
  creationDate:         LocalDate,
  vrn:                  Vrn,
  repaymentDetailsData: RepaymentDetailData
) derives CanEqual

object VrtRepaymentDetailDataMongo:
  given Format[LocalDate]                    = MongoJavatimeFormats.localDateFormat
  given OFormat[VrtRepaymentDetailDataMongo] = Json.format[VrtRepaymentDetailDataMongo]

  def apply(vrt: VrtRepaymentDetailData, vrtId: VrtId): VrtRepaymentDetailDataMongo =
    VrtRepaymentDetailDataMongo(
      _id = vrtId,
      creationDate = vrt.creationDate,
      vrn = vrt.vrn,
      repaymentDetailsData = vrt.repaymentDetailsData
    )
