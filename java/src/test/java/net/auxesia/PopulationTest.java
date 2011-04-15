/*
* The MIT License
* 
* Copyright (c) 2011 John Svazic
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
package net.auxesia;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

/**
 * JUnit 4 tests for <code>net.auxesia.Population</code>.
 * 
 * @see net.auxesia.Population
 * 
 * @author John Svazic
 * @version 1.0
 */
public class PopulationTest {
	/**
	 * Method to test <code>Population.getCrossover()</code>.
	 * 
	 * @see net.auxesia.Population#getCrossover()
	 */
	@Test
	public void testGetCrossover() {
		Population pop = new Population(1024, 0.8f, 0.1f, 0.05f);
		assertEquals(80, (int) (pop.getCrossover() * 100));
		
		pop = new Population(1024, 0.0f, 0.1f, 0.05f);
		assertEquals(0, (int) (pop.getCrossover() * 100));
		
		pop = new Population(1024, 1.0f, 0.1f, 0.05f);
		assertEquals(100, (int) (pop.getCrossover() * 100));
	}
	
	/**
	 * Method to test <code>Population.getElitism()</code>.
	 * 
	 * @see net.auxesia.Population#getElitism()
	 */
	@Test
	public void testGetElitism() {
		Population pop = new Population(1024, 0.8f, 0.1f, 0.05f);
		assertEquals(10, (int) (pop.getElitism() * 100));
		
		pop = new Population(1024, 0.8f, 0.0f, 0.05f);
		assertEquals(0, (int) (pop.getElitism() * 100));
		
		pop = new Population(1024, 0.8f, 0.99f, 0.05f);
		assertEquals(99, (int) (pop.getElitism() * 100));
	}

	/**
	 * Method to test <code>Population.getMutation()</code>.
	 * 
	 * @see net.auxesia.Population#getMutation()
	 */
	@Test
	public void testGetMutation() {
		Population pop = new Population(1024, 0.8f, 0.1f, 0.05f);
		assertEquals(5, (int) (pop.getMutation() * 100));
		
		pop = new Population(1024, 0.8f, 0.1f, 0.0f);
		assertEquals(0, (int) (pop.getMutation() * 100));
		
		pop = new Population(1024, 0.8f, 0.1f, 1.0f);
		assertEquals(100, (int) (pop.getMutation() * 100));
	}

	/**
	 * Method to test <code>Population.getPopulation()</code>.
	 * 
	 *  @see net.auxesia.Population#getPopulation()
	 */
	@Test
	public void testGetPopulation() {
		Population pop   = new Population(1024, 0.8f, 0.1f, 0.05f);
		Chromosome[] arr = pop.getPopulation();
		
		assertEquals(1024, arr.length);
		
		Chromosome[] newArr = new Chromosome[arr.length];
		System.arraycopy(arr, 0, newArr, 0, newArr.length);
		Arrays.sort(newArr);
		
		// Assert that the array is actually sorted.
		assertArrayEquals(arr, newArr);
	}
		
	/**
	 * Method to test <code>Population.evolve()</code>.
	 * 
	 *  @see net.auxesia.Population#evolve()
	 */
	@Test
	public void testEvolve() {
		Population pop = new Population(1024, 0.8f, 0.1f, 0.05f);
		Chromosome[] oldArr = pop.getPopulation();
		
		// Evolve and get the new population
		pop.evolve();
		Chromosome[] newArr = pop.getPopulation();
		
		// Check the details on the resulting evolved population.
		assertEquals(80, (int) (pop.getCrossover() * 100));
		assertEquals(10, (int) (pop.getElitism() * 100));
		assertEquals(5, (int) (pop.getMutation() * 100));
		
		// Check to ensure that the elitism took.
		final int elitismCount = Math.round(1024 * 0.1f);
		int counter = 0;
		for (int i = 0; i < oldArr.length; i++) {
			if (Arrays.binarySearch(newArr, oldArr[i]) >= 0) {
				++counter;
			}
		}
		// Account for the fact that mating/mutation may cause more than
		// just the fixed number of chromosomes to be identical.
		assertTrue(counter >= elitismCount);
		assertTrue(counter < oldArr.length);
	}
}