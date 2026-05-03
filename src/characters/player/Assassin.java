package characters.player;

import characters.Character;
import items.Items;
import items.weapons.Dagger;

public class Assassin extends Character {
    public Assassin() {
        name = "Assassino";
        baseHpMax = 100;
        baseStrength = 50;
        baseAgility = 81; //primaria
        baseIntelligence = 62;
        basePrecision = 62; //secondaria

        updateStats();
        this.totalHp = this.totalHpMax;
        this.currentMana = this.manaMax;
    }
    @Override
    public boolean canEquipWeapon(Items item){
        return item instanceof Dagger;
    }

    @Override
    protected void applyLevelUpStats() {
        this.baseAgility += 7;//boost primaria
        this.basePrecision += 5;//boost secondaria
        this.baseStrength += 2;
        this.baseIntelligence += 1;
        this.baseHpMax += 10;
    }
}
