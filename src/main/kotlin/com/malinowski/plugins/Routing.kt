package com.malinowski.plugins

import com.malinowski.GrayScale
import com.malinowski.LetterWord
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.delay
import java.io.File

private val path = System.getProperty("user.dir")

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json()
    }
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        post("/array") {
            val letterWord = call.receive<LetterWord>()
            val filtered = letterWord.copy(
                words = letterWord.words.filter {
                    it.contains(letterWord.letter)
                }
            )
            call.respond(filtered)
        }
        post("/grayscale/upload") { _ ->
            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val file = saveImage(part)
                    val bwFile = GrayScale().grayscale(file.absolutePath)
                    call.respond(bwFile)
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
                part.dispose()
            }
        }
        get("/grayscale/download/{name}") {
            val filename = call.parameters["name"]!!
            val file = File("$path/upload_grayscale/$filename")
            repeat(10) {
                println("RASPBERRY file exists ${file.name} ${file.exists()}")
                if (file.exists()) {
                    call.respondFile(file)
                    return@get
                }
                delay(1000) // wait for converting
            }
            call.respond(HttpStatusCode.NotFound)
        }

    }
}


private fun saveImage(part: PartData.FileItem): File {
    val name = part.originalFileName!!
    val file = File("$path/upload_original/$name")
    part.streamProvider().use { its ->
        file.outputStream().buffered().use {
            its.copyTo(it)
        }
    }
    println("RASPBERRY saved ${file.absolutePath}")
    return file
}
