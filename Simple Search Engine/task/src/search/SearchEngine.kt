package search

class SearchEngine(private val peopleList: List<String>) {
    private val invertedIndex = getInvertedIndex(peopleList)

    private fun getInvertedIndex(list: List<String>) = list.withIndex()
        .flatMap { indexedLine ->
            indexedLine.value.trim().lowercase()
                .split(" ")
                .map { word -> IndexedValue(indexedLine.index, word) }
        }
        .groupBy({ it.value }, { it.index })

    fun getPeopleList() = peopleList.joinToString("\n", "\n=== List of people ===\n", "\n")

    private fun findAll(currentInvertedIndex: Map<String, List<Int>>, requestWords: MutableList<String>): List<String> {
        //recursive algorithm
        return if (requestWords.size > 1) {
            if (currentInvertedIndex.isNotEmpty()) {
                mutableListOf()
            } else {
                val newInvertedIndex =
                    getInvertedIndex(currentInvertedIndex[requestWords[0]]?.map { peopleList[it] }?.toList()
                        ?: mutableListOf()
                    )
                requestWords.removeFirst()
                findAll(newInvertedIndex, requestWords)
            }
        } else {
            currentInvertedIndex[requestWords[0]]?.map { peopleList[it] }?.toList() ?: mutableListOf()
        }
    }

    private fun findAny(requestWords: List<String>): List<String> {
        val result = mutableSetOf<Int>()
        requestWords.forEach { invertedIndex[it]?.forEach { index -> result.add(index) } }
        return result.map { peopleList[it] }
    }

    private fun findNone(requestWords: List<String>): List<String> {
        val foundList = findAny(requestWords)
        return peopleList.filter { !foundList.contains(it) }
    }

    fun findByWords(requestWords: List<String>, strategy: Strategy): List<String> {
        return when (strategy) {
            Strategy.ALL -> findAll(invertedIndex, requestWords.toMutableList())
            Strategy.ANY -> findAny(requestWords)
            else -> findNone(requestWords)
        }
    }
}