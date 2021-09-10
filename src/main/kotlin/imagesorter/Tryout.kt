package imagesorter

import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import javax.imageio.ImageIO
import kotlin.io.path.absolutePathString

class Tryout {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            encodeImageToThumbnail()
        }

        fun encodeImageToThumbnail() {

            val testDir = "t1"
            val homeDir = Path.of(System.getProperty("user.home"))
            val baseDir = homeDir.resolve(Path.of("work", "easysort", testDir))
            if (Files.notExists(baseDir))
                throw IllegalStateException("Test-directory ${baseDir.absolutePathString()} does not exist")
            val imgPath = Files.list(baseDir).toList().get(0)
            println("Got an image path: $imgPath")

            val bi = ImageIO.read(imgPath.toFile())
            val w = bi.width
            val h = bi.height
            val zoom = 500.0 / w
            val w1 = (zoom * w).toInt()
            val h1 = (zoom * h).toInt()
            println("read the image: $w1 $h1")


            val oriString = "bezkoder tutorial"
            val encodedString: String = Base64.getEncoder().encodeToString(oriString.toByteArray())

            println(encodedString)
        }

        fun readTestimages() {
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