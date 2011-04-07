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
from gahelloworld import (Chromosome, Population)

class ChromosomeTest(unittest.TestCase):

	def test_fitness(self):
		"""
		Method used to test Chromosome.fitness(), and indirectly 
		Chromosome._update_fitness().
		"""
		c = Chromosome("Hello, world!")
		self.assertEqual(0, c.fitness)
	
		c = Chromosome("H5p&J;!l<X\\7l")
		self.assertEqual(399, c.fitness)
		
		c = Chromosome("Vc;fx#QRP8V\\$")
		self.assertEqual(297, c.fitness)

		c = Chromosome("t\\O`E_Jx$n=NF")
		self.assertEqual(415, c.fitness)

	def test_gen_random(self):
		"""
		Method used to test Chromosome.gen_random.
		"""
		for i in range(1000):
			c = Chromosome.gen_random()
			self.assertTrue(c.fitness >= 0)
			self.assertEqual(13, len(c.gene))
			for ch in c.gene:
				self.assertTrue(ord(ch) >= 32)
				self.assertTrue(ord(ch) <= 121)
	
	def test_mutate(self):
		"""
		Method to test Chromosome.mutate()
		"""
		for i in range(1000):
			c1 = Chromosome.gen_random()
			c2 = c1.mutate()
		
			self.assertEqual(len(c1.gene), len(c2.gene))
			
			diff = 0;
			for j in range(len(c1.gene)):
				if c1.gene[j] != c2.gene[j]:
					++diff
			
			self.assertTrue(diff <= 1)
	
	def test_mate(self):
		"""
		Method to test Chromosome.mate(Chromosome)
		"""
		c1 = Chromosome.gen_random()
		c2 = Chromosome.gen_random()

		# Check to ensure the right number of children are returned.
		children = c1.mate(c2)
		self.assertEqual(2, len(children))

		# Check the resulting child gene lengths
		self.assertEquals(13, len(children[0].gene))
		self.assertEquals(13, len(children[1].gene))
		
		# Determine the pivot point for the mating
		tmpArr = children[0].gene
		for pivot in range(len(c1.gene)):
			if c1.gene[pivot] != tmpArr[pivot]:
				break;
		
		# Check the first child.
		for i in range(len(tmpArr)):
			if (i < pivot):
				self.assertEqual(c1.gene[i], tmpArr[i])
			else:
				self.assertEqual(c2.gene[i], tmpArr[i])

		# Check the second child.
		tmpArr = children[1].gene
		for i in range(len(tmpArr)):
			if i < pivot:
				self.assertEqual(c2.gene[i], tmpArr[i])
			else:
				self.assertEqual(c1.gene[i], tmpArr[i])
				
if __name__ == '__main__':
    unittest.main()