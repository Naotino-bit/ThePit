package items.weapons;


public class Sword extends Weapons {
    public Sword(String name, String rarity, int damage, String boostedStat, int boostedStatVal) {
        this.name = name;
        this.rarity = rarity;
        this.stat = damage;
        this.boostedStat = boostedStat;
        this.boostedStatVal = boostedStatVal;
    }

}
