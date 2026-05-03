package characters.player;

import characters.Character;
import items.Items;
import items.weapons.Bow;
import items.weapons.Claymore;

public class Tank extends Character {
    public Tank() {
        name = "Tank";
        baseHpMax = 150;
        baseStrength = 105; //primaria
        baseAgility = 40;
        baseIntelligence = 70; //secondaria
        basePrecision = 40;

        updateStats();
        this.totalHp = this.totalHpMax;
        this.currentMana = this.manaMax;
    }

    @Override
    public boolean canEquipWeapon(Items item){
        return item instanceof Claymore;
    }

    @Override
    protected void applyLevelUpStats() {
        this.baseStrength += 8; //primaria
        this.baseAgility += 1; //secondaria
        this.basePrecision += 1;
        this.baseIntelligence += 3;
        this.baseHpMax += 30;
    }
}
