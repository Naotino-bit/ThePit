package items.weapons;

public class Staff extends Weapons{
    public Staff(String name, String rarity, int damage, String specialAbility) {
        this.name = name;
        this.rarity = rarity;
        this.damage = damage;
        this.specialAbility = specialAbility;
        equippedSlot.add("Primaria");
        equippedSlot.add("Secondaria");
    }
}
