package items.usables;

import characters.Character;

public class HealthPotion extends Usables {

    public HealthPotion(String name, String rarity, String stat, int value, int economicValue) {

        super(name, rarity, stat, value, economicValue);
    }

    @Override
    public String use(Character caster, Character target) {
        if (this.boostedStat.equals("Vitalità")) {
            caster.setHp(caster.getHp() + this.boostedStatVal);
            if (caster.getHp() > caster.getHpMax()) {
                caster.setHp(caster.getHpMax());
            }
            caster.removeFromInventory(this);
            return caster.getName() + " ha bevuto " + this.name + " e ha recuperato " + this.boostedStatVal + " HP!";
        }
        return "Nessun effetto applicabile.";
    }
}