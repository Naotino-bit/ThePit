package characters.enemies;

public class Orc extends Enemies {
    public Orc() {
        this.name = "Orco";

        // Statistiche
        this.baseHpMax = 150;
        this.baseStrength = 80;
        this.baseAgility = 15;
        this.baseIntelligence = 5;
        this.basePrecision = 40;

        this.expReward = 75;
        this.minMoneyDrop = 30;
        this.maxMoneyDrop = 60;

        this.generateLoot(3);
    }
}