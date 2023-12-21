import kotlin.time.measureTime

fun main() {

    fun numberDifferences(numbers: List<Int>): List<Int> = numbers.zipWithNext { a, b -> b - a }

    fun nextNumberInSequence(numbers: List<Int>): Int {
        val result = mutableListOf(numbers.last())
        var numberDiffs = numbers
        do {
            numberDiffs = numberDifferences(numberDiffs)
            val allZeroes = numberDiffs.all { it == 0 }
            result.add(numberDiffs.last())
        } while (!allZeroes)

        return result.sum()
    }

    fun previousNumberInSequence(numbers: List<Int>): Int {
        val result = mutableListOf(numbers.first())
        var numberDiffs = numbers
        do {
            numberDiffs = numberDifferences(numberDiffs)
            val allZeroes = numberDiffs.all { it == 0 }
            result.add(numberDiffs.first())
        } while (!allZeroes)

        return result.reduceRight { i, acc -> i - acc }
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val numbers = line.split(' ').map(String::toInt)
            nextNumberInSequence(numbers)
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line ->
            val numbers = line.split(' ').map(String::toInt)
            previousNumberInSequence(numbers)
        }
    }

    val input = readInput("Day09")
    measureTime { part1(input).println() }
        .also { println("Part 1: $it") }

    measureTime { part2(input).println() }
        .also { println("Part 2: $it") }

}