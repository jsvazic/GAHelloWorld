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

import scala.math.abs
import scala.util.Random

/**
 * A class representing a chromosome in the "Hello, world!" genetic
 * algorithm simulation.
 *
 * @author John Svazic
 *
 * @constructor Creates an instance of [[net.auxesia.Chromosome]].
 * @param gene The gene representing the chromosome.
 * @param fitness The fitness of the chromosome.
 */
class Chromosome private (val gene: String, val fitness: Int)

/**
 * A factory object for constructing new [[net.auxesia.Chromosome]] instances.
 */
object Chromosome {
  /** A constant used to determine fitness. */
  private val TARGET_GENE = "Hello, world!".toCharArray

  /**
   * Creates new [[net.auxesia.Chromsome]] instances.
   *
   * @param gene The gene representing the chromosome.
   *
   * @return A new [[net.auxesia.Chromosome]] instance.
   */
  def apply(gene: String) = {
    def calcFitness(gene: Array[Char]): Int = {
      gene.zip(TARGET_GENE).map(
        i => abs(i._1.intValue - i._2.intValue)).foldLeft(0)(_ + _)
    }

    new Chromosome(gene, calcFitness(gene.toCharArray))
  }

  /**
   * Helper method used to generate a random gene for use in constructing a
   * new [[net.auxesia.Chromosome]] instance.
   *
   * @return A valid gene for use when constructing a new
   * [[net.auxesia.Chromosome]] instance.
   */
  def generateRandom = {
    Chromosome(new Array[Char](TARGET_GENE.length).map(
      i => (Random.nextInt(90) + 32).toChar).mkString)
  }

  /**
   * Method to mate one [[net.auxesia.Chromosome]]'s gene with another,
   * resulting in two distinct offspring genes that can be used to create
   * new instances of [[net.auxesia.Chromosome]]'s as required.
   *
   * @param first The first gene to mate.
   * @param second The second gene to mate.
   *
   * @return A 2-element vector of resuling [[net.auxesia.Chromosome]]
   * genes created through the mating algorithm.
   */
  def mate(first: Chromosome, second: Chromosome) = {
    val pivot = Random.nextInt(first.gene.length)
    Vector[Chromosome](
      Chromosome(first.gene.substring(0, pivot) + second.gene.substring(pivot)),
      Chromosome(second.gene.substring(0, pivot) + first.gene.substring(pivot)))
  }

  /**
   * Method used to mutate the gene of the given chromosome, returning a
   * new [[net.auxesia.Chromsome]] instance.  Only one character in the
   * gene is mutated, the rest of the gene remains the same.
   *
   * @param ch The [[net.auxesia.Chromosome]] to mutate.
   *
   * @return A new [[net.auxesia.Chromosome]], with its gene having a one
   * character delta compared to the given [[net.auxesia.Chromosome]]'s gene.
   */
  def mutate(ch: Chromosome) = {
    var arr = ch.gene.toArray
    arr(Random.nextInt(ch.gene.length)) = (Random.nextInt(90) + 32).toChar
    Chromosome(arr.mkString)
  }
}