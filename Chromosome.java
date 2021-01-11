/**
 * Purpose: A Chromsome of an AI player with linear transformations
 * Author: Yifan Zong
 * Created on: 1/2/21
 */

import java.io.Serializable;
import java.util.Arrays;

public class Chromosome implements Serializable {
	private static final long serialVersionUID = 6769372495083936429L;
	private double[][] playingW, splitW;
	private double[] insuranceW, bettingW, playingB, splitB, insuranceB, bettingB;
	private double fitness;

	// Create new chromosome given matrices
	public Chromosome(double[][] playingW, double[] playingB, double[][] splitW, double[] splitB, double[] insuranceW,
			double[] insuranceB, double[] bettingW, double[] bettingB, double fitness) {
		this.playingW = playingW;
		this.playingB = playingB;
		this.splitW = splitW;
		this.splitB = splitB;
		this.insuranceW = insuranceW;
		this.insuranceB = insuranceB;
		this.bettingW = bettingW;
		this.bettingB = bettingB;
		this.fitness = fitness;
	}

	// Create new random chromosome
	public Chromosome() {
		playingW = new double[3][4];
		playingB = new double[3];
		splitW = new double[2][3];
		splitB = new double[2];
		insuranceW = new double[2];
		insuranceB = new double[2];
		bettingW = new double[5];
		bettingB = new double[5];

		// Random playingW
		for (int i = 0; i < playingW.length; i++) {
			for (int j = 0; j < playingW[i].length; j++) {
				playingW[i][j] = Math.random();
			}
		}

		// Random playingB
		for (int i = 0; i < playingB.length; i++) {
			playingB[i] = Math.random();
		}

		// Random splitW
		for (int i = 0; i < splitW.length; i++) {
			for (int j = 0; j < splitW[i].length; j++) {
				splitW[i][j] = Math.random();
			}
		}

		// Random splitB
		for (int i = 0; i < splitB.length; i++) {
			splitB[i] = Math.random();
		}

		// Random insuranceW
		for (int i = 0; i < insuranceW.length; i++) {
			insuranceW[i] += Math.random();
		}

		// Random insuranceB
		for (int i = 0; i < insuranceB.length; i++) {
			insuranceB[i] += Math.random();
		}

		// Random bettingW
		for (int i = 0; i < bettingW.length; i++) {
			bettingW[i] += Math.random();
		}

		// Random bettingB
		for (int i = 0; i < bettingB.length; i++) {
			bettingB[i] += Math.random();
		}
	}

	// Getters
	public double[][] playingW() {
		return playingW;
	}

	public double[] playingB() {
		return playingB;
	}

	public double[][] splitW() {
		return splitW;
	}

	public double[] splitB() {
		return splitB;
	}

	public double[] insuranceW() {
		return insuranceW;
	}

	public double[] insuranceB() {
		return insuranceB;
	}

	public double[] bettingW() {
		return bettingW;
	}

	public double[] bettingB() {
		return bettingB;
	}

	// Mutate each W and B
	public Chromosome mutate(double rate, double step) {
		double fitnessRate = (200000.0 - fitness) / 200000.0; // Modifies the step of mutations based on fitness

		// Mutate playingW
		for (int i = 0; i < playingW.length; i++) {
			for (int j = 0; j < playingW[i].length; j++) {
				if (Math.random() < rate) {
					playingW[i][j] += (Math.random() * 2.0 * step - step) * playingW[i][j] * fitnessRate;
				}
			}
		}

		// Mutate playingB
		for (int i = 0; i < playingB.length; i++) {
			if (Math.random() < rate) {
				playingB[i] += (Math.random() * 2.0 * step - step) * playingB[i] * fitnessRate;
			}
		}

		// Mutate splitW
		for (int i = 0; i < splitW.length; i++) {
			for (int j = 0; j < splitW[i].length; j++) {
				if (Math.random() < rate) {
					splitW[i][j] += (Math.random() * 2.0 * step - step) * splitW[i][j] * fitnessRate;
				}
			}
		}

		// Mutate splitB
		for (int i = 0; i < splitB.length; i++) {
			if (Math.random() < rate) {
				splitB[i] += (Math.random() * 2.0 * step - step) * splitB[i] * fitnessRate;
			}
		}

		// Mutate insuranceW
		for (int i = 0; i < insuranceW.length; i++) {
			if (Math.random() < rate) {
				insuranceW[i] += (Math.random() * 2.0 * step - step) * insuranceW[i] * fitnessRate;
			}
		}

		// Mutate insuranceB
		for (int i = 0; i < insuranceB.length; i++) {
			if (Math.random() < rate) {
				insuranceB[i] += (Math.random() * 2.0 * step - step) * insuranceB[i] * fitnessRate;
			}
		}

		// Mutate bettingW
		for (int i = 0; i < bettingW.length; i++) {
			if (Math.random() < rate) {
				bettingW[i] += (Math.random() * 2.0 * step - step) * bettingW[i] * fitnessRate;
			}
		}

		// Mutate bettingB
		for (int i = 0; i < bettingB.length; i++) {
			if (Math.random() < rate) {
				bettingB[i] += (Math.random() * 2.0 * step - step) * bettingB[i] * fitnessRate;
			}
		}
		updateFitness();
		return this;
	}

	// Evaluate fitness with a blackjack game
	// Fitness is the avg number of rounds played/betting unit (100 repetitions)
	public double updateFitness() {
		Shoe shoe = new Shoe(6);
		int[] betSpread = {1, 2, 4, 6, 8 };
		LearningAIPlayer player = new LearningAIPlayer(50000, shoe, betSpread, this);
		Blackjack game = new Blackjack(player, shoe);
		fitness = game.play() / 50000;
		return this.fitness;
	}

	public double fitness() {
		return fitness;
	}

	@Override
	public String toString() {
		return Arrays.deepToString(playingW) + "\n" + Arrays.deepToString(splitW) + "\n" + Arrays.toString(insuranceW)
				+ "\n" + Arrays.toString(bettingW) + "\n";
	}

}
