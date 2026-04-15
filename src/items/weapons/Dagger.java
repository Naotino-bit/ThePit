package items.weapons;

import java.util.ArrayList;

public class Dagger extends Weapons {
    protected ArrayList<String> equippedSlot = new ArrayList<String>();

    public Dagger(String name, String rarity, int damage, String specialAbility) {
        this.name = name;
        this.rarity = rarity;
        this.damage = damage;
        this.specialAbility = specialAbility;
        equippedSlot.add("Primaria");
    }

    @Override
    public ArrayList<String> getEquippedSlot() {
        return equippedSlot;
    }


}
