package com.example.shippar.Utils

import com.google.firebase.database.DatabaseReference

object UserDetails {
    var userID: String = ""
    var userEmail: String = ""
    var chatwithID: String = ""
    var chatwithEmail: String = ""
    var userType: String = ""
    var chatRef: DatabaseReference? = null
}