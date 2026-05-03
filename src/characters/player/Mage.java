package characters.player;

import characters.Character;
import items.Items;
import items.weapons.Bow;
import items.weapons.Staff;

public class Mage extends Character {
    public Mage() {
        name = "Mago";
        baseHpMax = 75;
        baseStrength = 30;
        baseAgility = 30;
        baseIntelligence = 115; //primaria
        basePrecision = 80; //secondaria

        updateStats();
        this.totalHp = this.totalHpMax;
        this.currentMana = this.manaMax;
    }
    @Override
    public boolean canEquipWeapon(Items item){
        return item instanceof Staff;
    }

    @Override
    protected void applyLevelUpStats() {
        this.baseIntelligence += 8; //boost primaria
        this.basePrecision += 3; //boost secondaria
        this.baseAgility += 2;
        this.baseStrength += 1;
        this.baseHpMax += 10;
    }
    @Override
    protected int getBaseDamage() {
        return this.weaponDamage + this.totalIntelligence;
    }
}
