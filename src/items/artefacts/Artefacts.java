package items.artefacts;

import items.Items;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Artefacts extends Items {
    protected ArrayList<String> equippedSlot = new ArrayList<String>();

    private String nameOfSet; //nome del set per identificare il bonus set
    private HashMap<String, Integer> subStats; //tutte le substats del singolo pezzo

    public Artefacts (String name, String rarity, String nameOfSet, String mainStat, int mainStatVal, String slotTarget, int economicValue) {
        this.name = name;
        this.rarity = rarity;

        this.boostedStat = mainStat;
        this.boostedStatVal = mainStatVal;

        this.nameOfSet = nameOfSet;
        this.equippedSlot.add(slotTarget);
        this.subStats = new HashMap<>();

        this.economicValue = economicValue;
    }

    public void addSubStat(String statName, int value){
        this.subStats.put(statName, value);
    }

    public String getNameOfSet(){ return nameOfSet; }
    public HashMap<String, Integer> getSubStats() { return subStats; }


    @Override
    public ArrayList<String> getEquippedSlot() {
        return equippedSlot;
    }

    @Override
    public String getDetails() {
        StringBuilder details = new StringBuilder(super.getDetails() + " | Set: " + getNameOfSet() + " | " + boostedStat + ": +" + boostedStatVal);

        if (getSubStats() != null && !getSubStats().isEmpty()) {
            details.append(" | SubStats: [");
            for (Map.Entry<String, Integer> entry : getSubStats().entrySet()) {
                details.append(entry.getKey()).append(": +").append(entry.getValue()).append(" ");
            }
            details.append("]");
        }

        return details.toString().replace(" ]", "]") + " | Valore: " + economicValue + " monete";
    }
}
