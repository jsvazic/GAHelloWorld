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

import scala.collection.immutable.List
import scala.math.round
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers

class PopulationSpec extends WordSpec with MustMatchers {

  "A Population" should {
    "provide a valid value for the crossover ratio" in {
      val p1 = Population(1024, 0.8f, 0.1f, 0.05f)
      val p2 = Population(1024, 0.0f, 0.1f, 0.05f)
      val p3 = Population(1024, 1.0f, 0.1f, 0.05f)

      (p1.crossover * 100).intValue must be === 80
      (p2.crossover * 100).intValue must be === 0
      (p3.crossover * 100).intValue must be === 100
    }

    "provide a valid value for the elitism ratio" in {
      val p1 = Population(1024, 0.8f, 0.1f, 0.05f)
      val p2 = Population(1024, 0.8f, 0.0f, 0.05f)
      val p3 = Population(1024, 0.8f, 0.99f, 0.05f)

      (p1.elitism * 100).intValue must be === 10
      (p2.elitism * 100).intValue must be === 0
      (p3.elitism * 100).intValue must be === 99
    }

    "provide a valid value for the mutation ratio" in {
      val p1 = Population(1024, 0.8f, 0.1f, 0.05f)
      val p2 = Population(1024, 0.8f, 0.1f, 0.0f)
      val p3 = Population(1024, 0.8f, 0.1f, 1.0f)

      (p1.mutation * 100).intValue must be === 5
      (p2.mutation * 100).intValue must be === 0
      (p3.mutation * 100).intValue must be === 100
    }

    "provide a valid initial population" in {
      val pop = Population(1024, 0.8f, 0.1f, 0.05f)
      pop.population must have length (1024)

      val list = (Vector() ++ pop.population).sortWith((s, t) => s.fitness < t.fitness)
      list must have length (1024)
      list.sameElements(pop.population) must be === true
    }

    "be able to evlove successfully" in {
      val pop = Population(1024, 0.8f, 0.1f, 0.05f)
      val list = Vector() ++ pop.population

      list must have length (1024)
      pop.evolve
      pop.population must have length (1024)
      
      (pop.crossover * 100).intValue must be === 80
      (pop.elitism * 100).intValue must be === 10
      (pop.mutation * 100).intValue must be === 5
	  
	  val elitisimMaxIdx = round(pop.population.length * pop.elitism)

      var counter = 0
      for (ch <- list) {
        if (pop.population contains ch) {
          counter += 1
        }
      }

      counter must be >= round(pop.population.length * pop.elitism)
      counter must be < pop.population.length
	  pop.population(elitisimMaxIdx).fitness >= list(elitisimMaxIdx).fitness
    }
	
	"use elitism properly" in {
      val pop = Population(1024, 0.8f, 0.1f, 0.05f)
      val list = Vector() ++ pop.population

      list must have length (1024)
	  for (i <- 1 to 100) {
		pop.evolve
	  }

	  val elitisimMaxIdx = round(pop.population.length * pop.elitism)
	  pop.population(elitisimMaxIdx).fitness >= list(elitisimMaxIdx).fitness
	}
  }
}