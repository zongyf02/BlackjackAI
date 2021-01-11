
/**
 * Purpose: An AI player learning via genetic algorithm
 * Author: Yifan Zong
 * Created on: 1/2/21
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class LearningAIPlayer extends Player {
	private int[] betSpread;
	private Chromosome chromosome;
	private BufferedReader reader;

	// Construct a player from a chromosome
	public LearningAIPlayer(double bankroll, Shoe shoe, int[] bettingSpread, Chromosome chromosome) {
		super(bankroll, shoe);
		betSpread = bettingSpread;
		this.chromosome = chromosome;
	}

	// Construct a player from the elite chromosome, asking player for the betting spread
	public LearningAIPlayer(double bankroll, Shoe shoe) {
		super(bankroll, shoe);
		chromosome = getEliteChromosome();
		System.out.println("What is the betting spread? Please enter a 5 unit betting spread.");
		System.out.println("For example, input \"1 2 4 6 8\" to bet");
		System.out.println(" 1 unit at a true count of 1 or less,");
		System.out.println(" 2 units at a true count of 2,");
		System.out.println(" 4 units at a true count of 3,");
		System.out.println(" 6 units at a true count of 4,");
		System.out.println(" 8 units at a true count of 5 or more.");
		reader = new BufferedReader(new InputStreamReader(System.in));
		String input[] = null;
		while (true) {
			try {
				input = reader.readLine().split(" ");
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				boolean allValid = true;
				betSpread = new int[input.length];
				if (betSpread.length != 5) {
					System.out.println("Please enter exactly 5 integer betting units");
					continue;
				}

				for (int i = 0; i < input.length; i++) {
					betSpread[i] = Integer.parseInt(input[i]);
					// Check for invalid betting spreads (bets equal or below 0)
					if (betSpread[i] <= 0) {
						System.out.println("Please input valid integer betting units");
						allValid = false;
						break;
					}
				}

				if (allValid) {
					break;
				}
			} catch (NumberFormatException e) {
				System.out.println("Please input a valid integer betting spread");
			}
		}
	}

	//Serialize and return a chromsome named elite
	public static Chromosome getEliteChromosome() {
		Chromosome elite = null;
		try {
			FileInputStream fileIn = new FileInputStream("Elite.ser");
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
	
	// Decide whether to split based on the splitting weight
	public static boolean split(PlayerHand playerHand, Chromosome chromosome, Card dealerCard, Shoe shoe) {
		double[] input = new double[3];
		input[0] = shoe.G5TrueCount();
		input[1] = dealerCard.val();
		input[2] = playerHand.val();
		double score1 = 0, score2 = 0;
		for (int i = 0; i < input.length; i++) {
			score1 += input[i] * chromosome.splitW()[0][i];
			score2 += input[i] * chromosome.splitW()[1][i];
		}
		score1 += chromosome.splitB()[0];
		score2 += chromosome.splitB()[1];

		return score1 > score2;
	}

	// Determine the optimal move if split is unavailable
	public static String getNonSplitMove(PlayerHand playerHand, Chromosome chromosome, Card dealerCard, Shoe shoe) {
		double[] input = new double[4];
		input[0] = shoe.G5TrueCount();
		input[1] = dealerCard.val();
		input[2] = playerHand.val();
		input[3] = playerHand.aces();
		double[] score = new double[3];
		double maxScore = Double.NEGATIVE_INFINITY;
		int maxIndex = -1;
		for (int i = 0; i < score.length; i++) {
			for (int j = 0; j < input.length; j++) {
				score[i] += input[j] * chromosome.playingW()[i][j] + chromosome.playingB()[i];
			}
			score[i] += chromosome.playingB()[i];

			if (maxScore <= score[i]) {
				maxScore = score[i];
				maxIndex = i;
			}
		}

		switch (maxIndex) {
		case 0:
			return "S";
		case 1:
			return "H";
		case 2:
			return "D";
		}
		return null;
	}

	// Determine the optimal move
	public static String getNextMove(PlayerHand playerHand, Chromosome chromosome, Card dealerCard, Shoe shoe) {
		// Try splitting if splitting is valid
		if (playerHand.size() == 2 && playerHand.get(0) == playerHand.get(1)
				&& split(playerHand, chromosome, dealerCard, shoe)) {
			return "SP";
		} else {
			return getNonSplitMove(playerHand, chromosome, dealerCard, shoe);
		}
	}

	// Apply the optimal move
	protected void applyMove(PlayerHand hand, Card dealerCard, String move) {
		switch (move) {
		case "D":
			// Double down
			try {
				System.out.println("D");
				doubleDown(hand, hand.bet());
				break;
			} catch (InvalidDoubleDownException e) {
				// e.printStackTrace();
			} catch (InvalidBetException e) {
				e.printStackTrace();
			} catch (ExceedsBankrollException e) {
				System.out.println("Not enough bankroll");
				// e.printStackTrace();
			} catch (ExceedsBetException e) {
				e.printStackTrace();
			}
		case "H":
			System.out.println("H");
			hit(hand); // Hit
			break;
		case "S":
			System.out.println("S");
			stand(hand); // Stand
			break;
		case "SP":
			// Split
			try {
				System.out.println("SP");
				split(hand);
				break;
			} catch (InvalidSplitException e) {
				e.printStackTrace();
			} catch (ExceedsBankrollException e) {
				System.out.println("Not enough bankroll");
				applyMove(hand, dealerCard, getNonSplitMove(hand, chromosome, dealerCard, shoe()));
				// e.printStackTrace();
			}
		}
	}

	@Override
	// Place bets for each of the hands
	public ArrayList<PlayerHand> placeBets() {
		// Determine the optimal amount to bet
		double maxScore = Double.NEGATIVE_INFINITY;
		int maxIndex = -1;
		for (int i = 0; i < chromosome.bettingW().length; i++) {
			double score = shoe().G7TrueCount() * chromosome.bettingW()[i] + chromosome.bettingB()[i];
			if (maxScore <= score) {
				maxScore = score;
				maxIndex = i;
			}
		}

		// Bet that amount for each hand
		for (int i = 0; i < hands().size(); i++) {
			try {
				placeBet(hands().get(i), betSpread[maxIndex]);
				System.out.println("Bet: " + betSpread[maxIndex]);
			} catch (InvalidBetException e) {
				e.printStackTrace();
			} catch (ExceedsBankrollException e) {
				maxIndex--;
				i--;
				// e.printStackTrace();
			}
		}

		return hands();
	}

	@Override
	// Place insurance for each of the hands
	public ArrayList<PlayerHand> placeInsurances() {
		double trueCount = shoe().G5TrueCount();
		double score1 = trueCount * chromosome.insuranceW()[0] + chromosome.insuranceB()[0];
		double score2 = trueCount * chromosome.insuranceW()[1] + chromosome.insuranceB()[1];

		for (int i = 0; i < hands().size(); i++) {
			if (score1 > score2) {
				try {
					placeInsurance(hands().get(i));
					System.out.println("Insurance placed");
				} catch (ExceedsBankrollException e) {
					System.out.println("Not enough bankroll to place insurance");
				}
			} else {
				System.out.println("No insurance");

			}
		}

		return hands();
	}

	@Override
	// Play each hand
	public ArrayList<PlayerHand> play(Card dealerCard) {
		for (int i = 0; i < hands().size(); i++) {
			System.out.println(hands().get(i));
			while (!hands().get(i).isDone()) {
				String move = getNextMove(hands().get(i), chromosome, dealerCard, shoe());
				applyMove(hands().get(i), dealerCard, move);
			}
		}
		return hands();
	}

	@Override
	// Player ruined if having less than the minimum betSpread
	protected boolean ruined() {
		return bankroll() < betSpread[0];
	}
}
