/**
 * File: SimResult.java
 * @author  Daniel Le
 * @date    06/14/2026
 * @version 1.0
 * Tracks statistics for a Blackjack simulation.
 */

package engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects per-hand outcomes and computes summary statistics for a simulation run.
 */
public class SimResult {

    private final String strategyName;
    private final int    numDecks;

    // Per-hand outcomes (+betMultiplier, -betMultiplier, 0)
    private final List<Double> outcomes        = new ArrayList<>();
    private final List<Double> bankrollHistory = new ArrayList<>();

    private double bankroll;
    private double peakBankroll;
    private double maxDrawdown = 0;

    /**
     * Constructs a new SimResult tracker with an initial bankroll setup.
     *
     * @param strategyName     the identifier name of the player strategy being tested
     * @param numDecks         the number of decks used in the shoe
     * @param startingBankroll the initial bankroll funding unit amount
     */
    public SimResult(String strategyName, int numDecks, double startingBankroll) {
        this.strategyName = strategyName;
        this.numDecks     = numDecks;
        this.bankroll     = startingBankroll;
        this.peakBankroll = startingBankroll;
        bankrollHistory.add(startingBankroll);
    }

    /**
     * Records the outcome of a single completed hand and updates bankroll history metrics.
     *
     * @param outcome the net units won or lost (e.g., +1.0 for standard win, +1.5 for BJ, -1.0 for loss)
     */
    public void record(double outcome) {
        outcomes.add(outcome);
        bankroll += outcome;
        if (bankroll > peakBankroll) peakBankroll = bankroll;
        
        double drawdown = peakBankroll - bankroll;
        if (drawdown > maxDrawdown) maxDrawdown = drawdown;
        bankrollHistory.add(bankroll);
    }

    /**
     * Gets the total count of simulated hands recorded.
     *
     * @return total hands processed
     */
    public int totalHands() {
        return outcomes.size(); 
    }

    /**
     * Counts the total number of winning hands.
     *
     * @return count of hands where outcome was greater than 0
     */
    public long wins()   {
        return outcomes.stream().filter(x -> x > 0).count();
    }

    /**
     * Counts the total number of losing hands.
     *
     * @return count of hands where outcome was less than 0
     */
    public long losses() {
        return outcomes.stream().filter(x -> x < 0).count();
    }

    /**
     * Counts the total number of pushed (tied) hands.
     *
     * @return count of hands where outcome was exactly 0
     */
    public long pushes() {
        return outcomes.stream().filter(x -> x == 0).count();
    }

    /**
     * Computes the ratio of wins to total hands.
     *
     * @return win rate percentage as a decimal fraction
     */
    public double winRate() {
        return (double) wins() / totalHands();
    }

    /**
     * Computes the ratio of losses to total hands.
     *
     * @return loss rate percentage as a decimal fraction
     */
    public double lossRate() {
        return (double) losses() / totalHands();
    }

    /**
     * Computes the ratio of pushes to total hands.
     *
     * @return push rate percentage as a decimal fraction
     */
    public double pushRate() {
        return (double) pushes() / totalHands(); 
    }

    /**
     * Calculates the Expected Value (EV) representing the average unit outcome per hand.
     * A negative EV represents a house advantage.
     *
     * @return the average unit win/loss rate per hand
     */
    public double ev() {
        return outcomes.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    /**
     * Evaluates the standard deviation of per-hand returns to measure game volatility.
     *
     * @return the standard deviation value in bet units
     */
    public double stdDev() {
        double mean = ev();
        double variance = outcomes.stream()
            .mapToDouble(x -> (x - mean) * (x - mean))
            .average().orElse(0);
        return Math.sqrt(variance);
    }

    /**
     * Calculates the 95% confidence interval half-width margin of error on the win rate.
     * Uses standard Wald formula framework: $CI = 1.96 \times \sqrt{\frac{p(1-p)}{n}}$.
     *
     * @return the statistical variance margin of error fraction
     */
    public double winRateCI95() {
        double p = winRate();
        return 1.96 * Math.sqrt(p * (1 - p) / totalHands());
    }

    /**
     * Inverts the Expected Value to show the traditional casino statistical house edge.
     *
     * @return the house edge expressed as a positive percentage
     */
    public double houseEdgePct() {
        return -ev() * 100;
    }

    /**
     * Gets the largest drawdown experienced.
     *
     * @return maximum recorded bankroll dip in units
     */
    public double maxDrawdown() {
        return maxDrawdown;
    }

    /**
     * Gets the terminal bankroll value at the end of the simulation execution.
     *
     * @return final bankroll unit value
     */
    public double finalBankroll(){
        return bankroll; 
    }

    /**
     * Estimates player bankruptcy using a standard Gambler's Ruin mathematical formula model.
     * <p>
     * Model formulation: $P(\text{ruin}) \approx \left(\frac{q}{p}\right)^B$ where $p = \text{win rate}$, 
     * $q = \text{loss rate}$, and $B = \text{starting bankroll}$.
     * Note: This calculation assumes a flat wagering strategy and can vary under scaling count strategies.
     * </p>
     *
     * @param startingBankrollUnits the number of initial units in the player's bankroll
     * @return estimated probability of hitting zero bankroll (ranging from 0.0 to 1.0)
     */
    public double ruinProbability(double startingBankrollUnits) {
        double p = winRate();
        double q = lossRate();

        if (p <= 0) return 1.0;
        if (p >= 1 || q <= 0) return 0.0;

        double ratio = q / p;
        if (ratio < 1.0) {
            return Math.pow(ratio, startingBankrollUnits);
        }

        // An unfavorable game (negative EV) results in eventual certain ruin
        return 1.0;
    }

    /**
     * Returns the step-by-step history trace logs of bankroll changes.
     *
     * @return a historical array sequence of bankroll states
     */
    public List<Double> getBankrollHistory() {
        return bankrollHistory; 
    }

    /**
     * Formats the collected metrics data indicators into a structured console dashboard presentation text layout.
     *
     * @return formatted analytics performance breakdown report string
     */
    @Override
    public String toString() {
        int n = totalHands();
        double startingBankroll = bankrollHistory.isEmpty() ? 0 : bankrollHistory.get(0);
        return String.format(
            "══════════════════════════════════════════════════════\n" +
            "  Strategy : %-35s\n" +
            "  Shoe     : %d deck(s)   |   Hands: %,d\n" +
            "──────────────────────────────────────────────────────\n" +
            "  Win  rate : %6.2f%% ± %.2f%%\n" +
            "  Loss rate : %6.2f%%\n" +
            "  Push rate : %6.2f%%\n" +
            "──────────────────────────────────────────────────────\n" +
            "  EV / hand  : %+.4f units   (house edge: %.3f%%)\n" +
            "  Std dev    : %.4f units\n" +
            "  Max drawdown: %.1f units\n" +
            "  Final bankroll: %.1f units  (started %.0f)\n" +
            "══════════════════════════════════════════════════════\n",
            strategyName,
            numDecks, n,
            winRate() * 100, winRateCI95() * 100,
            lossRate() * 100,
            pushRate() * 100,
            ev(), houseEdgePct(),
            stdDev(),
            maxDrawdown(),
            finalBankroll(),
            startingBankroll
        );
    }
}
