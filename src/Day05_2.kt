import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

data class MappingRange(val destinationStart: Long, val sourceStart: Long, val rangeLength: Long) {
    val sourceRange: LongRange = sourceStart..<sourceStart + rangeLength
}

data class CategoryStep(val source: String, val destination: String, val mappingRanges: List<MappingRange>)

data class AlmanacDS(val seeds: List<Long>, val categorySteps: List<CategoryStep>)

fun main() {

    fun parseInput(input: List<String>): AlmanacDS {
        val seeds = input.first().substringAfter(": ").split(" ").map(String::toLong)

        val categorySteps = mutableListOf<CategoryStep>()
        input.filter(String::isNotBlank)
            .drop(1)
            .forEach { line ->
                if (line.endsWith("map:")) {
                    val (source, destination) = line.split("-to-", " ")
                    CategoryStep(source, destination, emptyList()).let { categorySteps.add(it) }
                } else {
                    val (destinationStart, sourceStart, rangeLength) = line.split(" ").map(String::toLong)
                    val mappingRange = MappingRange(destinationStart, sourceStart, rangeLength)

                    val currentIndex = categorySteps.lastIndex
                    val currentStep = categorySteps[currentIndex]
                    val updatedMappings = currentStep.mappingRanges + mappingRange
                    categorySteps[currentIndex] = currentStep.copy(mappingRanges = updatedMappings)
                }
            }
        return AlmanacDS(seeds, categorySteps)
    }

    fun destinationForSource(source: Long, step: CategoryStep): Long {
        val sourceDestinationMap = step.mappingRanges.firstOrNull { source in it.sourceRange } ?: return source
        return sourceDestinationMap.run { destinationStart - sourceStart + source }
    }

    fun locationForSource(seed: Long, categorySteps: List<CategoryStep>): Long {
        var source = seed
        var destination: Long? = null

        for (step in categorySteps) {
            destination = destinationForSource(source, step)
            source = destination
        }
        return (destination ?: source)
    }

    /*
     * Each seed range is executed in a coroutine, so each range is executed in parallel.
     * This means that this method will take approximately the time of the largest seed range to execute.
     * It's an optimization for this brute force solution (where each seed of each range is processed).
     */
    fun minimumLocationForSeedRanges(seedRanges: List<LongRange>, categorySteps: List<CategoryStep>): Long {
        return runBlocking(Dispatchers.Default) {
            seedRanges.map { range ->
                async {
                    range.minOf { seed -> locationForSource(seed, categorySteps) }
                }
            }.awaitAll().min()
        }
    }

    fun part1(input: List<String>): Long {
        val (seeds, categorySteps) = parseInput(input)

        return seeds.minOf { seed -> locationForSource(seed, categorySteps) }
    }

    fun part2(input: List<String>): Long {
        val (seeds, categorySteps) = parseInput(input)

        val seedRanges = seeds.chunked(2).map { (start, length) -> (start..<start + length) }
        return minimumLocationForSeedRanges(seedRanges, categorySteps)
    }

    val input = readInput("Day05")
    measureTimeMillis { part1(input).println() }
        .also { println("Part 1: ${it}ms") } // Part 1 is better than my first solution, average of 3ms

    val part2 = measureTime { part2(input).println() }
    part2.toComponents { minutes, seconds, _ ->
        println("Part 2: ${minutes}min and ${seconds}s")
    } // Not ideal, but better than first my solution, average of 2min

}
