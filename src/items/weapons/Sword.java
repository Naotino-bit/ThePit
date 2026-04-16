package items.weapons;


import java.util.ArrayList;

public class Sword extends Weapons {
    public Sword(String name, String rarity, int damage, String specialAbility) {
        this.name = name;
        this.rarity = rarity;
        this.damage = damage;
        this.specialAbility = specialAbility;
        equippedSlot.add("Primaria");
    }

}
