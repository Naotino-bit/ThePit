package characters.enemies;


public class Witch extends Enemies {
    public Witch() {
        this.name = "Strega";

        this.baseHpMax = 15;
        this.baseStrength = 5;
        this.baseAgility = 10;
        this.baseIntelligence = 25;
        this.basePrecision = 15;

        this.expReward = 60;
        this.minMoneyDrop = 20;
        this.maxMoneyDrop = 45;

        this.generateLoot(2);

    }


}