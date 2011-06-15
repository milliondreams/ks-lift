/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tingendab.kslift.snippet

import net.liftweb.common._
import net.liftweb.http._
import S._
import net.liftweb.util._
import Helpers._
import js._
import JsCmds._
import JE._
import scala.xml._
import com.tingendab.kslift.model._
import net.liftweb.json.JsonDSL._
import com.mongodb._

class MsgSnippet {
  
  def showConversations = {
    User.currentUser match {
      case Full(user) => {
          ".conversation *" #> allMyConversations(Conversation.findAll("memberUsers" -> ("$oid" -> user._id.toString)))
        }
      case _ => {
          ".conversation *" #> "Message center is only available to registered user"
        }
    }
    
  }
  
  def allMyConversations(conversations:List[Conversation]) = conversations.map(conversation => renderConversation(conversation))
  
  def renderConversation(conv:Conversation):CssSel = 
    ".title" #> conv.title.is &
  ".message *" #> showMessages(conv.messages.is)
  
  
  def showMessages(messages:List[Message]) = messages.map(message => renderMessage(message))
  
  def renderMessage(msg:Message):CssSel = 
    ".sender *" #> "%s says,".format(msg.sender.is.displayName.toString) &
  ".messageBody *" #> msg.msgBody.toString &
  ".messageActions *" #> renderActions(msg)
  
  def renderActions(msg:Message):CssSel ={
    if(msg.messageType.is == MessageType.FriendRequest){
      ".actionLink" #> SHtml.a(Text("Accept"), acceptFriendship(User.find(msg.sender.is.user.is)))
    }else{
      ".actionLink" #> ""
    }
  }
  
  def acceptFriendship(friend:Box[User]) = {
    User.currentUser match {
      case Full(user) => {
          friend match {
            case Full(f) => {    
                val relUser = Relationship.createRecord.relationType(RelationType.friend).relatedTo(f._id.is)
                user.relationships.atomicUpdate{relUser::_}
                //user.save
                
                val relFriend = Relationship.createRecord.relationType(RelationType.friend).relatedTo(user._id.is)
                f.relationships.atomicUpdate{relFriend::_}
                //f.save
                
                Alert("Relationship established!")
             }
            case _ => Alert("Unknown sender!!!")
          }
        }
      case _ => Alert("Invalid request!")
    }
  }
}