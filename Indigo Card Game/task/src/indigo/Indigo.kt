package indigo

import java.util.*
import kotlin.system.exitProcess

val ranks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
val scoreRanks = listOf("10", "J", "Q", "K", "A")
val suits = listOf('♦', '♥', '♠', '♣')

class Indigo {
    private var deck = Stack<String>()
    private var playedCards = Stack<String>()
    private var playerCards = mutableListOf<String>()
    private var computerCards = mutableListOf<String>()
    private var countPlayedCards = 4
    private var playerScore = 0
    private var computerScore = 0
    private var playerWinCards = 0
    private var computerWinCards = 0
    private var lastWinner = ""

    private fun shuffleDeck(isAllGenerate: Boolean = false) {
        val currentDeck = if (isAllGenerate) {
            allDeckCards()
        } else {
            deck.toMutableList()
        }
        deck.clear()
        for (index in 0 until currentDeck.size) {
            val newIndex = (0 until currentDeck.lastIndex).random()
            if (newIndex != index) {
                val temp = currentDeck[newIndex]
                currentDeck[newIndex] = currentDeck[index]
                currentDeck[index] = temp
            }
        }
        for (card in currentDeck) {
            deck.add(card)
        }
    }

    private fun generateDeck(mode: GENERATE_MODE): String {
        return when (mode) {
            GENERATE_MODE.RESET -> {
                shuffleDeck(true)
                "Card deck is reset."
            }
            GENERATE_MODE.SHUFFLE -> {
                shuffleDeck()
                "Card deck is shuffled."
            }
        }
    }

    private fun allDeckCards(): MutableList<String> {
        val allCards = mutableListOf<String>()
        for (suit in suits) {
            for (rank in ranks) {
                allCards.add("$rank$suit")
            }
        }
        return allCards
    }

    private fun getCards(count: Int = 4): List<String> {
        return buildList {
            for (index in 1 until count + 1) {
                this.add(deck.pop())
            }
        }
    }

    private fun getSuitAndRankByCard(card: String): Pair<String, Char> {
        val suit = card.last()
        val rank = card.substringBefore(suit)
        return Pair(rank, suit)
    }

    private fun calculateScoreAndCards(): Pair<Int, Int> {
        var score = 0
        val cards = playedCards.size
        while (playedCards.isNotEmpty()) {
            val currentCard = getSuitAndRankByCard(playedCards.pop())
            if (scoreRanks.contains(currentCard.first)) {
                score++
            }
        }
        return Pair(score, cards)
    }

    private fun getWinnerPoints(playerFirst: Boolean) {
        if (playerWinCards == 0 && computerWinCards == 0) {
            if (playerFirst) {
                playerScore = 23
                playerWinCards = 52
            } else {
                computerScore = 23
                computerWinCards = 52
            }
        } else if (playedCards.isEmpty()) {
            showScoresAndCards()
            println("\nNo cards on the table")
            if (playerWinCards > computerWinCards) {
                playerScore += 3
            } else if (playerWinCards < computerWinCards) {
                computerScore += 3
            } else {
                if (playerFirst) playerScore += 3 else computerScore += 3
            }
            showScoresAndCards()
        } else {
            val plCards = playedCards.size
            val card = playedCards.peek()
            if (lastWinner == "Player") {
                getPoints("Player")
            } else getPoints("Computer")
            if (playerWinCards > computerWinCards) {
                playerScore += 3
            } else if (playerWinCards < computerWinCards) {
                computerScore += 3
            } else {
                if (playerFirst) playerScore += 3 else computerScore += 3
            }
            println("\n$plCards cards on the table, and the top card is $card")
            showScoresAndCards()
        }
    }

    private fun getPoints(playerName: String) {
        val winScoreAndCards = calculateScoreAndCards()
        if (playerName == "Player") {
            playerScore += winScoreAndCards.first
            playerWinCards += winScoreAndCards.second
        } else {
            computerScore += winScoreAndCards.first
            computerWinCards += winScoreAndCards.second
        }
    }

    private fun showScoresAndCards() {
        println("Score: Player $playerScore - Computer $computerScore")
        println("Cards: Player $playerWinCards - Computer $computerWinCards")
    }

    private fun showWinMenu(playerName: String) {
        println("$playerName wins cards")
        getPoints(playerName)
        if (playerCards.isNotEmpty() || computerCards.isNotEmpty() || deck.isNotEmpty()) {
            showScoresAndCards()
        }
        lastWinner = playerName
    }

    private fun isWinnerStep(card: String): Boolean {
        if (playedCards.isEmpty()) {
            playedCards.add(card)
            return false
        }
        // проверка масти и ранга
        val currentCard = getSuitAndRankByCard(card)
        val lastCard = getSuitAndRankByCard(playedCards.peek())
        playedCards.add(card)
        return currentCard.first == lastCard.first || currentCard.second == lastCard.second

    }

    private fun stepPlayer() {
        println(
            if (playedCards.isNotEmpty()) {
                "\n${playedCards.size} cards on the table, and the top card is ${playedCards.peek()}"
            } else "\nNo cards on the table"
        )
        if (playerCards.isEmpty()) {
            //инициализция карт игрока
            playerCards = getCards(6).toMutableList()
        }
        println(
            "Cards in hand: ${
                playerCards.joinToString(" ") {
                    (playerCards.indexOf(it) + 1).toString() + ")" + it
                }
            }"
        )
        var number: Int
        while (true) {
            println("Choose a card to play (1-${playerCards.size}):")
            try {
                val input = readln()
                if (input == "exit") {
                    println("Game Over")
                    exitProcess(1)
                }
                number = input.toInt()
                if (number in 1..playerCards.size) {
                    if (isWinnerStep(playerCards.removeAt(number - 1))) {
                        showWinMenu("Player")
                    }
                    return
                }
            } catch (_: Exception) {

            }
        }
    }

    private fun stepAI() {
        println(
            if (playedCards.isNotEmpty()) {
                "\n${playedCards.size} cards on the table, and the top card is ${playedCards.peek()}"
            } else "\nNo cards on the table"
        )
        if (computerCards.isEmpty()) {
            //инициализация карт компьютера
            computerCards = getCards(6).toMutableList()
        }
        println(computerCards.joinToString(" "))
        val playedCard = ComputerStrategy.getCardByStrategy(
            if (playedCards.isNotEmpty()) playedCards.peek() else null,
            computerCards
        )
        computerCards.remove(playedCard)
        println("Computer plays $playedCard")
        if (isWinnerStep(playedCard)) {
            showWinMenu("Computer")
        }
    }

    private fun isPlayerFirstQuestion(): Boolean {
        while (true) {
            println("Play first?")
            when (readln().lowercase()) {
                "yes" -> return true
                "no" -> return false
                "exit" -> {
                    println("Game Over")
                    exitProcess(1)
                }
            }
        }
    }

    fun startGame() {
        println("Indigo Card Game")
        //инициализация использованных карт
        val initialCards = getCards()
        playedCards.addAll(initialCards)
        if (isPlayerFirstQuestion()) {
            println("Initial cards on the table: ${initialCards.joinToString(" ")}")
            while (countPlayedCards < 52) {
                stepPlayer()
                stepAI()
                countPlayedCards += 2
            }
            getWinnerPoints(true)
        } else {
            println("Initial cards on the table: ${initialCards.joinToString(" ")}")
            while (countPlayedCards < 52) {
                stepAI()
                stepPlayer()
                countPlayedCards += 2
            }
            getWinnerPoints(false)
        }
        println("Game Over")
    }

    init {
        generateDeck(GENERATE_MODE.RESET)
    }
}