package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._
import _root_.net.liftweb.mongodb._
import _root_.net.liftweb.mongodb.record._
import _root_.net.liftweb.record._
import _root_.net.liftweb.mongodb.record.field._
import _root_.net.liftweb.record.field._
import _root_.com.tingendab.kslift.model._
import com.tingendab.kslift.db.SetupMongo


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("com.tingendab.kslift")

    //Set the template format to XHTML and the Output to HTML5
    //LiftRules.htmlProperties.default.set((r: Req) => new XHtmlInHtml5OutProperties(r.userAgent))
    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent)) 

    // Build SiteMap
    def sitemap() = SiteMap(
      Menu("Home") / "index" >> User.AddUserMenusAfter, // Simple menu form
      Menu("My Profile") / "user" / "profile" >> If(User.loggedIn_? _, S ? "Not available"),
      Menu("Browse Users") / "user" / "browse" >> If(User.loggedIn_? _, S ? "Not available"),
      Menu("Message Center") / "user" / "msgcenter" >> If(User.loggedIn_? _, S ? "Not available"),
      Menu("User Profile") / "profile" >> Hidden,
      // Menu with special Link
      Menu(Loc("Static", Link(List("static"), true, "/static/index"), 
	       "Static Content")))

    
    LiftRules.setSiteMapFunc(() => User.sitemapMutator(sitemap()))

    /*
     * Show the spinny image when an Ajax call starts
     */
     LiftRules.ajaxStart =
       Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

     /*
      * Make the spinny image go away when it ends
      */
     LiftRules.ajaxEnd =
       Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

     LiftRules.early.append(makeUtf8)

     LiftRules.loggedInTest = Full(() => User.loggedIn_?)
    
    LiftRules.statelessRewrite.append {
      case RewriteRequest(ParsePath("profile" :: key :: Nil,"",true,_),_,_) =>
           RewriteResponse("profile" :: Nil, Map("userid" -> key))
    }
     SetupMongo.setup

     }

     /**
      * Force the request to be UTF-8
      */
     private def makeUtf8(req: HTTPRequest) {
        req.setCharacterEncoding("UTF-8")
      }
     }
