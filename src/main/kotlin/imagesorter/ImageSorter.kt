package imagesorter

data class Rename(
    val srcName: String,
    val destName: String
)

object ImageSorter {

    fun renamings(originalOrder: List<String>, newOrder: List<String>): List<Rename> {
        val cip = commonImagePattern(originalOrder)

        return originalOrder.zip(newOrder).map { (a, b) -> Rename(a, b) }
    }

    fun newNames(imagePattern: ImagePattern.Full, size: Int): List<String> {
        val numbers = generateSequence { imagePattern.number until imagePattern.number + size }.toList()
        // TODO Continue here. Do not forget file extensions to be equal as before.
        throw NotImplementedError()
    }

    fun commonImagePattern(fileNames: List<String>): ImagePattern.Full {

        val defaultNumberLen = 5
        val defaultPrefix = "image-"

        data class PrefixGroup(
            val prefix: String,
            val minNumber: Int,
            val maxNumberLen: Int,
            val count: Int,
        )

        fun full(imagePattern: ImagePattern):ImagePattern.Full? {
            return if (imagePattern is ImagePattern.Full) imagePattern else null
        }

        fun prefixGroup(group: Pair<String, List<ImagePattern.Full>>): PrefixGroup {
            val patterns = group.second
            val minNumber = patterns.map { it.number }.minOrNull().let { it ?: 0 }
            val maxNumberLen = patterns.map { it.numberLen }.maxOrNull().let { it ?: 5 }
            return PrefixGroup(group.first, minNumber, maxNumberLen, patterns.size)
        }

        val imagePatterns = fileNames.map { ImageHandler.toFileDetails(it).imagePattern }
        val groups = imagePatterns.mapNotNull { full(it) }.groupBy { it.prefix }.toList()
        val prefixGroups = groups.map { prefixGroup(it) }.sortedBy { it.prefix }.sortedBy { it.count }

        return if (prefixGroups.isEmpty()) ImagePattern.Full(defaultPrefix, numberLen = defaultNumberLen, 0 )
        else {
            val pg = prefixGroups[0]
            ImagePattern.Full(pg.prefix, numberLen = pg.maxNumberLen, pg.minNumber )
        }
    }

}