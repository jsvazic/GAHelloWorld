package net.auxesia;

public class GAHelloWorld {
	private static final int SIZE = 2048;
	private static final int MAX_ITERATIONS = 16384;
	private static final int TOURNAMENT_SIZE = 256;	
	private static final float CROSSOVER_RATIO = 0.80f;
	private static final float ELITISM_RATIO = 0.10f;
	private static final float MUTATION_RATIO = 0.03f;

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		Population pop = new Population(SIZE, TOURNAMENT_SIZE, 
				CROSSOVER_RATIO, ELITISM_RATIO, MUTATION_RATIO);

		int i = 0;
		while ((i++ <= MAX_ITERATIONS) && (pop.getBest().fitness != 0)) {
			System.out.println("Generation " + i + ": " + pop.getBest().gene);
			pop.evolve();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Generation " + i + ": " + pop.getBest().gene);
		System.out.println("Total execution time: " + ((endTime - startTime) / 1000) + "s");
	}
}