package items.armors;

public class Helmet extends Armors {

    public Helmet(String name, String rarity, int defense, String boostedStat, int boostedStatVal, int economicValue) {
        this.name = name;
        this.rarity = rarity;
        this.stat = defense;
        this.boostedStat = boostedStat;
        this.boostedStatVal = boostedStatVal;
        this.economicValue = economicValue;

        this.equippedSlot.add("Testa");
    }
}