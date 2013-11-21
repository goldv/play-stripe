package gold.play.stripe

import gold.play.stripe.auth.StripeCredentials
import play.api._
import play.api.{Application,Play,PlayException}
import play.api.libs.ws._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import scala.concurrent.{Future => success}
import com.ning.http.client.Realm.AuthScheme
import play.api.libs.json._
import play.api.libs.functional.syntax._
import gold.play.stripe.User._
import gold.play.stripe.Charge._

object Stripe {
  
  case class StripeError(errorType: String, message: String, code: Option[String], param: Option[String])
  
  implicit val errorReads: Reads[StripeError] = (
      (__ \ "type").read[String] and
      (__ \ "message").read[String] and
      (__ \ "code").read[Option[String]] and
      (__ \ "param").read[Option[String]]
  )(StripeError)
  
  implicit val errorWrites: Writes[StripeError] = (
      (__ \ "type").write[String] and
      (__ \ "message").write[String] and
      (__ \ "code").write[Option[String]] and
      (__ \ "param").write[Option[String]]
  )(unlift(StripeError.unapply))
  
  val connectUrl = Play.current.configuration.getString("stripe.connectUrl")
  val authUrl    = Play.current.configuration.getString("stripe.authUrl")
  
  def pk(implicit credentials: StripeCredentials) = credentials.publishableKey
  
  def connect(state: Option[String] = None)(implicit app: App): String = {
    connectUrl match{
      case Some(url) => buildConnectUrl(url,state)
      case None => throw new PlayException("Configuration error","stripe.connectUrl not configured")
    }
  }
    
  def authenticate(authCode: String)(implicit credentials: StripeCredentials): Future[Either[StripeError,StripeToken]] = {
    authUrl match{
      case None => throw new PlayException("Configuration error","stripe.authUrl not configured")
      case Some(url) => {
        val params = Map("client_secret" -> Seq(credentials.secretKey),"code" -> Seq(authCode),"grant_type" -> Seq("authorization_code"))
        WS.url(url).post(params).map{ parseResponse[StripeToken](_) }
      }
    }
  }
  
  def charge(price: Int, currency: String, params: Map[String,Seq[String]])(implicit credentials: StripeCredentials): Future[Either[StripeError, Charge]] = {
    val p = params + ( "amount" -> Seq(price.toString) ) + ("currency" -> Seq(currency))
    val result = WS.url("https://api.stripe.com/v1/charges")
        .withAuth(credentials.secretKey, "", AuthScheme.BASIC)
        .post(p)
    result.map{ c => parseResponse[Charge](c) }
  }
  
  private def parseResponse[T](response: Response)(implicit reader: Reads[T]): Either[StripeError,T] = {
    Logger.info("stripe response: " + response.json.toString)
    // parse json into StripeToken
    response.status match {
      case status if(status == 200) => parseJson[T](response.json)
      case _ => parseJson[StripeError](response.json).right.flatMap(Left(_))
    }
  }
  
  /**
   * Parses json into the specified type T and maps any parse error into a StripeError
   */
  private def parseJson[T](json: JsValue)(implicit reader: Reads[T]): Either[StripeError,T] = {
    Json.fromJson[T](json).map(Right(_)).recoverTotal{ err =>
      Left(StripeError("parse_error",JsError.toFlatJson(err).toString,None,None))
    }
  }
  
  private def buildConnectUrl(url: String, state: Option[String])(implicit app: App): String = 
    url + "?response_type=code&client_id=" + app.clientId + "&scope=read_write" + state.map("&state=" + _).getOrElse("")
  
}