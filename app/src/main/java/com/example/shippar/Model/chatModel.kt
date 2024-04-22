package com.example.shippar.Model

class ChatModel(var message: String, var user: String) {
    constructor() : this("", "")

    fun getMessage(): String {
        return message
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun getUser(): String {
        return user
    }

    fun setUser(user: String) {
        this.user = user
    }
}