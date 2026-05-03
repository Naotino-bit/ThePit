package items.usables;

import characters.Character;

public class ManaPotion extends Usables {

    public ManaPotion(String name, String rarity, String stat, int value, int economicValue) {
        super(name, rarity, stat, value, economicValue);
    }

    @Override
    public String use(Character caster, Character target) {
        if (this.boostedStat.equals("Mana")) {
            caster.setCurrentMana(caster.getCurrentMana() + this.boostedStatVal);

            return caster.getName() + " ha bevuto " + this.name + " e ha recuperato " + this.boostedStatVal + " Mana!";
        }
        return "Nessun effetto applicabile.";
    }
}