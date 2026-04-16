package items.weapons;

import items.Items;

import java.util.ArrayList;

public class Weapons extends Items {
    protected ArrayList<String> equippedSlot = new ArrayList<String>();
    protected int damage;
    protected String specialAbility;

    @Override
    public String getDetails() {
        return super.getDetails() + " | Danno: " + damage + " | Abilità Speciale: " + specialAbility;
    }

    @Override
    public ArrayList<String> getEquippedSlot() {
        return equippedSlot;
    }
}
