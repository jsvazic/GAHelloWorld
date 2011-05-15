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
using System.Diagnostics;
using System.Linq;

namespace GAHelloWorld
{
    class Driver
    {
        static void Main(string[] args)
        {
            int populationSize = 2048;
            int maxGenerations = 16384;
            float crossoverRatio = .8f;
            float elitismRatio = .1f;
            float mutationRatio = .03f;

            Chromosome.SetTargetGene("Hello, world!");

            Population population = new Population(populationSize, crossoverRatio, elitismRatio, mutationRatio);

            Chromosome topChromosome = population.Chromosomes.First();

            Stopwatch sw = new Stopwatch();
            sw.Start();

            int count = 1;

            while ((count++ <= maxGenerations) && (topChromosome.Fitness != 0))
            {
                Console.WriteLine(topChromosome.Gene);
                population.Evolve();
                topChromosome = population.Chromosomes.First();
            }
            Console.WriteLine(topChromosome.Gene);

            sw.Stop();

            Console.WriteLine("{0} generations in {1} ms", count, sw.ElapsedMilliseconds);
            Console.ReadLine();
        }
    }
}