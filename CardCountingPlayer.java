
/**
 * Purpose: A CPU Blackjack player with a high-low strategy and the illustrious 18 deviations
 * Note that, unlike real card counters, the player will not quit the game when true count is below -1.0
 * Created by Yifan Zong
 * Created on: 26/12/2020
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CardCountingPlayer extends Player {
	private int[] betSpread;
	private BufferedReader reader;

	// Ask the user for a betting spread during construction
	public CardCountingPlayer(double bankroll, Shoe shoe) {
		super(bankroll, shoe);
		System.out.println("What is the betting spread?");
		System.out.println("For example, input \"1 2 4\" to bet");
		System.out.println(" 1 unit at a true count of 1 or less,");
		System.out.println(" 2 units at a true count of 2,");
		System.out.println(" 4 units at a true count of 3 or more.");
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
				for (int i = 0; i < input.length; i++) {
					betSpread[i] = Integer.parseInt(input[i]);
					// Check for invalid betting spreads (bets equal or below 0)
					if (betSpread[i] <= 0) {
						System.out.println("Please input valid integer betting units");
						allValid = false;
						break;
					}
				}
				
				if (betSpread.length > 0 && allValid) {
					break;
				}
			} catch (NumberFormatException e) {
				System.out.println("Please input a valid integer betting spread");
			}
		}
	}

	// Construct given a betting spread
	public CardCountingPlayer(double bankroll, Shoe shoe, int[] bettingSpread) {
		super(bankroll, shoe);
		betSpread = bettingSpread;
	}

	// Produces the next optimal move
	public static String getNextMove(PlayerHand playerHand, Card dealerCard, Shoe shoe) {
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

		// Raw output from simplified basic strat chart and deviations
		String output = applyChart(playerHand, dealerCard, chartNum, shoe);

		// Processes output
		switch (output) {
		case "X":
		case "D":
			return "D";
		case "O":
		case "Y":
			return "SP";
		case "N":
			return applyChart(playerHand, dealerCard, 0, shoe);
		default:
			return output;
		}
	}

	protected static String applyChart(PlayerHand playerHand, Card dealerCard, int chartNum, Shoe shoe) {
		// Handle deviations
		double trueCount = shoe.trueCount();
		int playerVal = playerHand.val(), dealerVal = dealerCard.val(), decks = shoe.decks();
		switch (chartNum) {
		case 0:
			// Rank 2
			if (playerVal == 16 && dealerVal == 10 && trueCount >= 0) {
				return "S";
			}

			// Rank 3
			if (playerVal == 15 && dealerVal == 10 && trueCount >= 4.0) {
				return "S";
			}

			// Rank 6
			if (playerVal == 10 && dealerVal == 10 && (trueCount >= 4.0 || (decks == 1 && trueCount >= 3.0))) {
				return "D";
			}

			// Rank 7, 8
			if (playerVal == 12 && ((dealerVal == 3 && (trueCount >= 3.0 || (decks > 2 && trueCount >= 2.0))
					|| (dealerVal == 2 && trueCount > 4.0)))) {
				return "S";
			}

			// Rank 9
			if (playerVal == 11 && dealerCard.rank() == 0
					&& (trueCount >= 0.0 || (decks <= 2 && trueCount >= -1.0) || (decks == 1 && trueCount >= -2.0))) {
				return "D";
			}

			// Rank 10
			if (playerVal == 9 && dealerVal == 2 && trueCount >= 1.0) {
				return "D";
			}

			// Rank 11
			if (playerVal == 10 && dealerCard.rank() == 0 && (trueCount >= 3.0 || (decks == 1 && trueCount >= 2.0))) {
				return "D";
			}

			// Rank 12
			if (playerVal == 9 && dealerVal == 7 && trueCount >= 4.0) {
				return "D";
			}

			// Rank 13
			if (playerVal == 16 && dealerVal == 9 && trueCount >= 5.0) {
				return "S";
			}

			// Rank 14
			if (playerVal == 13 && dealerVal == 2 && trueCount <= 0.0) {
				return "H";
			}

			// Rank 15, 16, 17
			if (playerVal == 12 && ((dealerVal == 4 && (trueCount <= 0.0 || (decks == 1 && trueCount <= 1.0)))
					|| (dealerVal == 5 && (trueCount <= -1.0 || (decks == 1 && trueCount <= 0.0)))
					|| (dealerVal == 6 && (trueCount <= -3.0 || (decks == 1 && trueCount <= -2.0))))) {
				return "H";
			}

			// Rank 18
			if (playerVal == 13 && dealerVal == 3 && (trueCount <= -2.0 || (decks == 1 && trueCount <= -1.0))) {
				return "H";
			}
			break;
		case 2:
			// Rank 4, 5
			if (playerVal == 20 && ((dealerVal == 5 && trueCount >= 5.0) || (dealerVal == 6 && trueCount >= 4.0))) {
				return "SP";
			}
		}

		return BasicStratPlayer.applyChart(playerHand, dealerCard, chartNum);
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
				applyMove(hand, dealerCard, applyChart(hand, dealerCard, 0, shoe()));
				// e.printStackTrace();
			}
		}
	}

	@Override
	// Place insurance according to deviation
	public ArrayList<PlayerHand> placeInsurances() {
		// Rank 1
		if (shoe().trueCount() >= 3.0 || (shoe().decks() >= 2 && shoe().trueCount() >= 2.4)
				|| (shoe().decks() == 1 && shoe().trueCount() >= 1.4))
			for (int i = 0; i < hands().size(); i++) {
				System.out.println(hands().get(i));
				try {
					placeInsurance(hands().get(i));
					System.out.println("Insurance placed");
				} catch (ExceedsBankrollException e) {
					System.out.println("Not enough bankroll to place insurance");
				}
			}
		else {
			System.out.println("No Insurance");
		}
		return hands();
	}

	@Override
	// Place bets according to betting spread
	public ArrayList<PlayerHand> placeBets() {
		for (int i = 0; i < hands().size(); i++) {
			for (int j = betSpread.length; j > 0; j--) {
				if (j <= shoe().trueCount()) {
					try {
						placeBet(hands().get(i), betSpread[j - 1]);
						break;
					} catch (InvalidBetException e) {
						e.printStackTrace();
					} catch (ExceedsBankrollException e) {
						// e.printStackTrace();
					}
				} else if (j == 1) {
					try {
						placeBet(hands().get(i), betSpread[j - 1]);
					} catch (InvalidBetException e) {
						e.printStackTrace();
					} catch (ExceedsBankrollException e) {
						e.printStackTrace();
					}
				}
			}
			System.out.println("Bet: " + hands().get(i).bet());
		}

		return hands();
	}

	@Override
	// Ruined when having less bankroll than the smallest betting spread
	protected boolean ruined() {
		return bankroll() < betSpread[0];
	}

	@Override
	// Play the hand
	public ArrayList<PlayerHand> play(Card dealerCard) {
		for (int i = 0; i < hands().size(); i++) {
			System.out.println(hands().get(i));
			while (!hands().get(i).isDone()) {
				String cardCountingMove = getNextMove(hands().get(i), dealerCard, shoe());
				applyMove(hands().get(i), dealerCard, cardCountingMove);
			}
		}
		return hands();
	}
}
