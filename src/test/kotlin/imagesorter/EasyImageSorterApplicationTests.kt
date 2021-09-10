package imagesorter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.test.context.SpringBootTest
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString

/*
"dia-00045.jpg",
"dia-00036.jpg",
"dia-00084.jpg",
"dia-00064.jpg",
"dia-00082.jpg",
"dia-00057.jpg",
"dia-00079.jpg",
"dia00024.jpg",
"dia-00048.jpg",
"dia00026.jpg",
"dia00017.jpg",
"dia-00055.jpg",
"dia-00030.jpg",
"dia-00063.jpg",
"dia-00043.jpg",
"dia-00035.jpg",
"dia00023.jpg",
"dia-00056.jpg",
"dia-00051.jpg",
"dia-00033.jpg",
"dia-00041.jpg",
"dia-00081.jpg",
"dia-00062.jpg",
"dia-00077.jpg",
"dia-00075.jpg",
"dia-00049.jpg",
"dia00025.jpg",
"dia-00004.jpg",
"dia-00003.jpg",
"dia-00001.jpg",
"dia00019.jpg",
"dia-00072.jpg",
"dia-00058.jpg",
 */


@SpringBootTest
class EasyImageSorterApplicationTests {

    fun contextLoads() {
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

    object ImageHandler {

        private val imageRegex by lazy { Regex("(\\w.*?)([0-9].*)\\..*") }

        sealed interface ImagePattern {

            data class Full(
                val prefix: String,
                val numberLen: Int,
                val startNumber: Int,
            ) : ImagePattern

            object Invalid : ImagePattern

        }

        fun toImagePattern(name: String): ImagePattern {
            return try {
                val match = imageRegex.find(name)!!
                val (prefix, numbers) = match.destructured
                ImagePattern.Full(prefix, numbers.length, numbers.toInt())
            } catch (_: Exception) {
                ImagePattern.Invalid
            }
        }
    }


    @ParameterizedTest
    @ValueSource(
        strings = [
            "dia-00062.jpg #full|dia-|5|62",
            "dia00062.jpg  #full|dia|5|62",
            "dia00063.jpg  #full|dia|5|63",
            "dia00062.JPG  #full|dia|5|62",
            "dia00062.PNG  #full|dia|5|62",
            "dia.JPEG      #invalid",
            "dia09809o.png #invalid",
        ]
    )
    fun testImagePattern(value: String) {
        val fileName = value.split("#")[0].trim()
        val expectedArray = value.split("#")[1].split("|")
        val expected = when (expectedArray[0]) {
            "full" -> ImageHandler.ImagePattern.Full(expectedArray[1], expectedArray[2].toInt(), expectedArray[3].toInt())
            "invalid" -> ImageHandler.ImagePattern.Invalid
            else -> throw IllegalStateException("Invalid qualifier ${expectedArray[0]}")
        }

        val imagePattern = ImageHandler.toImagePattern(fileName)

        assertEquals(expected, imagePattern)
    }

}
