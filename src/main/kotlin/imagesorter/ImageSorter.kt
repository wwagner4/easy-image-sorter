package imagesorter

data class Rename(
    val srcName: String,
    val destName: String
)

object ImageSorter {

    fun renamings(originalOrder: List<String>, newOrder: List<String>): List<Rename> {
        val commonImagePattern = commonImagePattern(newOrder)

        val extensions = originalOrder.map { ImageHandler.toFileDetails(it).extension }
        val newFileNames: List<String> = newNames(commonImagePattern, extensions)
        val originalNameMap: Map<String, String> = originalOrder.zip(newFileNames).toMap()

        val newOrderRenamed = newOrder.map {
            originalNameMap.getValue(it)
        }
        return originalOrder.zip(newOrderRenamed).map { (a, b) -> Rename(a, b) }
    }

    fun newNames(imagePattern: ImagePattern.Full, prefixes: List<String>): List<String> {
        val fmtStr = "%0${imagePattern.numberLen}d"
        return prefixes.withIndex().map {
            val index = it.index
            val prefix = it.value
            val indexStr = fmtStr.format(index + imagePattern.number)
            "${imagePattern.prefix}$indexStr.$prefix"
        }
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

        fun full(imagePattern: ImagePattern): ImagePattern.Full? {
            return if (imagePattern is ImagePattern.Full) imagePattern else null
        }

        fun prefixGroup(group: Pair<String, List<ImagePattern.Full>>): PrefixGroup {
            val patterns = group.second
            val minNumber = patterns.map { it.number }.minOrNull().let { it ?: 0 }
            val maxNumberLen = patterns.map { it.numberLen }.maxOrNull().let { it ?: 5 }
            return PrefixGroup(group.first, minNumber, maxNumberLen, patterns.size)
        }

        val imagePatterns =
            fileNames.filter { ImageHandler.isImageFile(it) }.map { ImageHandler.toFileDetails(it).imagePattern }
        val groups = imagePatterns.mapNotNull { full(it) }.groupBy { it.prefix }.toList()
        val prefixGroups = groups.map { prefixGroup(it) }.sortedBy { it.prefix }.sortedBy { -it.count }

        return if (prefixGroups.isEmpty()) ImagePattern.Full(defaultPrefix, numberLen = defaultNumberLen, 0)
        else {
            val pg = prefixGroups[0]
            ImagePattern.Full(pg.prefix, numberLen = pg.maxNumberLen, pg.minNumber)
        }
    }

}