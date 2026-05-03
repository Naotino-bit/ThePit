package characters.enemies;

public class Goblin extends Enemies {
    public Goblin() {
        this.name = "Goblin";

        // Statistiche
        this.baseHpMax = 60;
        this.baseStrength = 30;
        this.baseAgility = 70;
        this.baseIntelligence = 10;
        this.basePrecision = 40;

        this.expReward = 35;
        this.minMoneyDrop = 10;
        this.maxMoneyDrop = 20;

        this.updateStats();
        this.totalHp = this.totalHpMax;
        this.currentMana = this.manaMax;

        this.generateLoot(1);
    }
}