fun main() {

    fun String.replaceDigits() =
        replace("one", "o1e")
            .replace("two", "t2o")
            .replace("three", "t3e")
            .replace("four", "f4r")
            .replace("five", "f5e")
            .replace("six", "s6x")
            .replace("seven", "s7n")
            .replace("eight", "e8h")
            .replace("nine", "n9e")

    fun String.firstAndLastDigits(): Int {
        val first = first { it.isDigit() }
        val last = last { it.isDigit() }
        return ("$first$last").toInt()
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { it.firstAndLastDigits() }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line ->
            line.replaceDigits().run { firstAndLastDigits() }
        }
    }

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()

}
