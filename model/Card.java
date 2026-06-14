/**
 * File: Card.java
 * @author  Daniel Le
 * @date    06/14/2026
 * @version 1.0
 * Defines playing card elements for a card simulation model.
 */

package model;

/**
 * Represents a single standard playing card with an associated suit, face rank, 
 * and blackjack point valuation.
 */
public class Card {

    /**
     * Enumeration representing the four standard suits of a playing card deck.
     */
    public enum Suit { CLUBS, DIAMONDS, HEARTS, SPADES }

    /**
     * Enumeration representing the rank face value of a card, along with 
     * its base scoring value in a standard game of Blackjack.
     */
    public enum Face {
        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6),
        SEVEN(7), EIGHT(8), NINE(9), TEN(10),
        JACK(10), QUEEN(10), KING(10), ACE(11);

        private final int value;

        /**
         * Internal constructor tying a numerical value to a face rank.
         * * @param value numerical card weight
         */
        Face(int value) { this.value = value; }

        /**
         * Retrieves the game scoring value of the specific face rank.
         * * @return point value
         */
        public int getValue() { return value; }
    }

    private final Suit suit;
    private final Face face;


    /**
     * Constructs a permanent card pairing a given suit and face designation.
     *
     * @param suit the target suit element (e.g., HEARTS)
     * @param face the target rank tier (e.g., ACE)
     */
    public Card(Suit suit, Face face) {
        this.suit = suit;
        this.face = face;
    }

    /**
     * Retrieves the standard Blackjack point configuration of this card.
     *
     * @return the point value assigned to this card
     */
    public int getValue() {
        return face.getValue(); 
    }

    /**
     * Verifies whether this specific card is an Ace.
     *
     * @return {@code true} if the face rank matches {@link Face#ACE}, otherwise {@code false}
     */
    public boolean isAce() {
        return face == Face.ACE; 
    }

    /**
     * Gets the suit characteristic of this card.
     *
     * @return the associated {@link Suit}
     */
    public Suit getSuit() { 
        return suit; 
    }

    /**
     * Gets the face rank designation of this card.
     *
     * @return the associated {@link Face}
     */
    public Face getFace() {
        return face; 
    }

    /**
     * Generates a compact string notation representing the card.
     * 
     * @return short string descriptor representing the card state
     */
    @Override
    public String toString() {
        return face.name().charAt(0) + "" + suit.name().charAt(0);
    }
}
