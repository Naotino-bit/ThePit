package characters;

import items.Items;
import items.armors.Armors;
import items.artefacts.Artefacts;
import items.weapons.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Character {
    protected String name;  //classe

    //statistiche BASE
    protected int baseHpMax;
    protected int baseStrength; //Danno personaggio
    protected int baseAgility; //Precedenza turno, probabilità schivata
    protected int baseIntelligence; //Quantità, velocità recupero mana
    protected int basePrecision; //CRIT RATE / DMG Multiplier

    //statistiche + stat oggetti
    protected int totalHpMax;
    protected int totalHp;
    protected int totalStrength; //Danno personaggio
    protected int totalAgility; //Precedenza turno, probabilità schivata
    protected int totalIntelligence; //Quantità, velocità recupero mana
    protected int totalPrecision; //CRIT RATE / DMG Multiplier
    protected int weaponDamage; //Danno aggiunto dall'arma
    protected int armorDefence; //Difesa aumantata dallo scudo

    //SOLDI
    protected int money = 0;

    protected ArrayList<Items> inventory = new ArrayList<Items>();
    protected HashMap<String, Items> equippedItems = new LinkedHashMap<String, Items>();

    public Character(){
        equippedItems.put("Testa", null);
        equippedItems.put("Torso", null);
        equippedItems.put("Gambe", null);
        equippedItems.put("Piedi", null);
        equippedItems.put("Primaria", null);
        equippedItems.put("Secondaria", null);
        equippedItems.put("Mantello", null);
        equippedItems.put("Orecchini", null);
        equippedItems.put("Collana", null);
        equippedItems.put("Anello", null);
    }

    public abstract boolean canEquipWeapon(Items item);

    protected void updateStats(){
        //resetto il personaggio alle stats base
        this.totalHpMax = this.baseHpMax;
        this.totalStrength = this.baseStrength;
        this.totalAgility = this.baseAgility;
        this.totalIntelligence = this.baseIntelligence;
        this.totalPrecision = this.basePrecision;

        this.weaponDamage = 0; //resetto danno arma
        this.armorDefence = 0; //resetto difesa arma e armatura

        //serve a non calcolare due volte le armi a due mani
        HashSet<Items> uniqueEquipped = new HashSet<>(equippedItems.values());
        for(Items item : uniqueEquipped){
            if(item != null){

                if(item instanceof Shield || item instanceof Armors) this.armorDefence += item.getBaseStat();
                else if(item instanceof Weapons) this.weaponDamage += item.getBaseStat();
                switch(item.getBoostedStat()){
                    case "Forza":
                        this.totalStrength += item.getBoostedStatVal();
                        break;
                    case "Intelligenza":
                        this.totalIntelligence += item.getBoostedStatVal();
                        break;
                    case "Agilità":
                        this.totalAgility += item.getBoostedStatVal();
                        break;
                    case "Precisione":
                        this.totalPrecision += item.getBoostedStatVal();
                        break;
                    case "Vitalità":
                        this.totalHpMax += item.getBoostedStatVal();
                        break;
                }

                if(item instanceof Artefacts){
                    Artefacts artefact = (Artefacts) item;
                    for (Map.Entry<String, Integer> subStat : artefact.getSubStats().entrySet()){
                        String nameSubStat = subStat.getKey();
                        int valSubStat = subStat.getValue();

                        switch(nameSubStat) { //gasa lo switch scritto così
                            case "Forza":
                                this.totalStrength += valSubStat;
                                break;
                            case "Intelligenza":
                                this.totalIntelligence += valSubStat;
                                break;
                            case "Agilità":
                                this.totalAgility += valSubStat;
                                break;
                            case "Precisione":
                                this.totalPrecision += valSubStat;
                                break;
                            case "Vitalità":
                                this.totalHpMax += valSubStat;
                                break;
                        }
                    }
                }
            }
        }

        HashMap<String, Integer> setCounter = new HashMap<>();

        for(Items item : uniqueEquipped){
            if (item instanceof Artefacts) {
                Artefacts artefacts = (Artefacts) item;

                String nomeSet = artefacts.getNameOfSet();
                setCounter.put(nomeSet, setCounter.getOrDefault(nomeSet, 0) + 1);
            }
        }

        //
        for (Map.Entry<String, Integer> set : setCounter.entrySet()) {
            String nameOfSet = set.getKey();
            int equippedPieces = set.getValue();

            // Nomi fittizzi per ora
            if (nameOfSet.equals("Grande Albero")) {
                if (equippedPieces >= 2) this.totalIntelligence += 80;
                if (equippedPieces >= 4) this.totalStrength += 50;
            }
            else if (nameOfSet.equals("Gladiatore")) {
                if (equippedPieces >= 2) this.totalStrength += 18;
                if (equippedPieces >= 4) this.weaponDamage += 30;
            }
            // qua poi ne andranno altri
        }

        if(this.totalHp > this.totalHpMax) {this.totalHp = this.totalHpMax;}
    }


    public HashMap<String, Integer> getStats() {
        HashMap<String, Integer> stats = new HashMap<>();
        stats.put("HpMax", totalHpMax);
        stats.put("Strength" , totalStrength);
        stats.put("Agility", totalAgility);
        stats.put("Intelligence", totalIntelligence);
        stats.put("Precision", totalPrecision);
        return stats;
    }

    public HashMap<String, Integer> getBaseStats() {
        HashMap<String, Integer> stats = new HashMap<>();
        stats.put("HpMax", baseHpMax);
        stats.put("Strength" , baseStrength);
        stats.put("Agility", baseAgility);
        stats.put("Intelligence", baseIntelligence);
        stats.put("Precision", basePrecision);
        return stats;
    }

    public void takeDamage(int receivedDamage) {
        int dannoEffettivo = Math.max(0, receivedDamage - this.armorDefence);
        this.totalHp -= dannoEffettivo;
    }

    public void attack(Character target) {
        int finalDamage = this.weaponDamage + this.totalStrength;
        target.takeDamage(finalDamage);
    }

    public boolean isDead() {
        return this.totalHp <= 0;
    }

    //SETTERS
    public void setHp(int hp) {
        this.totalHp = hp;
    }

    //GETTERS
    public String getName() {return name;}
    public int getHpMax(){return totalHpMax;}
    public int getHp(){return  totalHp;}
    public String getEquippedItems() {
        String temp ="\n----- Equipaggiamento -----\n";
        for( String i : equippedItems.keySet()) {
            try {
                temp += i + ": " + equippedItems.get(i).getName() + "\n";
            } catch (NullPointerException e) {
                temp += i + ": " + "Non equipaggiato\n";
            }
            if(i.equals("Piedi") || i.equals("Secondaria")) {
                temp += "/\n";
            }
        }
        return temp;
    }
    public ArrayList<Items> getInventory(){
        return inventory;
    }

    //STAMPA DEL PERSONAGGIO
    public void presentation(){
        System.out.println("\n-----STATS-----\n" +
                "Classe: " + name + "" +
                "\nPunti vita: " + totalHpMax + "/" + totalHpMax +
                "\nForza: " + totalStrength +
                "\nAgilità: " + totalAgility +
                "\nIntelligenza: " + totalIntelligence +
                "\nPrecisione: " + totalPrecision);
    }


    public boolean addToInventory (Items item) {
        if(inventoryFull()) {
            return false;
        }
        inventory.add(item);
        return true;
    }

    public void removeFromInventory (Items item){
        inventory.remove(item);
    }

    public boolean inventoryFull() {return inventory.size() >= 2;}

    public ArrayList<String> getItemSlot(Items item) {
        return item.getEquippedSlot();
    } // non è tipo inutile questa broder?

    public boolean inInventory(Items item){return inventory.contains(item);}

    public void equip(Items item){
        if(!inInventory(item)){
            System.out.println("Non hai questo oggetto nell'inventario");
            return;
        }

        if(item instanceof Weapons){
            if(!canEquipWeapon(item)){
                System.out.println("Non puoi usare: "+ item.getName());
                return;
            }

            inventory.remove(item);
            equipWeaponLogic(item); //TODO ANCORA DA IMPLEMENTARE
        }

        else if (item instanceof Armors || item instanceof Artefacts) {
            inventory.remove(item);

            if(equippedItems.get(item.getEquippedSlot().getFirst()) != null){
                inventory.add(equippedItems.get(item.getEquippedSlot().getFirst()));
            }

            equippedItems.replace(item.getEquippedSlot().getFirst(), item);
            System.out.println("Hai equipaggiato: " + item.getName());
        }

        updateStats();
    }

    private void equipWeaponLogic(Items item){
        Items primaria = equippedItems.get("Primaria");
        Items secondaria = equippedItems.get("Secondaria");

        boolean isTwoHanded = (item instanceof Bow || item instanceof Staff || item instanceof Claymore);
        if(isTwoHanded){
            if(primaria != null){ inventory.add(primaria); }
            if(secondaria != null && secondaria != primaria){ inventory.add(secondaria);}

            equippedItems.replace("Primaria", item);
            equippedItems.replace("Secondaria", item);

            System.out.println("Hai equipaggiato " + item.getName() + " a due mani");
        }

        else if(item instanceof Dagger){
            if(primaria == null){
                equippedItems.replace("Primaria", item);
            }
            else if (secondaria == null) {
                if(primaria == secondaria){
                    inventory.add(primaria);
                    equippedItems.replace("Primaria", item);
                    equippedItems.replace("Secondaria", null);
                }
                else{
                    equippedItems.replace("Secondaria", item);
                }
            }
            else{
                inventory.add(primaria);
                equippedItems.replace("Primaria", item);
            }
            System.out.println("Hai equipaggiato " + item.getName());
        }
        else if(item instanceof Shield) {
            if(secondaria != null && primaria != secondaria) inventory.add(secondaria);

            if(primaria != null && primaria == secondaria){
                inventory.add(primaria);
                equippedItems.replace("Primaria", null);
            }
            equippedItems.replace("Secondaria", item);
            System.out.println("Hai equipaggiato " + item.getName());
        }
        else if (item instanceof Sword) {
            if(primaria != null && primaria != secondaria) inventory.add(primaria);
            if(primaria != null && primaria == secondaria){
                inventory.add(primaria);
                equippedItems.replace("Secondaria", null);
            }
            equippedItems.replace("Primaria", item);
            System.out.println("Hai equipaggiato " + item.getName());
        }
    }

    public void deEquip(String target){
        Items itemToDeEquip = equippedItems.get(target);
        if(itemToDeEquip != null){
            inventory.add(itemToDeEquip);

            if(target.equals("Primaria") && equippedItems.get("Secondaria") == itemToDeEquip){
                equippedItems.replace("Secondaria", null);
            }
            else if (target.equals("Secondaria") && equippedItems.get("Primaria") == itemToDeEquip ){
                equippedItems.replace("Primaria", null);
            }

            equippedItems.replace(target, null);
            System.out.println("Hai rimosso: " + itemToDeEquip.getName());

            updateStats();
        }
        else{
            System.out.println("Lo slot " + target + " è già vuoto");
        }
    }
}