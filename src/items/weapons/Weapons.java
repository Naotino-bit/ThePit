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
        String baseDetails = super.getDetails() + " | Danno: " + stat + " | " + boostedStat + ": +" + boostedStatVal;

        if (this.effect != null && !this.effect.equalsIgnoreCase("Nessuno")) {
            baseDetails += " | Effetto: " + this.effect;
        }
        return baseDetails;
    }

    @Override
    public ArrayList<String> getEquippedSlot() {
        return equippedSlot;
    }
}
