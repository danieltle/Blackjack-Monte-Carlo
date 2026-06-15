# Blackjack Monte Carlo Simulation Engine

A Monte Carlo simulation engine for analyzing Blackjack
strategies, built in Java. Models player decision logic as a pluggable
interface, runs up to 1,000,000 hands per strategy, and outputs
statistical metrics including expected value, house edge, max drawdown,
and 95% confidence intervals.

Built as an extension of a CS 231 (Data Structures & Algorithms) project
at Colby College, refactored from a single-file game into a
multi-layer simulation engine.

---

## What This Is

Every Blackjack strategy has a mathematically optimal play for every
situation. This engine tests whether that's true and by how much
suboptimal strategies cost you by simulating millions of hands and
measuring the results statistically.

The project has four interconnected layers:

| Layer | What it does |
|---|---|
| `model/` | Card, Hand (soft Ace logic), multi-deck Shoe with Hi-Lo tracking |
| `strategy/` | Pluggable Strategy interface with four implementations |
| `engine/` | Game engine with injected strategy, SimResult metrics collector |
| `Simulation` | Entry point — runs 1M hands per strategy, prints comparison report |

---

## Core Concepts

### The Card Model

A standard 6-deck shoe contains 312 cards. Aces count as 11 unless
that would bust the hand, in which case they drop to 1 (soft vs. hard
totals). Most beginner implementations miss this — it meaningfully
affects every EV calculation downstream.

### Strategy as an Interface

Decision logic is fully decoupled from the game engine via a `Strategy`
interface with one method:

```java
boolean shouldHit(Hand playerHand, Card dealerUpcard);
````

This mirrors the pattern used in algorithmic trading systems, where
signal logic is swapped without touching execution infrastructure.
Any new strategy — a neural net, a lookup table, a human — plugs in
with zero changes to the engine.

### Basic Strategy

The mathematically optimal hit/stand decision for every (player total,
dealer upcard) combination, encoded as a lookup table. Rows are player
totals, columns are dealer upcards 2–A. Separate tables for hard totals
and soft totals (hands containing an Ace still counting as 11).

### Hi-Lo Card Counting

Each card dealt updates a running count:

```text
2–6  → +1   (low cards removed: good for dealer)
7–9  →  0   (neutral)
10–A → −1   (high cards removed: bad for player)
```

The true count adjusts for decks remaining:

```text
True Count = Running Count / Decks Remaining
```

Bet sizing scales with true count (1×/2×/4×/8× spread). A rich shoe
(high true count) means more 10s and Aces remain — increasing blackjack
frequency and double-down value.

### Why the House Edge Is 2.78% Instead of ~0.5%

Basic Strategy's theoretical 0.5% house edge assumes the player can
double down and split pairs. Doubling on hard 10/11 vs. a weak dealer
upcard, and splitting Aces and 8s, are the highest-EV plays in the
game. This engine implements hit/stand decisions only, which is a known
simplification. Implementing double down and split is the next
planned milestone, and the point at which the simulation should
converge to the theoretical figure.

---

## Architecture

### model/

* `Card.java` — Suit + Face enums, blackjack point value (Ace = 11)
* `Hand.java` — Soft/hard Ace duality, `isSoft()`, `isBlackjack()`, `isBust()`
* `Shoe.java` — Multi-deck shoe, Hi-Lo running count, true count, penetration tracking

### strategy/

* `Strategy.java` — Interface: `shouldHit(Hand, Card dealerUpcard)`
* `BasicStrategy.java` — Hard + soft lookup tables (optimal hit/stand decisions)
* `CountingStrategy.java` — Hi-Lo true count with 1×–8× bet spread
* `TimidStrategy.java` — Stand at 12+
* `AggressiveStrategy.java` — Hit to 18+

### engine/

* `Blackjack.java` — Deal, player turn, dealer turn, resolve outcome
* `SimResult.java` — EV, confidence intervals, drawdown, ruin probability

### root/

* `Simulation.java` — Runs 1M hands per strategy and prints comparison reports

---

## Run

```bash
javac -d out model/*.java strategy/*.java engine/*.java Simulation.java
java -cp out Simulation
```

Requires Java 17+.

---

## Simulation Results (1,000,000 hands, 6-deck shoe, dealer stands soft 17)

| Strategy               | Win Rate       | EV / Hand | House Edge |
| ---------------------- | -------------- | --------- | ---------- |
| Timid (stand 12+)      | 41.67% ± 0.10% | -0.0809   | 8.09%      |
| Aggressive (hit to 18) | 39.85% ± 0.10% | -0.0957   | 9.57%      |
| Basic Strategy         | 43.19% ± 0.10% | -0.0278   | 2.78%      |
| Hi-Lo Counting         | 43.23% ± 0.10% | -0.0308   | 3.09%      |

Win rates include 95% confidence intervals. At 1,000,000 hands the
margin of error is approximately ±0.10%, which is small enough to
meaningfully distinguish strategy performance.

> House edge is higher than the theoretical ~0.5% because this engine
> implements hit/stand decisions only. Double down and split — the plays
> that generate the player's largest statistical edges — are not yet
> implemented. This is a known limitation and the next planned feature.

---

## Key Metrics (SimResult)

| Metric             | Definition                                           |
| ------------------ | ---------------------------------------------------- |
| EV / Hand          | Average net units won or lost per hand               |
| House Edge         | -EV expressed as a percentage                        |
| 95% CI             | `1.96 × sqrt(p(1-p)/n)` on win rate                  |
| Standard Deviation | Volatility of per-hand outcomes                      |
| Max Drawdown       | Largest peak-to-trough bankroll decline              |
| Ruin Probability   | Gambler's Ruin approximation given starting bankroll |

---

## Roadmap

* [ ] Double Down (required to reach theoretical 0.5% house edge)
* [ ] Split Pairs
* [ ] Surrender
* [ ] Bankroll time-series chart output
* [ ] Multi-strategy head-to-head over a shared shoe

---

## References

* Baldwin et al. (1956). *The Optimum Strategy in Blackjack*. Journal of the American Statistical Association.
* Thorp, E.O. (1962). *Beat the Dealer* — first mathematical proof that card counting works.
* Griffin, P. (1979). *The Theory of Blackjack* — EV tables and strategy derivations.
* Wizard of Odds — published Basic Strategy tables used to validate lookup implementation.

---

## Author

**Daniel Le**

Mathematics & Statistics • Computer Science

Colby College
