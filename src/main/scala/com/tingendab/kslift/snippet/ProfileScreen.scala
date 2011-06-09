/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tingendab.kslift.snippet

import com.tingendab.kslift.model._
import net.liftweb.common._
import net.liftweb.util._
import net.liftweb.http._


object ProfileScreen extends LiftScreen{
  override def screenTop = 
    <b>Edit your profile</b>

  object person extends ScreenVar(User.currentUser.map(_.profile.is) openOr Person.createRecord)
  addFields(() => person.is)
  
  def finish(){
    User.currentUser.foreach( u => {
        u.profile(person.is)
        u.save
      })
    S.notice("Your profile is updated!")
  } 
  
}
