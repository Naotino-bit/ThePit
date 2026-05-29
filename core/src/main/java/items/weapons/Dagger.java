package items.weapons;

import java.util.ArrayList;

public class Dagger extends Weapons {
    public Dagger(String name, String rarity, int damage, String boostedStat, int boostedStatVal, int economicValue, String effect) {
        this.name = name;
        this.rarity = rarity;
        this.stat = damage;
        this.boostedStat = boostedStat;
        this.boostedStatVal = boostedStatVal;
        this.economicValue = economicValue;
        this.effect = effect;
        equippedSlot.add("Primaria");
    }

}
