package gold.play.stripe.controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data.Forms._
import play.api.data._
import scala.concurrent.Future
import scala.concurrent.{Future => success}
import gold.play.stripe.Stripe
import gold.play.stripe.App
import gold.play.stripe.User._

import scala.concurrent.ExecutionContext.Implicits.global

object Connect extends Controller {
  
  case class StripeAuth(code: String)
  
   val stripeForm = Form( mapping("code" -> text)(StripeAuth.apply)(StripeAuth.unapply) )
  
  /**
   * TODO: make this configurable so also could serve up user defined page for non SPA
   * 
   * Action which returns json containing the connect url
   * @returns stripe connect url populated with relevant params
   */
  def connect = Action{
    Ok(Json.obj("url" -> Stripe.connect()))
  }
  
  def authenticate = Action.async{ implicit request =>
    stripeForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        success(BadRequest("No good"))
      },
      stripeAuth => {
        Stripe.authenticate(stripeAuth.code).map{ result =>
          result match{
            case Right(token) => {
              // Notify user service with token
              Ok(Json.toJson(token))
            }
            case Left(error) => {
              // Notify user service with error
              BadRequest(Json.toJson(error))
            }
          }
        } 
      })
  }
}