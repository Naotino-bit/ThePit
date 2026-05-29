package characters.enemies;

public class Orc extends Enemies {
    public Orc() {
        this.name = "Orco";

        // Statistiche
        this.baseHpMax = 50;
        this.baseStrength = 25;
        this.baseAgility = 5;
        this.baseIntelligence = 5;
        this.basePrecision = 15;

        this.expReward = 75;
        this.minMoneyDrop = 30;
        this.maxMoneyDrop = 60;

        this.generateLoot(3);
    }
}