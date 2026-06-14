/**
 * File: TimidStrategy.java
 * @author  Daniel Le
 * @date    06/14/2026
 * @version 1.0
 * Implements a low-risk, conservative simulation strategy profile.
 */

package strategy;

import model.Card;
import model.Hand;

/**
 * A timid strategy implementation that stands on any hand total of 12 or higher.
 */
public class TimidStrategy implements Strategy {

    /**
     * Determines whether to hit or stand based strictly on avoiding a potential bust.
     * Since any hard total of 12 or higher can technically bust on a 10-value draw, 
     * this strategy halts all draw decisions immediately upon reaching 12.
     *
     * @param hand          the player's current {@link Hand} state evaluation
     * @param dealerUpcard  the dealer's exposed face-up {@link Card} (ignored by this strategy)
     * @return {@code true} if the total point value is strictly less than 12, 
     * otherwise {@code false} (indicating a stand decision)
     */
    @Override
    public boolean shouldHit(Hand hand, Card dealerUpcard) {
        return hand.getTotalValue() < 12;
    }

    /**
     * Retrieves the descriptive, user-friendly identification name of this strategy.
     *
     * @return a string descriptor summarizing the profile behavior
     */
    @Override 
    public String name() { 
        return "Timid (stand 12+)"; 
    }
}