/**
 * A Player's Hand Author: Yifan Zong Created on: 23/12/2020
 */

public class PlayerHand extends Hand{
	private double bet;
	private boolean done, insured;

	// Set bet to 0 and done to false
	public PlayerHand(Card... cards) {
		super(cards);
		bet = 0.0;
		done = false;
		insured = false;
	}

	// Set done to true
	public PlayerHand done() {
		done = true;
		return this;
	}

	// Return done
	public boolean isDone() {
		return done;
	}

	// Return bet
	public double bet() {
		return bet;
	}

	// Update bet
	public PlayerHand placeBet(double bet) {
		this.bet = bet;
		return this;
	}

	// Return insurance
	public boolean insured() {
		return insured;
	}

	// Place an insurance
	public PlayerHand placeInsurance() {
		insured = true;
		return this;
	}

	@Override
	// Done if Hand value exceeds 21
	protected void updateVal(Card card) {
		super.updateVal(card);
		if (busted()) {
			done = true;
		}
	}

	@Override
	// Reset bet and done when clearing Hand
	public PlayerHand clear() {
		super.clear();
		bet = 0.0;
		done = false;
		insured = false;
		return this;
	}
}
