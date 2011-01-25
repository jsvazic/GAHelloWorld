package net.auxesia;

import java.util.Random;

public class Chromosome implements Comparable<Chromosome> {
	public final String gene;
	public final int fitness;
	
	private static final char[] TARGET_GENE = "Hello world!".toCharArray();
		
	public Chromosome(String gene) {
		this.gene    = gene;
		this.fitness = calculateFitness(gene);
	}
	
	private int calculateFitness(String gene) {
		int fitness = 0;
		char[] arr  = gene.toCharArray();
		for (int i = 0; i < arr.length; i++) {
			fitness += Math.abs(((int) arr[i]) - ((int) TARGET_GENE[i]));
		}
		
		return fitness;
	}
	
	public Chromosome mutate() {
		Random rand = new Random(System.currentTimeMillis());
		char[] arr  = gene.toCharArray();
		int idx     = rand.nextInt(arr.length);
		int delta   = (rand.nextInt() % 90) + 32;
		arr[idx]    = (char) ((arr[idx] + delta) % 122);

		return new Chromosome(String.valueOf(arr));
	}
	
	public Chromosome mate(Chromosome mate) {
		Random rand  = new Random(System.currentTimeMillis());
		
		char[] child = new char[gene.length()];	
		char[] arr1  = gene.toCharArray();
		char[] arr2  = mate.gene.toCharArray();
		int pivot    = rand.nextInt(child.length);
		
		System.arraycopy(arr1, 0, child, 0, pivot);
		System.arraycopy(arr2, pivot, child, pivot, (child.length - pivot));
		
		return new Chromosome(String.valueOf(child)); 
	}
	
	public static Chromosome generateRandom() {
		Random rand = new Random(System.currentTimeMillis());
		
		char[] arr = new char[TARGET_GENE.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = (char) ((rand.nextInt() % 90) + 32);
		}

		return new Chromosome(String.valueOf(arr));
	}

	@Override
	public int compareTo(Chromosome c) {
		if (fitness < c.fitness) {
			return -1;
		} else if (fitness > c.fitness) {
			return 1;
		}
		
		return 0;
	}
}