package imagesorter

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

    fun toImagePattern(imageFileName: String): ImagePattern {
        return try {
            val match = imageRegex.find(imageFileName)!!
            val (prefix, numbers) = match.destructured
            ImagePattern.Full(prefix, numbers.length, numbers.toInt())
        } catch (_: Exception) {
            ImagePattern.Invalid
        }
    }

    fun isImageFile(imageFileName: String): Boolean {
        val index = imageFileName.lastIndexOf(".")
        if (index < 0) return false
        return when (imageFileName.substring(index + 1).lowercase()) {
            "jpg" -> true
            "jpeg" -> true
            "png" -> true
            else -> false
        }
    }
}


