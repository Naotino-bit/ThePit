//TODO FABIO GESTIRE TUTTI GLI OGGETTI LANCIABILI + EFFETTI AD AREA E NON
package items.usables;

import characters.Character;

public class Throwables extends Usables {

    public boolean isAoE;

    public Throwables(String name, String rarity, String stat, int value, int economicValue) {
        super(name, rarity, stat, value, economicValue);
        if (name.toLowerCase().contains("bomba") || name.toLowerCase().contains("bomb")
                || name.toLowerCase().contains("frammentazione")) {
            this.isAoE = true;
        } else {
            this.isAoE = false;
        }
    }

    @Override
    public String use(Character caster, Character target) {
        if (caster == target) {
            // Se caster == target, significa che lo stiamo usando fuori dalla battaglia
            // (oppure stiamo provando a tirarlo addosso a noi stessi, che non ha senso)
            return "Non puoi usare questo oggetto fuori dal combattimento!";
        }

        if (this.boostedStat.equals("Danno")) {
            String log = target.takeDamage(this.boostedStatVal);
            return caster.getName() + " lancia " + this.name + " infliggendo " + this.boostedStatVal + " danni a "
                    + target.getName() + "!\n" + log;
        }
        return "Nessun effetto applicabile.";
    }
}