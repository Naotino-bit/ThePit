package items.weapons;

public class Shield extends Weapons{
    protected int defence;
    public Shield(String name, String rarity, int defence, String specialAbility) {
        this.name = name;
        this.rarity = rarity;
        this.defence = defence;
        this.specialAbility = specialAbility;
        equippedSlot.add("Secondaria");
    }
}
