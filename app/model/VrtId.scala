/*
 * Copyright 2020 HM Revenue & Customs
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
import controllers.ValueClassBinder.valueClassBinder
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.PathBindable
import reactivemongo.bson.BSONObjectID

final case class VrtId(value: String)

object VrtId {
  implicit val format: Format[VrtId] = implicitly[Format[String]].inmap(VrtId(_), _.value)
  implicit val journeyIdBinder: PathBindable[VrtId] = valueClassBinder(_.value)
  def fresh: VrtId = VrtId(BSONObjectID.generate.stringify)
}

