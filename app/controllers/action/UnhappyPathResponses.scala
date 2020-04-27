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

package controllers.action

import javax.inject.Singleton
import model.Vrn
import play.api.mvc.{Request, Result}
import play.api.mvc.Results.Unauthorized

@Singleton
class UnhappyPathResponses {

  def unauthorised(implicit request: Request[_]): Result = Unauthorized("You do not have access to this service")

  def unauthorised(vrn: Vrn)(implicit request: Request[_]): Result = Unauthorized(s"You do not have access to this vrn: ${vrn.value}")

}
