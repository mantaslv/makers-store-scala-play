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

import java.sql.SQLException

class UserControllerSpec
  extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting
    with BeforeAndAfterEach
    with TableDrivenPropertyChecks {

  lazy val fakeApp: Application = new GuiceApplicationBuilder().build()
  lazy val database: Database = fakeApp.injector.instanceOf[DBApi].database("default")

  override def beforeEach(): Unit = {
    Evolutions.cleanupEvolutions(database)
    Evolutions.applyEvolutions(database)
  }

  def createUserInDB(): Unit ={
    val connection = database.getConnection()
    try {
      val sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?);"
      val statement = connection.prepareStatement(sql)
      statement.setString(1, "testuser")
      statement.setString(2, "test@example.com")
      statement.setString(3, "$2a$10$waLr92BAywvbIuD3xNJO1O4FBlnFyvlSl7JuOKt1XA4PirMyB5DPW")
      statement.executeUpdate()
    } catch {
      case e: SQLException => e.printStackTrace()
    } finally {
      if (connection != null) connection.close()
    }
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

    "return bad request" when {
      val testCases = Table(
        ("test_desc", "username", "email", "password"),
        ("invalid username", "", "test@example.com", "Password$123"),
        ("invalid email", "testuser2", "test@example", "Password$123"),
        ("invalid password", "testuser2", "test@example.com", "Password")
      )

      forAll(testCases) { (test_desc: String, username: String, email: String, password: String) =>
        s"$test_desc" in {
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
      }
    }
  }

  "UserController POST /logIn" should {
    "succesfully log in a user" in {
      val userDAO = inject[UserDAO]
      val userController = new UserController(stubControllerComponents(), userDAO)(inject[ExecutionContext])

      createUserInDB()

      val request = FakeRequest(POST, "/logIn")
        .withJsonBody(Json.obj(
          "username" -> "testuser",
          "password" -> "Password$123")
        )
        .withCSRFToken

      val result = call(userController.logIn, request)

      status(result) mustBe OK
      val jsonResponse = contentAsJson(result)
      (jsonResponse \ "status").as[String] mustBe "success"
      (jsonResponse \ "message").as[String] must include("Logged in")
      session(result).get("user") mustBe Some("testuser")
    }

    "return unauthorized" when {
      val testCases = Table(
        ("test_desc", "username", "password"),
        ("No user found", "testuser2", "Password$123"),
        ("Invalid credentials", "testuser", "Password$12"),
      )

      forAll(testCases) { (test_desc: String, username: String, password: String) =>
        s"$test_desc" in {
          val userDAO = inject[UserDAO]
          val userController = new UserController(stubControllerComponents(), userDAO)(inject[ExecutionContext])

          createUserInDB()

          val request = FakeRequest(POST, "/signUp")
            .withJsonBody(Json.obj(
              "username" -> username,
              "password" -> password)
            )
            .withCSRFToken

          val result = call(userController.logIn, request)

          status(result) mustBe UNAUTHORIZED
          val jsonResponse = contentAsJson(result)
          (jsonResponse \ "status").as[String] mustBe "error"
          (jsonResponse \ "message").as[String] must include(s"$test_desc")
        }
      }
    }
  }

  "UserController GET /logOut" should {
    "successfully logout a user" in {
      val userDAO = inject[UserDAO]
      val userController = new UserController(stubControllerComponents(), userDAO)(inject[ExecutionContext])

      createUserInDB()

      FakeRequest(POST, "/logIn")
        .withJsonBody(Json.obj(
          "username" -> "testuser",
          "password" -> "Password$123")
        )
        .withCSRFToken

      val request = FakeRequest(GET, "/logOut")

      val result = call(userController.logOut, request)

      session(result).get("user") mustBe None
      redirectLocation(result) mustBe Some(routes.HomeController.index().url)
      flash(result).get("success") mustBe Some("You've been logged out")
    }
  }
}
