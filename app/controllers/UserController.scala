package controllers

import javax.inject._
import play.api.mvc._
import daos.UserDAO
import models.User
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._
import play.api.data._
import play.api.data.Forms._
import org.mindrot.jbcrypt.BCrypt

@Singleton
class UserController @Inject()(cc: ControllerComponents, userDAO: UserDAO)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  val userForm: Form[User] = Form(
    mapping(
      "id" -> optional(longNumber),
      "username" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText
    )(User.apply)(User.unapply)
  )

  def showSignUpForm = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.signup(""))
  }

  def signUp = Action.async(parse.json) { implicit request =>
    val json = request.body.as[JsObject]
    val username = (json \ "username").as[String]
    val email = (json \ "email").as[String]
    val password = (json \ "password").as[String]

    val user = User(None, username, email, password)

    userDAO.addUser(user).map { id =>
      Created(Json.obj("status" -> "success", "message" -> s"User $id created"))
    }.recover {
      case e: IllegalArgumentException => BadRequest(Json.obj("status" -> "error", "message" -> "Email, username or password is invalid"))
      case _ => InternalServerError(Json.obj("status" -> "error", "message" -> "User could not be created"))
    }
  }

  def showLogInForm = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.login(""))
  }

  def logIn = Action.async(parse.json) { implicit request =>
    (request.body \ "username").asOpt[String].zip((request.body \ "password").asOpt[String]).map {
      case (username, password) =>
        userDAO.findUserByUsername(username).map {
          case Some(user) if BCrypt.checkpw(password, user.password) =>
            Ok(Json.obj("status" -> "success", "message" -> "Logged in"))
          case None => Unauthorized(Json.obj("status" -> "error", "message" -> "No user found"))
          case _ => Unauthorized(Json.obj("status" -> "error", "message" -> "Invalid credentials"))
        }
    }.getOrElse(Future.successful(BadRequest("Invalid login data")))
  }
}
