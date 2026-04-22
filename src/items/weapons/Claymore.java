package items.weapons;

public class Claymore extends Weapons {
    public Claymore(String name, String rarity, int damage, String specialAbility) {
        this.name = name;
        this.rarity = rarity;
        this.stat = damage;
        this.boostedStat = boostedStat;
        this.boostedStatVal = boostedStatVal;

        equippedSlot.add("Primaria");
        equippedSlot.add("Secondaria");
    }

}
