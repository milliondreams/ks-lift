package com.tingendab.kslift.model

import net.liftweb.record.field._
import net.liftweb.mongodb.record.field._
import net.liftweb.mongodb.record._
import net.liftweb.mongodb._
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.json.JsonAST.JString
import net.liftweb.json.JsonDSL._
import com.tingendab.kslift.lib._

object ConversationMode extends Enumeration{
  type ConversationMode = Value
  val restricted, secured, moderated, invite, group, open = Value
}

class Conversation private() extends MongoRecord[Conversation] with ObjectIdPk[Conversation]{
  def meta = Conversation
  object title extends StringField(this, 100)
  object mode extends EnumField(this,ConversationMode)
  object memberUsers extends BsonRecordField(this,MemberUser)
  object creator extends ObjectIdRefField(this, User)
  object messages extends BsonRecordListField(this, Message)
}

object Conversation extends Conversation with MongoMetaRecord[Conversation]

class MemberUser private() extends BsonRecord[MemberUser]{
  def meta = MemberUser
  object user extends ObjectIdRefField(this, User)
  object displayName extends StringField(this, 50)
}

object MemberUser extends MemberUser with BsonMetaRecord[MemberUser]

object MessageType extends Enumeration {
  type MessageType = Value
  val FriendRequest, AppRequest, Inbox, Chat, Comment = Value
}

class Message private() extends BsonRecord[Message]{
  def meta = Message
  object messageType extends EnumField(this, MessageType)
  object sender extends BsonRecordField(this,MemberUser)
  object msgBody extends StringField(this, 1000)
}

object Message extends Message with BsonMetaRecord[Message]