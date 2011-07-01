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
    User.currentUser match {
      case Full(currentUser) => {
          if(filter == ""){
            ".peopleListing *" #> manyUsers(User.findAll, currentUser) 
          }else{
            ".peopleListing *" #> manyUsers (User.findAll("profile.displayName" -> filter), currentUser)
          }
        }
      case _ => {
          ".peopleListing" #> "You need to login to see the member list"
        }
    }
  }
  
  def manyUsers(users:List[User],currentUser:User) = users.map(user => singleUser(user, currentUser))
  
  def singleUser(user:User, currentUser:User):CssSel = {
    if(user._id.is == currentUser._id.is){
      "*" #> ""
    }else{
      ".name *" #> Text(user.getDisplayName) &
      ".name [href]" #> "/profile/%s".format(user._id.toString) &
      ".fullname *" #> Text(user.firstName.is + " " + user.lastName.is)
    }
  }

  lazy val profileUser = User.find("_id" -> S.param("userid").map(_.toString).openOr("0"))
  
  def sendFriendRequest(sender: User, reciever:User) = {
    val fromUser = MemberUser.createRecord
    .user(sender.id)
    .displayName(sender.getDisplayName);
    
    
    /* val toUser = MemberUser.createRecord
     .user(reciever.id)
     .displayName(reciever.getDisplayName)
     */
   
    val msg = Message.createRecord
    .sender(fromUser)
    .messageType(MessageType.FriendRequest)
    .msgBody("Do you acccept this friend request?")
    
    
    Conversation.createRecord
    .title("Friendship request from " + sender.getDisplayName + " to " + reciever.getDisplayName)
    .mode(ConversationMode.restricted)
    .memberUsers(sender._id.is::reciever._id.is::Nil)
    .messages(msg::Nil)
    .creator(sender._id.is)
    .save
    
    //Todo: Allow to attach personal message
    Alert("Request sent!")
  }
  
  def showProfile = {
    profileUser match {
      case Full(profileUser) => {
          
          println(profileUser.getDisplayName)
          User.currentUser match {
            case Full(currentUser) =>
              if(currentUser._id.toString == profileUser._id.toString){
                "#name *" #> Text(profileUser.getDisplayName) &
                "#action" #> SHtml.link("/user/profile", ()=>"Edit profile", Text("Edit profile"))
              }else{
                //var rels = currentUser.relationships.is.find(r => r.relationType.is == RelationType.friend && r.relatedTo.is ==  profileUser._id.is);
                if(currentUser.isRelated(profileUser, RelationType.friend)){
                  "#name *" #> Text(profileUser.getDisplayName) &
                  "#action" #> Text("Friend")
                }else{
                  "#name *" #> Text(profileUser.getDisplayName) &
                  "#action" #> SHtml.a(()=>sendFriendRequest(currentUser,profileUser), Text("Add friend"))
                }
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