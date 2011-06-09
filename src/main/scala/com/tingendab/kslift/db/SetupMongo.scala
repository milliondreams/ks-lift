/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tingendab.kslift.db

import _root_.net.liftweb.mongodb._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import net.liftweb.json.DefaultFormats
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonParser._
import net.liftweb.json.JsonDSL._

object SetupMongo {
  def setup {
    MongoDB.defineDb(DefaultMongoIdentifier, MongoAddress(MongoHost(), "kslift"))
  }

  def isMongoRunning: Boolean = {
    try {
      MongoDB.use(DefaultMongoIdentifier) ( db => { db.getLastError } )
      true
    }
    catch {
      case e => false
    }
  }
}
