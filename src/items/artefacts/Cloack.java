package items.artefacts;

import java.util.ArrayList;
import java.util.HashMap;

public class Cloack extends Artefacts{
    public Cloack(String name, String rarity, HashMap<String, Integer> bonus) {
        this.name = name;
        this.rarity = rarity;

        equippedSlot.add("Mantello");
    }

}
