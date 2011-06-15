package com.tingendab.kslift 
package model 

import net.liftweb.record.field._
import net.liftweb.mongodb.record.field._
import net.liftweb.mongodb.record._
import net.liftweb.mongodb._
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.json.JsonAST.JString
import net.liftweb.json.JsonDSL._
import com.tingendab.kslift.lib._
import org.bson.types.ObjectId
    
/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends MegaProtoUser[User]{
  def meta = User // what's the "meta" server
      
  object relationships extends BsonRecordListField(this,Relationship)
      
  object profile extends BsonRecordField[User, Person](this, Person)
      
}

object RelationType extends Enumeration {
  type RelationType = Value
  val friend, fiance, relative, brother, sister = Value  
}

case class Relationship private() extends BsonRecord[Relationship]{
  def meta = Relationship
  object relationType extends EnumField(this, RelationType)
  object relatedTo extends ObjectIdRefField(this, User)
}

object Relationship extends Relationship with BsonMetaRecord[Relationship]

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with MetaMegaProtoUser[User] {
  override def screenWrap = Full(<lift:surround with="default" at="content">
      <lift:bind /></lift:surround>)

  // comment this line out to require email validations
  override def skipEmailValidation = true
  
  override def signupFields: List[FieldPointerType] = List(firstName,
                                                  lastName,
                                                  email,
                                                  password)
      
  override def editFields: List[FieldPointerType] = List(firstName,
                                                         lastName,
                                                         email,
                                                         locale,
                                                         timezone)
}
    
class Person extends BsonRecord[Person]{
  def meta = Person
      
  // id: We will use the User object's ID
  /* Unique identifier for the Person. Each Person returned MUST include a non-empty id value. This identifier MUST be unique across this user's entire set of people, 
   * but MAY not be unique across multiple users' data. It MUST be a stable ID that does not change when the same contact is returned in subsequent requests. 
   * For instance, an e-mail address is not a good id, because the same person may use a different e-mail address in the future. 
   * Usually, in internal database ID will be the right choice here, e.g. "12345". */
  
  object displayName extends StringField(this, 50)
  /* The name of this Person, suitable for display to end-users. Each Person returned MUST include a non-empty displayName value. 
   * The name SHOULD be the full name of the Person being described if known (e.g. Cassandra Doll or Mrs. Cassandra Lynn Doll, Esq.), 
   * but MAY be a username or handle, if that is all that is available (e.g. doll). 
   * The value provided SHOULD be the primary textual label by which this Person is normally displayed by the Service Provider when presenting 
   * it to end-users. */
  //TEMP object name extends BsonRecordField(this, Name)
  /* The broken-out components and fully formatted version of the person's real name, as described in Section 11.1.3 (name Element). */
  
  object nickname extends OptionalStringField(this, 50)
  /* The casual way to address this Person in real life, e.g. "Bob" or "Bobby" instead of "Robert". 
   * This field SHOULD NOT be used to represent a user's username (e.g. jsmarr or daveman692); the latter should be represented by the preferredUsername 
   * field. */
  
  object birthday extends DateField(this)
  /* The birthday of this person. The value MUST be a valid xs:date (e.g. 1975-02-14. The year value MAY be set to 0000 when 
   * the age of the Person is private or the year is not available. */
  
  object anniversary extends DateField(this)
  /* The wedding anniversary of this person. The value MUST be a valid xs:date (e.g. 1975-02-14. The year 
   * value MAY be set to 0000 when the year is not available. */
  
  object gender extends EnumField(this, Gender)
  /* The gender of this person. Service Providers SHOULD return one of the following Canonical Values, if appropriate: 
   * male, female, or undisclosed, and MAY return a different value if it is not covered by one of these Canonical Values. */
  // note: // NOT SUPPORTES
  /* Notes about this person, with an unspecified meaning or usage (normally notes by the user about this person). This field MAY contain newlines. */

  // object preferredUsername extends OptionalStringField(this, 50) //Determine at runtime, removing anything after @ in email id
  /* The preferred username of this person on sites that ask for a username (e.g. jsmarr or daveman692). 
   * This field may be more useful for describing the owner (i.e. the value when /@me/@self is requested) 
   * than the user's person, e.g. Consumers MAY wish to use this value to pre-populate a username for this 
   * user when signing up for a new service. */

  //utcOffset: //Use User's timezone to get the offset
  /* The offset from UTC of this Person's current time zone, as of the time this response was returned. 
   * The value MUST conform to the offset portion of xs:dateTime, e.g. -08:00. Note that this value MAY change over 
   * time due to daylight saving time, and is thus meant to signify only the current value of the user's timezone offset. */

  // object connected extends BooleanField(this) //Tobe derived at runtime
  /* Boolean value indicating whether the user and this Person have established a bi-directionally asserted connection 
   * of some kind on the Service Provider's service. The value MUST be either true or false. The value MUST be true if 
   * and only if there is at least one value for the relationship field, described below, and is thus intended as a summary 
   * value indicating that some type of bi-directional relationship exists, for Consumers that aren't interested in the specific nature 
   * of that relationship. For traditional address books, in which a user stores information about other contacts without their explicit 
   * acknowledgment, or for services in which users choose to "follow" other users without requiring mutual consent, this value will always be false. */
  
  object emails extends MongoMapField[Person, EmailField[Person]](this) 
  /* E-mail address for this Person. The value SHOULD be canonicalized by the Service Provider, e.g. joseph@plaxo.com instead of joseph@PLAXO.COM. */

  object urls extends MongoMapField[Person, String](this)
  /* URL of a web page relating to this Person. The value SHOULD be canonicalized by the Service Provider, 
   * e.g. http://josephsmarr.com/about/ instead of JOSEPHSMARR.COM/about/. In addition to the standard Canonical Values for type, 
   * this field also defines the additional Canonical Values blog and profile.*/

  object phoneNumbers extends MongoMapField[Person, String](this)
  /* Phone number for this Person. No canonical value is assumed here. In addition to the standard Canonical Values for type, 
   * this field also defines the additional Canonical Values mobile, fax, and pager. */

  object ims extends MongoMapField[Person, String](this)
  /* Instant messaging address for this Person. No official canonicalization rules exist for all instant messaging addresses, 
   * but Service Providers SHOULD remove all whitespace and convert the address to lowercase, if this is appropriate for the service 
   * this IM address is used for. Instead of the standard Canonical Values for type, this field defines the following Canonical Values 
   * to represent currently popular IM services: aim, gtalk, icq, xmpp, msn, skype, qq, and yahoo. */

  //object photos extends MongoListField[Person, String](this)
  /* URL of a photo of this person. The value SHOULD be a canonicalized URL, and MUST point to an actual image file (e.g. a GIF, JPEG, 
   * or PNG image file) rather than to a web page containing an image. Service Providers MAY return the same image at different sizes, 
   * though it is recognized that no standard for describing images of various sizes currently exists. Note that this field SHOULD NOT be 
   * used to send down arbitrary photos taken by this user, but specifically profile photos of the contact suitable for display when 
   * describing the contact. */

  //object tags 
  //A user-defined category label for this person, e.g. "favorite" or "web20". These values SHOULD be case-insensitive, and there SHOULD NOT be multiple tags provided for a given person that differ only in case. Note that this field consists only of a string value.

  //TEMP object relationships extends MongoMapField[Person, ObjectIdRefField[User,User]](this)
  /* A bi-directionally asserted relationship type that was established between the user and this person by the Service Provider. The value SHOULD conform to one of the XFN relationship values (e.g. kin, friend, contact, etc.) if appropriate, but MAY be an alternative value if needed. Note this field is a parallel set of category labels to the tags field, but relationships MUST have been bi-directionally confirmed, whereas tags are asserted by the user without acknowledgment by this Person. Note that this field consists only of a string value. */

  object addresses extends MongoMapField[Person, BsonRecordField[Person, Address]](this) 
  /* A physical mailing address for this Person, as described in Section 11.1.4 (address Element). */

  object organizations extends MongoMapField[Person, BsonRecordField[Person, Organization]](this)
  /* A current or past organizational affiliation of this Person, as described in Section 11.1.5 (organization Element). */
  //accounts: //To be done later
  //An online account held by this Person, as described in Section 11.1.6 (account Element).
      
}
    
object Person extends Person with BsonMetaRecord[Person]
   
class Name private() extends BsonRecord[Name]{
  def meta = Name
  
  //object formatted extends String(this, 200)
  /* The full name, including all middle names, titles, and suffixes as appropriate, formatted for display 
   * (e.g. Mr. Joseph Robert Smarr, Esq.). This is the Primary Sub-Field for this field, for the purposes 
   * of sorting and filtering.*/
   
  object familyName extends OptionalStringField(this, 50)
  /* The family name of this Person, or "Last Name" in most Western languages (e.g. Smarr given the full 
   * name Mr. Joseph Robert Smarr, Esq.). */
   
  object givenName extends OptionalStringField(this, 50)
  /* The given name of this Person, or "First Name" in most Western languages (e.g. Joseph given the full 
   * name Mr. Joseph Robert Smarr, Esq.). */

  object middleName extends OptionalStringField(this, 50)
  /* The middle name(s) of this Person (e.g. Robert given the full name Mr. Joseph Robert Smarr, Esq.). */
   
  object honorificPrefix extends OptionalStringField(this, 5)
  /* The honorific prefix(es) of this Person, or "Title" in most Western languages (e.g. Mr. given the full 
   * name Mr. Joseph Robert Smarr, Esq.). */
   
  object honorificSuffix extends OptionalStringField(this, 5)
  /* The honorifix suffix(es) of this Person, or "Suffix" in most Western languages (e.g. Esq. given the 
   * full name Mr. Joseph Robert Smarr, Esq.). */
}
   
object Name extends Name with BsonMetaRecord[Name]
    
class Address private() extends BsonRecord[Address]{
  def meta = Address
  //formatted: ///To be generated at runtime
  /* The full mailing address, formatted for display or use with a mailing label. This field MAY contain newlines. 
   * This is the Primary Sub-Field for this field, for the purposes of sorting and filtering. */

  object streetAddress extends StringField(this, 200)
  /* The full street address component, which may include house number, street name, PO BOX, and 
   * multi-line extended street address information. This field MAY contain newlines. */

  object locality extends StringField(this, 200)
  /* The city or locality component. */

  object region extends StringField(this, 200)
  /* The state or region component. */

  object postalCode extends StringField(this, 10)
  /* The zipcode or postal code component. */

  object country extends CountryField(this)
  /* The country name component. */

}

object Address extends Address with BsonMetaRecord[Address]    
    
object Gender extends Enumeration {
  type Gender = Value
  val Male, Female, Other, Undisclosed = Value
}
    
class Organization private() extends BsonRecord[Organization]{
  def meta = Organization

  object name extends StringField(this, 20)
  /* The name of the organization (e.g. company, school, or other organization). This field MUST have a non-empty 
   * value for each organization returned. This is the Primary Sub-Field for this field, for the purposes of sorting and filtering. */

  object department extends StringField(this, 20)
  /* The department within this organization. */

  object title extends StringField(this, 20)
  /* The job title or organizational role within this organization. */

  object organizationType extends StringField(this, 20)
  /* The type of organization, with Canonical Values job and school. */

  object startDate extends StringField(this,20)
  /* The date this Person joined this organization. This value SHOULD be a valid xs:date if possible, but MAY be an unformatted string, since it is recognized that this field is often presented as free-text. */

  object endDate extends StringField(this, 20)
  /* The date this Person left this organization or the role specified by title within this organization. This value SHOULD be a valid xs:date if possible, but MAY be an unformatted string, since it is recognized that this field is often presented as free-text. */

  object location extends StringField(this,50)
  /* The physical location of this organization. This may be a complete address, or an abbreviated location like "San Francisco". */

  object description extends StringField(this, 1000)
  /* A textual description of the role this Person played in this organization. This field MAY contain newlines.  */
  
}

object Organization extends Organization with BsonMetaRecord[Organization]

