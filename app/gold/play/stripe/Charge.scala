package gold.play.stripe

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.libs.json.Writes._


case class Card(
    id: String, 
    last4: String, 
    cardType: String, 
    exp_month: Int, 
    exp_year: Int, 
    fingerprint: String, 
    customer: Option[String], 
    country: String, 
    name: Option[String],
    address_line1: Option[String], 
    address_line2: Option[String], 
    address_city: Option[String], 
    address_state: Option[String], 
    address_zip: Option[String], 
    address_country: Option[String], 
    cvc_check: Option[String],
    address_line1_check: Option[String], 
    address_zip_check: Option[String])
    
case class Refund(
    amount: Int,
    created: Long,
    currency: String,
    balance_transaction: Option[String])
    
case class Dispute(
    charge: String,
    amount: Int, 
    created: Long, 
    status: String, 
    livemode: Boolean, 
    currency: String, 
    reason: String, 
    balance_transaction: String, 
    evidence_due_by: Long, 
    evidence: String)

case class Charge (
  id: String,
  created: Long, 
  livemode: Boolean, 
  paid: Boolean, 
  amount: Int, 
  currency: String, 
  refunded: Boolean, 
  card: Card, 
  captured: Boolean, 
  //refunds: Option[Seq[Refund]],
  balance_transaction: String, 
  failure_message: Option[String], 
  failure_code: Option[String], 
  amount_refunded: Int, 
  customer: Option[String], 
  invoice: Option[String], 
  description: Option[String], 
  dispute: Option[Dispute], 
  metadata: Option[Map[String,String]]
)

object Charge{
  
  // need to create explicit read/write for card as can't have type as a case class param.
  implicit val cardReads: Reads[Card] = (
        (__ \ "id").read[String] and
        (__ \ "last4").read[String] and
        (__ \ "brand").read[String] and
        (__ \ "exp_month").read[Int] and
        (__ \ "exp_year").read[Int] and
        (__ \ "fingerprint").read[String] and
        (__ \ "customer").read[Option[String]] and
        (__ \ "country").read[String] and
        (__ \ "name").read[Option[String]] and
        (__ \ "address_line1").read[Option[String]] and
        (__ \ "address_line2").read[Option[String]] and
        (__ \ "address_city").read[Option[String]] and
        (__ \ "address_state").read[Option[String]] and
        (__ \ "address_zip").read[Option[String]] and
        (__ \ "address_country").read[Option[String]] and
        (__ \ "cvc_check").read[Option[String]] and
        (__ \ "address_line1_check").read[Option[String]] and
        (__ \ "address_zip_check").read[Option[String]] 
  )(Card)
  
  implicit val cardWrites: Writes[Card] = (
        (__ \ "id").write[String] and
        (__ \ "last4").write[String] and
        (__ \ "brand").write[String] and
        (__ \ "exp_month").write[Int] and
        (__ \ "exp_year").write[Int] and
        (__ \ "fingerprint").write[String] and
        (__ \ "customer").write[Option[String]] and
        (__ \ "country").write[String] and
        (__ \ "name").write[Option[String]] and
        (__ \ "address_line1").write[Option[String]] and
        (__ \ "address_line2").write[Option[String]] and
        (__ \ "address_city").write[Option[String]] and
        (__ \ "address_state").write[Option[String]] and
        (__ \ "address_zip").write[Option[String]] and
        (__ \ "address_country").write[Option[String]] and
        (__ \ "cvc_check").write[Option[String]] and
        (__ \ "address_line1_check").write[Option[String]] and
        (__ \ "address_zip_check").write[Option[String]] 
  )(unlift(Card.unapply))  
  
  implicit val rf = Json.format[Refund]
  implicit val df = Json.format[Dispute]
  implicit val format = Json.format[Charge]
}