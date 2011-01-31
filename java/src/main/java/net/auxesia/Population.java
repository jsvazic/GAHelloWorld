package net.auxesia;

import java.util.Arrays;
import java.util.Random;

public class Population {
	
	private int tournamentSize;
	private float elitism;
	private float mutation;
	private float crossover;
	private Chromosome[] popArr;

	public Population(int size, int tournamentSize, float crossoverRatio, float elitismRatio, 
			float mutationRatio) {
		
		this.popArr = generateInitialPopulation(size);
		this.tournamentSize = tournamentSize;
		this.crossover = crossoverRatio;
		this.elitism = elitismRatio;
		this.mutation = mutationRatio;
	}

	private static Chromosome[] generateInitialPopulation(int size) {
		Chromosome[] arr = new Chromosome[size];
		for (int i = 0; i < size; i++) {
			arr[i] = Chromosome.generateRandom();
		}

		Arrays.sort(arr);
		return arr;
	}

	public void evolve() {
		Chromosome[] buffer = new Chromosome[popArr.length];
		Random rand = new Random(System.currentTimeMillis());
		int idx = Math.round(popArr.length * elitism);
		System.arraycopy(popArr, 0, buffer, 0, idx);

		while (idx < buffer.length) {
			if (rand.nextFloat() <= crossover) {
				buffer[idx] = popArr[idx];
				if (rand.nextFloat() <= mutation) {
					buffer[idx] = buffer[idx].mutate();
				}
			} else {
				Chromosome[] parents = selectParents();
				buffer[idx] = parents[0].mate(parents[1]);
				if (rand.nextFloat() <= mutation) {
					buffer[idx] = buffer[idx].mutate();
				}
				++idx;
				if (idx < buffer.length) {
					buffer[idx] = parents[1].mate(parents[0]);
					if (rand.nextFloat() <= mutation) {
						buffer[idx] = buffer[idx].mutate();
					}
				}
			}
			++idx;
		}

		Arrays.sort(buffer);
		popArr = buffer;
	}
	
	public Chromosome getBest() {
		return popArr[0];
	}
	
	private Chromosome[] selectParents() {
		Random rand = new Random(System.currentTimeMillis());
		Chromosome[] parents = new Chromosome[2];

		for (int i = 0; i < 2; i++) {
			parents[i] = popArr[rand.nextInt(popArr.length)];
			for (int j = 0; j < tournamentSize; j++) {
				int idx = rand.nextInt(popArr.length);
				if (popArr[idx].fitness < parents[i].fitness) {
					parents[i] = popArr[idx];
				}
			}
		}
		
		return parents;
	}
}