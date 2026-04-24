package items.weapons;

public class Shield extends Weapons{
    protected int defence;
    public Shield(String name, String rarity, int defence, String boostedStat, int boostedStatVal) {
        this.name = name;
        this.rarity = rarity;
        this.stat = defence;
        this.boostedStat = boostedStat;
        this.boostedStatVal = boostedStatVal;
    }
}
