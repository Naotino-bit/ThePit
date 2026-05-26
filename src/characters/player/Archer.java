package characters.player;

import characters.Character;
import items.Items;
import items.weapons.Bow;

public class Archer extends Character {
    public Archer() {
        name = "Arciere";
        resetBaseStats();

        updateStats();
        this.totalHp = this.totalHpMax;
        this.currentMana = this.manaMax;
    }

    @Override
    protected void resetBaseStats() {
        baseHpMax = 25;
        baseStrength = 10;
        baseAgility = 25; //secondaria
        baseIntelligence = 10;
        basePrecision = 30; //primaria
    }

    @Override
    public boolean canEquipWeapon(Items item){
        return item instanceof Bow;
    }

    @Override
    protected void applyLevelUpStats() {
        this.basePrecision += 3;
        this.baseAgility += 2;
        this.baseStrength += 1;
        this.baseIntelligence += 1;
        this.baseHpMax += 5;
    }
}
