package characters;

import items.Items;
import items.armors.Armors;
import items.artefacts.Artefacts;

import java.lang.foreign.StructLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

public abstract class Character {
    protected String name;  //classe

    //statistiche BASE
    protected int baseHpMax;
    protected int baseStrength; //CRIT DMG Multiplier
    protected int baseAgility; //Precedenza turno, probabilità schivata
    protected int baseIntelligence; //Quantità, velocità recupero mana
    protected int basePrecision; //CRIT RATE Multiplier

    //statistiche + stat oggetti
    protected int totalHpMax;
    protected int totalHp;
    protected int totalStrength; //CRIT DMG Multiplier
    protected int totalAgility; //Precedenza turno, probabilità schivata
    protected int totalIntelligence; //Quantità, velocità recupero mana
    protected int totalPrecision; //CRIT RATE Multiplier

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

    protected void updateStats(){
        //resetto il personaggio alle stats base
        this.totalHpMax = this.baseHpMax;
        this.totalStrength = this.baseStrength;
        this.totalAgility = this.baseAgility;
        this.totalIntelligence = this.baseIntelligence;
        this.totalPrecision = this.basePrecision;

        for(Items item : equippedItems.values()){
            if(item != null){
                switch(item.getBoostedStat()){
                    case "Forza":
                        this.totalStrength += item.getBoostedStaVal();
                        break;
                    case "Intelligenza":
                        this.totalIntelligence += item.getBoostedStaVal();
                        break;
                    case "Agilità":
                        this.totalAgility += item.getBoostedStaVal();
                        break;
                    case "Precisione":
                        this.totalPrecision += item.getBoostedStaVal();
                        break;
                    default:
                        break;
                }
            }
        }
        if(this.totalHp > this.totalHpMax) {this.totalHp = this.totalHpMax;}
    }

    public void takeDamage(int receivedDamage) {
        //aggiungere calcolo difese
        totalHp -= receivedDamage;
    }

    public void attack(Character target) {
        int damage = this.totalStrength;
        target.takeDamage(damage);
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
    public void getEquippedItems() {
        System.out.println("\n----- Equipaggiamento -----");
        for( String i : equippedItems.keySet()) {
            try {
                System.out.println(i + ": " + equippedItems.get(i).getName());
            } catch (NullPointerException e) {
                System.out.println(i + ": " + "Non equipaggiato");
            }
            if(i.equals("Piedi") || i.equals("Secondaria")) {
                System.out.println("/");
            }
        }
    }
    public Object getInventory(){
        if(inventory.isEmpty()){
            return "Inventario vuoto";
        } else{
            return inventory;
        }
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


    public void addToInventory (Items item) {
        if(inventoryFull()) {
            System.out.println("Inventario pieno");
            return;
        }
        inventory.add(item);
    }

    public boolean inventoryFull() {return inventory.size() >= 20;}

    public ArrayList<String> getItemSlot(Items item) {
        return item.getEquippedSlot();
    } // non è tipo inutile questa broder?

    public boolean inInventory(Items item){return inventory.contains(item);}

    public void equip(Items item){
        if(inInventory(item)){
            inventory.remove(item);
        }

        if(item instanceof Armors || item instanceof Artefacts){
            equippedItems.replace(item.getEquippedSlot().getFirst(), item);
            System.out.println("Hai equippaggiato: " + item.getName());

            updateStats(); //ocho al mocho
        } else{
            System.out.println("Non puoi equipaggiare: " + item.getName());
            inventory.add(item);
        }
    }

    public void deEquip(String target){
        Items itemToDeEquip = equippedItems.get(target);
        if (itemToDeEquip != null){
            inventory.add(itemToDeEquip);
            equippedItems.replace(target, null);
            System.out.println("Hai rimosso: " + itemToDeEquip.getName());

            updateStats();
        } else{
            System.out.println("Lo slot " + target + " è già vuoto");
        }
    }


}