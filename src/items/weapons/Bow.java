package items.weapons;


import java.util.ArrayList;

public class Bow extends Weapons {
    protected ArrayList<String> equippedSlot = new ArrayList<String>();
    public Bow(String name, String rarity, int damage) {
        this.name = name;
        this.rarity = rarity;
        this.damage = damage;
        equippedSlot.add("Primaria");
    }

    @Override
    public ArrayList<String> getEquippedSlot() {
        return equippedSlot;
    }
}
