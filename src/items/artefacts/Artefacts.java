package items.artefacts;

import items.Items;

import java.util.ArrayList;
import java.util.HashMap;

public class Artefacts extends Items {
    protected ArrayList<String> equippedSlot = new ArrayList<String>();
    public Artefacts () {

    }

    @Override
    public ArrayList<String> getEquippedSlot() {
        return equippedSlot;
    }
}
