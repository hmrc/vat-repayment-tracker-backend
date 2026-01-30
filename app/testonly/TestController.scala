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

package testonly

import controllers.VrtController
import controllers.action.Actions
import model._
import model.des.RiskingStatus._
import model.des.{RepaymentDetailData, RiskingStatus}
import org.bson.types.ObjectId
import org.mongodb.scala.model.Filters.in
import org.mongodb.scala.SingleObservableFuture
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import repository.VrtRepo

import java.time.LocalDate
import java.time.LocalDate.now
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

@Singleton
class TestController @Inject() (
  cc:      ControllerComponents,
  actions: Actions,
  repo:    VrtRepo
)(implicit ec: ExecutionContext)
    extends VrtController(cc, actions, repo) {

  private val random: Random = scala.util.Random
  private def date: String   = now().toString

  private val possibleRiskStatus: Seq[RiskingStatus] = Seq(INITIAL, SENT_FOR_RISKING, CLAIM_QUERIED)

  private val possiblePeriods = Seq(
    PeriodKey("16AA"),
    PeriodKey("16AB"),
    PeriodKey("16AC"),
    PeriodKey("16AD"),
    PeriodKey("16AE"),
    PeriodKey("16AF"),
    PeriodKey("16AG"),
    PeriodKey("16AH"),
    PeriodKey("16AI"),
    PeriodKey("16AJ"),
    PeriodKey("16AK"),
    PeriodKey("16AL"),
    PeriodKey("16YA"),
    PeriodKey("16YB"),
    PeriodKey("16YC"),
    PeriodKey("16YD"),
    PeriodKey("16YE"),
    PeriodKey("16YF"),
    PeriodKey("16YG"),
    PeriodKey("16YH"),
    PeriodKey("16YI"),
    PeriodKey("16YJ"),
    PeriodKey("16YK"),
    PeriodKey("16YL"),
    PeriodKey("16A4"),
    PeriodKey("16B4"),
    PeriodKey("16C1"),
    PeriodKey("16A1"),
    PeriodKey("16B1"),
    PeriodKey("16C2"),
    PeriodKey("16A2"),
    PeriodKey("16B2"),
    PeriodKey("16C3"),
    PeriodKey("16A3"),
    PeriodKey("16B3"),
    PeriodKey("16C4")
  )

  def storeRepaymentDataTestOnly(): Action[VrtRepaymentDetailData] = Action.async(parse.json[VrtRepaymentDetailData]) {
    implicit request =>
      store()
  }

  def removeTestData(): Action[AnyContent] = Action.async {
    repo.collection
      .deleteMany(in("repaymentDetailsData.periodKey", possiblePeriods))
      .toFuture()
      .map(_ => Ok("Test data removed"))
  }

  def insertTestData(start: Int, end: Int, rows: Int): Action[AnyContent] = Action.async {
    Future.sequence(for (n <- start to end) yield insertRows(n, rows)).map { result =>
      Ok(s"Inserted ${result.sum.toString} rows ")
    }
  }

  private def insertRows(current: Int, rows: Int): Future[Int] =
    repo.collection
      .insertMany(bulkVrtRepaymentDetailData(current, rows))
      .toFuture()
      .map(_.getInsertedIds.keySet().size())

  private def bulkVrtRepaymentDetailData(current: Int, rows: Int): Seq[VrtRepaymentDetailDataMongo] =
    for (n <- 1 to rows) yield vrtRepaymentDetailData(Vrn(s"${current.toString}${n.toString}"))

  private def vrtRepaymentDetailData(vrn: Vrn) =
    VrtRepaymentDetailDataMongo(
      _id = VrtId(ObjectId.get.toString),
      creationDate = now(),
      vrn = vrn,
      repaymentDetailsData = RepaymentDetailData(
        returnCreationDate = LocalDate.parse(date),
        sentForRiskingDate = Option(LocalDate.parse(date)),
        lastUpdateReceivedDate = Option(LocalDate.parse(date)),
        periodKey = possiblePeriods(random.nextInt(possiblePeriods.length)).value,
        riskingStatus = possibleRiskStatus(random.nextInt(possibleRiskStatus.length)),
        random.nextInt(100),
        supplementDelayDays = Option(1),
        random.nextInt(100)
      )
    )
}
