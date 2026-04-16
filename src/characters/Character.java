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
    protected int hpMax;
    protected int hp;
    protected int strength; //CRIT DMG Multiplier
    protected int agility; //Precedenza turno, probabilità schivata
    protected int intelligence; //Quantità, velocità recupero mana
    protected int precision; //CRIT RATE Multiplier
    // ↓↓↓↓ stats che variano in base all'equipaggiamento ↓↓↓↓
    protected int damage = 2;
    protected int defence = 0;
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

    public void takeDamage(int receivedDamage) {
        //aggiungere calcolo difese
        hp -= receivedDamage;
    }

    public String attack(Character target) {
        //aggiungere metodo per calcolare il danno in base a armi e oggetti
        target.takeDamage(damage);

        if(isDead(target)) {
            return "Hai sconfitto: " + target.name;
        }
        return "Hai inflitto " + damage + " danni a " + target.name;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getHp() {
        return  hp;
    }
    public void presentation(){
        System.out.println("\n-----STATS-----\n" + "Classe: " + name + "\nPunti vita: " + hpMax + "\nForza: " + strength + "\nAgilità: " + agility + "\nIntelligenza: " + intelligence + "\nPrecisione: " + precision);
    }

    public boolean isDead(Character target) {
        if(target.hp <= 0){
          return true;
        } else {
            return false;
        }
    }

    public Object getInventory () { //SISTEMARLO CON LE EXEPTION
        if(inventory.size() <= 0) {
            return "Inventario vuoto";
        } else {
            return inventory;
        }
    }

    public void addToInventory (Items item) {
        if (inventoryFull()) {
            System.out.println("Inventario pieno");
            return;
        }
        inventory.add(item);
    }

    public boolean inventoryFull() {
        if(inventory.size() == 20) { //slot totali di inventario
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<String> getItemSlot(Items item) {
        return item.getEquippedSlot();
    }

    public boolean inInventory(Items item){
        if(inventory.contains(item)){
            return true;
        } else {
            return false;
        }
    }

    public void equip(Items item) {
        if(item instanceof Armors || item instanceof Artefacts){
            equippedItems.replace(item.getEquippedSlot().getFirst(), item);
            System.out.println("Hai equipaggiato: " + item);
        } else {
            System.out.println("Non puoi equipaggiare " + item.getName());
        }
    }

    public void deEquip(String target) {
        inventory.add(equippedItems.get(target));
        equippedItems.replace(target, null);
    }

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
}