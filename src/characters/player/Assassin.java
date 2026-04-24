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
    }
    @Override
    public boolean canEquipWeapon(Items item){
        return item instanceof Dagger;
    }

}
