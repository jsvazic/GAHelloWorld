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
using System.Diagnostics;
using System.Text;

namespace GAHelloWorld
{
    [DebuggerDisplay("Gene={Gene}")]
    public class Chromosome : IComparable<Chromosome>
    {
        private static char[] TARGET_GENE = null;
        private static Random rand = new Random(Environment.TickCount);

        public Chromosome(string gene)
        {
            this.Gene = gene;
            this.Fitness = CalculateFitness(gene);
        }

        public int CompareTo(Chromosome other)
        {
            return this.Fitness.CompareTo(other.Fitness);
        }

        public static void SetTargetGene(string targetGene)
        {
            TARGET_GENE = targetGene.ToCharArray();
        }

        public static int CalculateFitness(string gene)
        {
            int fitness = 0;

            for (int charIndex = 0; charIndex < TARGET_GENE.Length; charIndex++)
            {
                fitness += Math.Abs((int)gene[charIndex] - (int)TARGET_GENE[charIndex]);
            }

            return fitness;
        }

        public static Chromosome GenerateRandom()
        {
            StringBuilder geneBuilder = new StringBuilder();

            for (int count = 0; count < TARGET_GENE.Length; count++)
            {
                geneBuilder.Append((char)(rand.Next(0, 90) + 32));
            }

            return new Chromosome(geneBuilder.ToString());
        }

        public Chromosome Mutate()
        {
            char[] mutatedGene = this.Gene.ToCharArray();

            int randomIndex = rand.Next(0, this.Gene.Length);
            int mutateChange = rand.Next(32, 122);

            mutatedGene[randomIndex] = (char)mutateChange;

            return new Chromosome(String.Join("", mutatedGene));
        }

        public List<Chromosome> Mate(Chromosome mate)
        {
            int pivotIndex = rand.Next(0, this.Gene.Length - 1);

            string firstSplit = this.Gene.Substring(0, pivotIndex) + mate.Gene.Substring(pivotIndex);
            string secondSplit = mate.Gene.Substring(0, pivotIndex) + this.Gene.Substring(pivotIndex);

            return new List<Chromosome>(2) 
            { 
                new Chromosome(firstSplit),
                new Chromosome(secondSplit)
            };

        }

        public string Gene { get; private set; }

        public int Fitness { get; set; }
    }
}
