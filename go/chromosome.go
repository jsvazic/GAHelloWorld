/*
 * The MIT License
 *
 * Copyright (c) 2013 Song Gao <song@gao.io>
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

package main

import (
	"math/rand"
)

type chromosome struct {
	gene    []byte
	fitness float64
}

func randomByte() byte {
	return byte(rand.Int() % 256)
}

func randomChromosome(length int) chromosome {
	var gene []byte
	for i := 0; i < length; i++ {
		gene = append(gene, randomByte())
	}
	return chromosome{gene: gene}
}

func (chrom *chromosome) mutate() {
	chrom.gene[rand.Int()%len(chrom.gene)] = randomByte()
}

func crossover(c1 chromosome, c2 chromosome) []chromosome {
	pivot := rand.Int() % len(c1.gene)
	var first, second []byte
	for i := 0; i < len(c1.gene); i++ {
		if i < pivot {
			first = append(first, c1.gene[i])
			second = append(second, c2.gene[i])
		} else {
			first = append(first, c2.gene[i])
			second = append(second, c1.gene[i])
		}
	}
	return []chromosome{{gene: first}, {gene: second}}
}
