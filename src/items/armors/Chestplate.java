package items.armors;

import java.util.ArrayList;

public class Chestplate extends Armors{
    public Chestplate(String name, String rarity) {
        this.name = name;
        this.rarity = rarity;
        equippedSlot.add("Torso");
    }
}
