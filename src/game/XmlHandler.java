package game;

import items.Items;
import items.usables.ManaPotion;
import items.weapons.*;
import items.armors.*;
import items.usables.HealthPotion;
import items.usables.Throwables;

import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlHandler {

    public static ArrayList<Items> everyWeapon;
    public static ArrayList<Items> everyArmor;
    public static ArrayList<Items> everyArtefact;
    public static ArrayList<Items> everyUsable;
    public static ArrayList<Items> everyItem;

    public static void loadAllItems(){
        everyWeapon = loadWeapons();
        everyArmor = loadArmors();
        everyArtefact = loadArtefacts();
        everyUsable = loadUsables();

        everyItem = new ArrayList<>();
        if(everyWeapon != null) everyItem.addAll(everyWeapon);
        if(everyArmor != null) everyItem.addAll(everyArmor);
        if(everyArtefact != null) everyItem.addAll(everyArtefact);
        if(everyUsable != null) everyItem.addAll(everyUsable);
    }

    public static ArrayList<Items> loadWeapons() {
        ArrayList<Items> everyWeapon = new ArrayList<>();

        try {
            File xmlFile = new File("data/weapons.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("Weapon");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String type = element.getAttribute("tipo");
                    String name = element.getAttribute("nome");
                    String rarity = element.getAttribute("rarita");
                    int baseDamage = Integer.parseInt(element.getAttribute("dannoBase"));
                    String stat = element.getAttribute("stat");
                    int statVal = Integer.parseInt(element.getAttribute("valStat"));
                    int price = Integer.parseInt(element.getAttribute("prezzo"));

                    String effect = element.getAttribute("effetto");
                    if(effect.isEmpty()) effect = "Nessuno";

                    switch (type) {
                        case "Sword":
                            everyWeapon.add(new Sword(name, rarity, baseDamage, stat, statVal, price, effect));
                            break;
                        case "Dagger":
                            everyWeapon.add(new Dagger(name, rarity, baseDamage, stat, statVal, price, effect));
                            break;
                        case "Bow":
                            everyWeapon.add(new Bow(name, rarity, baseDamage, stat, statVal, price, effect));
                            break;
                        case "Claymore":
                            everyWeapon.add(new Claymore(name, rarity, baseDamage, stat, statVal, price, effect));
                            break;
                        case "Shield":
                            everyWeapon.add(new Shield(name, rarity, baseDamage, stat, statVal, price, effect));
                            break;
                        case "Staff":
                            everyWeapon.add(new Staff(name, rarity, baseDamage, stat, statVal, price, effect));
                            break;
                    }
                }
            }
            System.out.println("Caricate " + everyWeapon.size() + " armi dal file XML!");
        } catch (Exception e) {
            System.out.println("Errore nel caricamento del file XML: " + e.getMessage());
            e.printStackTrace();
        }
        return everyWeapon;
    }

    public static ArrayList<Items> loadArmors() {
        ArrayList<Items> everyArmor = new ArrayList<>();

        try {
            File xmlFile = new File("data/armors.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("Armor");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String type = element.getAttribute("tipo");
                    String name = element.getAttribute("nome");
                    String rarity = element.getAttribute("rarita");
                    int baseDefense = Integer.parseInt(element.getAttribute("difesaBase"));
                    String stat = element.getAttribute("stat");
                    int statVal = Integer.parseInt(element.getAttribute("valStat"));

                    // ESTRAZIONE DEL PREZZO
                    int price = Integer.parseInt(element.getAttribute("prezzo"));

                    switch (type) {
                        case "Helmet":
                            everyArmor.add(new Helmet(name, rarity, baseDefense, stat, statVal, price));
                            break;
                        case "Chestplate":
                            everyArmor.add(new Chestplate(name, rarity, baseDefense, stat, statVal, price));
                            break;
                        case "Leggins":
                            everyArmor.add(new Leggins(name, rarity, baseDefense, stat, statVal, price));
                            break;
                        case "Boots":
                            everyArmor.add(new Boots(name, rarity, baseDefense, stat, statVal, price));
                            break;
                    }
                }
            }
            System.out.println("Caricate " + everyArmor.size() + " armature dal file XML!");
        } catch (Exception e) {
            System.out.println("Errore nel caricamento di armors.xml: " + e.getMessage());
            e.printStackTrace();
        }
        return everyArmor;
    }

    public static ArrayList<Items> loadArtefacts() {
        ArrayList<Items> everyArtefact = new ArrayList<>();

        try {
            File xmlFile = new File("data/artefacts.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("Artefatto");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String type = element.getAttribute("tipo");
                    String name = element.getAttribute("nome");
                    String rarity = element.getAttribute("rarita");
                    String set = element.getAttribute("set");

                    // ESTRAZIONE DEL PREZZO
                    int price = Integer.parseInt(element.getAttribute("prezzo"));

                    Element mainStatNode = (Element) element.getElementsByTagName("MainStat").item(0);
                    String mainStat = mainStatNode.getAttribute("stat");
                    int mainStatVal = Integer.parseInt(mainStatNode.getAttribute("valore"));

                    java.util.HashMap<String, Integer> subStatsMap = new java.util.HashMap<>();

                    NodeList subStatsList = element.getElementsByTagName("Stat");

                    for (int j = 0; j < subStatsList.getLength(); j++) {
                        Node subNode = subStatsList.item(j);
                        if (subNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element subElement = (Element) subNode;
                            String subStatName = subElement.getAttribute("stat");
                            int subStatVal = Integer.parseInt(subElement.getAttribute("valore"));

                            subStatsMap.put(subStatName, subStatVal);
                        }
                    }

                    switch (type) {
                        case "Ring":
                            everyArtefact.add(new items.artefacts.Ring(name, rarity, set, mainStat, mainStatVal, subStatsMap, price));
                            break;
                        case "Cloack":
                            everyArtefact.add(new items.artefacts.Cloack(name, rarity, set, mainStat, mainStatVal, subStatsMap, price));
                            break;
                        case "Earrings":
                            everyArtefact.add(new items.artefacts.Earrings(name, rarity, set, mainStat, mainStatVal, subStatsMap, price));
                            break;
                        case "Necklace":
                            everyArtefact.add(new items.artefacts.Necklace(name, rarity, set, mainStat, mainStatVal, subStatsMap, price));
                            break;
                    }
                }
            }
            System.out.println("Caricati " + everyArtefact.size() + " artefatti dal file XML!");

        } catch (Exception e) {
            System.out.println("Errore nel caricamento di artefacts.xml: " + e.getMessage());
            e.printStackTrace();
        }

        return everyArtefact;
    }

    public static ArrayList<Items> loadUsables() {
        ArrayList<Items> everyUsable = new ArrayList<>();

        try {
            File xmlFile = new File("data/usables.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("Usable");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String type = element.getAttribute("tipo");
                    String name = element.getAttribute("nome");
                    String rarity = element.getAttribute("rarita");
                    String stat = element.getAttribute("stat");
                    int statVal = Integer.parseInt(element.getAttribute("valStat"));

                    // ESTRAZIONE DEL PREZZO
                    int price = Integer.parseInt(element.getAttribute("prezzo"));

                    switch (type) {
                        case "HealthPotion":
                            everyUsable.add(new HealthPotion(name, rarity, stat, statVal, price));
                            break;
                        case "ManaPotion":
                            everyUsable.add(new ManaPotion(name, rarity, stat, statVal, price));
                            break;
                        case "Throwable":
                            everyUsable.add(new Throwables(name, rarity, stat, statVal, price));
                            break;
                    }
                }
            }
            System.out.println("Caricati " + everyUsable.size() + " consumabili dal file XML!");
        } catch (Exception e) {
            System.out.println("Errore nel caricamento di usables.xml: " + e.getMessage());
            e.printStackTrace();
        }
        return everyUsable;
    }

    public static Items rollRandomItem(){
        java.util.Random rand = new java.util.Random();

        int roll = rand.nextInt(100) + 1;
        String rarity;

        //TODO POSSIBILITA' DI NON OTTENERE NULLA


        if(roll<= 50) return null; //possibilità di non droppare nulla

        roll = rand.nextInt(100)+1; //resettiamo le chanche e rolliamo la rarità
        if (roll <= 50) rarity = "Comune";
        else if (roll <= 80) rarity = "Raro";
        else if (roll <= 95) rarity = "Epico";
        else rarity = "Leggendario";

        ArrayList<Items> fullItemPool = new ArrayList<>();
        for (Items item : everyItem) {
            if (item.getRarity().equalsIgnoreCase(rarity)){
                fullItemPool.add(item);
            }
        }

        return fullItemPool.get(rand.nextInt(fullItemPool.size()));
    }
}