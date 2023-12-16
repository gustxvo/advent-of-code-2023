import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

typealias SourceDestinationMappers = Set<SourceDestinationMap>

data class SourceDestinationMap(val destinationStart: Long, val sourceStart: Long, val rangeLength: Long) {
    val sourceRange: LongRange = sourceStart..<sourceStart + rangeLength
}

data class Almanac(val seeds: List<LongRange>, val mappings: List<SourceDestinationMappers>) {

    val minimumLocation: Long = seeds.minOf { seedRange ->
        minimumLocationForSeedRange(seedRange)
    }

    private fun minimumLocationForSeedRange(seeds: LongRange): Long {
        return seeds.minOf { seed ->
            val soil = destinationTypeForSource(seed, mappings[0])
            val fertilizer = destinationTypeForSource(soil, mappings[1])
            val water = destinationTypeForSource(fertilizer, mappings[2])
            val light = destinationTypeForSource(water, mappings[3])
            val temperature = destinationTypeForSource(light, mappings[4])
            val humidity = destinationTypeForSource(temperature, mappings[5])
            val location = destinationTypeForSource(humidity, mappings[6])
            location
        }
    }

    private fun destinationTypeForSource(source: Long, category: SourceDestinationMappers): Long {
        val correspondingMap = category.firstOrNull { source in it.sourceRange }

        return correspondingMap?.run { destinationStart - sourceStart + source } ?: source
    }
}

// TODO: Optimize this code, it's currently horrible, memory hungry and slow, but it works
fun main() {

    fun parseInput(input: String): List<String> {
        val labelsRegex = "(.*:)".toRegex()
        return input.split(labelsRegex).filter(String::isNotBlank)
    }

    fun Almanac(seeds: List<LongRange>, categories: List<String>): Almanac {
        val mappings = categories.map { section ->
            val spaces = "\\s+".toRegex()
            section.split(spaces)
                .asSequence()
                .filter(String::isNotBlank)
                .map(String::toLong)
                .chunked(3)
                .map { (destinationStart, sourceStart, rangeLength) ->
                    SourceDestinationMap(
                        destinationStart = destinationStart,
                        sourceStart = sourceStart,
                        rangeLength = rangeLength,
                    )
                }
                .toSet()
        }
        return Almanac(seeds, mappings)
    }

    fun part1(input: String): Long {
        val almanac = parseInput(input).let { sections ->
            val seeds = sections.first().trim().split(" ").map { it.toLong()..it.toLong() }
            val steps = sections.subList(1, sections.size)

            Almanac(seeds, steps)
        }
        return almanac.minimumLocation
    }

    fun part2(input: String): Long {
        val almanac = parseInput(input).let { sections ->
            val seeds = sections.first()
                .trim()
                .split(" ")
                .map(String::toLong)
                .chunked(2)
                .map { (rangeStart, rangeLength) ->
                    rangeStart..rangeStart + rangeLength
                }

            val steps = sections.subList(1, sections.size)

            Almanac(seeds, steps)
        }
        return almanac.minimumLocation
    }

    val input = readFile("Day05")

    measureTimeMillis { part1(input).println() }
        .also { println("Part 1: ${it}ms") } // Part 1 is fine, average of 75ms

    val part2 = measureTime { part2(input).println() }
    part2.toComponents { minutes, seconds, _ ->
        println("Part 2: ${minutes}min and ${seconds}s")
    } // Please don't run this, it will take a while, average of 13min

}

fun readFile(name: String) = Path("src/$name.txt").readText()
