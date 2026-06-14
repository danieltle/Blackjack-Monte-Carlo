/**
 * File: CountingStrategy.java
 * @author  Daniel Le
 * @date    06/14/2026
 * @version 1.0
 * Implements a dynamic bet-sizing tracking model.
 */

package strategy;

import model.Card;
import model.Hand;
import model.Shoe;

/**
 * Implements a classic Hi-Lo card counting strategy that dynamically alters bet sizing.
 * 
 */
public class CountingStrategy implements Strategy {

    /** The underlying baseline play engine used for standard card hit/stand evaluation choices. */
    private final BasicStrategy base = new BasicStrategy();
    
    /** The active data card deck containing state trackers to scan true counting parameters. */
    private Shoe shoe;   

    /**
     * Injects the active shared simulation card containment resource context.
     * This binding allows the tracker to query live true count metrics before executing bets.
     *
     * @param shoe the active simulation tracking shoe context instance
     */
    public void setShoe(Shoe shoe) { 
        this.shoe = shoe; 
    }

    /**
     * Determines hand extension mechanics by delegating hit/stand choices directly to Basic Strategy.
     * Note: Illustrative card playing adjustments based on specific count indicators are skipped.
     *
     * @param hand          the player's current card configuration state tracker
     * @param dealerUpcard  the dealer's exposed face-up card item
     * @return {@code true} if the base strategy recommends a draw action, otherwise {@code false}
     */
    @Override
    public boolean shouldHit(Hand hand, Card dealerUpcard) {
        return base.shouldHit(hand, dealerUpcard);
    }

    /**
     * Computes the dynamic unit multiplier scale requirement targeting the next upcoming dealt hand.
     *
     * @return an integer multiplier representing the total bet scaling factor
     */
    public int getBetMultiplier() {
        // Fallback protection if shoe allocation has not been completed early
        if (shoe == null) return 1;
        
        double tc = shoe.getTrueCount();
        
        if (tc >= 6) {
            return 8; // Max betting tier
        } else if (tc >= 4) {
            return 4;
        } else if (tc >= 2) {
            return 2;
        } else {
            return 1; // Minimum table flat-bet baseline
        }
    }

    /**
     * Retrieves the structural profile tracking tag for reporting.
     *
     * @return identifying signature name string literal
     */
    @Override
    public String name() { 
        return "Hi-Lo Counting"; 
    }
}