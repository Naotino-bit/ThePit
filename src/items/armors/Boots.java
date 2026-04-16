package items.armors;

import java.util.ArrayList;

public class Boots extends Armors{
    public Boots(String name, String rarity) {
        this.name = name;
        this.rarity = rarity;
        equippedSlot.add("Piedi");
    }
}
