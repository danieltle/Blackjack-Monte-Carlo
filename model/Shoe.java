/**
 * File: Shoe.java
 * @author  Daniel Le
 * @date    06/14/2026
 * @version 1.0
 * Manages card containment and deck-tracking arithmetic indicators.
 */

package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages a dealer's shoe containing one or more standard 52-card playing decks.
 */
public class Shoe {

    /** The total count of standard 52-card decks initialized into the system. */
    private final int numDecks;
    
    /** The main vector list tracking available un-dealt card states inside the shoe. */
    private List<Card> cards = new ArrayList<>();
    
    /** The tracked running count indicator based on Hi-Lo strategy point scales. */
    private int runningCount = 0;
    
    /** The running lifetime counter tracking how many cards have been pulled from the set. */
    private int dealtCards   = 0;

    /**
     * Constructs a multi-deck shoe and builds out the randomized collection.
     *
     * @param numDecks the total number of physical decks to package inside this shoe
     */
    public Shoe(int numDecks) {
        this.numDecks = numDecks;
        build();
    }

    /**
     * Rebuilds a fresh complete deck matrix based on configuration constraints and shuffles them.
     */
    private void build() {
        cards.clear();
        runningCount = 0;
        dealtCards   = 0;
        
        // Loop structural iterations to insert uniform sets of 52 components
        for (int d = 0; d < numDecks; d++) {
            for (Card.Suit suit : Card.Suit.values()) {
                for (Card.Face face : Card.Face.values()) {
                    cards.add(new Card(suit, face));
                }
            }
        }
        Collections.shuffle(cards);
    }

    /**
     * Removes and deals the top card from the shoe matrix while adjusting current running counts.
     *
     * @return the drawn {@link Card} instance
     */
    public Card deal() {
        Card c = cards.remove(0);
        dealtCards++;
        
        // Evaluate Hi-Lo tracking metrics via numerical card values
        int v = c.getValue();
        if (v <= 6) {
            runningCount++;
        } else if (v >= 10) {
            runningCount--;
        }
        
        return c;
    }

    /**
     * Gets the remaining count of cards physically left in the deck.
     *
     * @return count of remaining cards
     */
    public int size() { 
        return cards.size(); 
    }

    /**
     * Calculates the macro deck capacity volume when fully loaded (Decks * 52).
     *
     * @return baseline absolute card limit
     */
    public int totalCards() { 
        return numDecks * 52; 
    }

    /**
     * Calculates the statistical True Count representing concentration margins of remaining high cards.
     * Formula expression: $\text{True Count} = \frac{\text{Running Count}}{\text{Decks Remaining}}$
     *
     * @return the calculated true count decimal scalar
     */
    public double getTrueCount() {
        double decksRemaining = cards.size() / 52.0;
        
        // Avoid risky division traps or extreme fractional spikes near the end of a shoe
        if (decksRemaining < 0.5) {
            return runningCount;
        }
        
        return runningCount / decksRemaining;
    }

    /**
     * Gets the running raw integer count.
     *
     * @return current raw running count value
     */
    public int getRunningCount() { 
        return runningCount; 
    }

    /**
     * Calculates the penetration ratio mapping out how much of the deck has already been processed.
     *
     * @return standard fraction ranging between 0.0 and 1.0
     */
    public double getPenetration() {
        return (double) dealtCards / totalCards();
    }

    /**
     * Checks penetration markers against threshold parameters and shuffles if capacity bounds are crossed.
     *
     * @param penetrationThreshold the point ratio limit requiring an automatic card reshuffle (e.g., 0.75)
     */
    public void reshuffleIfNeeded(double penetrationThreshold) {
        if (getPenetration() >= penetrationThreshold) {
            build();
        }
    }

    /**
     * Generates a structural diagnostic readout tracing shoe metrics values.
     * Format example: "Shoe[6 decks, 210 cards left, RC=4, TC=1.05]"
     *
     * @return descriptive string containing array summary data
     */
    @Override
    public String toString() {
        return String.format("Shoe[%d decks, %d cards left, RC=%d, TC=%.2f]",
                numDecks, cards.size(), runningCount, getTrueCount());
    }
}