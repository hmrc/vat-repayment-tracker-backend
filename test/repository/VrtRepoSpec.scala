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

package repository

import java.time.LocalDate

import model._
import play.api.libs.json.Json
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import reactivemongo.bson.BSONObjectID
import support.{DesData, ItSpec}

class VrtRepoSpec extends ItSpec {

  val repo = injector.instanceOf[VrtRepo]

  val vrn: Vrn = Vrn("2345678890")
  val vrn2: Vrn = Vrn("2345678891")
  val periodKey: PeriodKey = PeriodKey("18AC")
  val id = VrtId(BSONObjectID.generate.stringify)
  val id2 = VrtId(BSONObjectID.generate.stringify)

  override def beforeEach(): Unit = {
    val remove = repo.removeAll().futureValue
  }

  "Count should be 0 with empty repo" in {
    collectionSize shouldBe 0
  }

  "ensure indexes are created" in {

    val remove = repo.drop.futureValue
    val ensure = repo.ensureIndexes.futureValue
    repo.collection.indexesManager.list().futureValue.size shouldBe 5
  }

  "insert a record" in {
    val vrtData = VrtRepaymentDetailData(Some(id), LocalDate.now(), vrn, DesData.repaymentDetail)
    val upserted: UpdateWriteResult = repo.upsert(id, vrtData).futureValue
    upserted.n shouldBe 1

  }

  "find records by vrn and periodKey" in {
    val vrtData = VrtRepaymentDetailData(Some(id), LocalDate.now(), vrn, DesData.repaymentDetail)
    val vrtData2 = VrtRepaymentDetailData(Some(id2), LocalDate.now(), vrn2, DesData.repaymentDetail)
    val upserted: UpdateWriteResult = repo.upsert(id, vrtData).futureValue
    val upserted2: UpdateWriteResult = repo.upsert(id2, vrtData2).futureValue
    collectionSize shouldBe 2
    val found: List[VrtRepaymentDetailData] = repo.findByVrnAndPeriodKey(vrn, periodKey).futureValue
    found.size shouldBe 1
  }

  "find records by vrn, periodKey and riskingStatus" in {
    val vrtData = VrtRepaymentDetailData(Some(id), LocalDate.now(), vrn, DesData.repaymentDetail)
    val vrtData2 = VrtRepaymentDetailData(Some(id2), LocalDate.now(), vrn, DesData.repaymentDetail2)
    val upserted: UpdateWriteResult = repo.upsert(id, vrtData).futureValue
    val upserted2: UpdateWriteResult = repo.upsert(id2, vrtData2).futureValue
    collectionSize shouldBe 2
    val found: List[VrtRepaymentDetailData] = repo.findByVrnAndPeriodKeyAndRiskingStatus(vrn, periodKey, "REPAYMENT_APPROVED").futureValue
    found.size shouldBe 1
  }

  "removeByPeriodKeyForTest " in {
    val vrtData = VrtRepaymentDetailData(Some(id), LocalDate.now(), vrn, DesData.repaymentDetail)
    val vrtData2 = VrtRepaymentDetailData(Some(id2), LocalDate.now(), vrn, DesData.repaymentDetail2)
    val upserted: UpdateWriteResult = repo.upsert(id, vrtData).futureValue
    val upserted2: UpdateWriteResult = repo.upsert(id2, vrtData2).futureValue
    collectionSize shouldBe 2
    val removed: WriteResult = repo.removeByPeriodKeyForTest(List(PeriodKey("18AC"))).futureValue
    collectionSize shouldBe 0

  }
  private def collectionSize: Int = {
    repo.count(Json.obj()).futureValue
  }

}
