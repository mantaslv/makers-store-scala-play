package controllers

import daos.UserDAO
import models.Users
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.Play.materializer
import play.api.db.slick.DatabaseConfigProvider
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.CSRFTokenHelper.CSRFRequest
import play.api.test.Helpers._
import play.api.test._
import slick.jdbc.JdbcProfile
import slick.lifted
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext

class UserControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "UserController POST /signUp" should {

    "create a new user" in {
      val userDAO = inject[UserDAO]
      val userController = new UserController(stubControllerComponents(), userDAO)(inject[ExecutionContext])

      val request = FakeRequest(POST, "/signUp")
        .withJsonBody(Json.obj(
          "username" -> "testuser",
          "email" -> "test@example.com",
          "password" -> "Password$123")
        )
        .withCSRFToken

      val result = call(userController.signUp, request)

      status(result) mustBe CREATED
      val jsonResponse = contentAsJson(result)
      (jsonResponse \ "status").as[String] mustBe "success"
      (jsonResponse \ "message").as[String] must include("User")

      // Verify user is actually created in the database
      val maybeUser = await(userDAO.findUserByUsername("testuser"))
      maybeUser must not be empty
      maybeUser.get.email mustBe "test@example.com"
    }

    "return bad request for invalid username" in {
      val userDAO = inject[UserDAO]
      val userController = new UserController(stubControllerComponents(), userDAO)(inject[ExecutionContext])

      val request = FakeRequest(POST, "/signUp")
        .withJsonBody(Json.obj(
          "username" -> "",
          "email" -> "test@example.com",
          "password" -> "Password$123")
        )
        .withCSRFToken

      val result = call(userController.signUp, request)

      status(result) mustBe BAD_REQUEST
    }

    "return bad request for invalid email" in {
      val userDAO = inject[UserDAO]
      val userController = new UserController(stubControllerComponents(), userDAO)(inject[ExecutionContext])

      val request = FakeRequest(POST, "/signUp")
        .withJsonBody(Json.obj(
          "username" -> "testuser2",
          "email" -> "test@example",
          "password" -> "Password$123")
        )
        .withCSRFToken

      val result = call(userController.signUp, request)

      status(result) mustBe BAD_REQUEST
    }

    "return bad request for invalid password" in {
      val userDAO = inject[UserDAO]
      val userController = new UserController(stubControllerComponents(), userDAO)(inject[ExecutionContext])

      val request = FakeRequest(POST, "/signUp")
        .withJsonBody(Json.obj(
          "username" -> "testuser2",
          "email" -> "test@example.com",
          "password" -> "Password")
        )
        .withCSRFToken

      val result = call(userController.signUp, request)

      status(result) mustBe BAD_REQUEST
    }
  }
}
