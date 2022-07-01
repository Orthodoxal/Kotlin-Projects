package indigo

object ComputerStrategy {
    private var tableCard: String? = null
    private var cards: List<String> = listOf()

    private fun getSuitAndRankByCard(card: String): Pair<String, Char> {
        val suit = card.last()
        val rank = card.substringBefore(suit)
        return Pair(rank, suit)
    }

    private fun hasCandidates(candidates: List<String>): String {
        val tableCardParams = getSuitAndRankByCard(tableCard!!)
        val listSuitCandidates = mutableListOf<String>()
        for (candidate in candidates) {
            val candidateParams = getSuitAndRankByCard(candidate)
            if (candidateParams.second == tableCardParams.second) {
                listSuitCandidates.add(candidate)
            }
        }
        if (listSuitCandidates.size > 1) {
            return listSuitCandidates.random()
        } else {
            var listRankCandidates = listOf<String>()
            for (candidate in candidates) {
                val tempRankCandidates = mutableListOf(candidate)
                val candidateParam = getSuitAndRankByCard(candidate).first
                for (candidate2 in candidates) {
                    if (candidate != candidate2) {
                        val candidate2Param = getSuitAndRankByCard(candidate2).first
                        if (candidateParam == candidate2Param) {
                            tempRankCandidates.add(candidate2)
                        }
                    }
                }
                if (listRankCandidates.isEmpty() || listRankCandidates.size < tempRankCandidates.size) {
                    listRankCandidates = tempRankCandidates
                }
            }
            return if (listRankCandidates.size > 1) {
                listRankCandidates.random()
            } else {
                candidates.random()
            }
        }
    }

    private fun noCardOnTable(): String {
        val coincidences = mutableMapOf<Char, MutableList<String>>()
        for (card in cards) {
            val cardParams = getSuitAndRankByCard(card)
            val suit = cardParams.second
            if (coincidences.contains(suit)) {
                coincidences[suit]?.add(card)
            } else {
                coincidences[suit] = mutableListOf(card)
            }
        }
        val sorted = coincidences.entries.sortedBy { -1 * it.value.size }
        var listRankCandidates = listOf<String>()
        for (candidate in cards) {
            val tempRankCandidates = mutableListOf(candidate)
            val candidateParam = getSuitAndRankByCard(candidate).first
            for (candidate2 in cards) {
                if (candidate != candidate2) {
                    val candidate2Param = getSuitAndRankByCard(candidate2).first
                    if (candidateParam == candidate2Param) {
                        tempRankCandidates.add(candidate2)
                    }
                }
            }
            if (listRankCandidates.isEmpty() || listRankCandidates.size < tempRankCandidates.size) {
                listRankCandidates = tempRankCandidates
            }
        }
        return if (sorted[0].value.size >= listRankCandidates.size) sorted[0].value.random()
        else listRankCandidates.random()
    }

    private fun getCandidates(): List<String> {
        val list = mutableListOf<String>()
        if (tableCard != null) {
            val tableCardParams = getSuitAndRankByCard(tableCard!!)
            for (card in cards) {
                val cardParams = getSuitAndRankByCard(card)
                if (tableCardParams.first == cardParams.first
                    || tableCardParams.second == cardParams.second
                ) {
                    list.add(card)
                }
            }
        }
        return list
    }

    fun getCardByStrategy(tableCard: String?, cards: List<String>): String {
        this.tableCard = tableCard
        this.cards = cards
        val candidates = getCandidates()
        return when {
            (tableCard == null || candidates.isEmpty()) -> noCardOnTable()
            (candidates.size == 1) -> candidates[0]
            else -> hasCandidates(candidates)
        }
    }
}