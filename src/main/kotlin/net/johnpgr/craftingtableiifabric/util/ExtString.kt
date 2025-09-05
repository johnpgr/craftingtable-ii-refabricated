package net.johnpgr.craftingtableiifabric.util

fun String.breakLines(maxLength: Int = 36): List<String> {
    val chunks: MutableList<String> = mutableListOf()
    if (this.isEmpty()) return chunks

    val sentences = this.split(". ")
    for (sentence in sentences) {
        val words = sentence.split(" ")
        var chunk = ""

        for (word in words) {
            if (chunk.length + word.length + 1 > maxLength) { // +1 to account for the period
                chunks.add(chunk)
                chunk = ""
            }
            chunk += "$word "
        }

        if (chunk.isNotBlank()) {
            chunks.add(chunk.trim() + ".") // add the period at the end of each chunk
        }
    }

    val last = chunks[chunks.size - 1]
    chunks[chunks.size - 1] = last.substring(
        0,
        last.length - 1
    ) // remove the period from the last chunk

    return chunks
}
