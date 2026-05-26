package characters.enemies;

public class Goblin extends Enemies {
    public Goblin() {
        this.name = "Goblin";

        // Statistiche
        this.baseHpMax = 20;
        this.baseStrength = 10;
        this.baseAgility = 20;
        this.baseIntelligence = 5;
        this.basePrecision = 10;

        this.expReward = 35;
        this.minMoneyDrop = 10;
        this.maxMoneyDrop = 20;

        this.generateLoot(1);
    }
}