package main

import (
	"fmt"
	"math/rand"
	"time"
)

var objective = []byte("Hello, World!")

func main() {

	rand.Seed(time.Now().Unix())

	param := new(Parameters)
	param.ChromosomeLength = len(objective)
	param.CrossoverProbability = .5
	param.EliteismRatio = .7
	param.MaximumGeneration = 1024
	param.MutationProbability = .2
	param.PopulationSize = 100

	// Fitness function should be designed to work with the algorithm. In this
	// example, apparently crossover/mutation does not involve anything like
	// generating a new byte based on existing bytes. All it does is to either
	// pick current bytes from a parent, or randomly generate a new one. For this
	// particular algorithm, ascii value difference doesn't matter, i.e. there's
	// no difference between "difference of a and z" and "differenceo of a and
	// b".  Thus, the evaluation function should not make a difference on that.
	// Otherwise, "Gckkp, Xpskc!" will rank better than "Hello, Worzz!", which
	// leads the evolving to a wrong direction.
	param.Evaluator = func(gene []byte) float64 {
		var fitness float64
		for i, c := range gene {
			if objective[i] != c {
				fitness += 1
			}
		}
		return fitness
	}

	for run := 0; run < 8; run++ { // run the same algorithm 8 times
		start := time.Now()
		g, err := NewGA(param)
		if err != nil {
			fmt.Println(err)
			return
		}
		result, generation := g.Run()
		fmt.Printf("Got %q at generation %d in %v\n", result, generation, time.Since(start))
	}

}
