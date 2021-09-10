package imagesorter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class EasyImageSorterApplicationTests {

    @ParameterizedTest
    @ValueSource(
        strings = [
            "dia-00062.jpg   #dia-00062|jpg|full|dia-|5|62",
            "dia00062.jpg    #dia00062|jpg|full|dia|5|62",
            "dia00063.jpg    #dia00063|jpg|full|dia|5|63",
            "dia00062.JPG    #dia00062|JPG|full|dia|5|62",
            "dia0000062.PNG  #dia0000062|PNG|full|dia|7|62",
            "dia.JPEG        #dia|JPEG|invalid",
            "dia09809o.png   #dia09809o|png|invalid",
        ]
    )
    fun testImagePattern(value: String) {
        val fileName = value.split("#")[0].trim()
        val expectedArray = value.split("#")[1].split("|")
        val base = expectedArray[0]
        val ext = expectedArray[1]
        val expectedImagePattern = when (expectedArray[2]) {
            "full" -> ImagePattern.Full(
                expectedArray[3],
                expectedArray[4].toInt(),
                expectedArray[5].toInt()
            )
            "invalid" -> ImagePattern.Invalid
            else -> throw IllegalStateException("Invalid qualifier ${expectedArray[2]}")
        }
        val expected = FileDetails(fileName, base, ext, expectedImagePattern)

        val imagePattern = ImageHandler.toFileDetails(fileName)

        assertEquals(expected, imagePattern)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "dia-00062.jpg  #t",
            "dia00062.jpg   #t",
            "dia00062.JPG   #t",
            "dia00062.JPEG  #t",
            "dia00062.jpeg  #t",
            "dia00062.png   #t",
            "dia00062.PNG   #t",
            "dia00062.txt   #f",
            "dia00062       #f",
            "dia00062.      #f",
            ".png           #f",
        ]
    )
    fun testIsImage(value: String) {
        val fileName = value.split("#")[0].trim()
        val expected = when (val expectedString = value.split("#")[1]) {
            "t" -> true
            "f" -> false
            else -> throw IllegalStateException("Invalid qualifier $expectedString")
        }

        assertEquals(expected, ImageHandler.isImageFile(fileName))
    }

}
