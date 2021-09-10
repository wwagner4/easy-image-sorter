package imagesorter

import org.imgscalr.Scalr
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import javax.imageio.ImageIO
import kotlin.io.path.absolutePathString


@Suppress("unused")
class Tryout {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            encodeImageToThumbnail()
        }

        private fun encodeImageToThumbnail() {

            val testDir = "t1"
            val homeDir = Path.of(System.getProperty("user.home"))
            val baseDir = homeDir.resolve(Path.of("work", "easysort", testDir))
            if (Files.notExists(baseDir))
                throw IllegalStateException("Test-directory ${baseDir.absolutePathString()} does not exist")
            val imgPath = Files.list(baseDir).toList()[0]
            println("Got an image path: $imgPath")

            val id = ImageHandler.toFileDetails(imgPath.fileName.toString())

            val bi = ImageIO.read(imgPath.toFile())
            val w = bi.width
            val h = bi.height
            val zoom = 10.0 / w
            val w1 = (zoom * w).toInt()
            val h1 = (zoom * h).toInt()
            println("read the image: $w $h")
            println("read the image: $w1 $h1")

            val bi1 = Scalr.resize(bi, Scalr.Method.SPEED, Scalr.Mode.AUTOMATIC, w1, h1)

            val w3 = bi1.width
            val h3 = bi1.height
            println("read the image: $w $h")
            println("read the image: $w1 $h1")
            println("read the image: $w3 $h3")

            val os = ByteArrayOutputStream()
            ImageIO.write(bi1, id.extension, os)
            val b64 = Base64.getEncoder().encodeToString(os.toByteArray())

            println(b64)
        }

        private fun readTestimages() {
            val testDir = "t1"
            val homeDir = Path.of(System.getProperty("user.home"))
            val baseDir = homeDir.resolve(Path.of("work", "easysort", testDir))
            if (Files.notExists(baseDir))
                throw IllegalStateException("Test-directory ${baseDir.absolutePathString()} does not exist")
            Files.list(baseDir).forEach {
                val ns = it.fileName.toString()
                println(""""$ns",""")
            }
        }

    }


}