package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/web_services/grayscale") {
            call.respondText("It's a service to grayscale an image!")
        }
        get("/web_services/array") {
            call.respondText("It's a service to find the words containing a letter in an array!")
        }
    }
}
