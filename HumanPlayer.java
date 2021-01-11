
/**
 * A human player that asks users for decisions
 * Author: Yifan Zong
 * Created on: 23/12/2020
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class HumanPlayer extends Player {
	private BufferedReader reader;
	private String input;

	public HumanPlayer(double bankroll, Shoe shoe) {
		super(bankroll, shoe);
		reader = new BufferedReader(new InputStreamReader(System.in));
	}

	// Ask the user for a new bet and double down
	private PlayerHand doubleDown(PlayerHand hand) {
		// New bet
		System.out.println("Previous bet: " + hand.bet());
		System.out.println("How much to double down?");
		int bet = 0;
		while (true) {
			try {
				input = reader.readLine();
				bet = Integer.parseInt(input);
				doubleDown(hand, bet); // Double down
				break;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidDoubleDownException e) {
				System.out.println("You are not allowed to double down.");
			} catch (NumberFormatException | InvalidBetException e) {
				System.out.println("Invalid bet, please enter a valid integer.");
			} catch (ExceedsBankrollException e) {
				System.out.println("Bet exceeds bankroll. Please place a lower bet.");
			} catch (ExceedsBetException e) {
				System.out.println("Bet exceeds previous bet. Please place a lower bet.");
			}
		}

		return hand;
	}

	@Override
	// Player is ruined when they have less than 1 betting unit
	protected boolean ruined() {
		return bankroll() < 1.0;
	}

	@Override
	// Ask ther user to place a bet for each of their hands
	public ArrayList<PlayerHand> placeBets() {
		for (int i = 0; i < hands().size(); i++) {
			int bet;
			System.out.println("How much to bet?");
			try {
				input = reader.readLine();
				bet = Integer.parseInt(input);
				placeBet(hands().get(i), bet); // Place the bet for a hand
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException | InvalidBetException e) {
				System.out.println("Invalid bet, please an integer betting unit.");
				i--;
			} catch (ExceedsBankrollException e) {
				System.out.println("Bet exceeds bankroll. Please place a lower bet.");
				i--;
			}
		}

		return hands();
	}

	@Override
	// Ask the user to place an insurance;
	public ArrayList<PlayerHand> placeInsurances() {
		for (int i = 0; i < hands().size(); i++) {
			System.out.println(hands().get(i));
			System.out.println("Place an insurance? 1. Yes 2. No");
			try {
				int input;
				input = Integer.parseInt(reader.readLine());
				switch (input) {
				case 1:
					placeInsurance(hands().get(i));
					break;
				case 2:
					break;
				default: System.out.println("Please enter either \"1\" or \"2\".");
					i--;
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				System.out.println("Please enter either \"1\" or \"2\".");
				i--;
			} catch (ExceedsBankrollException e) {
				System.out.println("Not enough bankroll to place insurance");
			}
		}

		return hands();
	}

	@Override
	// Ask the user to play each of their hand
	public ArrayList<PlayerHand> play(Card dealerCard) {
		for (int i = 0; i < hands().size(); i++) {
			System.out.println(hands().get(i));
			// Loop until a Hand is done being played
			while (!hands().get(i).isDone()) {
				// Ask for player input
				System.out.println("What would you like to do? Press F for help.");
				try {
					input = reader.readLine().trim();
				} catch (IOException e) {
					e.printStackTrace();
				}

				switch (input.toUpperCase()) {
				case "D":
					// Double down
					if (bankroll() <= 0.0) {
						System.out.println("You don't have enough bankroll to double down.");
					} else {
						doubleDown(hands().get(i));
						break;
					}
				case "H":
					hit(hands().get(i)); // Hit
					break;
				case "S":
					stand(hands().get(i)); // Stand
					break;
				case "SP":
					// Split
					try {
						split(hands().get(i));
					} catch (InvalidSplitException e) {
						System.out.println("You are not allowed to split this hand.");
					} catch (ExceedsBankrollException e) {
						System.out.println("You don't have enough bankroll to split this hand.");
					}
					break;
				case "B":
					// Basic Player Strat
					System.out.println("Optimal move: " + BasicStratPlayer.getNextMove(hands().get(i), dealerCard));
					break;
				case "C":
					// Card Counter Strat
					System.out.println("Running count: " + shoe().runningCount());
					System.out.println("Decks Remaining: " + shoe().decksRemaining() + " True count: " + shoe().trueCount());
					System.out.println("Optimal move: " + CardCountingPlayer.getNextMove(hands().get(i), dealerCard, shoe()));
					break;
				case "G":
					//GA AI player
					System.out.println(LearningAIPlayer.getNextMove(hands().get(i), LearningAIPlayer.getEliteChromosome(), dealerCard, shoe()));
					break;
				case "F":
					System.out.println("H to hit. S to stand. D to double down. SP to split. B to consult basic strategy. C to consult card counting strategy. G to consult genetic algo player.");
					break;
				default:
					System.out.println("Invalid input, please enter again.");
				}
			}
		}

		return hands();
	}
}
