package items.artefacts;

import java.util.HashMap;

public class Ring extends Artefacts {

    public Ring(String name, String rarity, String nameOfSet, String mainStat, int mainStatVal, HashMap<String, Integer> bonus, int economicValue) {


        super(name, rarity, nameOfSet, mainStat, mainStatVal, "Anello", economicValue);

        if (bonus != null) {
            this.getSubStats().putAll(bonus);
        }
    }
}