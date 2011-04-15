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
class Chromosome private (val gene: String, val fitness: Int) {

  /**
   * Method used to mutate the gene of the given chromosome, returning a
   * new [[net.auxesia.Chromsome]] instance.  Only one character in the
   * gene is mutated, the rest of the gene remains the same.
   *
   * @return A new [[net.auxesia.Chromosome]] with one character replaced
   * in the original gene.
   */
  def mutate: Chromosome = {
    val pivot = Random.nextInt(gene.length)
    Chromosome(gene.substring(0, pivot) + (Random.nextInt(90) + 32).toChar
      + gene.substring(pivot + 1))
  }

  /**
   * Method to mate this [[net.auxesia.Chromosome]] with another, resulting
   * in two distinct offspring.
   *
   * @param other The other [[net.auxesia.Chromosome]] to mate with.
   *
   * @return A 2-element array of resuling [[net.auxesia.Chromosome]]
   * objects created through the mating algorithm.
   */
  def mate(other: Chromosome): Array[Chromosome] = {
    val arr = new Array[Chromosome](2)
    val pivot = Random.nextInt(gene.length)
    arr(0) = Chromosome(gene.substring(0, pivot) + other.gene.substring(pivot))
    arr(1) = Chromosome(other.gene.substring(0, pivot) + gene.substring(pivot))
    return arr
  }
}

/**
 * A factory object for constructing new [[net.auxesia.Chromosome]] instances.
 */
object Chromosome {
  /** A constant used to determine fitness. */
  private val TARGET_GENE = "Hello, world!"

  /**
   * Creates new [[net.auxesia.Chromsome]] instances.
   *
   * @param gene The gene representing the chromosome.
   *
   * @return A new [[net.auxesia.Chromosome]] instance.
   */
  def apply(gene: String) = {
    def calcFitness(gene: String): Int = {
      var f = 0
      for ((a, b) <- gene zip TARGET_GENE) f += abs(a.intValue - b.intValue)
      return f
    }

    new Chromosome(gene, calcFitness(gene))
  }

  /**
   * Helper method used to generate a random [[net.auxesia.Chromosome]]
   * instance.
   * 
   * @return A [[net.auxesia.Chromosome]] instance with a random, valid gene.
   */
  def generateRandom: Chromosome = {
    var g = ""
    for (i <- 1 to TARGET_GENE.length) g += (Random.nextInt(90) + 32).toChar
    Chromosome(g)
  }
}