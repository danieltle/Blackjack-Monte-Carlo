/**
 * File: BasicStrategy.java
 * @author  Daniel Le
 * @date    06/14/2026
 * @version 1.0
 * Implements standard card-play decisions via static lookup matrix tables.
 */

package strategy;

import model.Card;
import model.Hand;

/**
 * Implements the mathematically optimal Basic Strategy for a standard multi-deck Blackjack game.
 */
public class BasicStrategy implements Strategy {

    /**
     * Look-up matrix for Hard totals (no flexible Aces).
     * Rows: Player totals from 5 to 21 (mapped as index = total - 5).
     * Columns: Dealer upcard from 2 to Ace (indices 0 through 9).
     * Value: 'H' for Hit, 'S' for Stand.
     */
    private static final char[][] HARD = {
        // 5  - always hit
        {'H','H','H','H','H','H','H','H','H','H'},
        // 6  - always hit
        {'H','H','H','H','H','H','H','H','H','H'},
        // 7  - always hit
        {'H','H','H','H','H','H','H','H','H','H'},
        // 8  - always hit
        {'H','H','H','H','H','H','H','H','H','H'},
        // 9  - double vs 3-6, else hit -> mapped to hit
        {'H','H','H','H','H','H','H','H','H','H'},
        // 10 - double vs 2-9, else hit -> mapped to hit
        {'H','H','H','H','H','H','H','H','H','H'},
        // 11 - double vs 2-10, else hit -> mapped to hit
        {'H','H','H','H','H','H','H','H','H','H'},
        // 12 - stand against 4, 5, 6, else hit
        {'H','H','S','S','S','H','H','H','H','H'},
        // 13 - stand against 2 through 6, else hit
        {'S','S','S','S','S','H','H','H','H','H'},
        // 14 - stand against 2 through 6, else hit
        {'S','S','S','S','S','H','H','H','H','H'},
        // 15 - stand against 2 through 6, else hit
        {'S','S','S','S','S','H','H','H','H','H'},
        // 16 - stand against 2 through 6, else hit
        {'S','S','S','S','S','H','H','H','H','H'},
        // 17+ - always stand
        {'S','S','S','S','S','S','S','S','S','S'},
        // 18
        {'S','S','S','S','S','S','S','S','S','S'},
        // 19
        {'S','S','S','S','S','S','S','S','S','S'},
        // 20
        {'S','S','S','S','S','S','S','S','S','S'},
        // 21
        {'S','S','S','S','S','S','S','S','S','S'},
    };

    /**
     * Look-up matrix for Soft totals containing an active Ace valued at 11.
     * Rows: The value of the non-Ace card(s) ranging from 2 to 9 (mapped as index = value - 2).
     * Columns: Dealer upcard from 2 to Ace (indices 0 through 9).
     * Value: 'H' for Hit, 'S' for Stand.
     */
    private static final char[][] SOFT = {
        // A+2 = soft 13
        {'H','H','H','H','H','H','H','H','H','H'},
        // A+3 = soft 14
        {'H','H','H','H','H','H','H','H','H','H'},
        // A+4 = soft 15
        {'H','H','H','H','H','H','H','H','H','H'},
        // A+5 = soft 16
        {'H','H','H','H','H','H','H','H','H','H'},
        // A+6 = soft 17
        {'H','H','H','H','H','H','H','H','H','H'},
        // A+7 = soft 18 - stand against 2-7, hit against 8-A (simplifying double/stand rules)
        {'S','S','S','S','S','S','S','H','H','H'},
        // A+8 = soft 19 - always stand
        {'S','S','S','S','S','S','S','S','S','S'},
        // A+9 = soft 20 - always stand
        {'S','S','S','S','S','S','S','S','S','S'},
    };

    /**
     * Queries the underlying strategy matrices to determine whether the player should hit or stand.
     *
     * @param hand          the player's current {@link Hand} object state
     * @param dealerUpcard  the dealer's exposed face-up {@link Card}
     * @return {@code true} if the mathematical strategy advises drawing another card (Hit), 
     * {@code false} if the strategy advises stopping (Stand)
     */
    @Override
    public boolean shouldHit(Hand hand, Card dealerUpcard) {
        int col = dealerUpcardIndex(dealerUpcard);
        int total = hand.getTotalValue();

        // Immediate mechanical boundaries: never hit 21+, always hit un-bustable sub-4 hands
        if (total >= 21) return false;   
        if (total <= 4)  return true;    

        // Case 1: Soft Hand Strategy
        if (hand.isSoft()) {
            // Isolate the secondary card weight to find the row index matrix anchor
            int nonAceValue = total - 11;   // e.g., Soft 18 (11 + 7) -> nonAceValue = 7
            if (nonAceValue >= 2 && nonAceValue <= 9) {
                return SOFT[nonAceValue - 2][col] == 'H';
            }
            // Fallback for Soft 21 or exceptional soft combinations
            return false;
        }

        // Case 2: Hard Hand Strategy
        int row = Math.min(total, 21) - 5;
        if (row < 0)  return true;
        if (row >= HARD.length) return false;
        
        return HARD[row][col] == 'H';
    }

    /**
     * Translates a dealer upcard value into a zero-indexed column identifier tracking array states.
     * Maps as: 2 -> index 0, 3 -> index 1 ... 10/Face -> index 8, Ace -> index 9.
     * @param c the dealer's visible upcard
     * @return an integer column array index from 0 to 9
     */
    private int dealerUpcardIndex(Card c) {
        int v = c.getValue();
        if (v == 11) return 9;          // Card configuration value for an Ace is 11
        return Math.min(v - 2, 8);      // Maps 2 to 0, and bounds face cards (10) to 8
    }

    /**
     * Retrieves the tracking identification name of this strategy.
     *
     * @return string representation profile descriptor
     */
    @Override
    public String name() { 
        return "Basic Strategy"; 
    }
}