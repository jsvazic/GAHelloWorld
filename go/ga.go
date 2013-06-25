package main

import (
	"errors"
	"math/rand"
	"sort"
)

// Parameters is parameters used in the genetic algorithm solver
type Parameters struct {
	// CrossoverProbability is the probability of one chromosome crossover with
	// another chromosome to produce two offspring chromosomes in one evolving
	// process
	CrossoverProbability float64

	// MutationProbability is the probability that a chromosome produced by crossover process should mutate one byte in its gene
	MutationProbability float64

	// EliteismRatio is the ratio of chromosomes (that rank in front) in the
	// population that should absolutely be suviving.
	EliteismRatio float64

	// PopulationSize is the size of population
	PopulationSize int

	// ChromosomeLength is the length of chromosomes
	ChromosomeLength int

	// MaximumGeneration is the maximum number of generations that the solver can
	// envolve.
	MaximumGeneration int

	// Evaluator is the evaluation function, or fitness function, that the solver
	// should use. The function should take a byte slice, which is gene, and
	// returns a fitness value. The better the chromosome is, the lower value it
	// should return. 0 is assumed to be a perfect chromosome.
	Evaluator func([]byte) float64
}

// GA is a genetic algorithm solver
type GA struct {
	param *Parameters
	pop   population
}

func checkParam(param *Parameters) error {
	if param.CrossoverProbability < 0 || param.CrossoverProbability > 1 {
		return errors.New("CrossoverProbability should be in [0, 1]")
	}
	if param.MutationProbability < 0 || param.MutationProbability > 1 {
		return errors.New("MutationProbability should be in [0, 1]")
	}
	if param.EliteismRatio < 0 || param.EliteismRatio > 1 {
		return errors.New("EliteismRatio should be in [0, 1]")
	}
	if param.PopulationSize <= 2 {
		return errors.New("PopulationSize should > 2")
	}
	if param.ChromosomeLength <= 0 {
		return errors.New("ChromosomeLength should > 0")
	}
	if param.MaximumGeneration <= 0 {
		return errors.New("MaximumGeneration should > 0")
	}
	if param.Evaluator == nil {
		return errors.New("Evaluator cannot be nil")
	}
	return nil
}

// NewGA create a new genetic algorithm solver with param being parameters.
func NewGA(param *Parameters) (*GA, error) {
	if err := checkParam(param); err != nil {
		return nil, err
	}
	ret := &GA{param: param, pop: make([]chromosome, param.PopulationSize)}
	return ret, nil
}

func (g *GA) init() {
	for i := 0; i < g.param.PopulationSize; i++ {
		g.pop[i] = randomChromosome(g.param.ChromosomeLength)
		g.pop[i].fitness = g.param.Evaluator(g.pop[i].gene)
	}
}

func (g *GA) evolve() {

	// generate offspring
	for i := 0; i < g.param.PopulationSize; i++ {
		if rand.Float64() < g.param.CrossoverProbability {
			spouseIndex := rand.Int() % (g.param.PopulationSize - 1)
			if spouseIndex == i {
				spouseIndex += 1
			}
			offspring := crossover(g.pop[i], g.pop[spouseIndex])
			for _, chrom := range offspring {
				if rand.Float64() < g.param.MutationProbability {
					chrom.mutate()
				}
				chrom.fitness = g.param.Evaluator(chrom.gene)
				g.pop = append(g.pop, chrom)
			}
		}
	}

	// selection
	sort.Sort(g.pop)
	for i := int(g.param.EliteismRatio * float64(g.param.PopulationSize)); i < g.param.PopulationSize; i++ {
		// randomly select those who are not "eliteism"
		luckyIndex := rand.Int() % (g.param.PopulationSize - i)
		g.pop.Swap(i, luckyIndex)
	}
	g.pop = g.pop[:g.param.PopulationSize]

}

// Run runs genetic algorithm on g. It returns result, which is the evolved
// chromosome if succeeds, and generation, which is the generation at which the
// algorithm gets the result. Evolution is considered to be successful if
// fitness reaches 0
func (g *GA) Run() (result []byte, generation int) {
	g.init()
	for i := 0; i < g.param.MaximumGeneration; i++ {
		g.evolve()
		if g.pop[0].fitness == 0 {
			return g.pop[0].gene, i
		}
	}
	return nil, g.param.MaximumGeneration
}
