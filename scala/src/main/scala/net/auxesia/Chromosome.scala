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

class Chromosome private(val gene: String, val fitness: Int) {
	def mutate(): Chromosome = {
		val pivot = Random.nextInt(gene.length)
		Chromosome(gene.substring(0, pivot)+ (Random.nextInt(90) + 32).toChar 
				+ gene.substring(pivot + 1))
	}
	
	def mate(other: Chromosome): Array[Chromosome] = {
		val arr = new Array[Chromosome](2)
		val pivot = Random.nextInt(gene.length)
		arr(0) = Chromosome(gene.substring(0, pivot) + other.gene.substring(pivot))
		arr(1) = Chromosome(other.gene.substring(0, pivot) + gene.substring(pivot))		
		return arr
	}
}

object Chromosome {
	def apply(gene: String) = new Chromosome(gene, calcFitness(gene))
	private val TARGET_GENE = "Hello, world!"

	private def calcFitness(gene: String): Int = {
		var f = 0
		for ((a, b) <- gene zip TARGET_GENE) f += abs(a.intValue - b.intValue)
		return f
	}	
	
	def generateRandom(): Chromosome = {
		var g = ""
		for (i <- 1 to TARGET_GENE.length) g += (Random.nextInt(90) + 32).toChar
		apply(g)
	}
}