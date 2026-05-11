//TODO FABIO GESTIRE TUTTI GLI OGGETTI LANCIABILI + EFFETTI AD AREA E NON
package items.usables;

import characters.Character;

public class Throwables extends Usables {

    public Throwables(String name, String rarity, String stat, int value, int economicValue) {
        super(name, rarity, stat, value, economicValue);
    }

    @Override
    public String use(Character caster, Character target) {
        if (this.boostedStat.equals("Danno")) {
            target.takeDamage(this.boostedStatVal);
            return caster.getName() + " lancia " + this.name + " infliggendo " + this.boostedStatVal + " danni a " + target.getName() + "!";
        }
        return "Nessun effetto applicabile.";
    }
}