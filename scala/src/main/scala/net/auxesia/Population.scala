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

import akka.actor.{ Actor, PoisonPill }
import Actor._
import akka.routing.{ Routing, CyclicIterator }
import Routing._
import akka.dispatch.{ Dispatchers, Future }

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
class Population private (private val popSize: Int, val crossover: Float, val elitism: Float, val mutation: Float) {

  private var _population = Population.generateInitialPopulation(popSize)

  // Create the Akka workers
  private val nrOfWorkers = Runtime.getRuntime.availableProcessors
  private val workers = Vector.fill(nrOfWorkers)(actorOf(new Worker(mutation)).start())

  // Wrap them with a load-balancing router
  private val router = Routing.loadBalancerActor(CyclicIterator(workers)).start()

  /**
   * A public accessor for the underlying vector of [[net.auxesia.Chromosome]]
   * objects.
   */
  def population = _population

  /**
   * A helper method that converts a Future object to a Vector[Chromosome]
   *
   * @param f The Future[Any] object to convert.
   *
   * @return A Vector[Chromosome] object.
   */
  private def mkChromosome(f: Future[Any]) = {
    f.get match {
      case v: Vector[Chromosome] => v
      case ch: Chromosome => Vector(ch)
      case _ => Vector[Chromosome]()
    }
  }

  /**
   * Method used to evolve a new generation for the population.  This method
   * modifies the internal population represented by this class.
   */
  def evolve = {
    val elitismCount = round(_population.size * elitism)
    var buffer = _population.take(elitismCount)
    var futures: Vector[Future[Any]] = Vector()
    for (idx <- elitismCount to _population.size - 1) {
      futures :+= router ? (if (Random.nextFloat <= crossover) Mate(_population) else Mutate(_population(idx)))
    }

    futures.map(x => buffer = buffer ++ mkChromosome(x))

    // Grab the top population from the buffer.
    _population = buffer.sortWith(_.fitness < _.fitness).take(popSize)
  }

  /**
   * Method to shutdown the Akka router and associated workers.
   */
  def shutdown = {
    // send a PoisonPill to all workers telling them to shut down themselves
    router ! Broadcast(PoisonPill)

    // send a PoisonPill to the router, telling him to shut himself down
    router ! PoisonPill
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
    new Population(size, crossover, elitism, mutation)
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
    Vector.fill(size)(Chromosome.generateRandom).sortWith(_.fitness < _.fitness)
  }
}

/**
 * Trait used to define messages being sent to the evolution workers.
 */
sealed trait EvolutionMsg

/**
 * Case class used as an Akka message to mutate a given gene.
 *
 * @author John Svazic
 * @constructor Create a new mutate message with a given gene.
 * @param ch The [[net.auxesia.Chromosome]] to mutate.
 */
case class Mutate(ch: Chromosome) extends EvolutionMsg

/**
 * Case class defining an Akka message to mutate a given gene.
 *
 * @author John Svazic
 * @constructor Create a new mate message from a given population.
 * @param population The population of [[net.auxesia.Chromosome]]'s from which
 * to select candidates to mate.
 */
case class Mate(population: Vector[Chromosome]) extends EvolutionMsg

/**
 * Class defining an Akka worker for doing the evolutionary work as required
 * by the genetic algorithm.
 *
 * @author John Svazic
 * @constructor Create a new Akka worker with the given mutation ratio for
 * the GA simulation.
 * @param mutationRatio The mutation ratio for the GA simulation.
 */
class Worker(mutationRatio: Double) extends Actor {

  /**
   * A helper method to randomly mutate a given [[net.auxesia.Chromosome]]
   * based on the mutation ratio.
   *
   * @return Either a mutated [[net.auxesia.Chromosome]] or the given
   * [[net.auxesia.Chromosome]] unaltered, depending on if a random float is
   * less than the mutation ration.
   */
  private def randomMutate(ch: Chromosome) = {
    if (Random.nextFloat() <= mutationRatio) Chromosome.mutate(ch) else ch
  }

  /**
   * A helper method used to randomly select two [[net.auxesia.Chromosome]]s
   * based on a tournament selection algorithm.
   *
   * @param population The population from which to select the parents.
   * @return Two [[net.auxesia.Chromosome]] instances from the given
   * population, selected by a tournament selection algorithm.
   */
  private def selectParents(population: Vector[Chromosome]) = {
    val popSize = population.size
    val tournamentSize = 3
    var parents = Vector[Chromosome]()

    // Randomly select two parents via tournament selection.
    for (i <- 0 to 1) {
      var candidate = population(Random.nextInt(popSize))
      for (j <- 1 to tournamentSize) {
        val idx = Random.nextInt(popSize)
        if (population(idx).fitness < candidate.fitness) {
          candidate = population(idx)
        }
      }
      parents :+= candidate
    }
    parents
  }

  /**
   * Receive method as required by the Akka framework.
   */
  def receive = {
    case Mutate(chromosome) =>
      self reply randomMutate(chromosome)
    case Mate(population) =>
      val parents = selectParents(population)
      self reply Chromosome.mate(parents(0), parents(1)).map(randomMutate)
  }
}
