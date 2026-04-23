package characters.player;

import characters.Character;
import items.Items;
import items.weapons.Shield;
import items.weapons.Sword;

public class Warrior extends Character {
    public Warrior(){
        name = "Guerriero";
        baseHpMax = 120;
        baseStrength = 90; //primaria
        baseAgility = 85; //secondaria
        baseIntelligence = 30;
        basePrecision = 50;

        updateStats();
        this.totalHp = this.totalHpMax;
    }

    @Override
    public boolean canEquipWeapon(Items item) {
        return item instanceof Sword || item instanceof Shield;
    }
}
