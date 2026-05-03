package characters.enemies;

public class Skeleton extends Enemies {
    public Skeleton() {
        this.name = "Scheletro";

        // Statistiche
        this.baseHpMax = 80;
        this.baseStrength = 45;
        this.baseAgility = 50;
        this.baseIntelligence = 10;
        this.basePrecision = 70; 

        this.expReward = 45;
        this.minMoneyDrop = 15;
        this.maxMoneyDrop = 25;

        this.updateStats();
        this.totalHp = this.totalHpMax;
        this.currentMana = this.manaMax;

        this.generateLoot(1);
    }
}