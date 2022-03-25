package imagesorter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ImageSorterTest {

    @Test
    fun `rename - two files no exchanged`() {
        val nl = listOf(
            "a001.png",
            "a002.png",
        )
        val expected = listOf(
            Rename("a001.png", "a001.png"),
            Rename("a002.png", "a002.png"),
        )
        val result = ImageSorter.renamings(nl, null)

        assignResults(expected, result)
    }

    @Test
    fun `rename - two files exchanged`() {
        val nl = listOf(
            "a002.png",
            "a001.png",
        )
        val expected = listOf(
            Rename("a001.png", "a002.png"),
            Rename("a002.png", "a001.png"),
        )
        val result = ImageSorter.renamings(nl, null)

        assignResults(expected, result)
    }

    @Test
    fun `rename - three files first two exchanged`() {
        val nl = listOf(
            "a002.png",
            "a001.png",
            "a003.png",
        )
        val expected = listOf(
            Rename("a001.png", "a002.png"),
            Rename("a002.png", "a001.png"),
            Rename("a003.png", "a003.png"),
        ).shuffled()
        val result = ImageSorter.renamings(nl, null)

        assignResults(expected, result)
    }

    @Test
    fun `rename - three files last two exchanged`() {
        val nl = listOf(
            "a001.png",
            "a003.png",
            "a002.png",
        )
        val expected = listOf(
            Rename("a001.png", "a001.png"),
            Rename("a002.png", "a003.png"),
            Rename("a003.png", "a002.png"),
        ).shuffled()
        val result = ImageSorter.renamings(nl, null)

        assignResults(expected, result)
    }

    @Test
    fun `rename - three files rotated`() {
        val nl = listOf(
            "a003.png",
            "a001.png",
            "a002.png",
        )
        val expected = listOf(
            Rename("a001.png", "a002.png"),
            Rename("a002.png", "a003.png"),
            Rename("a003.png", "a001.png"),
        ).shuffled()
        val result = ImageSorter.renamings(nl, null)

        assignResults(expected, result)
    }

    @Test
    fun `rename - three files first and last exchanged`() {
        val nl = listOf(
            "a003.png",
            "a002.png",
            "a001.png",
        )
        val expected = listOf(
            Rename("a001.png", "a003.png"),
            Rename("a002.png", "a002.png"),
            Rename("a003.png", "a001.png"),
        )
        val result = ImageSorter.renamings(nl, null)

        assignResults(expected, result)
    }

    @Test
    fun `rename - last moved to first`() {
        val nl = listOf(
            "a003.png",
            "a001.png",
            "a002.png",
        )
        val expected = listOf(
            Rename("a001.png", "a002.png"),
            Rename("a002.png", "a003.png"),
            Rename("a003.png", "a001.png"),
        )
        val result = ImageSorter.renamings(nl, null)

        assignResults(expected, result)
    }

    @Test
    fun `rename - one file no extension and different place`() {
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
        val result = ImageSorter.renamings(nl, null)

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
        val ip = ImageSorter.commonImagePattern(fs, null)
        assertEquals(ImagePattern.Full("a", 3, 0), ip)
    }


    @Test
    fun `commonImagePattern - one prefix staring at 1`() {
        val fs = listOf(
            "a001.png",
            "a002.png",
            "a003.png",
        )
        val ip = ImageSorter.commonImagePattern(fs, null)
        assertEquals(ImagePattern.Full("a", 3, 1), ip)
    }

    @Test
    fun `commonImagePattern - numlen changing`() {
        val fs = listOf(
            "a001.png",
            "a0002.png",
            "a00003.png",
        )
        val ip = ImageSorter.commonImagePattern(fs, null)
        assertEquals(ImagePattern.Full("a", 5, 1), ip)
    }

    @Test
    fun `commonImagePattern - different prefixes`() {
        val fs = listOf(
            "a001.png",
            "a002.png",
            "b002.png",
            "b003.png",
            "b004.png",
        )
        val ip = ImageSorter.commonImagePattern(fs, null)
        assertEquals(ImagePattern.Full("b", 3, 2), ip)
    }

    @Test
    fun `commonImagePattern - different prefixes same count`() {
        val fs = listOf(
            "a001.png",
            "a002.png",
            "a003.png",
            "b002.png",
            "b003.png",
            "b004.png",
        )
        val ip = ImageSorter.commonImagePattern(fs, null)
        assertEquals(ImagePattern.Full("a", 3, 1), ip)
    }

    @Test
    fun `commonImagePattern - different prefixes same count descending`() {
        val fs = listOf(
            "x001.png",
            "x002.png",
            "x003.png",
            "b002.png",
            "b003.png",
            "b004.png",
        )
        val ip = ImageSorter.commonImagePattern(fs, null)
        assertEquals(ImagePattern.Full("b", 3, 2), ip)
    }

    @Test
    fun `commonImagePattern - no image group`() {
        val fs = listOf(
            "x.png",
            "hallo.png",
            "y.png",
            "y.jpeg",
            "b003.txt",
            "b004",
        )
        val ip = ImageSorter.commonImagePattern(fs, null)
        assertEquals(ImagePattern.Full("image-", 5, 0), ip)
    }

    @Test
    fun `newFileNames - number offset and different prefix`() {
        val imagePatterns = ImagePattern.Full("i-", 3, 10)
        val prefixes = listOf("jpeg", "PNG")
        val results = ImageSorter.newNames(imagePatterns, prefixes)
        assertEquals(2, results.size)
        assertEquals("i-010.jpeg", results[0])
        assertEquals("i-011.PNG", results[1])
    }

    @Test
    fun `newFileNames - different prefix`() {
        val imagePatterns = ImagePattern.Full("i-", 5, 0)
        val prefixes = listOf("jpeg", "jpg")
        val results = ImageSorter.newNames(imagePatterns, prefixes)
        assertEquals(2, results.size)
        assertEquals("i-00000.jpeg", results[0])
        assertEquals("i-00001.jpg", results[1])
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