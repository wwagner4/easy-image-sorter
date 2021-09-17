package imagesorter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Path

@SpringBootApplication
class EasyImageSorterApplication

fun main(args: Array<String>) {
    runApplication<EasyImageSorterApplication>(*args)
}

@RestController
class DirectoryEntriesResource {

    @GetMapping("/list")
    fun directoryEntries(): Iterable<DirectoryEntry> {
        val testDir = "t1"
        val homeDir = Path.of(System.getProperty("user.home"))
        val baseDir = homeDir.resolve(Path.of("work", "easysort", testDir))
        return ImageHandler.imagDirectoryEntries(baseDir, 200)
    }

}

@RestController
class GridResource {

    @GetMapping("/grid")
    fun directoryEntries(): Iterable<DirectoryEntry> {
        throw NotImplementedError()
    }

}

data class DirectoryEntry(val id: String, val image: String)

data class ImageEntry(val id: String, val image: String)

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

