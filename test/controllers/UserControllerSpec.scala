package controllers

import daos.UserDAO
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.Play.materializer
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.CSRFTokenHelper.CSRFRequest
import play.api.test.Helpers._
import play.api.test._
import org.scalatest.BeforeAndAfterEach
import scala.concurrent.ExecutionContext
import play.api.db.{DBApi, Database}
import play.api.db.evolutions._
import org.scalatest.prop.TableDrivenPropertyChecks
import play.api.Application

class UserControllerSpec
  extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting
    with BeforeAndAfterEach
    with TableDrivenPropertyChecks {

  def fakeApp(): Application = new GuiceApplicationBuilder().build()
  lazy val database: Database = fakeApp().injector.instanceOf[DBApi].database("default")

  override def beforeEach(): Unit = {
    Evolutions.cleanupEvolutions(database)
    Evolutions.applyEvolutions(database)
  }

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

    /*"return bad request for invalid credentials" in {
      val testCases = Table(
        ("test_desc", "username", "email", "password"),
        ("bad email", "testuser2", "test@example", "Password$123")
      )

      forAll(testCases) { (test_desc: String, username: String, email: String, password: String) =>
        withClue(s"Test case: $test_desc") {

        }
        val userDAO = inject[UserDAO]
        val userController = new UserController(stubControllerComponents(), userDAO)(inject[ExecutionContext])

        val request = FakeRequest(POST, "/signUp")
          .withJsonBody(Json.obj(
            "username" -> username,
            "email" -> email,
            "password" -> password)
          )
          .withCSRFToken

        val result = call(userController.signUp, request)

        status(result) mustBe BAD_REQUEST
      }
    }*/

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
