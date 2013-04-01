# The MIT License
# 
# Copyright (c) 2011 John Svazic
# 
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
# 
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
# 
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.

"""
A python script that demonstrates a simple "Hello, world!" application using
genetic algorithms.

@author: John Svazic
"""

from random import (choice, random, randint)

__all__ = ['Chromosome', 'Population']

class Chromosome:
    """
    This class is used to define a chromosome for the gentic algorithm 
    simulation.  
       
    This class is essentially nothing more than a container for the details
    of the chromosome, namely the gene (the string that represents our 
    target string) and the fitness (how close the gene is to the target 
    string).
       
    Note that this class is immutable.  Calling mate() or mutate() will 
    result in a new chromosome instance being created.
    """

    _target_gene = "Hello, world!"
    
    def __init__(self, gene):
        self.gene = gene
        self.fitness = Chromosome._update_fitness(gene)
    
    def mate(self, mate):
        """
        Method used to mate the chromosome with another chromosome, 
        resulting in a new chromosome being returned.
        """
        pivot = randint(0, len(self.gene) - 1)
        gene1 = self.gene[:pivot] + mate.gene[pivot:]
        gene2 = mate.gene[:pivot] + self.gene[pivot:]
        
        return Chromosome(gene1), Chromosome(gene2)
    
    def mutate(self):
        """
        Method used to generate a new chromosome based on a change in a 
        random character in the gene of this chromosome.  A new chromosome 
        will be created, but this original will not be affected.
        """
        gene = list(self.gene)
        delta = randint(32, 121)
        idx = randint(0, len(gene) - 1)
        gene[idx] = chr((ord(gene[idx]) + delta) % 122)
        
        return Chromosome(''.join(gene))

    @staticmethod            
    def _update_fitness(gene):
        """
        Helper method used to return the fitness for the chromosome based
        on its gene.
        """
        fitness = 0
        for a, b in zip(gene, Chromosome._target_gene):
            fitness += abs(ord(a) - ord(b))
            
        return fitness
        
    @staticmethod
    def gen_random():
        """
        A convenience method for generating a random chromosome with a random
        gene.
        """
        gene = []
        for x in range(len(Chromosome._target_gene)):
            gene.append(chr(randint(32, 121)))
                
        return Chromosome(''.join(gene))
        
class Population:
    """
    A class representing a population for a genetic algorithm simulation.
    
    A population is simply a sorted collection of chromosomes 
    (sorted by fitness) that has a convenience method for evolution.  This
    implementation of a population uses a tournament selection algorithm for
    selecting parents for crossover during each generation's evolution.
    
    Note that this object is mutable, and calls to the evolve()
    method will generate a new collection of chromosome objects.
    """
    
    _tournamentSize = 3
    
    def __init__(self, size=1024, crossover=0.8, elitism=0.1, mutation=0.03):
        self.elitism = elitism
        self.mutation = mutation
        self.crossover = crossover
        
        buf = []
        for i in range(size): buf.append(Chromosome.gen_random())
        self.population = list(sorted(buf, key=lambda x: x.fitness))
                        
    def _tournament_selection(self):
        """
        A helper method used to select a random chromosome from the 
        population using a tournament selection algorithm.
        """
        best = choice(self.population)
        for i in range(Population._tournamentSize):
            cont = choice(self.population)
            if (cont.fitness < best.fitness): best = cont
                    
        return best

    def _selectParents(self):
        """
        A helper method used to select two parents from the population using a
        tournament selection algorithm.
        """
                    
        return (self._tournament_selection(), self._tournament_selection())
        
    def evolve(self):
        """
        Method to evolve the population of chromosomes.
        """
        size = len(self.population)
        idx = int(round(size * self.elitism))
        buf = self.population[:idx]
        
        while (idx < size):
            if random() <= self.crossover:
                (p1, p2) = self._selectParents()
                children = p1.mate(p2)
                for c in children:
                    if random() <= self.mutation:
                        buf.append(c.mutate())
                    else:
                        buf.append(c)
                idx += 2
            else:
                if random() <= self.mutation:
                    buf.append(self.population[idx].mutate())
                else:
                    buf.append(self.population[idx])
                idx += 1
        
        self.population = list(sorted(buf[:size], key=lambda x: x.fitness))

if __name__ == "__main__":
	maxGenerations = 16384
	pop = Population(size=2048, crossover=0.8, elitism=0.1, mutation=0.3)

	for i in range(1, maxGenerations + 1):
		print("Generation %d: %s" % (i, pop.population[0].gene))
		if pop.population[0].fitness == 0: break
		else:pop.evolve()
	else:
		print("Maximum generations reached without success.")