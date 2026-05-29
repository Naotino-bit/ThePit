package items.weapons;


public class Bow extends Weapons {
    public Bow(String name, String rarity, int damage, String boostedStat, int boostedStatVal, int economicValue, String effect) {
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
