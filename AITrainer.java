
/**
* Purpose: Trainan AI with genetic algo
 * Author: Yifan Zong
 * Created on: 1/2/21
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Comparator;

public class AITrainer {
	private double mutationRate, mutationStep;
	private int gen;
	private Chromosome[] population;

	// Set population size, mutation rate, mutation step, and create the appropriate
	// amount of new chromosomes
	public AITrainer(int populationSize, double mutationRate, double mutationStep) {
		gen = 0;
		this.mutationRate = mutationRate;
		this.mutationStep = mutationStep;
		population = new Chromosome[populationSize];
		for (int i = 0; i < populationSize; i++) {
			Chromosome chromosome = new Chromosome();
			chromosome.updateFitness();
			population[i] = chromosome;
		}
	}

	// Create a new chromosome from 2 chromosomes
	public Chromosome breed(Chromosome chromosome1, Chromosome chromosome2) {
		double[][] playingW1, splitW1, playingW2, splitW2, playingW, splitW;
		double[] playingB1, splitB1, insuranceW1, insuranceB1, bettingW1, bettingB1, playingB2, splitB2, insuranceW2,
				insuranceB2, bettingW2, bettingB2, playingB, splitB, insuranceW, insuranceB, bettingW, bettingB;
		playingW1 = chromosome1.playingW();
		playingB1 = chromosome1.playingB();
		splitW1 = chromosome1.splitW();
		splitB1 = chromosome1.splitB();
		insuranceW1 = chromosome1.insuranceW();
		insuranceB1 = chromosome1.insuranceB();
		bettingW1 = chromosome1.bettingW();
		bettingB1 = chromosome1.bettingB();
		playingW2 = chromosome2.playingW();
		playingB2 = chromosome2.playingB();
		splitW2 = chromosome2.splitW();
		splitB2 = chromosome2.splitB();
		insuranceW2 = chromosome2.insuranceW();
		insuranceB2 = chromosome2.insuranceB();
		bettingW2 = chromosome2.bettingW();
		bettingB2 = chromosome2.bettingB();
		playingW = new double[3][4];
		playingB = new double[3];
		splitW = new double[2][3];
		splitB = new double[2];
		insuranceW = new double[2];
		insuranceB = new double[2];
		bettingW = new double[5];
		bettingB = new double[5];

		// Cross-over playingW
		for (int i = 0; i < playingW.length; i++) {
			for (int j = 0; j < playingW[i].length; j++) {
				if ((int) (Math.random() * 2) == 0) {
					playingW[i][j] = playingW1[i][j];
				} else {
					playingW[i][j] = playingW2[i][j];
				}
			}
		}

		// Cross-over playingB
		for (int i = 0; i < playingB.length; i++) {
			if ((int) (Math.random() * 2) == 0) {
				playingB[i] = playingB1[i];
			} else {
				playingB[i] = playingB2[i];
			}
		}

		// Cross-over splitW
		for (int i = 0; i < splitW.length; i++) {
			for (int j = 0; j < splitW[i].length; j++) {
				if ((int) (Math.random() * 2) == 0) {
					splitW[i][j] = splitW1[i][j];
				} else {
					splitW[i][j] = splitW2[i][j];
				}
			}
		}

		// Cross-over splitB
		for (int i = 0; i < splitB.length; i++) {
			if ((int) (Math.random() * 2) == 0) {
				splitB[i] = splitB1[i];
			} else {
				splitB[i] = splitB2[i];
			}
		}

		// Cross-over insuranceW
		for (int i = 0; i < insuranceW.length; i++) {
			if ((int) (Math.random() * 2) == 0) {
				insuranceW[i] = insuranceW1[i];
			} else {
				insuranceW[i] = insuranceW2[i];
			}
		}

		// Cross-over insuranceB
		for (int i = 0; i < insuranceB.length; i++) {
			if ((int) (Math.random() * 2) == 0) {
				insuranceB[i] = insuranceB1[i];
			} else {
				insuranceB[i] = insuranceB2[i];
			}
		}

		// Cross-over bettingW
		for (int i = 0; i < bettingW.length; i++) {
			if ((int) (Math.random() * 2) == 0) {
				bettingW[i] = bettingW1[i];
			} else {
				bettingW[i] = bettingW2[i];
			}
		}

		// Cross-over bettingB
		for (int i = 0; i < bettingB.length; i++) {
			if ((int) (Math.random() * 2) == 0) {
				bettingB[i] = bettingB1[i];
			} else {
				bettingB[i] = bettingB2[i];
			}
		}

		double fitness = (chromosome1.fitness() + chromosome2.fitness()) / 2.0;

		return new Chromosome(playingW, playingB, splitW, splitB, insuranceW, insuranceB, bettingW, bettingB, fitness);
	}

	// Select 60% of top, breed and mutate for n gens then ask the user for
	// permission to continue
	public void train() {
		while (true) {
			// Sort population by fitness
			Arrays.parallelSort(population, new Comparator<Chromosome>() {
				@Override
				public int compare(Chromosome chromosome1, Chromosome chromosome2) {
					return (int) chromosome2.fitness() - (int) chromosome1.fitness();
				}
			});

			// Serialize and store the elite
			Chromosome elite0 = population[0];
			try {
				FileOutputStream fileOut = new FileOutputStream("Elite0" + "Gen" + gen + ".ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(elite0);
				out.close();
				fileOut.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("Gen" + gen + ": " + elite0.fitness());

			// Let the top 10% breed for the new 10% of the population
			Chromosome[] newPopulation = new Chromosome[population.length];
			for (int j = 0; j < (int) (newPopulation.length * 0.1); j++) {
				int rnd1 = 0, rnd2 = 0;
				while (rnd1 == rnd2) {
					rnd1 = (int) (Math.random() * population.length / 10.0);
					rnd2 = (int) (Math.random() * population.length / 10.0);
				}
				Chromosome newChromosome = breed(population[rnd1], population[rnd2]);
				newPopulation[j] = newChromosome.mutate(mutationRate, mutationStep);
			}

			// Let a new 75% bred from the old top 50%
			for (int j = (int) (newPopulation.length * 0.1); j < (int) (newPopulation.length * 0.85); j++) {
				int rnd1 = 0, rnd2 = 0;
				while (rnd1 == rnd2) {
					rnd1 = (int) (Math.random() * population.length / 2.0);
					rnd2 = (int) (Math.random() * population.length / 2.0);
				}
				Chromosome newChromosome = breed(population[rnd1], population[rnd2]);
				newPopulation[j] = newChromosome.mutate(mutationRate, mutationStep);
			}

			// Let the last 15% be from random breeding of the whole old population
			// Significantly increases genetic diversity
			for (int j = (int) (newPopulation.length * 0.85); j < newPopulation.length; j++) {
				int rnd1 = 0, rnd2 = 0;
				while (rnd1 == rnd2) {
					rnd1 = (int) (Math.random() * population.length);
					rnd2 = (int) (Math.random() * population.length);
				}
				Chromosome newChromosome = breed(population[rnd1], population[rnd2]);
				newPopulation[j] = newChromosome.mutate(mutationRate, mutationStep);
			}

			population = newPopulation;
			gen++;
		}
	}

	// Retrieve the best chomosome from the nth generation
	public Chromosome retrieveElite(int gen) {
		Chromosome elite = null;
		try {
			FileInputStream fileIn = new FileInputStream("Elite0" + "Gen" + gen + ".ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			elite = (Chromosome) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return elite;
	}

	// Calculate the mean fitness
	public static double fitnessMean(Chromosome[] population) {
		double sum = 0.0;
		for (Chromosome chromosome : population) {
			sum += chromosome.fitness();
		}
		return sum / population.length;
	}

	// Calculate the SD of fitness
	public static double fitnessSD(Chromosome[] population) {
		double standardDeviation = 0.0;
		double mean = fitnessMean(population);

		for (Chromosome chromosome : population) {
			standardDeviation += Math.pow(chromosome.fitness() - mean, 2);
		}

		return Math.sqrt(standardDeviation / population.length);
	}

	public static void main(String[] args) {
		AITrainer trainer = new AITrainer(0, 0, 0); // Select population, mutation rate, and step
//		trainer.train();
		for (int i = 0; i < 145; i++) {
			double fitness = 0;
			Chromosome elite = trainer.retrieveElite(i);
			fitness = elite.fitness();

			System.out.println("Gen" + i + ": " + fitness);
		}
	}
}
