package items.artefacts;

import java.util.HashMap;

public class Earrings extends Artefacts {

    public Earrings(String name, String rarity, String nameOfSet, String mainStat, int mainStatVal, HashMap<String, Integer> bonus) {


        super(name, rarity, nameOfSet, mainStat, mainStatVal, "Orecchini");

        if (bonus != null) {
            this.getSubStats().putAll(bonus);
        }
    }
}