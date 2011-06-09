package com.tingendab.kslift {
  package model {

    import net.liftweb.record.field._
    import net.liftweb.mongodb.record.field._
    import net.liftweb.mongodb.record._
    import net.liftweb.mongodb._
    import net.liftweb.util._
    import net.liftweb.common._

    import com.tingendab.kslift.lib._
    
    /**
     * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
     */
    class User extends MegaProtoUser[User] {
      def meta = User // what's the "meta" server
      
      //object relationships extends MongoMapField[User, User](this)
      
      object profile extends BsonRecordField[User, Person](this, Person)
      
    }

    /**
     * The singleton that has methods for accessing the database
     */
    object User extends User with MetaMegaProtoUser[User] {
      override def screenWrap = Full(<lift:surround with="default" at="content">
          <lift:bind /></lift:surround>)

      // comment this line out to require email validations
      override def skipEmailValidation = true
      
      override def editFields: List[FieldPointerType] = List(firstName,
                                                             lastName,
                                                             email,
                                                             locale,
                                                             timezone)
    }
    
    class Person extends BsonRecord[Person]{
      def meta = Person
      
      object displayName extends StringField(this,50)
      
      object gender extends EnumField(this, Gender)
      
      object dateOfBirth extends DateField(this)
      
    }
    
    object Person extends Person with BsonMetaRecord[Person]

    object Gender extends Enumeration {
      type Gender = Value
      val Male, Female, Other, Undisclosed = Value
    }
  }
}
