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

package uk.gov.hmrc.vrt.tasks

import play.api.Logging
import play.api.inject._
import repository.VrtRepo
import uk.gov.hmrc.mongo.MongoComponent

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

class TasksModule extends SimpleModule(bind[DropUnusedLegacyCollection].toSelf.eagerly())

@Singleton
class DropUnusedLegacyCollection @Inject() (mongoComponent: MongoComponent)(implicit ec: ExecutionContext) extends Logging {
  logger.warn("**************** Start drop of unused legacy collection...")

  mongoComponent.client
    .getDatabase("vat-repayment-tracker-backend") // update
    .getCollection("repayment-details-new-mongo") // update
    .drop()
    .toFuture()
    .map { _ => logger.info("**************** drop of unused legacy collection done.") }
}
