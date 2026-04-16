package items.armors;

import java.util.ArrayList;

public class Helmet extends Armors{
    public Helmet(String name, String rarity) {
        this.name = name;
        this.rarity = rarity;
        equippedSlot.add("Testa");
    }
}
