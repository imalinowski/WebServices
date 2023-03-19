package com.malinowski

import kotlinx.coroutines.*
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileNotFoundException
import javax.imageio.ImageIO
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
class GrayScale : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.IO.limitedParallelism(10)
    // new thread for grayscale with limited 10 threads

    private val defPath = System.getProperty("user.dir")

    fun grayscale(path: String) {
        launch { // launch new coroutine for task
            val file = File(path)
            if (!file.exists()) {
                throw FileNotFoundException()
            }
            println("RASPBERRY grayscale of ${file.name} STARTED!!! <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")

            // will complete in new coroutine
            val origin = withContext(Dispatchers.IO) {
                ImageIO.read(file)
            }
            val blackAndWhite = BufferedImage(origin.width, origin.height, BufferedImage.TYPE_BYTE_GRAY)
            val graphics2D = blackAndWhite.createGraphics()
            graphics2D.drawImage(origin, 0, 0, null)

            // will complete in new coroutine
            withContext(Dispatchers.IO) {
                ImageIO.write(
                    blackAndWhite, "png",
                    File("$defPath/upload_grayscale/${file.name}")
                )
            }
            println("RASPBERRY saved $defPath/upload_grayscale/${file.name}")
            println("RASPBERRY grayscale of ${file.name} FINISHED!!! >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        }
    }

    companion object {
        private val impl by lazy {
            GrayScale()
        }

        fun invoke() = impl
    }
}