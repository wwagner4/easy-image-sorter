package imagesorter

import org.apache.tomcat.util.codec.binary.Base64
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Path

@SpringBootApplication
class EasyImageSorterApplication

fun main(args: Array<String>) {
    runApplication<EasyImageSorterApplication>(*args)
}

@RestController
class EasySorterResource {

    @GetMapping("/list")
    fun directoryEntries(): Iterable<ImageEntry> {
        val testDir = "t1"
        val homeDir = Path.of(System.getProperty("user.home"))
        val baseDir = homeDir.resolve(Path.of("work", "easysort", testDir))
        return ImageHandler.imagDirectoryEntries(baseDir, 100)
    }

    @GetMapping("/grid/{idBase64}")
    fun raster(@PathVariable idBase64: String): Grid {
        val id = String(Base64.decodeBase64URLSafe(idBase64))
        println("-- grid $id --")
        val grid = ImageHandler.grid(id, 100)
        // println(grid)
        return grid
    }

}

data class ImageEntry(val id: String, val image: String)

data class Grid(
    val id: String,
    val entries: Iterable<ImageEntry>
)


data class FileDetails(
    val originalName: String,
    val baseName: String,
    val extension: String,
    val imagePattern: ImagePattern
)

sealed interface ImagePattern {

    data class Full(
        val prefix: String,
        val numberLen: Int,
        val number: Int,
    ) : ImagePattern

    object Invalid : ImagePattern

}

data class Base64Data(
    val value: String,
    val format: String,
    val name: String,
)

