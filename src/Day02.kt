fun main() {

    val colors = mapOf("red" to 12, "green" to 13, "blue" to 14)

    fun parseLineInput(line: String): List<String> {
        return line.substringAfter(": ")
            .split("; ")
            .flatMap { it.split(", ") }
    }

    fun maxNumberOfEachColor(quantityPerColor: List<String>): Int = quantityPerColor.maxOf { color ->
        color.takeWhile(Char::isDigit).toInt()
    }

    fun part1(input: List<String>): Int {
        return input
            .filterNot { line ->
                parseLineInput(line)
                    .map { gameSet ->
                        val number = gameSet.takeWhile(Char::isDigit).toInt()
                        val color = gameSet.substringAfter(" ")

                        colors.getOrDefault(color, 0) >= number
                    }
                    .contains(false)
            }.sumOf { line ->
                line.substringBefore(":")
                    .replace("Game ", "")
                    .toInt()
            }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line ->
            parseLineInput(line)
                .groupBy { gameSet -> gameSet.substringAfter(" ") }
                .map { (_, quantityPerColor) -> maxNumberOfEachColor(quantityPerColor) }
                .reduce { acc, number -> acc * number }
        }
    }

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()

}
