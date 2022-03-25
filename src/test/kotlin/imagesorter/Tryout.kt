package imagesorter

import imagesorter.ImageHandler.placeholderImage
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.io.path.absolute


@Suppress("unused")
class Tryout {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            createPlaceholderImage()
        }

        private fun createPlaceholderImage() {
            val size = 100
            val fontName = "Arial"
            val text = "ERROR reading IMG_20181109_111607_264.jpg"

            val outDir = Path("build", "images")
            if (Files.notExists(outDir)) Files.createDirectories(outDir)
            val file = outDir.resolve("placeholder.jpg")

            val img = placeholderImage(size, fontName, text)

            ImageIO.write(img, "jpg", file.toFile())
            if (Files.notExists(file)) throw RuntimeException("Could not create thumbnail for $file")
            println("Wrote placeholder to ${file.absolute()}")
        }

    }

}
