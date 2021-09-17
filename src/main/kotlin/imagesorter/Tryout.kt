package imagesorter

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString


@Suppress("unused")
class Tryout {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            imageEntries()

        }

        private fun imageEntries() {

            fun toJson(imageEntry: ImageEntry): String {
                return """
                    {
                        "id": "${imageEntry.id}",
                        "image": "${imageEntry.image}"
                    }
                """.trimIndent()
            }

            val testDir = "t1/t11"
            val homeDir = Path.of(System.getProperty("user.home"))
            val tDir = homeDir.resolve(Path.of("work", "easysort", testDir))
            val json = ImageHandler.imageEntries(tDir,200)
                .joinToString(",\n", "[", "]") { toJson(it) }
            println(json)
        }

        private fun directoryEntries() {

            fun toJson(directoryEntry: DirectoryEntry): String {
                return """
                    {
                        "id": "${directoryEntry.id}",
                        "image": "${directoryEntry.image}"
                    }
                """.trimIndent()
            }

            val testDir = "t1"
            val homeDir = Path.of(System.getProperty("user.home"))
            val baseDir = homeDir.resolve(Path.of("work", "easysort", testDir))
            val json = ImageHandler.imagDirectoryEntries(baseDir, 200)
                .joinToString(",\n", "[", "]") { toJson(it) }
            println(json)
        }

        private fun encodeImageToThumbnail() {

            val testDir = "t1"
            val homeDir = Path.of(System.getProperty("user.home"))
            val baseDir = homeDir.resolve(Path.of("work", "easysort", testDir))
            Files.list(baseDir).forEach {
                if (ImageHandler.isImageFile(it)) {
                    val b64 = ImageHandler.base64Thumbnail(it, 200)
                    val html = """<img src="data:image/${b64.format};base64, ${b64.value}" alt="${b64.name}" />"""
                    println(html)
                }
            }
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


