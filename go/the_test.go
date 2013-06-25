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
