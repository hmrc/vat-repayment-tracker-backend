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

package controllers

import java.time.LocalDate

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import model.des.RepaymentDetailData
import model.{PeriodKey, Vrn, VrtId, VrtRepaymentDetailData}
import play.api.Logger
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import reactivemongo.bson.BSONObjectID
import repository.VrtRepo
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TestController @Inject() (cc: ControllerComponents, vrtRepo: VrtRepo)(implicit ec: ExecutionContext) extends BackendController(cc) {

  def storeRepaymentDataTestOnly(): Action[VrtRepaymentDetailData] = Action.async(parse.json[VrtRepaymentDetailData]) { implicit request =>
    Logger.debug(s"received ${request.body.toString}")
    for {
      data <- vrtRepo.findByVrnAndPeriodKeyAndRiskingStatus(request.body.vrn,
                                                            PeriodKey(request.body.repaymentDetailsData.periodKey),
                                                            request.body.repaymentDetailsData.riskingStatus)
      vrtId: VrtId = if (data.size == 0) VrtId.fresh else data(0)._id.getOrElse(throw new RuntimeException("No id"))
      result <- vrtRepo.upsert(vrtId, request.body.copy(_id = Some(vrtId)))

    } yield {
      Ok(s"updated ${result.n.toString} records")
    }

  }

  val r = scala.util.Random

  val date = LocalDate.now().toString

  val possibleRiskStatus = Seq("INITIAL", "SENT_FOR_RISKING", "CLAIM_QUERIED")

  val possiblePeriods = Seq(PeriodKey("16AA"), PeriodKey("16AB"), PeriodKey("16AC"), PeriodKey("16AD"), PeriodKey("16AE"), PeriodKey("16AF"), PeriodKey("16AG"), PeriodKey("16AH"), PeriodKey("16AI"), PeriodKey("16AJ"), PeriodKey("16AK"), PeriodKey("16AL"),
                            PeriodKey("16YA"), PeriodKey("16YB"), PeriodKey("16YC"), PeriodKey("16YD"), PeriodKey("16YE"), PeriodKey("16YF"), PeriodKey("16YG"), PeriodKey("16YH"), PeriodKey("16YI"), PeriodKey("16YJ"), PeriodKey("16YK"), PeriodKey("16YL"),
                            PeriodKey("16A4"), PeriodKey("16B4"), PeriodKey("16C1"), PeriodKey("16A1"), PeriodKey("16B1"), PeriodKey("16C2"), PeriodKey("16A2"), PeriodKey("16B2"), PeriodKey("16C3"), PeriodKey("16A3"), PeriodKey("16B3"), PeriodKey("16C4")
  )

  def removeTestData(): Action[AnyContent] = Action.async { implicit request =>

    for (
      result <- vrtRepo.removeByPeriodKeyForTest(possiblePeriods.toList)
    ) yield (Ok("Test data removed"))

  }

  def insertTestData(start: Int, end: Int, rows: Int): Action[AnyContent] = Action.async { implicit request =>

    val b: Future[IndexedSeq[Int]] = Future.sequence(for (n <- start to end) yield insertRows(n, rows))

    for {
      result <- b
    } yield (Ok(s"Inserted ${result.sum} rows "))

  }

  private def insertRows(current: Int, rows: Int): Future[Int] = {

    for {
      inserted <- vrtRepo.bulkInsert(createBulkVrtRepaymentDetailData(current, rows))

    } yield (inserted.n)

  }

  private def createBulkVrtRepaymentDetailData(current: Int, rows: Int): Seq[VrtRepaymentDetailData] = {
    for (n <- 1 to rows) yield (createVrtRepaymentDetailData(Vrn(s"${current}${n}")))
  }

  private def createVrtRepaymentDetailData(vrn: Vrn): VrtRepaymentDetailData = {

    val id = VrtId(BSONObjectID.generate.stringify)

    val repaymentDetail: RepaymentDetailData = RepaymentDetailData(
      LocalDate.parse(date),
      Option(LocalDate.parse(date)),
      Option(LocalDate.parse(date)),
      possiblePeriods(r.nextInt(possiblePeriods.length)).value,
      possibleRiskStatus(r.nextInt(possibleRiskStatus.length)),
      r.nextInt(100),
      Option(1),
      r.nextInt(100)
    )
    VrtRepaymentDetailData(Some(id), LocalDate.now(), vrn, repaymentDetail)

  }

}

