/**
 * File: Simulation.java
 * @author  Daniel Le
 * @date    06/14/2026
 * @version 1.0
 * Main execution orchestration block running automated gameplay profiles.
 */

import engine.Blackjack;
import engine.SimResult;
import model.Shoe;
import strategy.*;

/**
 * Acts as the centralized execution driver for multi-million-hand Blackjack simulations.
 */
public class Simulation {

    /** The absolute total benchmark volume size of hands to process for each independent strategy iteration. */
    private static final int    NUM_HANDS          = 1_000_000;
    
    /** The standard card-dealing shoe profile setting (6 full physical decks). */
    private static final int    NUM_DECKS          = 6;
    
    /** The theoretical relative foundation tracking reserve assigned to incoming analytics results. */
    private static final double STARTING_BANKROLL  = 1_000.0;

    /**
     * Main entry point execution stack compiling statistical risk performance tables.
     *
     * @param args command-line interface argument arrays (ignored)
     */
    public static void main(String[] args) {
        System.out.println("            BLACKJACK SIMULATION ENGINE              ");
        System.out.printf( "  %,d hands | %d-deck shoe | dealer stands soft 17  %n",
                NUM_HANDS, NUM_DECKS);
        System.out.println();

        // Instantiate pluggable strategy profiles to process
        Strategy[] strategies = {
            new TimidStrategy(),
            new AggressiveStrategy(),
            new BasicStrategy(),
            new CountingStrategy()
        };

        // --- Execution Pass 1: Verbose Comprehensive Results Output ---
        for (Strategy strat : strategies) {
            SimResult result = runSimulation(strat);
            System.out.println(result);
            System.out.println();
        }

        // --- Execution Pass 2: Structured Macro Analysis Summary Grid ---
        System.out.println("   Strategy                     Win Rate   EV/hand  House Edge");

        for (Strategy strat : strategies) {
            SimResult r = runSimulation(strat);
            System.out.printf("   %-27s %6.2f%%     %+.4f %6.3f%%  %n",
                    strat.name(), r.winRate() * 100, r.ev(), r.houseEdgePct());
        }
        System.out.println();
    }

    /**
     * Provisions isolation resources, provisions structural states, and loops execution threads 
     * across the designated hand volume matrix.
     * <p>
     * For tracking-strategy configurations, this driver injects a direct shoe reference link and 
     * checks wager multiplication metrics immediately before handing orchestration execution control 
     * down to the underlying dealer card loops.
     * </p>
     *
     * @param strategy the custom game strategy to apply to the loop
     * @return a compiled {@link SimResult} analytics dataset object asset containing execution telemetry
     */
    private static SimResult runSimulation(Strategy strategy) {
        Shoe shoe = new Shoe(NUM_DECKS);
        Blackjack game = new Blackjack(strategy, shoe);
        SimResult result = new SimResult(strategy.name(), NUM_DECKS, STARTING_BANKROLL);

        // Inject the active shoe resource into the counting strategy if type-matching tracks true
        if (strategy instanceof CountingStrategy cs) {
            cs.setShoe(shoe);
        }

        // Run the main core loop iterations
        for (int i = 0; i < NUM_HANDS; i++) {
            // Determine bet unit scaling dynamically before drawing cards
            int betMult = (strategy instanceof CountingStrategy cs)
                    ? cs.getBetMultiplier() : 1;
            
            // Execute the play loop hand sequence and store financial tracking impacts
            double outcome = game.playHand(betMult);
            result.record(outcome);
        }
        return result;
    }
}