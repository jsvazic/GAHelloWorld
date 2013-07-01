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
	"launchpad.net/gocheck"
	"testing"
)

func Test(t *testing.T) { gocheck.TestingT(t) }

type MySuite struct{}

var _ = gocheck.Suite(&MySuite{})

func (s *MySuite) TestChromosomeRandomInitializer(c *gocheck.C) {
	length := 64
	chrom := randomChromosome(length)
	c.Assert(len(chrom.gene), gocheck.Equals, length)
}

func (s *MySuite) TestChromosomeMutation(c *gocheck.C) {
	length := 64
	chrom := randomChromosome(length)
	original := make([]byte, length)
	copy(original, chrom.gene)
	chrom.mutate()
	allSame := true
	for i := range original {
		if original[i] != chrom.gene[i] {
			allSame = false
			break
		}
	}
	c.Assert(allSame, gocheck.Equals, false)
}

func (s *MySuite) TestChromosomeCrossover(c *gocheck.C) {
	length := 64
	chrom1 := randomChromosome(length)
	chrom2 := randomChromosome(length)
	offsprings := crossover(chrom1, chrom2)
	for _, offspring := range offsprings {
		for i := range offspring.gene {
			fromParent := offspring.gene[i] == chrom1.gene[i] || offspring.gene[i] == chrom2.gene[i]
			c.Assert(fromParent, gocheck.Equals, true)
		}
	}
}

func (s *MySuite) TestFitnessFunction(c *gocheck.C) {
	gene := make([]byte, len(objective))
	copy(gene, objective)
	c.Assert(evaluate(gene), gocheck.Equals, 0.0)
	for i := range gene {
		gene[i] = byte((int(gene[i]) + i + 1) % 255)
		c.Assert(evaluate(gene), gocheck.Equals, float64(i+1))
	}
}

func (s *MySuite) TestGA(c *gocheck.C) {
	param := new(Parameters)
	param.ChromosomeLength = len(objective)
	param.CrossoverProbability = .5
	param.EliteismRatio = .7
	param.MaximumGeneration = 65536
	param.MutationProbability = .2
	param.PopulationSize = 128
	param.Evaluator = evaluate

	g, err := NewGA(param)
	c.Assert(err, gocheck.IsNil)
	result, _ := g.Run()
	c.Assert(string(result), gocheck.Equals, string(objective))
}
