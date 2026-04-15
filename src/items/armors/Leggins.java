package items.armors;

import java.util.ArrayList;

public class Leggins extends Armors{

    protected ArrayList<String> equippedSlot = new ArrayList<String>();
    public Leggins(String name, String rarity) {
        this.name = name;
        this.rarity = rarity;
        equippedSlot.add("Gambe");
    }
}
