package imagesorter

object ImageHandler {

    private val imageRegex by lazy { Regex("(\\w.*?)([0-9].*)\\..*") }

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

    fun toFileDetails(imageFileName: String): FileDetails {
        val imagePattern =  try {
            val match = imageRegex.find(imageFileName)!!
            val (prefix, numbers) = match.destructured
            ImagePattern.Full(prefix, numbers.length, numbers.toInt())
        } catch (_: Exception) {
            ImagePattern.Invalid
        }
        val index = imageFileName.lastIndexOf(".")
        val extension = imageFileName.substring(index + 1)
        val base = imageFileName.substring(0, index)
        return FileDetails(imageFileName, base, extension, imagePattern)
    }

    fun isImageFile(imageFileName: String): Boolean {
        val index = imageFileName.lastIndexOf(".")
        if (index < 1) return false
        return when (imageFileName.substring(index + 1).lowercase()) {
            "jpg" -> true
            "jpeg" -> true
            "png" -> true
            else -> false
        }
    }
}


