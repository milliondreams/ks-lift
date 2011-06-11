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
import com.foursquare.rogue.Rogue._
import net.liftweb.json.JsonDSL._
//import net.liftweb.mongodb.record._

class UserSnippet{
  var filter = ""
  def listUsers = if(filter == ""){
    ".userDisplay *" #> User.findAll.map(user => <li><a href={"/profile/" + user._id}>{user.profile.is.displayName}</a></li>)
  }else{
    ".userDisplay *" #> User.findAll("profile.displayName" -> filter).map(user => <li><a href={"/profile/" + user._id}>{user.profile.is.displayName}</a></li>)
  }
}