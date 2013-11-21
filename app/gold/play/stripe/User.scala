package gold.play.stripe

import gold.play.stripe.auth.StripeCredentials
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class StripeToken(publishableKey: String, liveMode: Boolean, userId: String, refreshToken: String, accessToken: String)

trait User {
  def token: StripeToken
}

object User{
  implicit val tokenReads: Reads[StripeToken] = (
    (__ \ "stripe_publishable_key").read[String] and
    (__ \ "livemode").read[Boolean] and
    (__ \ "stripe_user_id").read[String] and
    (__ \ "refresh_token").read[String] and
    (__ \ "access_token").read[String]
  )(StripeToken)
  
  implicit val tokenWrites: Writes[StripeToken] = (
    (__ \ "stripe_publishable_key").write[String] and
    (__ \ "livemode").write[Boolean] and
    (__ \ "stripe_user_id").write[String] and
    (__ \ "refresh_token").write[String] and
    (__ \ "access_token").write[String]
  )(unlift(StripeToken.unapply))  
}