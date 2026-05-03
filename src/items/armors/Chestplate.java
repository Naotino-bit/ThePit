package items.armors;

public class Chestplate extends Armors {

    public Chestplate(String name, String rarity, int defense, String boostedStat, int boostedStatVal, int economicValue) {
        this.name = name;
        this.rarity = rarity;
        this.stat = defense;
        this.boostedStat = boostedStat;
        this.boostedStatVal = boostedStatVal;
        this.economicValue = economicValue;

        this.equippedSlot.add("Torso");
    }
}