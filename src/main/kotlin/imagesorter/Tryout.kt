package imagesorter

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString


@Suppress("unused")
class Tryout {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            renameFiles()
        }

        private fun renameFiles() {


            println("rename file")
            val dir = Path.of("/home/wwagner4/work/easysort/t1/t11")
            println("directory ${dir.absolutePathString()} exists: ${Files.exists(dir)}")
            if (Files.exists(dir)) {
                println("File $dir exists")
                Files.list(dir)
                    .filter { ImageHandler.isImageFile(it) }
                    .toList()
                    .sortedBy { it.fileName.toString() }
                    .forEach { println(it.fileName.toString()) }
                ImageSorter.rename(
                    dir, listOf(
                        Rename("dia-00026.jpg", "dia-00002.jpg"),
                        Rename("dia-00001.jpg", "dia-00027.jpg"),
                        Rename("dia-00003.jpg", "dia-00003.jpg"),
                        Rename("dia-00027.jpg", "dia-00001.jpg"),
                        Rename("dia-00002.jpg", "dia-00026.jpg"),
                    )
                )
            }
        }

        private fun imageEntries() {

            val id = "/home/wwagner4/work/easysort/t1/t12"
            // val id = "/home/wwagner4/work/easysort/t1/t11/t112"
            // val id = "/home/wwagner4/work/easysort/t1/t11/t111"

            val grid = ImageHandler.grid(id, 100)
            println(grid)
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


