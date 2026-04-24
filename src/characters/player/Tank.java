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
    }

    @Override
    public boolean canEquipWeapon(Items item){
        return item instanceof Claymore;
    }
}
