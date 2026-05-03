package items;

import java.util.ArrayList;

public abstract class Items {
    protected String name;
    protected String rarity;
    protected int stat;
    protected String boostedStat;
    protected int boostedStatVal;

    protected int economicValue;

    public String getName() {
        return this.name;
    }
    public String getRarity() { return this.rarity; }

    public ArrayList<String> getEquippedSlot(){
        ArrayList<String> temp = new ArrayList<String>();
        temp.add("Slot item");
        return temp;
    }
    public int getBaseStat(){ return stat;}

    public String getDetails() {
        return "Nome: " + name + " | Rarità: " + rarity;
    }

    public int getBoostedStatVal(){ return this.boostedStatVal;}
    public String getBoostedStat(){ return this.boostedStat;}

    public int getEconomicValue(){ return this.economicValue; }
}