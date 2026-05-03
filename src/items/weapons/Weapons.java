package items.weapons;

import items.Items;
import java.util.ArrayList;

public class Weapons extends Items {
    protected ArrayList<String> equippedSlot = new ArrayList<String>();

    protected String effect;

    public String getEffect(){
        return this.effect;
    }


    @Override
    public String getDetails() {
        return super.getDetails() + " | Danno: " + stat + " | " + boostedStat + ": " + boostedStatVal;
    }

    @Override
    public ArrayList<String> getEquippedSlot() {
        return equippedSlot;
    }
}
