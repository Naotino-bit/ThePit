package items.weapons;

public class Claymore extends Weapons {
    public Claymore(String name, String rarity, int damage, String boostedStat, int boostedStatVal) {
        this.name = name;
        this.rarity = rarity;
        this.stat = damage;
        this.boostedStat = boostedStat;
        this.boostedStatVal = boostedStatVal;
    }

}
