package items;

import java.util.ArrayList;

public abstract class Items {
    protected String name;
    protected String rarity;
    protected int damage;

    public String getName() {
        return this.name;
    }
    public String getRarity() {
        return this.rarity;
    }
    public int getDamage() {
        return this.damage;
    }

    public ArrayList<String> getEquippedSlot(){
        ArrayList<String> temp = new ArrayList<String>();
        temp.add("Slot item");
        return temp;
    }

    public String getDetails() {
        return "Nome: " + name + " | Rarità: " + rarity + " | Danno: " + damage;
    }

}