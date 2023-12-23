import kotlin.time.measureTime

fun main() {

    fun List<String>.printMaze() = forEach { line ->
        line.forEach { char ->
            val output = when (char) {
                '7' -> '┓'
                'L' -> '┗'
                'J' -> '┛'
                'F' -> '┏'
                '|' -> '┃'
                '-' -> '━'
                else -> char
            }
            print(output)
        }
        print('\n')
    }

    data class Tile(val x: Int, val y: Int, val char: Char) {

        constructor(x: Int, y: Int, input: List<String>) : this(x, y, input[y][x])

        private fun connectedPipesToStart(input: List<String>): Pair<Tile, Tile> {
            val (above, abovePossibilities) = input.getOrNull(y - 1)?.get(x) to arrayOf('|', '7', 'F')
            val (below, belowPossibilities) = input.getOrNull(y + 1)?.get(x) to arrayOf('|', 'L', 'J')
            val (left, leftPossibilities) = input[y].getOrNull(x - 1) to arrayOf('-', 'L', 'F')
            val (right, rightPossibilities) = input[y].getOrNull(x + 1) to arrayOf('-', '7', 'J')

            var firstNeighbor: Tile? = null
            if (above in abovePossibilities) {
                firstNeighbor = above?.let { Tile(x, y - 1, above) }
            }
            if (below in belowPossibilities) {
                val tile = below?.let { Tile(x, y + 1, it) }
                if (firstNeighbor != null && tile != null) {
                    return firstNeighbor to tile
                } else firstNeighbor = tile
            }
            if (left in leftPossibilities) {
                val tile = left?.let { Tile(x - 1, y, it) }
                if (firstNeighbor != null && tile != null) {
                    return firstNeighbor to tile
                } else firstNeighbor = tile
            }
            if (right in rightPossibilities) {
                val tile = right?.let { Tile(x + 1, y, it) }
                check(firstNeighbor != null && tile != null)
                return firstNeighbor to tile
            }
            error("No such combination")
        }

        fun neighbors(input: List<String>): Pair<Tile, Tile> {
            return when (char) {
                'S' -> connectedPipesToStart(input)
                '|' -> {
                    val north = Tile(x, y - 1, input)
                    val south = Tile(x, y + 1, input)
                    north to south
                }

                '-' -> {
                    val east = Tile(x - 1, y, input)
                    val west = Tile(x + 1, y, input)
                    east to west
                }

                'L' -> {
                    val north = Tile(x, y - 1, input)
                    val east = Tile(x + 1, y, input)
                    north to east
                }

                'J' -> {
                    val north = Tile(x, y - 1, input)
                    val west = Tile(x - 1, y, input)
                    north to west
                }

                '7' -> {
                    val south = Tile(x, y + 1, input)
                    val west = Tile(x - 1, y, input)
                    south to west
                }

                'F' -> {
                    val south = Tile(x, y + 1, input)
                    val east = Tile(x + 1, y, input)
                    south to east
                }

                else -> error("Unconnected pipe")
            }
        }
    }

    fun startingPoint(input: List<String>): Pair<Int, Int> {
        val startY = input.indexOfFirst { line -> line.contains('S') }
        val startX = input[startY].indexOf('S')
        return startX to startY
    }

    fun tubeLoop(startX: Int, startY: Int, input: List<String>): List<Tile> {
        var tile = Tile(startX, startY, input[startY][startX])
        val tubeLoop = mutableListOf(tile)
        var previous: Tile? = null
        var i = 0
        do {
            val (first, second) = tile.neighbors(input)
            tile = if (previous != first) first else second
            tubeLoop.add(tile)
            previous = tubeLoop[i]
            i++
        } while (tile.char != 'S')

        return tubeLoop
    }

    fun part1(input: List<String>): Int {
        val (x, y) = startingPoint(input)
        return tubeLoop(x, y, input).size / 2
    }

    // TODO: Missing solution for part 2
    fun part2(input: List<String>): Int {
        val (x, y) = startingPoint(input)
        val tubeLoop = tubeLoop(x, y, input)
        return tubeLoop.size
    }

    val input = readInput("Day10").also { it.printMaze() }
    measureTime { part1(input).println() }
        .also { println("Part 1: $it") }

    measureTime { part2(input).println() }
        .also { println("Part 2: $it") }
}