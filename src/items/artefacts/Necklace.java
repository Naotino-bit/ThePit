package items.artefacts;

import java.util.ArrayList;
import java.util.HashMap;

public class Necklace extends Artefacts{
    public Necklace(String name, String rarity, HashMap<String, Integer> bonus) {
        this.name = name;
        this.rarity = rarity;

        equippedSlot.add("Collana");
    }

}
