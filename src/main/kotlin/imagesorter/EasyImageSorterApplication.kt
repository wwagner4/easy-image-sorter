package imagesorter

import org.apache.tomcat.util.codec.binary.Base64
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.*
import java.io.InputStreamReader
import java.io.StringReader
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
        val id = String(Base64.decodeBase64URLSafe(idBase64), Charsets.ISO_8859_1)
        println("-- grid $id --")
        return ImageHandler.grid(id, 100)
    }

    @PostMapping("/sort")
    fun sort(@RequestBody sorted: Sort) {
        println("-- sort --")
        println(sorted)
        val renames = ImageSorter.renamings(sorted.images)
        renames.forEach { println(it) }
        ImageSorter.rename(Path.of(sorted.id), renames)
        println( "Renamed ${sorted.id}")
    }
}

data class ImageEntry(val id: String, val image: String)

data class Sort(val id: String, val images: List<String>)

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

