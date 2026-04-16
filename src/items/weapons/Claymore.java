package items.weapons;

import java.util.ArrayList;

public class Claymore extends Weapons {
    public Claymore(String name, String rarity, int damage, String specialAbility) {
        this.name = name;
        this.rarity = rarity;
        this.damage = damage;
        this.specialAbility = specialAbility;

        equippedSlot.add("Primaria");
        equippedSlot.add("Secondaria");
    }

}
