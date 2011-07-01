/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tingendab.kslift.snippet

import org.joda.time.DateTime
import net.liftweb.common._
import net.liftweb.json.DefaultFormats
import net.liftweb.http._
import S._
import net.liftweb.mongodb._
import net.liftweb.util._
import Helpers._
import js._
import net.liftweb.http.js.jquery.JqJsCmds._
import net.liftweb.http.js.jquery.JqJE._
import net.liftweb.http.js.jquery._
import JsCmds._
import JE._
import scala.xml._
import com.mongodb.QueryBuilder
import com.ocpsoft.pretty.time.PrettyTime
import com.tingendab.kslift.model._
import net.liftweb.json.JsonDSL._
import com.mongodb._
import com.foursquare.rogue.Rogue._


class StatusSnippet {
  
  implicit val formats = DefaultFormats + new DateSerializer 
  
  var status = ""
  def showUpdateForm = {
    User.currentUser match {
      case Full(user) => {
          "#loggedinStatusPost ^^" #> "selectit" &
          "#newStatus" #> SHtml.ajaxTextarea(status, (s) => {status = s.toString}) &
          "#postAction *" #> SHtml.ajaxButton("Post this",() => updateStatus(status, user))
        }
      case _ => {
          "#notloggedinStatusPost ^^" #> "selectit" &
          ".registerMessage *" #> Text("Welcome to Kickstart Lift!")
        }
    }
  }
  
  def updateStatus(msg:String, user:User) ={
    def update = Status.createRecord
    .owner(user._id.is)
    .ownerDisplayName(user.getDisplayName)
    .status(msg)
    update.save
    println(update)
        
    S.notice("Status updated!")
    addNewToTimeline(update) &
    JqId(JE.Str("newStatus")) ~> JqAttr("value", "")
  }
  
  var lastUpdate = new DateTime()
  
  def showTimeline = {
    def allUpdates = Status.findAll(Nil,("createdTime" -> -1), Limit(10))
    lastUpdate = new DateTime()
    "#statusList" #> listStatus(allUpdates)    
  }
  
  def addNewToTimeline(savedStatus:Status):JsCmd = {
    //def allUpdates = Status.findAll(("createdTime" -> ("$gt" -> formats.dateFormat.format(lastUpdate.getTime))),("createdTime" -> -1), Limit(10))
    println("----------------------------------------------")
    println("Current Time " + new DateTime())
    println("Last update Time " + lastUpdate)
        
    //def allUpdates = Status where (_.createdTime after lastUpdate) fetch()
    //def newstatus:NodeSeq = listStatus(allUpdates)
    def newstatus:NodeSeq = renderStatus(savedStatus)
    println("Status NodeSeq -- " + newstatus) 
    lastUpdate = new DateTime()
    JqId(Str("timeline")) ~> JqPrepend(newstatus)
  }
  
  def listStatus(manyStatus:List[Status]) = manyStatus.flatMap(status => renderStatus(status))
    
  def renderStatus(status:Status):NodeSeq = {   
    def p = new PrettyTime()
    (".statusFrom *" #> Text(status.ownerDisplayName.toString) &
     ".statusMsg *" #> Text(status.status.toString) &
     ".statusTime *" #>  Text(p.format(status.createdTime.is.getTime))
    )(statusContTemplate)
  }
  
  def statusContTemplate =      
    <div class="statusContainer">
      <div class="statusTitle"><span class="statusFrom"></span> said this <span class="statusTime"></span></div> 
      <div class="statusMsg"></div>
    </div>
    

}

case class JqPrepend(content: NodeSeq) extends JsExp with JsMember with JQueryRight with JQueryLeft {
  println("In class -- " + content)
  override val toJsCmd = {
    println("The content -- " + content)
    "prepend(" + fixHtmlFunc("inline", content){str => str }+ ")"
  } 
}