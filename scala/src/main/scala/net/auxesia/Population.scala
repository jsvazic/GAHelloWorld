/*
 * The MIT License
 * 
 * Copyright (c) 2011 John Svazic
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.auxesia

import scala.collection.Iterator
import scala.collection.immutable.Vector
import scala.math.round
import scala.util.Random

/**
 * Class defining a genetic algorithm population for the "Hello, world!" 
 * simulation.
 * 
 * @author John Svazic
 * @constructor Create a new population with an initial population defined, 
 * along with specific crossover, elitism and mutation rates.
 * @param _population The vector of [[net.auxesia.Chromosome]] objects 
 * representing the population.
 * @param crossover The crossover ratio.
 * @param elitism The elitism ratio.
 * @param mutation The mutation ratio.
 */
class Population private (private var _population: Vector[Chromosome], val crossover: Float, val elitism: Float, val mutation: Float) {
	/**
	 * A public accessor for the underlying vector of [[net.auxesia.Chromosome]]
	 * objects.
	 */
	def population = _population
	
	/**
	 * Method used to evolve a new generation for the population.  This method
	 * modifies the internal population represented by this class.
	 */
	def evolve = {
		// Create a buffer for the new generation
		val popSize = _population.length
		val elitismCount = round(popSize * elitism)
		var buffer = _population.take(elitismCount)
		val rest = _population.takeRight(popSize - elitismCount)
		
		def randomMutate(ch: Chromosome): Chromosome = {
			if (Random.nextFloat() <= mutation) ch.mutate else ch
		}
		
		def selectParents: Array[Chromosome] = {
			val tournamentSize = 3
			val parents = new Array[Chromosome](2)
	
			// Randomly select two parents via tournament selection.
			for (i <- 0 to 1) {
				parents(i) = _population(Random.nextInt(_population.length))
				for (j <- 1 to tournamentSize) {
					val idx = Random.nextInt(_population.length)
					if (_population(idx).fitness < parents(i).fitness) {
						parents(i) = _population(idx)
					}
				}
			}
			
			return parents			
		}
		
		for (ch <- rest) {
			if (Random.nextFloat() <= crossover) {
				// Select the parents and mate to get their children
				val parents  = selectParents
				val children = parents(0).mate(parents(1))
				
				buffer = randomMutate(children(0)) +: buffer
				buffer = randomMutate(children(1)) +: buffer				
			} else {
				buffer = randomMutate(ch) +: buffer
			}
		}

		_population = buffer.sortWith((s, t) => s.fitness < t.fitness).take(popSize)
	}	
}

/**
 * Factory for [[net.auxesia.Population]] instances.
 */
object Population {
	/**
	 * Create a [[net.auxesia.Population]] with a given size, crossover ratio, elitism ratio
	 * and mutation ratio.
	 * 
	 * @param size The size of the population.
	 * @param crossover The crossover ratio for the population.
	 * @param elitism The elitism ratio for the population.
	 * @param mutation The mutation ratio for the population.
	 * 
	 * @return A new [[net.auxesia.Population]] instance with the defined
	 * parameters and an initialized set of [[net.auxesia.Chromosome]] objects
	 * representing the population.
	 */
	def apply(size: Int, crossover: Float, elitism: Float, mutation: Float) = {
		new Population(generateInitialPopulation(size), crossover, elitism, mutation)
	}
	
	/**
	 * Helper method used to generate an initial population of random
	 * [[net.auxesia.Chromosome]] objects for a given population size.
	 * 
	 * @param size The size of the population.
	 * 
	 * @return A [[scala.collection.immutable.List]] of the defined size
	 * populated with random [[net.auxesia.Chromosome]] objects.
	 */
	private def generateInitialPopulation(size: Int): Vector[Chromosome] = {
		var pop = Vector[Chromosome]()
		for (i <- 1 to size) {
			pop = Chromosome.generateRandom +: pop
		}

		pop.sortWith((s, t) => s.fitness < t.fitness)
	}
}