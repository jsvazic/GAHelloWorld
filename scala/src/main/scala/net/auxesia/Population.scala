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

import akka.actor.{Actor, PoisonPill}
import Actor._
import akka.routing.{Routing, CyclicIterator}
import Routing._
import akka.dispatch.Dispatchers

import scala.collection.Iterator
import scala.collection.immutable.Vector
import scala.math.round
import scala.util.Random

import java.util.concurrent.CountDownLatch

/** Trait defining an evolution message, used when evolving a population. */
sealed trait EvolutionMessage

case object Done extends EvolutionMessage

case class Mutate(chromosome: Chromosome) extends EvolutionMessage

case class Mate(first: Chromosome, second: Chromosome) extends EvolutionMessage

case class Result(arr: Array[Chromosome]) extends EvolutionMessage

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
class Population private (private var _population: Array[Chromosome], val crossover: Float, val elitism: Float, val mutation: Float) {
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
		val elitismCount = round(_population.length * elitism)
		
		def randomMutate(ch: Chromosome): Chromosome = {
			if (Random.nextFloat() <= mutation) Chromosome.mutate(ch) else ch
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
		
		var idx = 0
		val buffer = new Array[Chromosome](_population.length)
		while (idx < buffer.length) {
			if (idx < elitismCount) {
				buffer(idx) = _population(idx)
			} else if (Random.nextFloat() <= crossover) {
				// Select the parents and mate to get their children
				val parents  = selectParents
				val children = Chromosome.mate(parents(0), parents(1))
				
				buffer(idx) = randomMutate(children(0))
				idx += 1
				if (idx < buffer.length) {
					buffer(idx) = randomMutate(children(1))
				}
			} else {
				buffer(idx) = randomMutate(_population(idx))
			}
			idx += 1
		}

		_population = buffer.sortWith((s, t) => s.fitness < t.fitness)
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
	private def generateInitialPopulation(size: Int): Array[Chromosome] = {
		new Array[Chromosome](size).map(i => Chromosome.generateRandom).sortWith(
				(s, t) => s.fitness < t.fitness)
	}
}

class Worker extends Actor {
  def receive = {
	case x: Mate =>
	  self reply Result(Chromosome.mate(x.first, x.second))
    case y: Mutate =>
      self reply Result(Array[Chromosome](Chromosome.mutate(y.chromosome)))
  }
}

class Evolver(latch: CountDownLatch) extends Actor {

  var nrOfMessages: Int = _
  var nrOfResults: Int = _
  var items = Vector[Chromosome]()

  // create the workers
  val workers = Vector.fill(Runtime.availableProcessors)(actorOf[Worker].start())

  // wrap them with a load-balancing router
  val router = Routing.loadBalancerActor(CyclicIterator(workers)).start()

  def receive = {
    case Done =>
	  // send a PoisonPill to all workers telling them to shut down themselves
	  router ! Broadcast(PoisonPill)
	  
	  // send a PoisonPill to the router, telling him to shut himself down
	  router ! PoisonPill

	case mate: Mate =>
	  nrOfMessages += 1
	  // schedule work
	  router ! mate
	  

	case mutate: Mutate =>
	  nrOfMessages += 1
	  // schedule work
	  router ! mutate

	case result: Result =>
	  for (item <- result.arr) items = items :+ item
	  nrOfResults += 1
	  if (nrOfResults == nrOfMessages) self.stop()
  }
  
  override def postStop() {
    latch.countDown()
  }
}