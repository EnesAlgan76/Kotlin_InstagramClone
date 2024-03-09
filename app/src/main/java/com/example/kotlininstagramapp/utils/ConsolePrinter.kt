package com.example.kotlininstagramapp.utils


object ConsolePrinter {
    fun printYellow(message: String) {
        println("\u001B[34m$message\u001B[44m")
    }
}

