/**
 * File: Strategy.java
 * @author  Daniel Le
 * @date    06/14/2026
 * @version 1.0
 * Declares the contract structure for simulation playing choices.
 */

package strategy;

import model.Card;
import model.Hand;

/**
 * Defines a pluggable contract interface for establishing Blackjack player behavior strategies.
 */
public interface Strategy {
    
    /**
     * Evaluates the current state of a round to decide whether the player should hit or stand.
     *
     * @param playerHand   the {@link Hand} instance tracking the player's current card values
     * @param dealerUpcard the single visible face-up {@link Card} held by the dealer
     * @return {@code true} if the strategy rules advise drawing an additional card (Hit), 
     * {@code false} if the strategy dictates locking the current total (Stand)
     */
    boolean shouldHit(Hand playerHand, Card dealerUpcard);

    /**
     * Retrieves a human-readable, descriptive identifier label representing the strategy.
     *
     * @return the unique identifying name string of the strategy implementation
     */
    String name();
}