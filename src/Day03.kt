import kotlin.system.measureTimeMillis

typealias Coordinates = Pair<Int, Int>

fun main() {

    fun mapNumbersToCoordinates(input: List<String>): Map<Int, List<Coordinates>> {
        val result = mutableMapOf<Int, MutableList<Pair<Int, Int>>>()

        for (i in input.indices) {
            val line = input[i].trim()
            var currentGroup = ""
            var currentGroupIndices = mutableListOf<Pair<Int, Int>>()

            for (j in line.indices) {
                val char = line[j]
                if (char.isDigit()) {
                    currentGroup += char
                    currentGroupIndices.add(Pair(i, j))
                } else {
                    if (currentGroup.isNotEmpty()) {
                        result.merge(currentGroup.toInt(), currentGroupIndices) { oldList, newList ->
                            oldList.apply { addAll(newList) }
                        }
                        currentGroup = ""
                        currentGroupIndices = mutableListOf()
                    }
                }
            }

            if (currentGroup.isNotEmpty()) {
                result.merge(currentGroup.toInt(), currentGroupIndices) { oldList, newList ->
                    oldList.apply { addAll(newList) }
                }
            }
        }

        return result
    }

    fun findAdjacentNumbers(
        mappedNumbers: Map<Int, List<Coordinates>>,
        input: List<String>,
        symbolPosition: Coordinates
    ): Set<Int> {
        val (row, column) = symbolPosition
        val numberCoordinates = mutableSetOf<Pair<Int, Int>>()

        for (rowIndex in row - 1..row + 1) {
            for (columnIndex in column - 1..column + 1) {
                val char = input[rowIndex][columnIndex]
                if (char.isDigit()) {
                    numberCoordinates.add(rowIndex to columnIndex)
                }
            }
        }

        return numberCoordinates.map { coordinates ->
            mappedNumbers.entries.find { entry -> entry.value.any { it == coordinates } }?.key ?: 0
        }.toSet()
    }

    fun part1(input: List<String>): Int {
        val numbers = mapNumbersToCoordinates(input)

        val symbolPositions = input.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { columnIndex, char ->
                val regex = """[^\d(.)]""".toRegex()
                if (regex.matches(char.toString())) {
                    rowIndex to columnIndex
                } else null
            }
        }
        return symbolPositions.flatMap { findAdjacentNumbers(numbers, input, it) }.sum()
    }

    fun part2(input: List<String>): Long {
        val numbers = mapNumbersToCoordinates(input)

        val symbolPositions = input.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { columnIndex, char ->
                if (char == '*') {
                    rowIndex to columnIndex
                } else null
            }
        }

        return symbolPositions.map { findAdjacentNumbers(numbers, input, it) }
            .filter { it.size == 2 }
            .sumOf { partNumbers -> partNumbers.reduce { acc, element -> acc * element }.toLong() }
    }

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()

}
