package items.armors;

import java.util.ArrayList;

public class Chestplate extends Armors{
    protected ArrayList<String> equippedSlot = new ArrayList<String>();
    public Chestplate(String name, String rarity) {
        this.name = name;
        this.rarity = rarity;
        equippedSlot.add("Torso");
    }
}
