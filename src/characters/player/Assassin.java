package characters.player;

import characters.Character;
import items.Items;
import items.weapons.Dagger;

public class Assassin extends Character {
    public Assassin() {
        name = "Assassino";
        resetBaseStats();

        updateStats();
        this.totalHp = this.totalHpMax;
        this.currentMana = this.manaMax;
    }

    @Override
    protected void resetBaseStats() {
        baseHpMax = 30;
        baseStrength = 15;
        baseAgility = 25; //primaria
        baseIntelligence = 15;
        basePrecision = 20; //secondaria
    }
    @Override
    public boolean canEquipWeapon(Items item){
        return item instanceof Dagger;
    }

    @Override
    protected void applyLevelUpStats() {
        this.baseAgility += 3;//boost primaria
        this.basePrecision += 2;//boost secondaria
        this.baseStrength += 1;
        this.baseIntelligence += 1;
        this.baseHpMax += 4;
    }
}
