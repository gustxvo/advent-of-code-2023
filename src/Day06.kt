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
        return (1..<totalTime).count { timeUsed ->
            val speed = totalTime - timeUsed
            timeUsed * speed > distanceRecord
        }
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
    part1(input).println()
    part2(input).println()

}