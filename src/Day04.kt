import kotlin.math.pow

fun main() {

    data class Card(val cardNumber: Int, val numberOfMatches: Int, val count: Int = 1)

    infix fun Int.powerOf(i: Int): Int {
        return toDouble().pow(i).toInt()
    }

    fun parseCardInput(line: String): Card {
        val cardNumber = line.substringAfter("Card")
            .replace(" ", "")
            .takeWhile(Char::isDigit)
            .toInt()

        val regex = "\\s+".toRegex()
        val (winningNumbers, yourNumbers) = line.substringAfter(":")
            .split("|")
            .map { section -> section.trim().split(regex).map(String::toInt).toSet() }

        val matchesCount = winningNumbers.intersect(yourNumbers).count()

        return Card(cardNumber, matchesCount)
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            parseCardInput(line).run { 2 powerOf numberOfMatches - 1 }
        }
    }

    fun part2(input: List<String>): Int {
        val cards = input.map { line -> parseCardInput(line) }.toMutableList()
        cards.mapIndexed { index, card ->
            for (i in 1..card.numberOfMatches) {
                val nextIndex = index + i
                val nextCard = cards[nextIndex]
                cards[nextIndex] = nextCard.copy(count = nextCard.count + 1 * card.count)
            }
        }

        return cards.sumOf { it.count }
    }

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()

}
