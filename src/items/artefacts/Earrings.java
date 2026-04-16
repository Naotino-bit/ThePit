package items.artefacts;

import java.util.ArrayList;
import java.util.HashMap;

public class Earrings extends Artefacts{
    public Earrings(String name, String rarity, HashMap<String, Integer> bonus) {
        this.name = name;
        this.rarity = rarity;

        equippedSlot.add("Orecchini");
    }

}
