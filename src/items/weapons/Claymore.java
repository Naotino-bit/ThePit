package items.weapons;

import java.util.ArrayList;

public class Claymore extends Weapons {
    protected ArrayList<String> equippedSlot = new ArrayList<String>();

    public Claymore(String name, String rarity, int damage, String specialAbility) {
        this.name = name;
        this.rarity = rarity;
        this.damage = damage;
        this.specialAbility = specialAbility;

        equippedSlot.add("Primaria");
        equippedSlot.add("Secondaria");
    }

    @Override
    public ArrayList<String> getEquippedSlot() {
        return equippedSlot;
    }
}
