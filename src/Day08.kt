import java.math.BigInteger
import kotlin.time.measureTime

private typealias DirectionMappings = Map<String, DirectionMapping>

private typealias DirectionMapping = Pair<String, String>

fun main() {

    data class InstructionMappings(val instructions: String, val mappings: DirectionMappings)

    fun parseInput(input: List<String>): InstructionMappings {
        val directionsRegex = """(\w+) = .(\w+), (\w+).""".toRegex()
        val instructions = input.first()
        val directionMappings = input.drop(2).associate { line ->
            val (direction, left, right) = directionsRegex.matchEntire(line)?.destructured ?: error("Invalid input")
            direction to DirectionMapping(left, right)
        }
        return InstructionMappings(instructions, directionMappings)
    }

    fun part1(input: List<String>): Int {
        val (instructions, directionMappings) = parseInput(input)

        var direction = "AAA"
        var steps = 0
        while (direction != "ZZZ") {
            val (left, right) = directionMappings.getValue(direction)
            val index = steps % instructions.length
            direction = if (instructions[index] == 'L') left else right

            steps++
        }
        return steps
    }

    /**
     * Returns the least common multiple LCM of elements in this list
     */
    fun List<Long>.lcm(): BigInteger = map(BigInteger::valueOf).reduce { acc, number -> acc * number / acc.gcd(number) }

    fun part2(input: List<String>): BigInteger {
        val (instructions, directionMappings) = parseInput(input)
        val targetDirections = directionMappings.filterKeys { it.endsWith("A") }.keys

        val steps = targetDirections.map { targetDirection ->
            var i = 0
            var direction = targetDirection
            while (!direction.endsWith("Z")) {
                val (left, right) = directionMappings.getValue(direction)
                val index = i % instructions.length
                direction = if (instructions[index] == 'L') left else right
                i++
            }
            i.toLong()
        }

        return steps.lcm()
    }

    val input = readInput("Day08")
    measureTime { part1(input).println() }
        .also { println("Part 1: $it") }

    measureTime { part2(input).println() }
        .also { println("Part 2: $it") }

}
