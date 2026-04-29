package items.artefacts;

import java.util.HashMap;

public class Cloack extends Artefacts {

    public Cloack(String name, String rarity, String nameOfSet, String mainStat, int mainStatVal, HashMap<String, Integer> bonus) {


        super(name, rarity, nameOfSet, mainStat, mainStatVal, "Mantello");

        if (bonus != null) {
            this.getSubStats().putAll(bonus);
        }
    }
}