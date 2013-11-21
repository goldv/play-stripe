package gold.play.stripe.auth

import java.util.Date
import play.api.Play.current
import gold.play.stripe.PlayConfiguration
  
trait StripeCredentials {
  def publishableKey: String
  def secretKey: String
}

object StripeCredentials {
  
  def apply(publishableKey: String, secretKey: String) = SimpleStripeCredentials(publishableKey, secretKey)
  
  lazy val fromConfiguration: StripeCredentials = SimpleStripeCredentials(PlayConfiguration("stripe.publishableKey"), PlayConfiguration("stripe.secretKey"))
  
  implicit def implicitStripCredentials:StripeCredentials = fromConfiguration
}

case class SimpleStripeCredentials(publishableKey: String, secretKey: String) extends StripeCredentials



