package com.malinowski

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileNotFoundException
import javax.imageio.ImageIO

class GrayScale {
    private val defPath = System.getProperty("user.dir")

    suspend fun grayscale(path: String): File {

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

        val bwPath = "$defPath/upload_grayscale/${file.name}"
        val bwFile = File(bwPath)
        // will complete in new coroutine
        withContext(Dispatchers.IO) {
            ImageIO.write(
                blackAndWhite, "png",
                bwFile
            )
        }
        println("RASPBERRY saved $bwPath")
        println("RASPBERRY grayscale of ${file.name} FINISHED!!! >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")

        return bwFile
    }

    companion object {
        private val impl by lazy {
            GrayScale()
        }

        fun invoke() = impl
    }
}