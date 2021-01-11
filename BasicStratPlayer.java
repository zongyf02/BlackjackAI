
/**
 * Purpose: A CPU Blackjack player using the Basic Strat Chart
 * Author: Alex Li
 * Created on: 23/12/2020
 * Modified by: Yifan Zong
 * Modified on: 26/12/2020
 */

import java.util.ArrayList;

public class BasicStratPlayer extends Player {
	// Initialize and populate basicChart and rowValues
	private static FileIO fileReader = new FileIO();
	private static final char[][][] basicChart = fileReader.getChart();
	private static final int[][] rowValues = fileReader.getRowValues();

	public BasicStratPlayer(double bankroll, Shoe shoe) {
		super(bankroll, shoe);
	}

	// Produces the next optimal move
	public static String getNextMove(PlayerHand playerHand, Card dealerCard) {
		// Determine if calculating hard or soft total, and whether there can be a split
		int chartNum = 0; // Default hard total
		// Soft total
		if (playerHand.aces() > 0) {
			chartNum = 1;
		}
		// Split
		if (playerHand.size() == 2 && playerHand.get(0).rank() == playerHand.get(1).rank()) {
			chartNum = 2;
		}

		// Raw output from the basic strat chart
		String output = applyChart(playerHand, dealerCard, chartNum);

		// Processes output and produces it as a string
		switch (output) {
		case "X":
		case "D":
			return "D";
		case "O":
		case "Y":
			return "SP";
		case "N":
			return applyChart(playerHand, dealerCard, 0);
		default:
			return output;
		}
	}

	// Determines the optimal move according to the basic strat chart
	protected static String applyChart(PlayerHand playerHand, Card dealerCard, int chartNum) {
		// Handle cases not in the simplified chart
		switch (chartNum) {
		case 0:
			if (playerHand.val() >= 17) {
				return "S";
			}
			if (playerHand.val() <= 8) {
				return "H";
			}
			break;
		case 1:
			if (playerHand.val() >= 20) {
				return "S";
			}
			break;
		}

		// Determine the row on the chart
		int i;
		for (i = 0; i < rowValues[chartNum].length; i++) {
			if (chartNum == 0 && rowValues[chartNum][i] == playerHand.val()) {
				break;
			} else if (chartNum == 1 && rowValues[chartNum][i] == playerHand.val() - 11) {
				break;
			} else if (chartNum == 2 && rowValues[chartNum][i] * 2 == playerHand.val()) {
				break;
			}
		}

		// Determine the column and produce the corresponding character
		for (int j = 0; j < basicChart[chartNum][i].length; j++) {
			if (j + 2 == dealerCard.val()) {
				return Character.toString(basicChart[chartNum][i][j]);
			}
		}

		return null;
	}

	// Apply a move
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
				applyMove(hand, dealerCard, applyChart(hand, dealerCard, 0));
				// e.printStackTrace();
			}
		}
	}

	@Override
	// Play the hand
	public ArrayList<PlayerHand> play(Card dealerCard) {
		for (int i = 0; i < hands().size(); i++) {
			System.out.println(hands().get(i));
			while (!hands().get(i).isDone()) {
				String move = getNextMove(hands().get(i), dealerCard);
				applyMove(hands().get(i), dealerCard, move);
			}
		}
		return hands();
	}

	@Override
	// Always bet 1 unit
	public ArrayList<PlayerHand> placeBets() {
		for (int i = 0; i < hands().size(); i++) {
			System.out.println("Bet: 1.0");
			try {
				placeBet(hands().get(i), 1.0);
			} catch (InvalidBetException e) {
				e.printStackTrace();
			} catch (ExceedsBankrollException e) {
				e.printStackTrace();
			}
		}
		return hands();
	}

	@Override
	// Player is ruined when having less one betting unit
	protected boolean ruined() {
		return bankroll() < 1.0;
	}

	@Override
	// Never place insurance
	public ArrayList<PlayerHand> placeInsurances() {
		for (int i = 0; i < hands().size(); i++) {
			System.out.println(hands().get(i));
			System.out.println("No Insurance");
		}
		return hands();
	}

}
