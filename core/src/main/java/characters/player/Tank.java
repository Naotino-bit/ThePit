package characters.player;

import characters.Character;
import items.Items;
import items.weapons.Bow;
import items.weapons.Claymore;

public class Tank extends Character {
    public Tank() {
        name = "Tank";
        resetBaseStats();

        updateStats();
        this.totalHp = this.totalHpMax;
        this.currentMana = this.manaMax;
    }

    @Override
    protected void resetBaseStats() {
        baseHpMax = 45;
        baseStrength = 30; //primaria
        baseAgility = 10;
        baseIntelligence = 20; //secondaria
        basePrecision = 15;
    }

    @Override
    public boolean canEquipWeapon(Items item){
        return item instanceof Claymore;
    }

    @Override
    protected void applyLevelUpStats() {
        this.baseStrength += 3; //primaria
        this.baseAgility += 1; //secondaria
        this.basePrecision += 1;
        this.baseIntelligence += 2;
        this.baseHpMax += 8;
    }
}
