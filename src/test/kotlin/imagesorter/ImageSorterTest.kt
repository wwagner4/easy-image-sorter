package imagesorter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ImageSorterTest {

    @Test
    fun `rename - two files same prefix`() {
        val ol = listOf(
            "a001.png",
            "a002.png",
        )
        val nl = listOf(
            "a002.png",
            "a001.png",
        )
        val expected = listOf(
            Rename("a001.png", "a002.png"),
            Rename("a002.png", "a001.png"),
        )
        val result = ImageSorter.renamings(ol, nl)

        assignResults(expected, result)
    }

    @Test
    fun `rename - one file no prefix`() {
        val ol = listOf(
            "a001.png",
            "a002.png",
            "x.png",
        )
        val nl = listOf(
            "a001.png",
            "x.png",
            "a002.png",
        )
        val expected = listOf(
            Rename("a001.png", "a001.png"),
            Rename("x.png", "a002.png"),
            Rename("a002.png", "a003.png"),
        )
         val result = ImageSorter.renamings(ol, nl)

        assignResults(expected, result)
    }

    @Test
    fun `commonImagePattern - one prefix `() {
        val fs = listOf(
            "a000.png",
            "a001.png",
            "a002.png",
            "a003.png",
        )
        val ip = ImageSorter.commonImagePattern(fs)
        assertEquals(ImagePattern.Full("a", 3, 0), ip)
    }


    private fun assignResults(expected: List<Rename>, result: List<Rename>) {

        fun toSortString(rename: Rename): String {
            return "${rename.srcName.trim()}${rename.destName.trim()}"
        }

        fun toCompareString(rename: Rename): String {
            return "[ ${rename.srcName.trim()} -> ${rename.destName.trim()} ]"
        }

        val es = expected.sortedBy { toSortString(it) }.map { toCompareString(it) }
        val rs = result.sortedBy { toSortString(it) }.map { toCompareString(it) }

        assertEquals(es, rs)
    }

}