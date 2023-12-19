import kotlin.system.measureTimeMillis

enum class HandType {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIRS,
    THREE_OF_KIND,
    FULL_HOUSE,
    FOUR_OF_KIND,
    FIVE_OF_KIND
}

fun main() {

    val cardMappings = mapOf('A' to 14, 'K' to 13, 'Q' to 12, 'J' to 11, 'T' to 10)

    data class Card(val card: Char, val weight: Int)

    data class CardHand(val handType: HandType, val cards: List<Card>) : Comparable<CardHand> {

        override fun compareTo(other: CardHand): Int {
            return handType.compareToOrNull(other.handType) ?: cards.compareTo(other.cards)
        }

        private operator fun List<Card>.compareTo(otherCards: List<Card>): Int {
            return this.zip(otherCards)
                .map { (card, otherCard) -> card.weight to otherCard.weight }
                .first { (card, otherCard) -> card != otherCard }
                .let { (card, otherCard) -> card.compareTo(otherCard) }
        }
    }

    data class GameRound(val cardHand: CardHand, val bidAmount: Long)

    fun HandType(cardOccurrences: Map<Char, Int>): HandType {
        val containsPair = cardOccurrences.containsValue(2)
        val handType = when (cardOccurrences.size) {
            1 -> HandType.FIVE_OF_KIND
            2 -> if (!containsPair) HandType.FOUR_OF_KIND else HandType.FULL_HOUSE
            3 -> if (!containsPair) HandType.THREE_OF_KIND else HandType.TWO_PAIRS
            4 -> HandType.ONE_PAIR
            5 -> HandType.HIGH_CARD
            else -> error("Unpredicted card size")
        }
        return handType
    }

    fun CardHand(cardsList: String): CardHand {
        val cards = cardsList.map { card -> Card(card = card, weight = cardMappings[card] ?: card.digitToInt()) }
        val cardOccurrences = cards.groupBy { it.card }.mapValues { (_, occurrences) -> occurrences.count() }

        return CardHand(handType = HandType(cardOccurrences), cards = cards)
    }

    fun CardHand(cardsList: String, jokers: Int): CardHand {
        val cards = cardsList.map { card ->
            val cardWeight = if (card == 'J') -1 else cardMappings[card]
            Card(card = card, weight = cardWeight ?: card.digitToInt())
        }
        val cardOccurrences = cards.groupBy { it.card }
            .mapValues { (_, occurrences) -> occurrences.count() }
            .toMutableMap()

        // Add Jokers to the highest occurrences of a card expect himself
        val cardWithMostOccurrences = cardOccurrences.filterNot { it.key == 'J' }.maxByOrNull { it.value }?.key
        if (cardWithMostOccurrences != null) {
            cardOccurrences[cardWithMostOccurrences] = cardOccurrences[cardWithMostOccurrences]?.plus(jokers) ?: 0
        }

        if (cardOccurrences['J'] != 5) cardOccurrences.remove('J') // Removes this key if not five Jokers

        return CardHand(handType = HandType(cardOccurrences), cards = cards)
    }

    fun totalWinnings(gameRounds: List<GameRound>): Long {
        return gameRounds.sortedBy(GameRound::cardHand)
            .mapIndexed { index, round ->
                val rank = index + 1
                rank * round.bidAmount
            }.sum()
    }

    fun createGame(input: List<String>, createGame: (String, Long) -> GameRound): List<GameRound> {
        return input.map { line ->
            val (cards, bidAmount) = line.split(' ')
            createGame(cards, bidAmount.toLong())
        }
    }

    fun part1(input: List<String>): Long {
        val gameRounds = createGame(input) { cards, bid ->
            GameRound(cardHand = CardHand(cards), bidAmount = bid)
        }
        return totalWinnings(gameRounds)
    }

    fun part2(input: List<String>): Long {
        val gameRounds = createGame(input) { cards, bid ->
            val jokers = cards.count { card -> card == 'J' }
            GameRound(cardHand = CardHand(cards, jokers), bidAmount = bid)
        }
        return totalWinnings(gameRounds)
    }

    val input = readInput("Day07")
    measureTimeMillis { part1(input).println() }
        .also { println("Part 1: ${it}ms") }

    measureTimeMillis { part2(input).println() }
        .also { println("Part 2: ${it}ms") }

}
