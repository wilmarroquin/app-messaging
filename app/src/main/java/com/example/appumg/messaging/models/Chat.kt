package com.example.appumg.messaging.models

import com.google.firebase.Timestamp

class Chat (
    var administratorsId: List<String>?=null,
    var chatId: String?=null,
    var chatName: String = "",
    var creationTimestamp: Timestamp= Timestamp.now(),
    var creatorId: String = "",
    var hasCustomIcon: Boolean = false,
    var lastMessageTimestamp: Timestamp= Timestamp.now(),
    var lastMessage: String = "",
    var membersId: List<String>?=null,
    var iconUrl: String?= ""
)