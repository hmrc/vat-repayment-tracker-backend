/*
 * Copyright 2022 HM Revenue & Customs
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

import com.google.inject.Inject
import model.Vrn
import play.api.Logger
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class Actions @Inject() (authorisedAction: AuthorisedAction, unhappyPathResponses: UnhappyPathResponses)(implicit ec: ExecutionContext) {

  def authorised(vrn: Vrn): ActionBuilder[AuthorisedRequest, AnyContent] = authorisedAction andThen validateVrn(vrn)

  val authorised: ActionBuilder[AuthorisedRequest, AnyContent] = authorisedAction

  private def validateVrn(vrn: Vrn): ActionRefiner[AuthorisedRequest, AuthorisedRequest] =
    new ActionRefiner[AuthorisedRequest, AuthorisedRequest] {

      override protected def refine[A](request: AuthorisedRequest[A]): Future[Either[Result, AuthorisedRequest[A]]] =
        vrnCheck(request, vrn)

      override protected def executionContext: ExecutionContext = ec
    }

  private def vrnCheck[A](request: AuthorisedRequest[A], vrn: Vrn): Future[Either[Result, AuthorisedRequest[A]]] = {
    request.enrolmentsVrn match {
      case Some(typedVrn) =>
        if (typedVrn.vrn.value == vrn.value) {
          Future.successful(Right(request))
        } else {
          Logger("application").debug(s"""User logged in and passed vrn: ${vrn.value}, has enrolment for ${typedVrn.vrn.value}""")
          Future.successful(Left(unhappyPathResponses.unauthorised(vrn)))
        }
      case None =>
        Logger("application").debug(s"""User logged in and passed vrn: ${vrn.value}, but have not enrolments""")
        Future.successful(Left(unhappyPathResponses.unauthorised))
    }
  }
}
