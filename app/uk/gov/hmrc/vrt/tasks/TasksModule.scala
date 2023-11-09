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

class TasksModule extends SimpleModule(bind[RenameCollectionTask].toSelf.eagerly())

@Singleton
class RenameCollectionTask @Inject() (
    legacyRepo: TempVrtNewMongoRepo,
    activeRepo: VrtRepo
)(implicit ec: ExecutionContext) extends Logging {
  logger.warn("**************** STARTING Mongo clean-up task: Starting to transfer legacy documents to active collection...")

  legacyRepo.allDocumentsInNewMongo
    .map { allDocuments =>
      logger.warn("**************** Mongo clean-up task: Legacy documents retrieved from legacy collection...")

      allDocuments.foreach { document =>
        logger.warn(s"**************** Mongo clean-up task: copying document ${document._id.toString}...")
        activeRepo.upsert(document)
      }
    } map { _ =>
      logger.warn(s"**************** Mongo clean-up task: all documents copied across. Now dropping legacy collection...")
      legacyRepo.collection.drop().toFuture()
        .map{ _ =>
          logger.warn("**************** COMPLETE Mongo clean-up task: Legacy documents inserted to active collection.")
        }

    }
}
