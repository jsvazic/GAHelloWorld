using System;
using System.Text;
using System.Collections.Generic;
using System.Linq;
using Microsoft.VisualStudio.TestTools.UnitTesting;

using GAHelloWorld;

namespace GAHelloWorldTest
{
    [TestClass]
    public class ChromosomeTest
    {
        [TestMethod]
        public void TestMutate()
        {
            Chromosome.SetTargetGene("ADE");

            Chromosome first = new Chromosome("ABC");
            Chromosome second = new Chromosome("DEF");

            var children = first.Mate(second);

            Console.WriteLine(children.First().Gene);
            Console.WriteLine(children.Last().Gene);
        }

        [TestMethod]
        public void TestFitnessMatch()
        {
            Chromosome.SetTargetGene("ABCD");

            Chromosome first = new Chromosome("ABCD");

            System.Diagnostics.Debug.Assert(first.Fitness == 0);
        }

        [TestMethod]
        public void TestFitnessNoMatch()
        {
            Chromosome.SetTargetGene("ABCF");

            Chromosome first = new Chromosome("ABCE");

            System.Diagnostics.Debug.Assert(first.Fitness == 1);
        }

        [TestMethod]
        public void TestSelfComparison()
        {
            Chromosome test = new Chromosome("#$A12");
            
            System.Diagnostics.Debug.Assert(test.CompareTo(test) == 0);
        }

        [TestMethod]
        public void TestMutationDifferences()
        {
            Chromosome.SetTargetGene("1234ABCD");

            Chromosome chrome1 = new Chromosome("!@ACE345");
            Chromosome chrome2 = new Chromosome("789XYZ89");

            var children = chrome1.Mate(chrome2);

            foreach (Chromosome child in children)
            {
                System.Diagnostics.Debug.Assert(child.CompareTo(chrome1) != 0);
                System.Diagnostics.Debug.Assert(child.CompareTo(chrome2) != 0);
            }
        }
    }
}
