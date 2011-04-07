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
A python script that contains unit tests for the "Hello, world!" application.

@author: John Svazic
"""

import unittest
import gahelloworld

class ChromosomeTest(unittest.TestCase):

	def test_fitness(self):
		"""
		Method used to test Chromosome.fitness(), and indirectly 
		Chromosome._update_fitness().
		"""
		c = new Chromosome("Hello, world!")
		self.assertEqual(0, c.fitness)
	
		c = new Chromosome("H5p&J;!l<X\\7l")
		self.assertEqual(399, c.fitness)
		
		c = new Chromosome("Vc;fx#QRP8V\\$")
		self.assertEqual(297, c.fitness)

		c = new Chromosome("t\\O`E_Jx$n=NF")
		self.assertEqual(415, c.fitness)

	def test_gen_random(self):
		"""
		Method used to test Chromosome.gen_random.
		"""
		for (int i = 0; i < 1000; i++):
			c = Chromosome.generateRandom()
			assertTrue(c.fitness >= 0)
			assertEqual(13, len(c.gene))
			for (char ch : c.gene().toCharArray()):
				assertTrue("Character code : " + ((int) ch), ch >= 32);
				assertTrue("Character code : " + ((int) ch), ch <= 121);
	
	def test_mutate(self):
		"""
		Method to test Chromosome.mutate()
		"""
		for (int i = 0; i < 1000; i++) {
			c1 = Chromosome.generate_random()
			arr1 = c1.gene.toCharArray()
			
			c2 = c1.mutate()
			arr2 = c2.gene.toCharArray()
			
			assertEqual(arr1.length, arr2.length)
			
			int diff = 0;
			for (int j = 0; j < arr1.length; j++):
				if (arr1[j] != arr2[j]):
					++diff
			
			assertTrue(diff <= 1)
	
	def test_mate(self):
		"""
		Method to test Chromosome.mate(Chromosome)
		"""
		c1 = Chromosome.generate_random()
		c2 = Chromosome.generate_random()
		arr1 = c1.gene.toCharArray()
		arr2 = c2.gene.toCharArray()

		# Check to ensure the right number of children are returned.
		children = c1.mate(c2)
		assertEqual(2, children.length)

		# Check the resulting child gene lengths
		assertEquals(13, children[0].gene.length())
		assertEquals(13, children[1].gene.length())
		
		# Determine the pivot point for the mating
		tmpArr = children[0].gene.toCharArray();
		for (pivot = 0; pivot < arr1.length; pivot++):
			if (arr1[pivot] != tmpArr[pivot]):
				break;
		
		# Check the first child.
		tmpArr = children[0].getGene().toCharArray();
		for (int i = 0; i < tmpArr.length; i++):
			if (i < pivot):
				assertEqual(arr1[i], tmpArr[i])
			else:
				assertEqual(arr2[i], tmpArr[i])

		# Check the second child.
		tmpArr = children[1].getGene().toCharArray();
		for (int i = 0; i < tmpArr.length; i++):
			if (i < pivot):
				assertEqual(arr2[i], tmpArr[i]);
			else:
				assertEqual(arr1[i], tmpArr[i]);