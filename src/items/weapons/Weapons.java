package items.weapons;

import items.Items;

public class Weapons extends Items {

    protected int damage;
    protected String specialAbility;

    @Override
    public String getDetails() {
        return super.getDetails() + " | Danno: " + damage + " | Abilità Speciale: " + specialAbility;
    }
}
