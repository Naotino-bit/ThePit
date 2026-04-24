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
    }
    @Override
    public boolean canEquipWeapon(Items item){
        return item instanceof Staff;
    }
}
