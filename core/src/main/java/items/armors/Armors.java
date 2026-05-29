package items.armors;

import items.Items;

import java.util.ArrayList;

public class Armors extends Items {
    protected ArrayList<String> equippedSlot = new ArrayList<String>();
    protected int physicalDefence;
    protected int magicalDefence;

    @Override
    public ArrayList<String> getEquippedSlot() {
        return equippedSlot;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + " | Difesa: " + stat + " | " + boostedStat + ": +" + boostedStatVal + " | Valore: " + economicValue + " monete";
    }
}
