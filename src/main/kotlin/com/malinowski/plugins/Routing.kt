package com.malinowski.plugins

import com.malinowski.GrayScale
import com.malinowski.LetterWord
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

private val path = System.getProperty("user.dir")

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        post("/array") {
            launch(Dispatchers.Default) { // launch on new thread
//                println("RASPBERRY array >> ${call.receiveText()}")
//                withContext(Dispatchers.IO) {
//                    val text = BufferedReader(call.receiveStream().reader()).readText()
//                    println("RASPBERRY array >> $text")
//                }
                val data = call.receive<LetterWord>()
                val filteredData = data.copy(
                    words = data.words.filter { it.contains(data.letter) }
                )
                call.respond(filteredData)
            }
        }
        post("/grayscale/upload") { _ ->
            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val file = saveImage(part)
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
