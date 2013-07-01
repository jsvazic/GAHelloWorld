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
	"fmt"
	"math/rand"
	"time"
)

var objective = []byte("Hello, World!")

// Fitness function should be designed to work with the algorithm. In this
// example, apparently crossover/mutation does not involve anything like
// generating a new byte based on existing bytes. All it does is to either pick
// current bytes from a parent, or randomly generate a new one. For this
// particular algorithm, ascii value difference doesn't matter, i.e. there's no
// difference between "difference of a and z" and "differenceo of a and b".
// Thus, the evaluation function should not make a difference on that.
// Otherwise, "Gckkp, Xpskc!" will rank better than "Hello, Worzz!", which
// leads the evolving to a wrong direction.
func evaluate(gene []byte) float64 {
	var fitness float64
	for i, c := range gene {
		if objective[i] != c {
			fitness += 1
		}
	}
	return fitness
}

func main() {

	rand.Seed(time.Now().Unix())

	param := new(Parameters)
	param.ChromosomeLength = len(objective)
	param.CrossoverProbability = .5
	param.EliteismRatio = .7
	param.MaximumGeneration = 1024
	param.MutationProbability = .2
	param.PopulationSize = 100

	param.Evaluator = evaluate

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
