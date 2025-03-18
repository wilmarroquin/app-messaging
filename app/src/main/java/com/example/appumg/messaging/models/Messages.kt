package com.example.appumg.messaging.models

import com.google.firebase.Timestamp

data class Messages (
    var messageId:String?=null,
    var senderId: String="",
    var text:String="",
    var hasAttachedImage:Boolean=false,
    var messageTimestamp: Timestamp= Timestamp.now()
)