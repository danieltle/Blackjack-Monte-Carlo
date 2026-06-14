/**
 * File: Blackjack.java
 * @author  Daniel Le
 * @date    06/14/2026
 * @version 1.0
 * The core game engine.
 */

package engine;

import model.Card;
import model.Hand;
import model.Shoe;
import strategy.CountingStrategy;
import strategy.Strategy;

/**
 * The core game engine responsible for orchestrating a single hand of Blackjack.
 */
public class Blackjack {

    private final Strategy strategy;
    private final Shoe     shoe;
    private final Hand     playerHand = new Hand();
    private final Hand     dealerHand = new Hand();

    /** 
     * The threshold at which the shoe must be reshuffled. 
     * 0.75 means the shoe is shuffled after 75% of the cards have been dealt.
     */    
    private static final double RESHUFFLE_PENETRATION = 0.75;

    /**
     * Constructs a new Blackjack engine instance with a specific strategy and card shoe.
     * <p>
     * If the provided strategy is an instance of {@link CountingStrategy}, this 
     * constructor automatically links the shoe to the strategy so it can track the count.
     * </p>
     *
     * @param strategy the player strategy to use (e.g., Basic Strategy, Card Counting)
     * @param shoe     the shared shoe from which cards will be dealt
     */
    public Blackjack(Strategy strategy, Shoe shoe) {
        this.strategy = strategy;
        this.shoe     = shoe;
        // Inject shoe into counting strategy if applicable
        if (strategy instanceof CountingStrategy cs) cs.setShoe(shoe);
    }

    /**
     * Plays a single hand of Blackjack using a variable bet multiplier.
     * 
     * @param betMultiplier the wager size for this hand, in base units
     * @return the net outcome of the hand multiplied by the wager size:
     * <ul>
     * <li>{@code +1.5 * multiplier} for a natural player blackjack win</li>
     * <li>{@code +1.0 * multiplier} for a standard player win</li>
     * <li>{@code 0.0} for a push (tie)</li>
     * <li>{@code -1.0 * multiplier} for a player loss or bust</li>
     * </ul>
     */
    public double playHand(int betMultiplier) {
        shoe.reshuffleIfNeeded(RESHUFFLE_PENETRATION);

        playerHand.reset();
        dealerHand.reset();

        // Initial deal
        playerHand.add(shoe.deal());
        dealerHand.add(shoe.deal());
        playerHand.add(shoe.deal());
        dealerHand.add(shoe.deal());   // dealer's second card is face-down

        Card dealerUpcard = dealerHand.getCard(0);

        // Check for blackjacks
        boolean playerBJ = playerHand.isBlackjack();
        boolean dealerBJ = dealerHand.isBlackjack();

        if (playerBJ && dealerBJ) return 0;                   // push
        if (playerBJ)             return 1.5 * betMultiplier; // BJ pays 3:2
        if (dealerBJ)             return -1.0 * betMultiplier;

        // Player turn 
        while (strategy.shouldHit(playerHand, dealerUpcard)) {
            playerHand.add(shoe.deal());
            if (playerHand.isBust()) return -1.0 * betMultiplier;
        }

        // Dealer turn (stands on soft 17) 
        while (dealerHand.getTotalValue() < 17
               || (dealerHand.getTotalValue() == 17 && dealerHand.isSoft())) {
            dealerHand.add(shoe.deal());
        }

        if (dealerHand.isBust()) return +1.0 * betMultiplier;

        // Compare totals 
        int pTotal = playerHand.getTotalValue();
        int dTotal = dealerHand.getTotalValue();

        if      (pTotal > dTotal) return +1.0 * betMultiplier;
        else if (dTotal > pTotal) return -1.0 * betMultiplier;
        else                      return 0;
    }

    /**
     * Plays a single hand of Blackjack using a standard flat bet of 1 unit.
     *
     * @return the net outcome of the hand 
     * @see #playHand(int)
     */
    public double playHand() {
        return playHand(1); 
    }

    /**
     * Gets the player's current hand.
     *
     * @return the {@link Hand} object representing the player's cards
     */
    public Hand getPlayerHand() {
        return playerHand; 
    }

    /**
     * Gets the dealer's current hand.
     *
     * @return the {@link Hand} object representing the dealer's cards
     */
    public Hand getDealerHand() {
        return dealerHand; 
    }
}
