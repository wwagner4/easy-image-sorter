package imagesorter

import org.imgscalr.Scalr
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import javax.imageio.ImageIO
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

    fun isImageFile(imageFile: Path): Boolean {
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

    fun base64Thumbnail(imageFile: Path, thumbnailSize: Int): Base64Data {
        val fileDetails = toFileDetails(imageFile.fileName.toString())

        val image = ImageIO.read(imageFile.toFile())
        val thumb = if (image.width < image.height) {
            val zoom = thumbnailSize.toDouble() / image.width
            val w1 = ceil(zoom * image.width).toInt()
            val h1 = ceil(zoom * image.height).toInt()
            val resized = Scalr.resize(image, Scalr.Method.SPEED, Scalr.Mode.AUTOMATIC, w1, h1)
            val offset = floor((h1 - w1) / 2.0).toInt()
            Scalr.crop(resized, 0, offset, thumbnailSize, thumbnailSize)
        } else {
            val zoom = thumbnailSize.toDouble() / image.height
            val w1 = ceil(zoom * image.width).toInt()
            val h1 = ceil(zoom * image.height).toInt()
            val resized = Scalr.resize(image, Scalr.Method.SPEED, Scalr.Mode.AUTOMATIC, w1, h1)
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


    fun imagDirectoryEntries(baseDir: Path, thumbnailSize: Int): Iterable<DirectoryEntry> {

        fun toImageDirectoryEntry(path: Path): DirectoryEntry? {

            fun directoryEntry(firstImage: Path): DirectoryEntry {
                val id = path.toAbsolutePath().toString()
                val base64Data = base64Thumbnail(firstImage, thumbnailSize)
                val base64HtmlString = "data:image/${base64Data.format};base64, ${base64Data.value}"
                return DirectoryEntry(id, base64HtmlString)
            }

            fun toDirectoryEntry(): DirectoryEntry? {
                fun anyImageFile(files: List<Path>): DirectoryEntry? {
                    if (files.isEmpty()) return null
                    val head = files[0]
                    if (isImageFile(head)) return directoryEntry(head)
                    return anyImageFile(files.drop(1))
                }
                return anyImageFile(Files.list(path).toList())
            }
            if (Files.isDirectory(path)) return toDirectoryEntry()
            return null
        }

        return Files.walk(baseDir)
            .toList()
            .mapNotNull { toImageDirectoryEntry(it) }
    }

    fun imageEntries(tDir: Path, thumbnailSize: Int): Iterable<GridEntry> {

        fun directoryEntry(file: Path): GridEntry? {
            val id = file.fileName.toString()
            if (!isImageFile(file)) return null
            val base64Data = base64Thumbnail(file, thumbnailSize)
            val base64HtmlString = "data:image/${base64Data.format};base64, ${base64Data.value}"
            return GridEntry(id, base64HtmlString)
        }
        return Files.list(tDir).toList().mapNotNull { directoryEntry(it) }
    }


}


