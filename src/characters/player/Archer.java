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
    }

    @Override
    public boolean canEquipWeapon(Items item){
        return item instanceof Bow;
    }
}
