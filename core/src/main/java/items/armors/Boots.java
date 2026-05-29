package items.armors;

public class Boots extends Armors {

    public Boots(String name, String rarity, int defense, String boostedStat, int boostedStatVal, int economicValue) {
        this.name = name;
        this.rarity = rarity;
        this.stat = defense;
        this.boostedStat = boostedStat;
        this.boostedStatVal = boostedStatVal;
        this.economicValue = economicValue;

        this.equippedSlot.add("Piedi");
    }
}