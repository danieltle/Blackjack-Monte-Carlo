/**
 * File: AggressiveStrategy.java
 * @author  Daniel Le
 * @date    06/14/2026
 * @version 1.0
 * Implements a high-risk simulation strategy profile.
 */

package strategy;

import model.Card;
import model.Hand;

/**
 * An aggressive strategy implementation that hits continuously until reaching a total of 18 or higher.
 */
public class AggressiveStrategy implements Strategy {

    /**
     * Determines whether to hit or stand based strictly on achieving a score of at least 18.
     *
     * @param hand          the player's current {@link Hand} state evaluation
     * @param dealerUpcard  the dealer's exposed face-up {@link Card} (ignored by this strategy)
     * @return {@code true} if the total point value is strictly less than 18, 
     * otherwise {@code false} (indicating a stand decision)
     */
    @Override
    public boolean shouldHit(Hand hand, Card dealerUpcard) {
        return hand.getTotalValue() < 18;
    }

    /**
     * Retrieves the descriptive, user-friendly identification name of this strategy.
     *
     * @return a string descriptor summarizing the profile behavior
     */
    @Override 
    public String name() { 
        return "Aggressive (hit to 18)"; 
    }
}