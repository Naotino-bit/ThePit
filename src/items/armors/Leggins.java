package items.armors;

public class Leggins extends Armors {

    public Leggins(String name, String rarity, int defense, String boostedStat, int boostedStatVal, int economicValue) {
        this.name = name;
        this.rarity = rarity;
        this.stat = defense;
        this.boostedStat = boostedStat;
        this.boostedStatVal = boostedStatVal;
        this.economicValue = economicValue;

        this.equippedSlot.add("Gambe");
    }
}