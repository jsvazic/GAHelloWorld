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

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers

class ChromosomeSpec extends WordSpec with MustMatchers {
  "A Chromosome" should {
    "provide a valid fitness for known genes" in {
      val c1 = Chromosome("Hello, world!")
      val c2 = Chromosome("H5p&J;!l<X\\7l")
      val c3 = Chromosome("Vc;fx#QRP8V\\$")
      val c4 = Chromosome("t\\O`E_Jx$n=NF")

      c1.fitness must be === 0
      c2.fitness must be === 399
      c3.fitness must be === 297
      c4.fitness must be === 415
    }

    "be able to generate a valid random instance of itself" in {
      for (i <- 1 to 1000) {
	    val chromosome = Chromosome.generateRandom
		chromosome.gene.length must be === 13
        for (ch <- chromosome.gene) {
          ch.intValue must be >= 32
          ch.intValue must be <= 121
        }
		
        chromosome.fitness must be >= 0
      }
    }

    "mutate no more than one element of its gene" in {
      for (i <- 1 to 1000) {
        val c1 = Chromosome.generateRandom
        val c2 = Chromosome.mutate(c1)
        c1.gene.length must be === c2.gene.length

        var diff = 0
        for ((a, b) <- c1.gene zip c2.gene) if (a != b) diff += 1
        diff must be <= 1
      }
    }

    "be able to mate correctly with another Chromosome" in {
      val c1 = Chromosome.generateRandom
      val c2 = Chromosome.generateRandom

      // Check to ensure the right number of children are returned.
      val children = Chromosome.mate(c1, c2)
      children.length must be === 2

      // Check the resulting child gene lengths
      children(0).gene.length must be === 13
      children(1).gene.length must be === 13

      // Determine the pivot point for the mating
      var pivot = -1
      for (((a, b), idx) <- c1.gene zip children(0).gene zip (0 to c1.gene.length - 1)) {
        if (pivot < 0 && a != b) pivot = idx
      }

      pivot must be < c1.gene.length

      // Check the first child.
      for (idx <- 0 to c1.gene.length - 1) {
        if (idx < pivot) {
		  c1.gene(idx) must be === children(0).gene(idx)
        } else {
		  c2.gene(idx) must be === children(0).gene(idx)
		}
      }

      // Check the second child.
      for (idx <- 0 to children(1).gene.length - 1) {
        if (idx < pivot) {
		  c2.gene(idx) must be === children(1).gene(idx)
		} else {
		  c1.gene(idx) must be === children(1).gene(idx)
		}
      }
    }
  }
}