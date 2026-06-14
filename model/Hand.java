/**
 * File: Hand.java
 * @author  Daniel Le
 * @date    06/14/2026
 * @version 1.0
 * Manages hand scoring states for a Blackjack simulation.
 */

package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a collection of cards held by a player or the dealer.
 */
public class Hand {

    /** The sequence of cards currently held in this hand. */
    private final List<Card> cards = new ArrayList<>();

    /**
     * Clears all cards from the hand, resetting it for a new round of play.
     */
    public void reset() { 
        cards.clear(); 
    }

    /**
     * Adds a dealt card to the hand.
     *
     * @param card the {@link Card} instance to include
     */
    public void add(Card card) { 
        cards.add(card); 
    }

    /**
     * Gets the total number of cards currently in the hand.
     *
     * @return the number of cards
     */
    public int size() { 
        return cards.size(); 
    }

    /**
     * Retrieves a specific card from the hand based on its deal order index.
     *
     * @param index the position index of the target card
     * @return the {@link Card} at the specified index
     */
    public Card getCard(int index) { 
        return cards.get(index); 
    }

    /**
     * Calculates the optimal, highest non-busting total score for the hand.
     *
     * @return the optimal point total for this hand
     */
    public int getTotalValue() {
        int total = 0;
        int aces  = 0;

        // Sum the initial card weights, assuming Aces count as 11 natively
        for (Card c : cards) {
            total += c.getValue();
            if (c.isAce()) {
                aces++;
            }
        }

        // Step down soft Aces if the hand is busting
        while (total > 21 && aces > 0) {
            total -= 10;   // Demote an Ace from 11 down to 1
            aces--;
        }

        return total;
    }

    /**
     * Checks if the hand is "soft" (contains an Ace that is actively counting as 11 points).
     *
     * @return {@code true} if the hand contains a flexible high Ace, {@code false} if it is a hard hand
     */
    public boolean isSoft() {
        int hardTotal = 0;
        boolean hasAce = false;

        // Calculate what the hand would equal if all Aces were locked at a value of 1
        for (Card c : cards) {
            if (c.isAce()) {
                hardTotal += 1;
                hasAce = true;
            } else {
                hardTotal += c.getValue();
            }
        }

        // If the optimized total is higher than the hard baseline, an Ace is acting as an 11
        return hasAce && (getTotalValue() > hardTotal);
    }

    /**
     * Evaluates if the hand is an identical pair that can be split under traditional rules.
     *
     * @return {@code true} if the hand is a splittable pair, otherwise {@code false}
     */
    public boolean isPair() {
        return cards.size() == 2 &&
               cards.get(0).getValue() == cards.get(1).getValue();
    }

    /**
     * Determines whether the total point configuration of the hand has exceeded 21.
     *
     * @return {@code true} if the hand is a bust, otherwise {@code false}
     */
    public boolean isBust() { 
        return getTotalValue() > 21; 
    }

    /**
     * Verifies if the hand represents a natural "Blackjack".
     *
     * @return {@code true} if the hand is a natural blackjack, otherwise {@code false}
     */
    public boolean isBlackjack() {
        return cards.size() == 2 && getTotalValue() == 21;
    }

    /**
     * Generates a descriptive string text breakdown of the hand state.
     *
     * @return descriptive visual telemetry string
     */
    @Override
    public String toString() {
        return cards.toString() + " : " + getTotalValue()
               + (isSoft() ? " (soft)" : "");
    }
}