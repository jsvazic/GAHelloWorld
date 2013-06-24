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
