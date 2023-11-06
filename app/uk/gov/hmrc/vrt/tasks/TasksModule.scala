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

import org.mongodb.scala.MongoNamespace
import org.mongodb.scala.model.RenameCollectionOptions
import play.api.Logging
import play.api.inject._
import uk.gov.hmrc.mongo.MongoComponent

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

class TasksModule extends SimpleModule(bind[RenameCollectionTask].toSelf.eagerly())

@Singleton
class RenameCollectionTask @Inject() (mongoComponent: MongoComponent)(implicit ec: ExecutionContext) extends Logging {
  logger.info("**************** Start collection rename task...")

  mongoComponent.client
    .getDatabase("vat-repayment-tracker-backend")
    .getCollection("repayment-details-new-mongo")
    .renameCollection(MongoNamespace("vat-repayment-tracker-backend", "repayment-details"))
    .toFuture()
    .map { _ => logger.info("**************** collection rename task done.") }
}
