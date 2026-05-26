package characters.player;

import characters.Character;
import items.Items;
import items.weapons.Bow;
import items.weapons.Staff;

public class Mage extends Character {
    public Mage() {
        name = "Mago";
        resetBaseStats();

        updateStats();
        this.totalHp = this.totalHpMax;
        this.currentMana = this.manaMax;
    }

    @Override
    protected void resetBaseStats() {
        baseHpMax = 25;
        baseStrength = 10;
        baseAgility = 10;
        baseIntelligence = 35; //primaria
        basePrecision = 20; //secondaria
    }
    @Override
    public boolean canEquipWeapon(Items item){
        return item instanceof Staff;
    }

    @Override
    protected void applyLevelUpStats() {
        this.baseIntelligence += 3; //boost primaria
        this.basePrecision += 2; //boost secondaria
        this.baseAgility += 1;
        this.baseStrength += 1;
        this.baseHpMax += 4;
    }
}
