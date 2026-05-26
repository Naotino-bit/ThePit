package characters.player;

import characters.Character;
import items.Items;
import items.weapons.Shield;
import items.weapons.Sword;

public class Warrior extends Character {
    public Warrior() {
        name = "Guerriero";
        resetBaseStats();

        updateStats();
        this.totalHp = this.totalHpMax;
        this.currentMana = this.manaMax;
    }

    @Override
    protected void resetBaseStats() {
        baseHpMax = 40;
        baseStrength = 25; //primaria
        baseAgility = 25; //secondaria
        baseIntelligence = 10;
        basePrecision = 15;
    }

    @Override
    public boolean canEquipWeapon(Items item) {
        return item instanceof Sword || item instanceof Shield;
    }

    @Override
    protected void applyLevelUpStats() {
        this.baseStrength += 3; //primaria
        this.baseAgility += 2; //secondaria
        this.basePrecision += 1;
        this.baseIntelligence += 1;
        this.baseHpMax += 6;
    }
}
