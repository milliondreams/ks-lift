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
//import net.liftweb.mongodb.record._

class UserSnippet extends StatefulSnippet{
  
  override def dispatch = {
    case "listUsers" => listUsers
    case "showProfile" => showProfile
  }
  
  var filter = ""
  def listUsers = {
    if(filter == ""){
      ".userDisplay *" #> User.findAll.map(user => <li><a href={"/profile/" + user._id}>{user.profile.is.displayName}</a></li>)
    }else{
      ".userDisplay *" #> User.findAll("profile.displayName" -> filter).map(user => <li><a href={"/profile/" + user._id}>{user.profile.is.displayName}</a></li>)
    }
  }
  
  lazy val profileUser = User.find("_id" -> S.param("userid").map(_.toString).openOr("0"))
  
  def sendFriendRequest(sender: User, reciever:User) = {
    val fromUser = MemberUser.createRecord
    .user(sender.id)
    .displayName(sender.profile.is.displayName.toString);
    
    
    val toUser = MemberUser.createRecord
    .user(reciever.id)
    .displayName(reciever.profile.is.displayName.toString)
    
    val msg = Message.createRecord
    .sender(fromUser)
    .messageType(MessageType.FriendRequest)
    .msgBody("Do you acccept this friend request?")
    
    
    Conversation.createRecord
    .title("Friendship request from " + sender.profile.is.displayName)
    .mode(ConversationMode.restricted)
    .memberUsers(fromUser::toUser::Nil)
    .save
    
    //Todo: Allow to attach personal message
    Alert("Request sent!")
  }
  
  
  def showProfile = {
    profileUser match {
      case Full(profileUser) => {
          
          "#name" #> profileUser.profile.is.displayName.toString
          User.currentUser match {
            case Full(currentUser) =>
              if(currentUser._id.toString == profileUser._id.toString){
                "#action" #> SHtml.link("/user/profile", ()=>"Edit profile", Text("Edit profile"))
              }else{
                //TODO: If the user is already friend
                "#action" #> SHtml.a(Text("Add friend"),sendFriendRequest(currentUser,profileUser))
              }
            case _ => {
                "#action" #> SHtml.link("/user_mgt/login", ()=>"Edit profile", Text("Login to add as friend"))
              }
          }
        }
      case _ => "#name" #> "User not found"
    }
    
  }
  
  
}