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
  def listMsg = {
    User.currentUser match {
      case Full(user) => {
          //val qry = QueryBuilder.start("memberUsers").is(user._id.is).get
          ".msgdisplay" #> Conversation.findAll("memberUsers" -> ("$oid" -> user._id.toString)).map{conv =>
          //".msgdisplay" #> Conversation.findAll(qry).map{conv =>
            <li>{conv.title.is}</li>
          }
        }
      case _ => {
          ".msgdisplay" #> "Please login"
      }
    }
  }
}