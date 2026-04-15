package items.armors;

import java.util.ArrayList;

public class Helmet extends Armors{
    protected ArrayList<String> equippedSlot = new ArrayList<String>();
    public Helmet(String name, String rarity) {
        this.name = name;
        this.rarity = rarity;
        equippedSlot.add("Testa");
    }
}
