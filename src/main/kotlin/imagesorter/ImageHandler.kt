package imagesorter

import org.imgscalr.Scalr
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import javax.imageio.ImageIO
import kotlin.io.path.absolute
import kotlin.math.ceil
import kotlin.math.floor

object ImageHandler {

    private val imageRegex by lazy { Regex("(\\w.*?)([0-9].*)\\..*") }

    fun toFileDetails(imageFileName: String): FileDetails {
        val imagePattern = try {
            val match = imageRegex.find(imageFileName)!!
            val (prefix, numbers) = match.destructured
            ImagePattern.Full(prefix, numbers.length, numbers.toInt())
        } catch (_: Exception) {
            ImagePattern.Invalid
        }
        val index = imageFileName.lastIndexOf(".")
        val extension = imageFileName.substring(index + 1)
        val base = imageFileName.substring(0, index)
        return FileDetails(imageFileName, base, extension, imagePattern)
    }

    private fun isImageFile(imageFile: Path): Boolean {
        return Files.exists(imageFile)
                && Files.isRegularFile(imageFile)
                && isImageFile(imageFile.fileName.toString())
    }

    fun isImageFile(imageFileName: String): Boolean {
        val index = imageFileName.lastIndexOf(".")
        if (index < 1) return false
        return when (imageFileName.substring(index + 1).lowercase()) {
            "jpg" -> true
            "jpeg" -> true
            "png" -> true
            else -> false
        }
    }

    fun placeholderImage(size: Int, fontName: String, text: String): BufferedImage {

        fun splitTxt(txt: String, max: Int): List<String> {
            val words = txt.split("\\s".toRegex())
            return words.flatMap { it.chunked(max) }
        }

        val fontSize = size / 10.0
        val max = 1.3 * size / fontSize
        val txtList = splitTxt(text, max.toInt())

        val font = Font(fontName, Font.BOLD, fontSize.toInt())
        val img = BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)
        val g2d = img.createGraphics()
        // addRenderHints(g2d)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY)
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE)
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
        g2d.font = font
        val fm = g2d.fontMetrics
        g2d.color = Color.YELLOW
        g2d.fillRect(0, 0, size, size)
        g2d.color = Color.BLACK
        txtList.withIndex().forEach {
            val i = it.index
            val txt = it.value
            g2d.drawString(txt, 0, fm.ascent + (fontSize * i).toInt())
        }
        g2d.dispose()
        return img
    }

    private fun base64Thumbnail(imageFile: Path, thumbnailSize: Int): Base64Data {
        val fileDetails = toFileDetails(imageFile.fileName.toString())

        val image = readImage(imageFile)
        val thumb =
            if (image == null) {
                placeholderImage(thumbnailSize, "Arial", "ERROR reading ${imageFile.fileName}")
            } else if (image.width < image.height) {
                val zoom = thumbnailSize.toDouble() / image.width
                val w1 = ceil(zoom * image.width).toInt()
                val h1 = ceil(zoom * image.height).toInt()
                val resized = Scalr.resize(image, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, w1, h1)
                val offset = floor((h1 - w1) / 2.0).toInt()
                Scalr.crop(resized, 0, offset, thumbnailSize, thumbnailSize)
            } else {
                val zoom = thumbnailSize.toDouble() / image.height
                val w1 = ceil(zoom * image.width).toInt()
                val h1 = ceil(zoom * image.height).toInt()
                val resized = Scalr.resize(image, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, w1, h1)
                val offset = floor((w1 - h1) / 2.0).toInt()
                Scalr.crop(resized, offset, 0, thumbnailSize, thumbnailSize)
            }

        val os = ByteArrayOutputStream()
        ImageIO.write(thumb, fileDetails.extension, os)
        return Base64Data(
            value = Base64.getEncoder().encodeToString(os.toByteArray()),
            format = fileDetails.extension,
            name = fileDetails.originalName,
        )
    }

    private fun readImage(imageFile: Path): BufferedImage? {
        return try {
            ImageIO.read(imageFile.toFile())
        } catch (e: Exception) {
            println("Error reading image ${imageFile.absolute()}. $e")
            null
        }
    }

    fun imagDirectoryEntries(baseDir: Path, thumbnailSize: Int): Iterable<ImageEntry> {

        fun toImageDirectoryEntry(path: Path): ImageEntry? {

            fun directoryEntry(firstImage: Path): ImageEntry {
                val id = path.toAbsolutePath().toString()
                val base64Data = base64Thumbnail(firstImage, thumbnailSize)
                val base64HtmlString = "data:image/${base64Data.format};base64, ${base64Data.value}"
                return ImageEntry(id, base64HtmlString)
            }

            fun toDirectoryEntry(): ImageEntry? {
                fun anyImageFile(files: List<Path>): ImageEntry? {
                    if (files.isEmpty()) return null
                    val head = files.sortedBy { it.fileName.toString() }[0]
                    if (isImageFile(head)) return directoryEntry(head)
                    return anyImageFile(files.drop(1))
                }
                return anyImageFile(Files.list(path).toList())
            }
            if (Files.isDirectory(path)) println("loading ${path.fileName}")
            if (Files.isDirectory(path)) return toDirectoryEntry()
            return null
        }

        return Files.walk(baseDir)
            .toList()
            .mapNotNull { toImageDirectoryEntry(it) }
    }

    private fun imageEntries(imagesDir: Path, thumbnailSize: Int): Iterable<ImageEntry> {

        fun directoryEntry(file: Path): ImageEntry? {
            println("loading ${file.fileName}")
            val id = file.fileName.toString()
            if (!isImageFile(file)) return null
            val base64Data = base64Thumbnail(file, thumbnailSize)
            val base64HtmlString = "data:image/${base64Data.format};base64, ${base64Data.value}"
            return ImageEntry(id, base64HtmlString)
        }

        val entries = Files.list(imagesDir).parallel().map { directoryEntry(it) }
        return entries.toList().filterNotNull().sortedBy { it.id }
    }

    fun grid(id: String, thumbnailSize: Int): Grid {
        val tDir = Path.of(id)
        val gridEntries: Iterable<ImageEntry> = imageEntries(tDir, thumbnailSize)
        return Grid(id = id, gridEntries)
    }


}


