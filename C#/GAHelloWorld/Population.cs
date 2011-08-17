/* 
The MIT License

Copyright (c) 2011 John Svazic

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

Author: Patrick Hyatt 

*/

using System;
using System.Collections.Generic;
using System.Linq;

namespace GAHelloWorld
{
    public class Population
    {
        private const int TOURNAMENT_SIZE = 3;

        private static Random _rand = new Random(Environment.TickCount);

        public Population(int size, float crossoverRatio, float eliteismRatio, float mutationRatio)
        {
            this.Crossover = crossoverRatio;
            this.Eliteism = eliteismRatio;
            this.Mutation = mutationRatio;

            InitializePopulation(size);
        }

        private void InitializePopulation(int size)
        {
            this.Chromosomes = new List<Chromosome>(size);
            for (int count = 0; count < size; count++)
            {
                this.Chromosomes.Add(Chromosome.GenerateRandom());
            }

            this.Chromosomes.Sort();
        }

        public void Evolve()
        {
            List<Chromosome> evolvedSet = new List<Chromosome>(this.Chromosomes);

            int unchangedIndex = (int)Math.Round(this.Chromosomes.Count * this.Eliteism);

            for (int changedIndex = unchangedIndex; changedIndex < this.Chromosomes.Count - 1; changedIndex++)
            {
                if (_rand.NextDouble() <= this.Crossover)
                {
                    List<Chromosome> parents = this.SelectParents();
                    List<Chromosome> children = parents.First().Mate(parents.Last());

                    evolvedSet[changedIndex] = children.First();

                    if (_rand.NextDouble() <= this.Mutation)
                    {
                        evolvedSet[changedIndex] = evolvedSet[changedIndex].Mutate();
                    }

                    if (changedIndex < evolvedSet.Count - 1)
                    {
                        changedIndex++;

                        evolvedSet[changedIndex] = children.Last();
                        if (_rand.NextDouble() <= this.Mutation)
                        {
                            evolvedSet[changedIndex] = evolvedSet[changedIndex].Mutate();
                        }
                    }
                }
                else
                {
                    if (_rand.NextDouble() <= this.Mutation)
                    {
                        evolvedSet[changedIndex] = evolvedSet[changedIndex].Mutate();
                    }
                }

                changedIndex++;
            }

            evolvedSet.Sort();

            this.Chromosomes = evolvedSet;
        }

        private List<Chromosome> SelectParents()
        {
            List<Chromosome> parents = new List<Chromosome>(2);

            for (int parentIndex = 0; parentIndex < 2; parentIndex++)
            {
                parents.Add(this.Chromosomes[_rand.Next(this.Chromosomes.Count - 1)]);

                for (int tournyIndex = 0; tournyIndex < TOURNAMENT_SIZE; tournyIndex++)
                {
                    int randomIndex = _rand.Next(this.Chromosomes.Count - 1);
                    if (this.Chromosomes[randomIndex].Fitness < parents[parentIndex].Fitness)
                    {
                        parents[parentIndex] = this.Chromosomes[randomIndex];
                    }
                }
            }

            return parents;
        }

        public float Eliteism { get; set; }

        public float Mutation { get; set; }

        public float Crossover { get; set; }

        public List<Chromosome> Chromosomes { get; set; }
    }
}
