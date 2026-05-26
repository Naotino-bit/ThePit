package characters.enemies;

public class Skeleton extends Enemies {
    public Skeleton() {
        this.name = "Scheletro";

        // Statistiche
        this.baseHpMax = 25;
        this.baseStrength = 15;
        this.baseAgility = 15;
        this.baseIntelligence = 5;
        this.basePrecision = 20;

        this.expReward = 45;
        this.minMoneyDrop = 15;
        this.maxMoneyDrop = 25;

        this.generateLoot(1);
    }
}