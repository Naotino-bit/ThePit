package items.artefacts;

import java.util.ArrayList;
import java.util.HashMap;

public class Ring extends Artefacts{
    public Ring(String name, String rarity, HashMap<String, Integer> bonus){
        this.name = name;
        this.rarity = rarity;

        equippedSlot.add("Anello");
    }

}
