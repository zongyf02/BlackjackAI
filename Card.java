
/**
 * Card object Require UTF8 encoding to view properly Author: Yifan Zong Created
 * on: 23/12/2020
 */
public class Card{
	private int id;

	// A Card is identified by an integer between 0 to 51
	public Card(int id) {
		this.id = id % 52;
	}

	// Rank of a card is an integer between 0 to 12
	public int rank() {
		return id % 13;
	}

	// Suit is an integer between 0 to 3
	public int suit() {
		return id / 13;
	}

	// Value of a numbered card is their face value, J/Q/K are 10, A is either 1 or 11
	public int val() {
		int val = rank();
		if (val > 9) {
			val = 9;
		}
		if (val == 0) {
			val = 10;
		}
		return val + 1;
	}

	// Count a card with the highLow system
	public int highLow() {
		int rank = rank();
		if (rank == 0 || rank >= 9) {
			return -1;
		}
		if (rank <= 5) {
			return 1;
		}
		return 0;
	}

	// Griffin 5 count
	//A 2 3 4 5 6 7 8 9 10
	//0 2 2 4 5 4 3 1 -1 -5
	public int G5() {
		int rank = rank();
		switch (rank) {
		case 0: return 0;
		case 1:
		case 2: return 2;
		case 3:
		case 5: return 4;
		case 4: return 5;
		case 6: return 3;
		case 7: return 1;
		case 8: return -1;
		default: return -5;
		}
	}
	
	// Griffin 7 count
	// A 2 3 4 5 6 7 8  9 10
	//-6 4 4 5 7 5 3 0 -2 -5
	public int G7() {
		int rank = rank();
		switch (rank) {
		case 0: return -6;
		case 1:
		case 2: return 4;
		case 3:
		case 5: return 5;
		case 4: return 7;
		case 6: return 3;
		case 7: return 0;
		case 8: return -2;
		default: return -5;
		}
	}
	
	@Override
	// Return the card as a string
	// Require UTF8 encoding to view properly due to unicode symbols
	public String toString() {
		int temp = rank();
		String rank = null;
		switch (temp) {
		case 0:
			rank = "A";
			break;
		case 10:
			rank = "J";
			break;
		case 11:
			rank = "Q";
			break;
		case 12:
			rank = "K";
			break;
		default:
			rank = Integer.toString(temp + 1);
			break;
		}

		temp = suit();
		String suit = null;
		switch (temp) {
		case 0:
			suit = "\u2666";
			break;
		case 1:
			suit = "\u2663";
			break;
		case 2:
			suit = "\u2665";
			break;
		case 3:
			suit = "\u2660";
		}

		return rank + suit;
	}
}
