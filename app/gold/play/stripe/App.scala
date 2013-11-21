package gold.play.stripe

import play.api.Play.current
import gold.play.stripe.auth.StripeCredentials

trait App {
  def clientId: String
  def credentials: StripeCredentials
}

object App{
  def apply(clientId: String)(implicit credentials: StripeCredentials) = SimpleApp(clientId, credentials)
  
  lazy val fromConfiguration: App = App(PlayConfiguration("stripe.clientId"))
  
  implicit def implicitApp:App = fromConfiguration
}

case class SimpleApp(clientId: String, credentials: StripeCredentials) extends App