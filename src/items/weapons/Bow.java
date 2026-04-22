package items.weapons;


public class Bow extends Weapons {
    public Bow(String name, String rarity, int damage, String boostedStat, int boostedStatVal) {
        this.name = name;
        this.rarity = rarity;
        this.stat = damage;
        this.boostedStat = boostedStat;
        this.boostedStatVal = boostedStatVal;
        equippedSlot.add("Primaria");
    }

}
