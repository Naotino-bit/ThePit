package characters.player;

import characters.Character;
import items.Items;
import items.weapons.Shield;
import items.weapons.Sword;

public class Warrior extends Character {
    public Warrior(){
        name = "Guerriero";
        baseHpMax = 120;
        baseStrength = 90; //primaria
        baseAgility = 85; //secondaria
        baseIntelligence = 30;
        basePrecision = 50;

        updateStats();
        this.totalHp = this.totalHpMax;
        this.currentMana = this.manaMax;
    }

    @Override
    public boolean canEquipWeapon(Items item) {
        return item instanceof Sword || item instanceof Shield;
    }

    @Override
    protected void applyLevelUpStats() {
        this.baseStrength += 7; //primaria
        this.baseAgility += 3; //secondaria
        this.basePrecision += 2;
        this.baseIntelligence += 1;
        this.baseHpMax += 15;
    }
}
