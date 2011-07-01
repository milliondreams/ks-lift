package com.tingendab.kslift.model

import net.liftweb.record.LifecycleCallbacks
import net.liftweb.record.field._
import net.liftweb.mongodb.record.field._
import net.liftweb.mongodb.record._
import net.liftweb.mongodb._
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.json.JsonDSL._
import com.tingendab.kslift.lib._
import org.joda.time.DateTime

class Status private() extends MongoRecord[Status] with ObjectIdPk[Status]{
  def meta = Status
    
  object owner extends ObjectIdRefField[Status, User](this, User)
  object ownerDisplayName extends StringField(this, 50)
  object status extends StringField(this, 512)
  object isReplyTo extends ObjectIdRefField[Status, Status](this, Status){
    override def optional_? = true
  }
    
  object createdTime extends DateTimeField(this){
    override def defaultValue = java.util.Calendar.getInstance
  }
    
  object updatedTime extends DateTimeField(this) with LifecycleCallbacks {
    override def defaultValue = java.util.Calendar.getInstance
    override def beforeSave() {
      super.beforeSave
      this.set(java.util.Calendar.getInstance)
    }
  }
}

object Status extends Status with MongoMetaRecord[Status]
