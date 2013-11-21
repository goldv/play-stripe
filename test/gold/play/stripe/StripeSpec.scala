package gold.play.stripe

import java.io.File
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import org.specs2.mutable.Before
import org.specs2.mutable.Specification
import play.api.http.HeaderNames
import play.api.libs.ws.WS
import play.api.test.FakeApplication
import play.api.libs.json._
import org.specs2.execute.AsResult
import org.specs2.specification.Example
import play.api.test.FakeApplication
import play.api.test.Helpers._
import scala.util.Success
import scala.util.Failure
import scala.concurrent.duration.Duration
import java.lang.IllegalArgumentException
import scala.concurrent.Awaitable

class StripeSpec extends Specification {
  
  sequential
  
  def fakeApplication(additionalConfiguration: Map[String, _ <: Any] = Map.empty) =
    FakeApplication(new File("./test"), additionalConfiguration = additionalConfiguration)
    
  implicit class InAppExample(s: String) {
    def inApp[T: AsResult](r: => T): Example =
      s in running(fakeApplication()) {
        r
      }
  }
    
  "Stripe" should{
    
    "connect" should{
      "create connect url" inApp {
        val url = "https://connect.stripe.com/oauth/authorize?response_type=code&client_id=ca_2x9O6fXx8z6z0ikAK4bApEGypHOaxKDr&scope=read_write"
        Stripe.connect() must_== url 
      }
      
      "create connect url with state" inApp {
        val url = "https://connect.stripe.com/oauth/authorize?response_type=code&client_id=ca_2x9O6fXx8z6z0ikAK4bApEGypHOaxKDr&scope=read_write&state=123"
        Stripe.connect(Some("123")) must_== url
      }
    }
    
    "charge" should{
      "charge account" in {
        success
      }
    }
  }

}