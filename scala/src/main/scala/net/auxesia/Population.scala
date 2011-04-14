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
import scala.collection.immutable.List
import scala.math.round
import scala.util.Random

class Population private (private var _population: List[Chromosome], val crossover: Float, val elitism: Float, val mutation: Float) {
	def population = _population
	
	def evolve(): Unit = {
		// Create a buffer for the new generation
		val popSize = _population.length
		val elitismCount = round(popSize * elitism)
		var buffer = _population.take(elitismCount)
		val rest = _population.takeRight(popSize - elitismCount)
		
		def randomMutate(ch: Chromosome) = {
			if (Random.nextFloat() <= mutation) ch.mutate() else ch
		}
		
		for (ch <- rest) {
			if (Random.nextFloat() <= crossover) {
				// Select the parents and mate to get their children
				val parents  = selectParents
				val children = parents(0).mate(parents(1))
				
				buffer = randomMutate(children(0)) :: buffer
				buffer = randomMutate(children(1)) :: buffer				
			} else {
				buffer = randomMutate(ch) :: buffer
			}
		}

		_population = buffer.sortWith((s, t) => s.fitness < t.fitness).take(popSize)
	}
	
	private def selectParents(): Array[Chromosome] = {
		val parents = new Array[Chromosome](2)

		// Randomly select two parents via tournament selection.
		for (i <- 0 to 1) {
			parents(i) = _population(Random.nextInt(_population.length))
			for (j <- 1 to 3) {
				val idx = Random.nextInt(_population.length)
				if (_population(idx).fitness < parents(i).fitness) {
					parents(i) = _population(idx)
				}
			}
		}
		
		return parents
	}
}

object Population {
	def apply(size: Int, crossover: Float, elitism: Float, mutation: Float) = {
		new Population(generateInitialPopulation(size), crossover, elitism, mutation)
	}
	
	private def generateInitialPopulation(size: Int): List[Chromosome] = {
		var pop = List[Chromosome]()
		for (i <- 1 to size) {
			pop = Chromosome.generateRandom :: pop
		}

		return pop.sortWith((s, t) => s.fitness < t.fitness)
	}
}