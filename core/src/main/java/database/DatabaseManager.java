package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

import characters.Character;
import characters.player.*;
import game.XmlHandler;
import items.Items;
import java.util.ArrayList;
import java.util.Map;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:data/thepit.db";

    public static void initializeDatabase() {
        try {
            // Assicuriamoci che la cartella data esista
            File directory = new File("data");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            try (Connection conn = DriverManager.getConnection(DB_URL);
                 Statement stmt = conn.createStatement()) {
                
                String sqlRuns = "CREATE TABLE IF NOT EXISTS runs (" +
                             "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                             "character_class TEXT NOT NULL, " +
                             "level_reached INTEGER NOT NULL, " +
                             "money INTEGER NOT NULL, " +
                             "run_date DATETIME DEFAULT CURRENT_TIMESTAMP)";
                stmt.execute(sqlRuns);
                
                String sqlSave = "CREATE TABLE IF NOT EXISTS save_states (" +
                             "slot_id INTEGER PRIMARY KEY, " +
                             "player_name TEXT, " +
                             "class_name TEXT, " +
                             "level INTEGER, " +
                             "hp INTEGER, " +
                             "mana INTEGER, " +
                             "money INTEGER, " +
                             "exp INTEGER, " +
                             "inventory TEXT, " +
                             "equipped TEXT, " +
                             "save_date DATETIME DEFAULT CURRENT_TIMESTAMP)";
                stmt.execute(sqlSave);

                System.out.println("Database inizializzato con successo.");
            }
        } catch (SQLException e) {
            System.out.println("Errore nell'inizializzazione del database: " + e.getMessage());
        }
    }

    public static void saveRun(String characterClass, int levelReached, int money) {
        String sql = "INSERT INTO runs (character_class, level_reached, money, run_date) VALUES (?, ?, ?, DATETIME('now', 'localtime'))";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, characterClass);
            pstmt.setInt(2, levelReached);
            pstmt.setInt(3, money);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Errore durante il salvataggio della run: " + e.getMessage());
        }
    }

    public static String getTopRuns() {
        String sql = "SELECT character_class, level_reached, money, run_date FROM runs ORDER BY level_reached DESC, money DESC LIMIT 5";
        StringBuilder sb = new StringBuilder();
        sb.append("===== CLASSIFICA (MIGLIORI RUN) =====\n");
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int count = 1;
            while (rs.next()) {
                String charClass = rs.getString("character_class");
                int level = rs.getInt("level_reached");
                int money = rs.getInt("money");
                String date = rs.getString("run_date");
                
                sb.append(count).append(". [").append(date).append("] ")
                  .append(charClass).append(" - Livello: ").append(level)
                  .append(" - Monete: ").append(money).append("\n");
                count++;
            }
            if (count == 1) {
                sb.append("Nessuna run salvata finora. Inizia a giocare!\n");
            }
        } catch (SQLException e) {
            sb.append("Errore durante il recupero della classifica: ").append(e.getMessage()).append("\n");
        }
        return sb.toString();
    }

    public static void saveGameState(int slotId, Character player) {
        String sql = "INSERT OR REPLACE INTO save_states (slot_id, player_name, class_name, level, hp, mana, money, exp, inventory, equipped, save_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, DATETIME('now', 'localtime'))";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, slotId);
            pstmt.setString(2, player.getName());
            pstmt.setString(3, player.getClass().getSimpleName());
            pstmt.setInt(4, player.getLevel());
            pstmt.setInt(5, player.getHp());
            pstmt.setInt(6, player.getCurrentMana());
            pstmt.setInt(7, player.getMoney());
            pstmt.setInt(8, player.getExp());
            
            StringBuilder inv = new StringBuilder();
            for (Items item : player.getInventory()) {
                inv.append(item.getName()).append(",");
            }
            pstmt.setString(9, inv.toString());
            
            StringBuilder eq = new StringBuilder();
            for (Map.Entry<String, Items> entry : player.getEquippedItemsRaw().entrySet()) {
                if (entry.getValue() != null) {
                    eq.append(entry.getKey()).append(":").append(entry.getValue().getName()).append(",");
                }
            }
            pstmt.setString(10, eq.toString());
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Errore salvataggio game state: " + e.getMessage());
        }
    }

    public static String getSaveSlotsInfo() {
        String sql = "SELECT slot_id, class_name, level, save_date FROM save_states ORDER BY slot_id ASC";
        StringBuilder sb = new StringBuilder();
        boolean[] slots = new boolean[3];
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("slot_id");
                if (id >= 1 && id <= 3) {
                    slots[id - 1] = true;
                    // Format for UI parsing
                    sb.append(id).append(";").append(rs.getString("class_name"))
                      .append(";").append(rs.getInt("level"))
                      .append(";").append(rs.getString("save_date")).append("|");
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore getSaveSlotsInfo: " + e.getMessage());
        }
        for (int i = 0; i < 3; i++) {
            if (!slots[i]) {
                sb.append((i + 1)).append(";Vuoto;0;N/A|");
            }
        }
        return sb.toString();
    }

    public static Character loadGameState(int slotId) {
        String sql = "SELECT * FROM save_states WHERE slot_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, slotId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String className = rs.getString("class_name");
                Character player = null;
                switch (className) {
                    case "Warrior": player = new Warrior(); break;
                    case "Archer": player = new Archer(); break;
                    case "Assassin": player = new Assassin(); break;
                    case "Mage": player = new Mage(); break;
                    case "Tank": player = new Tank(); break;
                    default: return null;
                }
                
                player.setMoney(rs.getInt("money"));
                
                int levelToReach = rs.getInt("level");
                for (int i = 1; i < levelToReach; i++) {
                    player.forceLevelUp();
                }
                player.setExp(rs.getInt("exp"));
                player.forceHpAndMana(rs.getInt("hp"), rs.getInt("mana"));
                
                String inv = rs.getString("inventory");
                if (inv != null && !inv.isEmpty()) {
                    for (String itemName : inv.split(",")) {
                        if (!itemName.isEmpty()) {
                            Items item = getItemByName(itemName);
                            if (item != null) player.addToInventory(item);
                        }
                    }
                }
                
                String eq = rs.getString("equipped");
                if (eq != null && !eq.isEmpty()) {
                    for (String eqData : eq.split(",")) {
                        if (!eqData.isEmpty() && eqData.contains(":")) {
                            String[] parts = eqData.split(":");
                            Items item = getItemByName(parts[1]);
                            if (item != null) {
                                // Add to inventory if not already there, wait no, equip just takes it from inventory usually, or maybe we can just equip it.
                                // It's better to add to inventory and then equip, but wait, `equip` removes it from inventory if it's there. 
                                // Actually, let's just add it to inventory and equip.
                                player.addToInventory(item);
                                player.equip(item);
                            }
                        }
                    }
                }
                return player;
            }
        } catch (SQLException e) {
            System.out.println("Errore caricamento: " + e.getMessage());
        }
        return null;
    }

    private static Items getItemByName(String name) {
        if (XmlHandler.everyItem == null || XmlHandler.everyItem.isEmpty()) XmlHandler.loadAllItems();
        for (Items i : XmlHandler.everyItem) {
            if (i.getName().equals(name)) return i.clone(); // Restituire un clone per evitare reference condivise
        }
        return null;
    }

    public static void deleteSaveState(int slotId) {
        String sql = "DELETE FROM save_states WHERE slot_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, slotId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Errore eliminazione salvataggio: " + e.getMessage());
        }
    }
}
