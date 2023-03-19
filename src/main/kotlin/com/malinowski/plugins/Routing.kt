package com.malinowski.plugins

import com.malinowski.GrayScale
import io.ktor.http.*
import io.ktor.http.ContentDisposition.Companion.File
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.delay
import java.io.File

private val path  = System.getProperty("user.dir")

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/web_services/array") {
            call.respondText("It's a service to find the words containing a letter in an array!")
        }
        post("/grayscale/upload") { _ ->
            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                if(part is PartData.FileItem) {
                    val name = part.originalFileName!!
                    val file = File("$path/upload_original/$name")
                    println("RASPBERRY saved ${file.absolutePath}")
                    part.streamProvider().use { its ->
                        file.outputStream().buffered().use {
                            its.copyTo(it)
                        }
                    }
                    // launch grayscale
                    GrayScale().grayscale(file.absolutePath)
                }
                part.dispose()
            }
        }
        get("/grayscale/download/{name}") {
            val filename = call.parameters["name"]!!
            val file = File("$path/upload_grayscale/$filename")
            repeat(10) {
                println("RASPBERRY file exists ${file.name} ${file.exists()}")
                if(file.exists()) {
                    call.respondFile(file)
                    return@get
                }
                delay(1000) // wait for converting
            }
            call.respond(HttpStatusCode.NotFound)
        }

    }
}
