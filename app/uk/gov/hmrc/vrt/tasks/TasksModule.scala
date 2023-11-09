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

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

class TasksModule extends SimpleModule(bind[CopyCacheDocuments].toSelf.eagerly())

@Singleton
class CopyCacheDocuments @Inject()(
    legacyRepo: TempVrtNewMongoRepo,
    activeRepo: VrtRepo
)(implicit ec: ExecutionContext) extends Logging {
  logger.warn("**************** STARTING Mongo clean-up task: Starting to transfer legacy documents to active collection...")

  legacyRepo.allDocumentsInNewMongo.flatMap { allDocuments =>
    logger.warn(s"**************** Mongo clean-up task: ${allDocuments.length.toString} legacy documents retrieved from legacy collection...")

    activeRepo.collection.insertMany(allDocuments).toFuture()

  } map { result =>
    logger.warn(s"**************** Mongo clean-up task: Insertion of legacy documents into active collection acknowledged? ${result.wasAcknowledged().toString}")
  }
}
