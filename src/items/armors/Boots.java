package items.armors;

import java.util.ArrayList;

public class Boots extends Armors{

    protected ArrayList<String> equippedSlot = new ArrayList<String>();
    public Boots(String name, String rarity) {
        this.name = name;
        this.rarity = rarity;
        equippedSlot.add("Piedi");
    }
}
