# BlackjackAI
Made by: Yifan Zong

Extension of BlackjackTrainer

Now has an AI trained via a genetic algorithm that utilizes both the more sophisticated and effecitve Griffin 5 and Griffin 7 card-counting system.

Comapred to high-low (the standard card-counting system used by most professional card counters to beat the dealer), Griffin 5 and Griffin 7 require significantly more computing but reflect much more accurately players' edge.

Griffin 7 has the highest betting efficiencies of all card-counting systems. The new AI uses it when determining bets.

Griffin 5 has the highest playing and insurance efficiencies. The new AI uses it when making playing and insurance decisions.

To read more about G5 and G7: [Link](https://www.blackjackreview.com/wp/encyclopedia/g/#Griffin)

To read more about how card-counting systems are rated according to betting, playing, and insurance efficiencies: [Link](https://www.blackjackreview.com/wp/encyclopedia/card-counting-system-comparisons/#SUM)

# How to use:
AITrainer has a main class that trains LearningAIPLayer. Input the population, mutation rate, and mutation step parameters and the AITrainer run until you stop the program. Each generation's most effective chromosome is serialized and stored. A mutation rate of 0.2 and step of 1.0 seems to work the best. I tested populations of 50, 100, 1000. Population seems to make little difference in the long run. For a population of 1000, the chromosome seem to stabilize after 100 generations.

I included the elite of each of my 145 generations run (1000 population, 0.2 mutation rate, 1 mutation step) in the zip file.

Blackjack has a main class that simply lets you and different AIs play blackjack. It can also let you see the fitness score of the basic strat and high-low card-counting AI.

# Performance
The new AI outperforms the previous basic strat AI by over 250% and the high-low with Illustrious 18 deviations AI by 60%.

The fitness or performance is simply the average number of rounds an AI can play given 1 betting unit. The new AI can play around 200 rounds per unit. The high-low AI can play around 120 rounds per unit. The basic strat AI plays less than 80 rounds per unit.

# Difference to other machine-learning blackjack AI
This AI has to not only decide whether to hit or stand, it also has to decide how much to bet, when to place insurance, and when to split or double. It significantly outperforms AIs that only decide whether to hit or stand.

This AI does not seek to replicate basic strategy either. Many other machine learning blackjack AIs seek to replicate the basic stategy chart. However, even when basic strat is played perfectly, the dealer still has around 0.5% edge. In other words, AIs that try to replicate the basic strat cannot "beat the dealer". This AI, on the other hand, takes advantage of card-counting. Thanks to the Griffins 5 and Griffin 7 systems it uses, it outperforms the high-low card-counting and the Illustrious 18 deviations by 60%. It also outperforms basic strategy by over 250%.

# AI in more detail
The new AI player's chromosome has 4 neural networks.
The first determines how much to bet; it takes the Griffin 7 count as input and returns a bet amount as output.
The second determines when to place insurance; it takes the Griffin 5 count and the dealer's card value as input and return y/n as output.
The third determines when to split; it takes the Griffin 5 count, the dealer's card value, and the player's hand value as input and return y/n as output.
Finally, the fourth deterimes which non-split move to play. It takes the Griffin 5 count, the dealer's card value, the player's hand value, and the number of aces in the player's hand as input and return a non-split move (stand, hit, double down) as output.

No network has hidden layers. No ReLU, Tanh, or sigmoid function is used to normalize values. Everything is done via a linear transformation with bias.

The genetic algorithm selects the top 10% performers in a population to breed and fill 10% of the new population.
It then selects the top 50% performers to breed and fill 75% of the new population.
It then allows all populations to breed and fill the last 15% of the new population.
By allowing some bottom perfermers to breed, I found that genetic diversity is significantly improved and the resulting AIs are magnitudes smarter (20 rounds/unit vs 200 rounds/unit).

# Documentation:
Most classes are very similar or identical to those in BlackjackTrainer

New classes include AITrainer, LearningAIPlayer, and Chromosme

Check src for details

Questions? Email: zongyf02@gmail.com
