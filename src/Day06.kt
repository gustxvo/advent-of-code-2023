import kotlin.system.measureTimeMillis

fun main() {

    fun parseInput(input: List<String>): List<Pair<Int, Int>> {
        val whitespaces = "\\s+".toRegex()
        val (time, distances) = input.map { line ->
            line.substringAfter(":").trim().split(whitespaces).map(String::toInt)
        }
        return time.zip(distances)
    }

    fun winningPossibilities(totalTime: Long, distanceRecord: Long): Int {
        var equalPair = 0
        val winningPairs = (1..totalTime / 2).count { timeUsed ->
            val speed = totalTime - timeUsed
            if (speed == timeUsed) equalPair++
            timeUsed * speed > distanceRecord
        }
        return (winningPairs * 2 - equalPair)
    }

    fun part1(input: List<String>): Int {
        return parseInput(input).map { (totalTime, distanceRecord) ->
            winningPossibilities(totalTime.toLong(), distanceRecord.toLong())
        }.reduce { acc, possibilities -> acc * possibilities }
    }

    fun part2(input: List<String>): Int {
        val (totalTime, distanceRecord) = input.map { line ->
            line.substringAfter(":")
                .replace(" ", "")
                .toLong()
        }
        return winningPossibilities(totalTime, distanceRecord)
    }

    val input = readInput("Day06")
    measureTimeMillis { part1(input).println() }
        .also { println("Part 1: ${it}ms") }

    measureTimeMillis { part2(input).println() }
        .also { println("Part 2: ${it}ms") }

}