package characters.player;

import characters.Character;
import items.Items;
import items.weapons.Bow;

public class Archer extends Character {
    public Archer() {
        name = "Arciere";
        baseHpMax = 75;
        baseStrength = 30;
        baseAgility = 90; //secondaria
        baseIntelligence = 30;
        basePrecision = 105; //primaria

        updateStats();
        this.totalHp = this.totalHpMax;
        this.currentMana = this.manaMax;
    }

    @Override
    public boolean canEquipWeapon(Items item){
        return item instanceof Bow;
    }

    @Override
    protected void applyLevelUpStats() {
        this.basePrecision += 7;
        this.baseAgility += 5;
        this.baseStrength += 2;
        this.baseIntelligence += 1;
        this.baseHpMax += 15;
    }
}
