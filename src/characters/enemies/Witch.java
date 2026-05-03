package characters.enemies;


public class Witch extends Enemies {
    public Witch() {
        this.name = "Strega";

        this.baseHpMax = 45;
        this.baseStrength = 10;
        this.baseAgility = 40;
        this.baseIntelligence = 85;
        this.basePrecision = 60;

        this.expReward = 60;
        this.minMoneyDrop = 20;
        this.maxMoneyDrop = 45;

        this.updateStats();
        this.totalHp = this.totalHpMax;
        this.currentMana = this.manaMax;


        this.generateLoot(2);

    }

    @Override
    protected int getBaseDamage() {
        return this.weaponDamage + this.totalIntelligence;
    }
}