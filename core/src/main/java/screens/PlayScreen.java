package screens;

import client.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import network.NetworkManager;

public class PlayScreen extends ScreenAdapter {

    private Main game;
    private Stage stage;
    private Skin skin;
    private NetworkManager networkManager;

    private Table rootTable;
    private Table buttonTable;
    private Table statsContainer;

    private Texture[] classTextures;
    private Texture dungeonBg, battleBg, iconSword, iconShield, iconBackpack, iconWand;
    private Texture iconHelmet, iconArmor, iconPants, iconBoots, iconGreatsword, iconRing;
    private Texture iconCloak, iconEarrings, iconNecklace, iconHealthPotion, iconManaPotion, iconThrowingKnife;
    private Texture iconBow, iconDagger;
    private Label globalStatsLabel;
    private int selectedClassIndex = -1;
    private String selectedClassName = "";
    private TextButton confirmClassButton;
    private String[] classStats = {
            "--- Guerriero ---\nHpMax: 120\nForza: 90\nAgilità: 85\nIntelligenza: 30\nPrecisione: 50",
            "--- Arciere ---\nHpMax: 75\nForza: 30\nAgilità: 90\nIntelligenza: 30\nPrecisione: 105",
            "--- Assassino ---\nHpMax: 100\nForza: 50\nAgilità: 81\nIntelligenza: 62\nPrecisione: 62",
            "--- Mago ---\nHpMax: 75\nForza: 30\nAgilità: 30\nIntelligenza: 115\nPrecisione: 80",
            "--- Tank ---\nHpMax: 150\nForza: 105\nAgilità: 40\nIntelligenza: 70\nPrecisione: 40"
    };

    private String lastMessage = "Benvenuto in THE PIT!";
    private String currentState = "MENU";

    private static class EnemyInfo {
        String name;
        int hp;
        int maxHp;
    }

    private static class ShopItemInfo {
        String name;
        int price;
        String details;
        String type;
        String slot;
    }

    private static class ItemInfo {
        String name;
        String type;
        String details;
        String slot;
        int sellValue;

        ItemInfo(String name, String type, String details, String slot) {
            this(name, type, details, slot, 0);
        }

        ItemInfo(String name, String type, String details, String slot, int sellValue) {
            this.name = name;
            this.type = type;
            this.details = details;
            this.slot = slot;
            this.sellValue = sellValue;
        }
    }

    private int playerLevel = 1;
    private int playerHp = 100;
    private int playerMaxHp = 100;
    private int playerMoney = 0;
    private java.util.ArrayList<EnemyInfo> currentEnemies = new java.util.ArrayList<>();
    private java.util.ArrayList<ItemInfo> inventoryItems = new java.util.ArrayList<>();
    private java.util.ArrayList<ShopItemInfo> shopItems = new java.util.ArrayList<>();
    private java.util.Map<String, ItemInfo> equippedMap = new java.util.HashMap<>();
    private Texture zombieTex, orcTex, skeletonTex, goblinTex, witchTex;
    private boolean isSelectingTarget = false;
    private String selectedAttackType = "";
    private int selectedIndexInInventory = -1;
    private java.util.ArrayList<String> turnOrder = new java.util.ArrayList<>();
    private int playerStr, playerAgi, playerInt, playerPre, playerDef, playerMana, playerManaMax, playerCrit,
            playerDodge, playerPa;
    private ItemInfo groundItem;
    private java.util.List<String> messageLog = new java.util.ArrayList<>();
    private final java.util.Queue<String> messageQueue = new java.util.LinkedList<>();
    private float messageTimer = 0f;
    private String lastFeedbackMessage = ""; // Messaggio di feedback visibile nel pannello dettagli

    private Label messageLabel;
    private Texture uiBoxTex, buttonBgTex;

    public PlayScreen(Main game, NetworkManager manager) {
        this.game = game;
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);

        createBasicSkin();

        uiBoxTex = new Texture(Gdx.files.internal("ui_box.png"));
        buttonBgTex = new Texture(Gdx.files.internal("button_bg.png"));

        TextButton.TextButtonStyle confirmStyle = new TextButton.TextButtonStyle();
        com.badlogic.gdx.scenes.scene2d.utils.Drawable confirmBg = new Image(buttonBgTex).getDrawable();
        confirmStyle.up = confirmBg;
        confirmStyle.over = skin.newDrawable(confirmBg, new Color(0.8f, 0.8f, 0.8f, 1f));
        confirmStyle.down = skin.newDrawable(confirmBg, new Color(0.5f, 0.5f, 0.5f, 1f));
        confirmStyle.font = skin.getFont("default");
        confirmStyle.fontColor = Color.WHITE;

        confirmClassButton = new TextButton("CONFERMA SCELTA", confirmStyle);
        confirmClassButton.setVisible(false);
        confirmClassButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedClassIndex != -1) {
                    sendCommand(String.valueOf(selectedClassIndex));
                }
            }
        });

        // Main layout table
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.pad(20);
        stage.addActor(rootTable);

        // Load textures for icons and bg
        dungeonBg = new Texture(Gdx.files.internal("dungeon_bg.png"));
        battleBg = new Texture(Gdx.files.internal("battle_bg.png"));
        iconSword = new Texture(Gdx.files.internal("icons/sword.png"));
        iconShield = new Texture(Gdx.files.internal("icons/shield.png"));
        iconBackpack = new Texture(Gdx.files.internal("icons/backpack.png"));
        iconWand = new Texture(Gdx.files.internal("icons/wand.png"));
        iconHelmet = new Texture(Gdx.files.internal("icons/helmet.png"));
        iconArmor = new Texture(Gdx.files.internal("icons/armor.png"));
        iconPants = new Texture(Gdx.files.internal("icons/pants.png"));
        iconBoots = new Texture(Gdx.files.internal("icons/boots.png"));
        iconGreatsword = new Texture(Gdx.files.internal("icons/greatsword.png"));
        iconRing = new Texture(Gdx.files.internal("icons/ring.png"));
        iconCloak = new Texture(Gdx.files.internal("icons/cloak.png"));
        iconEarrings = new Texture(Gdx.files.internal("icons/earrings.png"));
        iconNecklace = new Texture(Gdx.files.internal("icons/necklace.png"));
        iconHealthPotion = new Texture(Gdx.files.internal("icons/health_potion.png"));
        iconManaPotion = new Texture(Gdx.files.internal("icons/mana_potion.png"));
        iconThrowingKnife = new Texture(Gdx.files.internal("icons/throwing_knife.png"));
        iconBow = new Texture(Gdx.files.internal("icons/bow.png"));
        iconDagger = new Texture(Gdx.files.internal("icons/dagger.png"));
        classTextures = new Texture[] {
                new Texture(Gdx.files.internal("classes/warrior.png")),
                new Texture(Gdx.files.internal("classes/archer.png")),
                new Texture(Gdx.files.internal("classes/assassin.png")),
                new Texture(Gdx.files.internal("classes/mage.png")),
                new Texture(Gdx.files.internal("classes/tank.png"))
        };
        zombieTex = new Texture(Gdx.files.internal("enemies/zombie.png"));
        orcTex = new Texture(Gdx.files.internal("enemies/orc.png"));
        skeletonTex = new Texture(Gdx.files.internal("enemies/skeleton.png"));
        goblinTex = new Texture(Gdx.files.internal("enemies/goblin.png"));
        witchTex = new Texture(Gdx.files.internal("enemies/witch.png"));


        // Global stats label
        globalStatsLabel = new Label("", skin);
        globalStatsLabel.setAlignment(Align.center);
        globalStatsLabel.getColor().a = 1f;

        // Container for stats with background
        Table statsTable = new Table();
        statsTable.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0.6f)));
        statsTable.pad(20);
        statsTable.add(globalStatsLabel).minWidth(400);
        statsTable.getColor().a = 0f;
        this.statsContainer = statsTable;

        // Log label (now for the dialogue box)
        messageLabel = new Label(lastMessage, skin, "dialog");
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.topLeft);

        // Textures loaded earlier

        // Button table
        buttonTable = new Table();
        buttonTable.defaults().pad(10).minWidth(200).minHeight(60);

        this.networkManager = manager;
        this.networkManager.setListener(new NetworkManager.NetworkListener() {
            @Override
            public void onMessageReceived(String message) {
                Gdx.app.postRunnable(() -> handleServerMessage(message));
            }

            @Override
            public void onConnectionError(String error) {
                Gdx.app.postRunnable(() -> {
                    lastMessage = "ERRORE DI CONNESSIONE: " + error;
                    updateLog();
                });
            }
        });

        updateButtons();
    }

    private void createBasicSkin() {
        skin = new Skin();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.valueOf("333333"));
        pixmap.fill();
        skin.add("button_up", new Texture(pixmap));

        pixmap.setColor(Color.valueOf("555555"));
        pixmap.fill();
        skin.add("button_over", new Texture(pixmap));

        pixmap.setColor(Color.valueOf("111111"));
        pixmap.fill();
        skin.add("button_down", new Texture(pixmap));

        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));

        pixmap.dispose();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        parameter.size = 28;
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.minFilter = Texture.TextureFilter.Linear;
        BitmapFont font = generator.generateFont(parameter);
        skin.add("default", font);

        parameter.size = 56;
        BitmapFont titleFont = generator.generateFont(parameter);
        skin.add("title", titleFont);

        generator.dispose();

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = titleFont;
        titleStyle.fontColor = Color.GOLD;
        skin.add("title", titleStyle);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("button_up");
        textButtonStyle.over = skin.newDrawable("button_over");
        textButtonStyle.down = skin.newDrawable("button_down");
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        Label.LabelStyle dialogStyle = new Label.LabelStyle();
        dialogStyle.font = skin.getFont("default");
        dialogStyle.fontColor = Color.WHITE;
        skin.add("dialog", dialogStyle);

        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        skin.add("default", scrollPaneStyle);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = skin.getFont("default");
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.background = skin.newDrawable("button_down");
        textFieldStyle.cursor = skin.newDrawable("button_over");
        skin.add("default", textFieldStyle);
    }

    public void handleServerMessage(String message) {
        boolean wasInBattle = currentState.equals("BATTLE");

        // Reset selezione solo dopo un'azione che modifica lo zaino
        if (message.contains("Hai equipaggiato") || message.contains("Hai usato") ||
                message.contains("Hai buttato") || message.contains("Hai gettato") ||
                message.contains("annullata") || message.contains("Zaino chiuso")) {
            selectedIndexInInventory = -1;
        }

        // Parse states like {END:IDLE}
        if (message.contains("{END:")) {
            int startIndex = message.indexOf("{END:");
            int endIndex = message.indexOf("}", startIndex);
            if (endIndex != -1) {
                String newState = message.substring(startIndex + 5, endIndex);
                // Puliamo il log quando si cambia stato, ma NON tra stati di inventario correlati
                boolean oldIsInventory = currentState.startsWith("INVENTORY_");
                boolean newIsInventory = newState.startsWith("INVENTORY_");
                if (!newState.equals(currentState) && !(oldIsInventory && newIsInventory)) {
                    messageLog.clear();
                    lastMessage = "";
                    lastFeedbackMessage = "";
                    if (messageLabel != null) messageLabel.setText("");
                } else if (!newState.equals(currentState) && oldIsInventory && newIsInventory) {
                    // Tra stati inventario, teniamo i messaggi ma resettiamo il feedback se cambiamo
                    // lasciamo lastFeedbackMessage intatto per mostrare l'errore
                }

                currentState = newState;
                message = message.substring(0, startIndex) + message.substring(endIndex + 1);
            }
        }

        boolean isBattleState = wasInBattle || currentState.equals("BATTLE");

        // Parse {CLASS_INFO:...}
        if (message.contains("{CLASS_INFO:")) {
            int startIndex = message.indexOf("{CLASS_INFO:");
            int endIndex = message.indexOf("}", startIndex);
            if (endIndex != -1) {
                String cInfo = message.substring(startIndex + 12, endIndex);
                selectedClassName = cInfo;
                switch (cInfo.toLowerCase()) {
                    case "warrior": case "guerriero": selectedClassIndex = 1; break;
                    case "archer": case "arciere": selectedClassIndex = 2; break;
                    case "assassin": case "assassino": selectedClassIndex = 3; break;
                    case "mage": case "mago": selectedClassIndex = 4; break;
                    case "tank": selectedClassIndex = 5; break;
                    default: selectedClassIndex = 1; break;
                }
                message = message.substring(0, startIndex) + message.substring(endIndex + 1);
            }
        }

        // Parse {BATTLE_INFO:...}
        if (message.contains("{BATTLE_INFO:")) {
            int startIndex = message.indexOf("{BATTLE_INFO:");
            int endIndex = message.indexOf("}", startIndex);
            if (endIndex != -1) {
                String battleData = message.substring(startIndex + 13, endIndex);
                parseBattleInfo(battleData);
                message = message.substring(0, startIndex) + message.substring(endIndex + 1);
            }
        }

        // Parse {MSG:...}
        if (message.contains("{MSG:")) {
            int lastIndex = 0;
            while ((lastIndex = message.indexOf("{MSG:", lastIndex)) != -1) {
                int endIndex = message.indexOf("}", lastIndex);
                if (endIndex != -1) {
                    String msg = message.substring(lastIndex + 5, endIndex);
                    msg = msg.replace("\\n", "\n"); // Unescape newlines!
                    if (!msg.trim().isEmpty()) {
                        String[] lines = msg.split("\n");
                        for (String line : lines) {
                            String trimmed = line.trim();
                            if (!trimmed.isEmpty()) {
                                // Intercetta messaggi di errore per mostrarli nel pannello dettagli
                                if (trimmed.startsWith("Non puoi") || trimmed.startsWith("Non hai")) {
                                    lastFeedbackMessage = trimmed;
                                }
                                if (isBattleState) {
                                    messageQueue.add(trimmed);
                                } else {
                                    messageLog.add(trimmed);
                                    if (messageLog.size() > 5)
                                        messageLog.remove(0);
                                }
                            }
                        }
                    }
                    lastIndex = endIndex + 1;
                } else
                    break;
            }
            if (!isBattleState) {
                while (!messageQueue.isEmpty()) {
                    String qMsg = messageQueue.poll();
                    messageLog.add(qMsg);
                    if (messageLog.size() > 5)
                        messageLog.remove(0);
                }
                updateLog();
            } else {
                if (messageTimer <= 0 && !messageQueue.isEmpty()) {
                    String firstMsg = messageQueue.poll();
                    messageLog.add(firstMsg);
                    if (messageLog.size() > 5)
                        messageLog.remove(0);
                    updateLog();
                    messageTimer = 0.8f;
                }
            }
            // Puliamo i tag MSG dal messaggio per i passi successivi
            message = message.replaceAll("\\{MSG:[^}]*\\}", "");
        }

        if (!isBattleState && !messageQueue.isEmpty()) {
            while (!messageQueue.isEmpty()) {
                String qMsg = messageQueue.poll();
                messageLog.add(qMsg);
                if (messageLog.size() > 5)
                    messageLog.remove(0);
            }
            updateLog();
        }

        // Parse {OVERFLOW_INFO:...}
        if (message.contains("{OVERFLOW_INFO:")) {
            int startIndex = message.indexOf("{OVERFLOW_INFO:");
            int endIndex = message.indexOf("}", startIndex);
            if (endIndex != -1) {
                String data = message.substring(startIndex + 15, endIndex);
                String[] parts = data.split(",");
                if (parts.length >= 4) {
                    groundItem = new ItemInfo(parts[0], parts[1], parts[2].replace("\\n", "\n"), parts[3]);
                }
                message = message.substring(0, startIndex) + message.substring(endIndex + 1);
            }
        }
        // Parse {SHOP_INFO:...}
        if (message.contains("{SHOP_INFO:")) {
            int startIndex = message.indexOf("{SHOP_INFO:");
            int endIndex = message.indexOf("}", startIndex);
            if (endIndex != -1) {
                String shopData = message.substring(startIndex + 11, endIndex);
                message = message.substring(0, startIndex) + message.substring(endIndex + 1);

                shopItems.clear();
                if (!shopData.isEmpty()) {
                    String[] items = shopData.split(";");
                    for (String itemStr : items) {
                        String[] itemParts = itemStr.split(",");
                        if (itemParts.length >= 3) {
                            ShopItemInfo info = new ShopItemInfo();
                            info.name = itemParts[0];
                            info.price = Integer.parseInt(itemParts[1]);
                            info.details = itemParts[2];
                            info.type = (itemParts.length >= 5) ? itemParts[3] : "USABLE";
                            info.slot = (itemParts.length >= 5) ? itemParts[4] : "NONE";
                            shopItems.add(info);
                        }
                    }
                }
            }
        }

        // Parse {TURN_ORDER:...}
        if (message.contains("{TURN_ORDER:")) {
            int startIndex = message.indexOf("{TURN_ORDER:");
            int endIndex = message.indexOf("}", startIndex);
            if (endIndex != -1) {
                String turnData = message.substring(startIndex + 12, endIndex);
                message = message.substring(0, startIndex) + message.substring(endIndex + 1);

                turnOrder.clear();
                if (!turnData.isEmpty()) {
                    String[] turns = turnData.split(",");
                    for (String t : turns)
                        turnOrder.add(t);
                }
            }
        }

        // Parse {INVENTORY_INFO:...}
        if (message.contains("{INVENTORY_INFO:")) {
            int startIndex = message.indexOf("{INVENTORY_INFO:");
            int endIndex = message.indexOf("}", startIndex);
            if (endIndex != -1) {
                String invData = message.substring(startIndex + 16, endIndex);
                message = message.substring(0, startIndex) + message.substring(endIndex + 1);

                inventoryItems.clear();
                if (!invData.isEmpty()) {
                    String[] items = invData.split(";");
                    for (String itemStr : items) {
                        String[] itemParts = itemStr.split(",");
                        if (itemParts.length >= 4) {
                            int sellVal = 0;
                            if (itemParts.length >= 5) {
                                sellVal = Integer.parseInt(itemParts[4]);
                            }
                            inventoryItems.add(new ItemInfo(itemParts[0], itemParts[1], itemParts[2].replace("\\n", "\n"), itemParts[3], sellVal));
                        }
                    }
                }
            }
        }

        // Parse {EQUIPPED_INFO:...}
        if (message.contains("{EQUIPPED_INFO:")) {
            int startIndex = message.indexOf("{EQUIPPED_INFO:");
            int endIndex = message.indexOf("}", startIndex);
            if (endIndex != -1) {
                String equipData = message.substring(startIndex + 15, endIndex);
                message = message.substring(0, startIndex) + message.substring(endIndex + 1);

                equippedMap.clear();
                if (!equipData.isEmpty()) {
                    String[] items = equipData.split(";");
                    for (String itemStr : items) {
                        String[] itemParts = itemStr.split(",");
                        if (itemParts.length >= 3) {
                            equippedMap.put(itemParts[0],
                                    new ItemInfo(itemParts[1], "EQUIP", itemParts[2].replace("\\n", "\n"), itemParts[0]));
                        }
                    }
                }
            }
        }

        isSelectingTarget = false;

        if (currentState.equals("CONFIRM_SELECTION") && selectedClassIndex != -1) {
            sendCommand("Conferma");
        } else {
            if (!currentState.equals("MENU") && !currentState.equals("CONFIRM_SELECTION")) {
                confirmClassButton.setVisible(false);
            }
            updateButtons();
        }
    }

    private void updateLog() {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        int max = 3;
        int startIndex = Math.max(0, messageLog.size() - max);
        for (int i = startIndex; i < messageLog.size(); i++) {
            sb.append(messageLog.get(i));
            if (i < messageLog.size() - 1)
                sb.append("\n");
        }
        messageLabel.setText(sb.toString());
    }

    private void sendCommand(String command) {
        lastMessage = "> " + command;
        lastFeedbackMessage = ""; // Puliamo il feedback al prossimo comando
        updateLog();
        networkManager.sendCommand(command);
    }

    private void updateLayout() {
        rootTable.clearChildren();
        if (currentState.equals("MENU")) {
            Label titleLabel = new Label("SELEZIONA UNA CLASSE", skin, "title");
            titleLabel.setAlignment(Align.center);

            rootTable.add(titleLabel).padBottom(60).row();
            rootTable.add(buttonTable).center().row();
            rootTable.add(statsContainer).padTop(40).center().row();
            rootTable.add(confirmClassButton).padTop(60).width(350).height(90).center();
        } else if (currentState.equals("IDLE") || currentState.equals("BATTLE") || currentState.startsWith("INVENTORY_")
                || currentState.equals("PLAYER_INFO") || currentState.equals("ROOM") || currentState.equals("SHOP")) {
            // Pokémon Style RPG Layout

            // Top: Game View
            Table topView = new Table();
            if (currentState.equals("BATTLE") || currentState.equals("INVENTORY_BATTLE")) {
                topView.setBackground(new Image(battleBg).getDrawable());
            } else {
                topView.setBackground(new Image(dungeonBg).getDrawable());
            }

            if (currentState.equals("BATTLE")) {
                // POKEMON BATTLE LAYOUT
                Table battleArea = new Table();

                // --- ENEMIES AREA (Top Right) ---
                Table enemiesArea = new Table();
                for (EnemyInfo enemy : currentEnemies) {
                    Table enemyBox = new Table();
                    Image enemySprite = new Image(getEnemyTexture(enemy.name));

                    Table enemyStatsOverlay = new Table();
                    enemyStatsOverlay.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0.7f)));
                    enemyStatsOverlay.pad(10);
                    enemyStatsOverlay.add(new Label(enemy.name, skin)).left().padBottom(5).row();

                    Image eHpBg = new Image(skin.newDrawable("white", Color.DARK_GRAY));
                    Image eHpFill = new Image(skin.newDrawable("white", Color.RED));
                    Stack eHpStack = new Stack(eHpBg);
                    Table eFillTable = new Table();
                    eFillTable.left();
                    float eHpPercent = enemy.maxHp > 0 ? (float) enemy.hp / enemy.maxHp : 0;
                    eFillTable.add(eHpFill).width(150 * eHpPercent).height(18).left();
                    eHpStack.add(eFillTable);

                    Label eHpLabel = new Label(enemy.hp + "/" + enemy.maxHp, skin);
                    eHpLabel.setAlignment(Align.center);
                    eHpLabel.setFontScale(0.6f);
                    eHpStack.add(eHpLabel);

                    enemyStatsOverlay.add(eHpStack).width(150).height(18).left();

                    enemyBox.add(enemySprite).size(150, 150).center().row();
                    enemyBox.add(enemyStatsOverlay).padTop(10);
                    enemiesArea.add(enemyBox).pad(20);
                }

                // --- PLAYER AREA (Bottom Left) ---
                Table playerArea = new Table();
                if (selectedClassIndex != -1) {
                    Image playerSprite = new Image(classTextures[selectedClassIndex - 1]);
                    playerArea.add(playerSprite).size(200, 200).bottom().left();
                }

                Table playerStatsOverlay = new Table();
                playerStatsOverlay.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0.7f)));
                playerStatsOverlay.pad(15);
                playerStatsOverlay.add(new Label(selectedClassName.toUpperCase() + " Lv." + playerLevel, skin)).left()
                        .padBottom(5).row();
                Image hpBarBg = new Image(skin.newDrawable("white", Color.DARK_GRAY));
                Image hpBarFill = new Image(skin.newDrawable("white", Color.GREEN));
                Stack hpStack = new Stack(hpBarBg);
                Table fillTable = new Table();
                fillTable.left();
                float hpPercent = playerMaxHp > 0 ? (float) playerHp / playerMaxHp : 0;
                fillTable.add(hpBarFill).width(200 * hpPercent).height(20).left();
                hpStack.add(fillTable);

                Label hpTextLabel = new Label(playerHp + " / " + playerMaxHp, skin);
                hpTextLabel.setAlignment(Align.center);
                hpTextLabel.setFontScale(0.8f);
                hpStack.add(hpTextLabel);

                playerStatsOverlay.add(hpStack).width(200).height(20).left().padTop(5).row();

                // MANA BAR
                Image mpBarBg = new Image(skin.newDrawable("white", Color.DARK_GRAY));
                Image mpBarFill = new Image(skin.newDrawable("white", Color.CYAN));
                Stack mpStack = new Stack(mpBarBg);
                Table mpFillTable = new Table();
                mpFillTable.left();
                float mpPercent = playerManaMax > 0 ? (float) playerMana / playerManaMax : 0;
                mpFillTable.add(mpBarFill).width(200 * mpPercent).height(12).left();
                mpStack.add(mpFillTable);

                Label mpTextLabel = new Label("MP: " + playerMana + " / " + playerManaMax, skin);
                mpTextLabel.setAlignment(Align.center);
                mpTextLabel.setFontScale(0.7f);
                mpStack.add(mpTextLabel);

                playerStatsOverlay.add(mpStack).width(200).height(12).left().padTop(5).row();

                Label paLabel = new Label("PA (Azioni): " + playerPa, skin);
                paLabel.setColor(Color.YELLOW);
                paLabel.setFontScale(0.8f);
                playerStatsOverlay.add(paLabel).left().padTop(5);

                playerArea.add(playerStatsOverlay).padLeft(20).bottom().padBottom(30);

                battleArea.add(enemiesArea).expand().right().bottom().padRight(350).padBottom(-300).row();
                battleArea.add(playerArea).expand().left().bottom().pad(40);

                topView.add(battleArea).expand().fill().row();

                // --- TURN ORDER BAR (Top Overlay) ---
                Table turnBar = new Table();
                turnBar.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0.6f)));
                turnBar.pad(10);
                for (int i = 0; i < Math.min(turnOrder.size(), 8); i++) {
                    String name = turnOrder.get(i);
                    Table turnIcon = new Table();

                    Texture iconTex = getEnemyTexture(name);
                    if (name.equalsIgnoreCase("Warrior") || name.equalsIgnoreCase("Guerriero"))
                        iconTex = classTextures[0];
                    else if (name.equalsIgnoreCase("Archer") || name.equalsIgnoreCase("Arciere"))
                        iconTex = classTextures[1];
                    else if (name.equalsIgnoreCase("Assassin") || name.equalsIgnoreCase("Assassino"))
                        iconTex = classTextures[2];
                    else if (name.equalsIgnoreCase("Mage") || name.equalsIgnoreCase("Mago"))
                        iconTex = classTextures[3];
                    else if (name.equalsIgnoreCase("Tank"))
                        iconTex = classTextures[4];
                    else if (selectedClassName != null && name.equalsIgnoreCase(selectedClassName)) {
                        iconTex = classTextures[Math.max(0, selectedClassIndex - 1)];
                    }

                    Image img = new Image(iconTex);
                    if (i == 0) {
                        turnIcon.setBackground(skin.newDrawable("white", new Color(1, 0.8f, 0, 0.6f)));
                        turnIcon.add(img).size(70, 70).pad(5);
                    } else {
                        turnIcon.setBackground(skin.newDrawable("white", new Color(0.5f, 0.5f, 0.5f, 0.4f)));
                        turnIcon.add(img).size(50, 50).pad(5);
                    }

                    turnBar.add(turnIcon).padLeft(10);
                    if (i < Math.min(turnOrder.size(), 8) - 1) {
                        turnBar.add(new Label(">", skin)).padLeft(10);
                    }
                }

                Stack topStack = new Stack();
                topStack.add(topView);
                Table overlay = new Table();
                overlay.top();
                overlay.add(turnBar).padTop(20);
                topStack.add(overlay);

                topView = new Table();
                topView.add(topStack).expand().fill();
            } else if (currentState.equals("SHOP")) {
                // Custom Graphical Shop Panel
                Table shopTable = new Table();
                shopTable.setFillParent(true);
                shopTable.pad(30);

                Table panel = new Table();
                panel.setBackground(skin.newDrawable("white", new Color(0.02f, 0.02f, 0.02f, 0.85f)));
                panel.pad(20);

                // Header Table
                Table header = new Table();
                Label titleLabel = new Label("MERCANTE", skin, "title");
                titleLabel.setFontScale(0.7f);
                titleLabel.setColor(Color.GOLD);
                Label moneyLabel = new Label("ORO: " + playerMoney + " Monete", skin);
                moneyLabel.setFontScale(0.9f);
                moneyLabel.setColor(Color.GOLD);

                header.add(titleLabel).expandX().left();
                header.add(moneyLabel).right();
                panel.add(header).expandX().fillX().padBottom(20).row();

                // Body: stock (Buy) on left, player inventory (Sell) on right
                Table body = new Table();
                body.defaults().top();

                // Left Column: MERCE IN VENDITA
                Table buyCol = new Table();
                buyCol.defaults().left();
                Label buyTitle = new Label("MERCE IN VENDITA", skin);
                buyTitle.setColor(Color.CYAN);
                buyCol.add(buyTitle).padBottom(15).row();

                Table buyList = new Table();
                buyList.defaults().left().pad(5);

                if (shopItems.isEmpty()) {
                    Label soldOut = new Label("Merce esaurita!", skin);
                    soldOut.getColor().a = 0.5f;
                    buyList.add(soldOut).colspan(3).pad(20);
                } else {
                    for (int i = 0; i < shopItems.size(); i++) {
                        final int itemIndex = i + 1;
                        final ShopItemInfo item = shopItems.get(i);
                        
                        ItemInfo dummyItem = new ItemInfo(item.name, item.type, item.details, item.slot);
                        Texture icon = getItemIcon(dummyItem);
                        Image itemImg = new Image(icon);
                        buyList.add(itemImg).size(50, 50).padRight(10).padBottom(20);

                        Table infoTable = new Table();
                        Label nameL = new Label(item.name.toUpperCase(), skin);
                        nameL.setFontScale(0.75f);
                        nameL.setWrap(true);
                        
                        Label descL = new Label(item.details, skin);
                        descL.setFontScale(0.55f);
                        descL.setColor(Color.LIGHT_GRAY);
                        descL.setWrap(true);
                        
                        infoTable.add(nameL).width(230).left().row();
                        infoTable.add(descL).width(230).left();
                        buyList.add(infoTable).width(230).left().padRight(10).padBottom(20);

                        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
                        com.badlogic.gdx.scenes.scene2d.utils.Drawable bgDrawable = new Image(buttonBgTex).getDrawable();
                        btnStyle.up = bgDrawable;
                        btnStyle.over = skin.newDrawable(bgDrawable, new Color(0.8f, 0.8f, 0.8f, 1f));
                        btnStyle.down = skin.newDrawable(bgDrawable, new Color(0.5f, 0.5f, 0.5f, 1f));
                        btnStyle.font = skin.getFont("default");
                        TextButton buyBtn = new TextButton("COMPRA\n" + item.price + " Oro", btnStyle);
                        buyBtn.getLabel().setFontScale(0.6f);
                        buyBtn.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                sendCommand("COMPRA " + itemIndex);
                            }
                        });
                        buyList.add(buyBtn).width(120).height(50).padBottom(20).row();
                    }
                }

                ScrollPane buyScroll = new ScrollPane(buyList, skin);
                buyScroll.setFadeScrollBars(false);
                buyScroll.setScrollingDisabled(true, false);
                buyCol.add(buyScroll).width(420).height(500);

                // Right Column: COSA VENDERE
                Table sellCol = new Table();
                sellCol.defaults().left();
                Label sellTitle = new Label("IL TUO ZAINO", skin);
                sellTitle.setColor(Color.GREEN);
                sellCol.add(sellTitle).padBottom(15).row();

                Table sellList = new Table();
                sellList.defaults().left().pad(5);

                if (inventoryItems.isEmpty()) {
                    Label emptyInv = new Label("Zaino vuoto!", skin);
                    emptyInv.getColor().a = 0.5f;
                    sellList.add(emptyInv).colspan(3).pad(20);
                } else {
                    for (int i = 0; i < inventoryItems.size(); i++) {
                        final int itemIndex = i + 1;
                        final ItemInfo item = inventoryItems.get(i);
                        
                        Texture icon = getItemIcon(item);
                        Image itemImg = new Image(icon);
                        sellList.add(itemImg).size(50, 50).padRight(10).padBottom(20);

                        Table infoTable = new Table();
                        Label nameL = new Label(item.name.toUpperCase(), skin);
                        nameL.setFontScale(0.75f);
                        nameL.setWrap(true);
                        
                        Label descL = new Label(item.details, skin);
                        descL.setFontScale(0.55f);
                        descL.setColor(Color.LIGHT_GRAY);
                        descL.setWrap(true);
                        
                        infoTable.add(nameL).width(230).left().row();
                        infoTable.add(descL).width(230).left();
                        sellList.add(infoTable).width(230).left().padRight(10).padBottom(20);

                        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
                        com.badlogic.gdx.scenes.scene2d.utils.Drawable bgDrawable = new Image(buttonBgTex).getDrawable();
                        btnStyle.up = bgDrawable;
                        btnStyle.over = skin.newDrawable(bgDrawable, new Color(0.8f, 0.8f, 0.8f, 1f));
                        btnStyle.down = skin.newDrawable(bgDrawable, new Color(0.5f, 0.5f, 0.5f, 1f));
                        btnStyle.font = skin.getFont("default");
                        TextButton sellBtn = new TextButton("VENDI\n" + item.sellValue + " Oro", btnStyle);
                        sellBtn.getLabel().setFontScale(0.6f);
                        sellBtn.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                sendCommand("VENDI " + itemIndex);
                            }
                        });
                        sellList.add(sellBtn).width(120).height(50).padBottom(20).row();
                    }
                }

                ScrollPane sellScroll = new ScrollPane(sellList, skin);
                sellScroll.setFadeScrollBars(false);
                sellScroll.setScrollingDisabled(true, false);
                sellCol.add(sellScroll).width(420).height(500);

                body.add(buyCol).width(420).padRight(50);
                body.add(sellCol).width(420);

                panel.add(body).expand().fill().padTop(10);
                shopTable.add(panel).expand().fill();
                
                Stack topStack = new Stack();
                topStack.add(topView);
                topStack.add(shopTable);
                
                topView = new Table();
                topView.add(topStack).expand().fill();
            } else if (currentState.startsWith("INVENTORY_")) {
                // FULL-SCREEN INVENTORY GUI WITH STATS (Works for Main and Battle)
                Table content = new Table();
                content.pad(50);

                String titleText;
                if (currentState.equals("INVENTORY_OVERFLOW")) {
                    titleText = "ZAINO PIENO - SCEGLI COSA SOSTITUIRE";
                } else {
                    titleText = currentState.equals("INVENTORY_BATTLE") ? "ZAINO CONSUMABILI" : "ZAINO";
                }

                Label invTitle = new Label(titleText, skin, "title");
                invTitle.setColor(currentState.equals("INVENTORY_OVERFLOW") ? Color.RED : Color.GOLD);
                content.add(invTitle).colspan(2).padBottom(30).row();

                // Left: Grid
                Table grid = new Table();
                grid.defaults().pad(10);
                for (int i = 0; i < 20; i++) { // Mostriamo 20 slot (4 righe da 5)
                    Table slot = new Table();
                    slot.setBackground(skin.newDrawable("white", new Color(1, 1, 1, 0.1f)));

                    if (i < inventoryItems.size()) {
                        final int index = i + 1;
                        final int finalI = i;
                        ItemInfo item = inventoryItems.get(i);
                        Texture icon = getItemIcon(item);

                        Image itemImg = new Image(icon);
                        slot.add(itemImg).size(90, 90).padTop(5).row();
                        Label nameLabel = new Label(item.name.toUpperCase(), skin);
                        nameLabel.setWrap(true);
                        nameLabel.setAlignment(Align.center);
                        nameLabel.setFontScale(0.55f);
                        slot.add(nameLabel).width(170).pad(5).expandY().fillY().center();

                        // Evidenzia se selezionato
                        if (selectedIndexInInventory == i) {
                            slot.setBackground(skin.newDrawable("white", new Color(1, 0.8f, 0, 0.5f)));
                        }

                        slot.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                selectedIndexInInventory = finalI;
                                // In battaglia o overflow non inviamo il comando subito, aspettiamo la conferma
                                // col tasto
                                // dedicato
                                if (currentState.equals("INVENTORY_BATTLE")
                                        || currentState.equals("INVENTORY_OVERFLOW")) {
                                    updateButtons();
                                } else {
                                    sendCommand(String.valueOf(index));
                                }
                            }
                        });
                    } else {
                        slot.add().size(90, 90).row();
                        Label emptyLabel = new Label("VUOTO", skin);
                        emptyLabel.getColor().a = 0.2f;
                        slot.add(emptyLabel).padTop(5);
                    }

                    grid.add(slot).size(180, 150);
                    if ((i + 1) % 5 == 0)
                        grid.row();
                }
                content.add(grid).expand().fill().center();

                // Right: Details Panel
                Table detailsPanel = new Table();
                detailsPanel.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0.6f)));
                detailsPanel.top();
                detailsPanel.pad(20);
                detailsPanel.add(new Label("DETTAGLI", skin)).padBottom(20).row();

                // Mostra messaggio di feedback (errori, avvisi) in evidenza
                if (lastFeedbackMessage != null && !lastFeedbackMessage.isEmpty()) {
                    Label feedbackLabel = new Label(lastFeedbackMessage, skin);
                    feedbackLabel.setColor(Color.RED);
                    feedbackLabel.setWrap(true);
                    feedbackLabel.setAlignment(Align.center);
                    feedbackLabel.setFontScale(0.85f);
                    detailsPanel.add(feedbackLabel).width(300).padBottom(15).row();
                }

                if (currentState.equals("INVENTORY_OVERFLOW") && groundItem != null) {
                    detailsPanel.add(new Label("A TERRA", skin)).padBottom(10).row();
                    Label nameLabel = new Label(groundItem.name.toUpperCase(), skin);
                    nameLabel.setColor(Color.CYAN);
                    detailsPanel.add(nameLabel).padBottom(5).row();

                    Label groundInfo = new Label(groundItem.details, skin);
                    groundInfo.setWrap(true);
                    groundInfo.setAlignment(Align.center);
                    groundInfo.setFontScale(0.7f);
                    detailsPanel.add(groundInfo).width(300).padBottom(20).row();
                    detailsPanel.add(new Image(skin.newDrawable("white", Color.GRAY))).height(2).fillX().pad(10).row();
                }

                if (selectedIndexInInventory != -1 && selectedIndexInInventory < inventoryItems.size()) {
                    ItemInfo item = inventoryItems.get(selectedIndexInInventory);

                    // Box per l'oggetto selezionato
                    Table selectedBox = new Table();
                    selectedBox.add(new Label("OGGETTO SELEZIONATO", skin)).padBottom(10).row();
                    Label nameL = new Label(item.name.toUpperCase(), skin);
                    nameL.setColor(Color.GOLD);
                    nameL.setWrap(true);
                    nameL.setAlignment(Align.center);
                    selectedBox.add(nameL).width(300).padBottom(10).row();
                    Label detailsL = new Label(item.details, skin);
                    detailsL.setWrap(true);
                    detailsL.setAlignment(Align.center);
                    detailsL.setFontScale(0.8f);
                    selectedBox.add(detailsL).width(250).row();

                    detailsPanel.add(selectedBox).expandX().fillX().padBottom(20).row();

                    // Box per il confronto (se applicabile)
                    if (equippedMap.containsKey(item.slot)) {
                        ItemInfo equipped = equippedMap.get(item.slot);
                        detailsPanel.add(new Image(skin.newDrawable("white", Color.GRAY))).height(2).fillX().pad(10)
                                .row();

                        Table compareBox = new Table();
                        compareBox.add(new Label("ATTUALE (" + item.slot.toUpperCase() + ")", skin)).padBottom(10)
                                .row();
                        Label eqNameL = new Label(equipped.name, skin);
                        eqNameL.setColor(Color.LIGHT_GRAY);
                        compareBox.add(eqNameL).padBottom(5).row();
                        Label eqDetailsL = new Label(equipped.details, skin);
                        eqDetailsL.setWrap(true);
                        eqDetailsL.setAlignment(Align.center);
                        eqDetailsL.setFontScale(0.8f);
                        compareBox.add(eqDetailsL).width(250).row();

                        detailsPanel.add(compareBox).expandX().fillX();
                    }
                } else {
                    Label hint = new Label("Seleziona un oggetto\nper vedere le statistiche", skin);
                    hint.setAlignment(Align.center);
                    detailsPanel.add(hint).width(300).expandY().center();
                }

                ScrollPane detailsScroll = new ScrollPane(detailsPanel, skin);
                detailsScroll.setFadeScrollBars(false);
                detailsScroll.setScrollingDisabled(true, false); // Solo scroll verticale

                content.add(detailsScroll).width(380).expandY().fillY().padLeft(30).row();

                Table bottomArea = new Table();
                messageLabel.setAlignment(Align.center);
                messageLabel.setWrap(true);
                bottomArea.add(messageLabel).width(1200).height(120).padBottom(15).row();
                bottomArea.add(buttonTable).expandX().fillX().height(80);
                content.add(bottomArea).colspan(2).expandX().fillX().padTop(20);

                Stack invStack = new Stack();
                invStack.add(new Image(skin.newDrawable("white", new Color(0.05f, 0.05f, 0.05f, 0.95f))));
                invStack.add(content);

                topView.add(invStack).expand().fill();

            } else if (currentState.equals("PLAYER_INFO")) {
                // --- NEW CHARACTER SCREEN LAYOUT (3 COLUMNS) ---
                Table content = new Table();
                content.pad(30);

                Label title = new Label("SCHEDA PERSONAGGIO", skin, "title");
                title.setColor(Color.GOLD);
                content.add(title).colspan(4).padBottom(40).row();

                // COLUMN 1: ARMOR
                Table armorCol = new Table();
                armorCol.add(new Label("ARMOR", skin)).padBottom(10).row();
                String[] armorSlots = { "Testa", "Torso", "Gambe", "Piedi" };
                String[] armorLabels = { "TESTA", "TORSO", "GAMBE", "PIEDI" };
                for (int i = 0; i < armorSlots.length; i++) {
                    armorCol.add(createEquipSlot(armorSlots[i], armorLabels[i])).size(150, 120).fill().pad(5).row();
                }

                // COLUMN 2: ARTIFACTS
                Table artCol = new Table();
                artCol.add(new Label("ARTEFATTI", skin)).padBottom(10).row();
                String[] artSlots = { "Mantello", "Orecchini", "Collana", "Anello" };
                String[] artLabels = { "MANTELLO", "ORECCHINI", "COLLANA", "ANELLO" };
                for (int i = 0; i < artSlots.length; i++) {
                    artCol.add(createEquipSlot(artSlots[i], artLabels[i])).size(150, 120).fill().pad(5).row();
                }

                // COLUMN 3: WEAPONS
                Table weapCol = new Table();
                weapCol.add(new Label("ARMI", skin)).padBottom(10).row();
                weapCol.add(createEquipSlot("Primaria", "MAIN HAND")).size(150, 120).pad(5).row();
                weapCol.add(createEquipSlot("Secondaria", "OFF HAND")).size(150, 120).pad(5).row();

                content.add(armorCol).top().padRight(20);
                content.add(artCol).top().padRight(20);
                content.add(weapCol).top().padRight(40);

                // COLUMN 4: STATS
                Table statsPanel = new Table();
                statsPanel.setBackground(skin.newDrawable("white", new Color(1, 1, 1, 0.05f)));
                statsPanel.pad(20);

                statsPanel.add(new Label("STATISTICHE", skin)).padBottom(15).row();
                statsPanel.add(new Label("LIVELLO: " + playerLevel, skin)).left().padBottom(5).row();
                statsPanel.add(new Label("PV: " + playerHp + "/" + playerMaxHp, skin)).left().padBottom(5).row();
                statsPanel.add(new Label("MANA: " + playerMana + "/" + playerManaMax, skin)).left().padBottom(5).row();
                statsPanel.add(new Image(skin.newDrawable("white", Color.GRAY))).height(1).fillX().pad(10).row();
                statsPanel.add(new Label("FORZA: " + playerStr, skin)).left().row();
                statsPanel.add(new Label("AGILITÀ: " + playerAgi, skin)).left().row();
                statsPanel.add(new Label("INTEL.: " + playerInt, skin)).left().row();
                statsPanel.add(new Label("DIFESA: " + playerDef, skin)).left().row();
                statsPanel.add(new Label("PREC.: " + playerPre, skin)).left().row();
                statsPanel.add(new Image(skin.newDrawable("white", Color.GRAY))).height(1).fillX().pad(10).row();
                statsPanel.add(new Label("CRIT.: " + playerCrit + "%", skin)).left().row();
                statsPanel.add(new Label("SCHIV.: " + playerDodge + "%", skin)).left().row();

                content.add(statsPanel).top().width(250).fillY();

                Stack stack = new Stack();
                stack.add(new Image(skin.newDrawable("white", new Color(0, 0, 0, 0.95f))));
                stack.add(content);
                topView.add(stack).expand().fill();

            } else {
                // IDLE / PLAYER_INFO LAYOUT
                Table statsOverlay = new Table();
                statsOverlay.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0.5f)));
                statsOverlay.pad(15);

                Table barTable = new Table();
                barTable.add(new Label(selectedClassName.toUpperCase() + " Lv." + playerLevel, skin)).left()
                        .padBottom(8).row();
                barTable.add(new Label("PV", skin)).left();

                Image hpBarFill = new Image(skin.newDrawable("white", Color.RED));
                Stack hpStack = new Stack(new Image(skin.newDrawable("white", Color.DARK_GRAY)));
                Table hpFillTable = new Table();
                hpFillTable.left();
                float hpPercent = playerMaxHp > 0 ? (float) playerHp / playerMaxHp : 0;
                hpFillTable.add(hpBarFill).width(180 * hpPercent).height(15).left();
                hpStack.add(hpFillTable);

                Label hpTextLabel = new Label(playerHp + "/" + playerMaxHp, skin);
                hpTextLabel.setAlignment(Align.center);
                hpTextLabel.setFontScale(0.6f);
                hpStack.add(hpTextLabel);

                barTable.add(hpStack).width(180).height(15).padLeft(10).row();

                barTable.add(new Label("MP", skin)).left();
                Image mpBarFill = new Image(skin.newDrawable("white", Color.CYAN));
                Stack mpStack = new Stack(new Image(skin.newDrawable("white", Color.DARK_GRAY)));
                Table mpFillTable = new Table();
                mpFillTable.left();
                float mpPercent = playerManaMax > 0 ? (float) playerMana / playerManaMax : 0;
                mpFillTable.add(mpBarFill).width(180 * mpPercent).height(15).left();
                mpStack.add(mpFillTable);

                Label mpTextLabel = new Label(playerMana + "/" + playerManaMax, skin);
                mpTextLabel.setAlignment(Align.center);
                mpTextLabel.setFontScale(0.6f);
                mpStack.add(mpTextLabel);

                barTable.add(mpStack).width(180).height(15).padLeft(10).row();
                statsOverlay.add(barTable);
                topView.add(statsOverlay).expand().top().left().pad(30);

                if (selectedClassIndex != -1) {
                    Image characterSprite = new Image(classTextures[selectedClassIndex - 1]);
                    topView.add(characterSprite).size(350, 350).bottom().expand();
                } else {
                    topView.add().expand();
                }
                topView.add().expand(); // Spacer to balance layout
            }

            // Redesigned Bottom Dashboard
            Table dashboard = new Table();
            dashboard.setBackground(skin.newDrawable("white", new Color(0.05f, 0.05f, 0.05f, 1f)));
            dashboard.pad(10);

            // 2. CENTER: Dialogue / Log
            Stack dialogStack = new Stack();
            dialogStack.add(new Image(skin.newDrawable("white", new Color(0, 0, 0, 0.8f))));
            Table textTable = new Table();
            textTable.add(messageLabel).width(1200).pad(20).top().left().row();
            
            ScrollPane dialogScroll = new ScrollPane(textTable, skin);
            dialogScroll.setFadeScrollBars(false);
            dialogScroll.setScrollingDisabled(true, false);
            dialogScroll.setForceScroll(false, true); // Keep vertical scroll active if needed
            dialogScroll.layout();
            dialogScroll.setScrollPercentY(1f);
            
            dialogStack.add(dialogScroll);

            dashboard.add(dialogStack).expand().fill().padRight(10);

            // 3. RIGHT: Actions
            if (!currentState.startsWith("INVENTORY_")) {
                Table actionGrid = new Table();
                actionGrid.add(buttonTable).expand().fill();
                dashboard.add(actionGrid).width(620).fillY();
            }

            if (currentState.startsWith("INVENTORY_")) {
                rootTable.add(topView).expand().fill();
            } else {
                rootTable.add(topView).expand().fill().row();
                rootTable.add(dashboard).height(280).fillX();
            }
        } else {
            rootTable.add(messageLabel).expand().fill().pad(50).row();
            rootTable.add(buttonTable).fillX().pad(20);
        }
    }

    private void updateButtons() {
        buttonTable.clearChildren();
        if (!messageQueue.isEmpty()) {
            return;
        }
        updateLayout();

        switch (currentState) {
            case "MENU":
                String[] classes = { "Warrior", "Archer", "Assassin", "Mage", "Tank" };
                for (int i = 0; i < classes.length; i++) {
                    final int num = i + 1;
                    final int index = i;

                    Image img = new Image(classTextures[i]);
                    // Imposta l'origine esattamente al centro di un'immagine 150x150 per uno
                    // scaling perfetto
                    img.setOrigin(75, 75);
                    img.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            selectedClassIndex = num;
                            confirmClassButton.setVisible(true);
                            confirmClassButton.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.2f)));
                            globalStatsLabel.setText(classStats[index]);
                            statsContainer.getColor().a = 1f;
                        }

                        @Override
                        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                            super.enter(event, x, y, pointer, fromActor);
                            if (pointer == -1) {
                                img.clearActions();
                                img.addAction(Actions.parallel(
                                        Actions.scaleTo(1.2f, 1.2f, 0.15f),
                                        Actions.color(Color.GOLD, 0.15f)));

                                globalStatsLabel.setText(classStats[index]);
                                statsContainer.clearActions();
                                statsContainer.addAction(Actions.fadeIn(0.15f));
                            }
                        }

                        @Override
                        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                            super.exit(event, x, y, pointer, toActor);
                            if (pointer == -1) {
                                img.clearActions();
                                img.addAction(Actions.parallel(
                                        Actions.scaleTo(1f, 1f, 0.15f),
                                        Actions.color(Color.WHITE, 0.15f)));

                                if (selectedClassIndex != -1) {
                                    globalStatsLabel.setText(classStats[selectedClassIndex - 1]);
                                    statsContainer.getColor().a = 1f;
                                } else {
                                    statsContainer.clearActions();
                                    statsContainer.addAction(Actions.fadeOut(0.15f));
                                }
                            }
                        }
                    });

                    // Aggiunge direttamente l'immagine al buttonTable senza container innestati
                    buttonTable.add(img).size(150, 150).pad(20);
                }
                break;
            case "CONFIRM_SELECTION":
                addButton("Conferma", () -> sendCommand("Conferma"));
                addButton("Annulla", () -> sendCommand("Annulla"));
                break;
            case "IDLE":
                addIconButton("ESPLORA", iconSword, () -> sendCommand("Avanza"));
                addIconButton("ZAINO", iconBackpack, () -> sendCommand("Zaino"));
                addIconButton("PERSONAGGIO", iconShield, () -> sendCommand("Personaggio"));
                addButton("DISCONNETTI", () -> {
                    networkManager.sendCommand("DISCONNECT");
                    networkManager.disconnect();
                    game.setScreen(new WelcomeScreen(game));
                });
                break;
            case "SHOP":
                addButton("ESCI", () -> sendCommand("ESCI"));
                break;
            case "ROOM":
                String fullLog = String.join(" ", messageLog);
                if (fullLog.contains("Negozio") || fullLog.contains("MERCE")) {
                    addButton("COMPRA 1", () -> sendCommand("COMPRA 1"));
                    addButton("COMPRA 2", () -> sendCommand("COMPRA 2"));
                    addButton("COMPRA 3", () -> sendCommand("COMPRA 3"));
                    addButton("INVENTARIO", () -> sendCommand("INVENTARIO"));
                    addButton("VENDI 1", () -> sendCommand("VENDI 1"));
                    addButton("ESCI", () -> sendCommand("ESCI"));
                } else if (fullLog.contains("Fontana")) {
                    addButton("BEVI 10", () -> sendCommand("BEVI 10"));
                    addButton("BEVI 50", () -> sendCommand("BEVI 50"));
                    addButton("ESCI", () -> sendCommand("ESCI"));
                } else {
                    addButton("ESCI", () -> sendCommand("ESCI"));
                }
                break;
            case "INVENTORY_MAIN":
                addButton("ESCI", () -> sendCommand("Esci"));
                break;
            case "INVENTORY_BATTLE":
                if (selectedIndexInInventory != -1 && selectedIndexInInventory < inventoryItems.size()) {
                    addIconButton("USA", iconWand, () -> {
                        int indexToSend = selectedIndexInInventory + 1;
                        sendCommand(String.valueOf(indexToSend));
                    });
                }
                addButton("ESCI", () -> sendCommand("Esci"));
                break;
            case "INVENTORY_ACTION":
                if (selectedIndexInInventory != -1 && selectedIndexInInventory < inventoryItems.size()) {
                    ItemInfo item = inventoryItems.get(selectedIndexInInventory);
                    if (item.type.equals("USABLE")) {
                        addIconButton("USA", iconWand, () -> sendCommand("Usa"));
                    } else {
                        addIconButton("EQUIPAGGIA", iconShield, () -> sendCommand("Equipaggia"));
                    }
                }
                addIconButton("BUTTA", iconBackpack, () -> sendCommand("Butta"));
                addButton("ANNULLA", () -> {
                    selectedIndexInInventory = -1;
                    sendCommand("Annulla");
                });
                break;
            case "INVENTORY_OVERFLOW":
                if (selectedIndexInInventory != -1) {
                    addIconButton("SOSTITUISCI", iconSword, () -> {
                        int indexToSend = selectedIndexInInventory + 1;
                        sendCommand(String.valueOf(indexToSend));
                    });
                }
                addIconButton("LASCIA", iconBackpack, () -> sendCommand("Lascia"));
                break;
            case "PLAYER_INFO":
                addButton("ESCI", () -> sendCommand("Esci"));
                break;
            case "BATTLE":
                if (!isSelectingTarget && selectedAttackType.isEmpty()) {
                    addIconButton("ATT. FISICO", iconSword, () -> {
                        selectedAttackType = "PHYSIC_MENU";
                        isSelectingTarget = false;
                        updateButtons();
                    });
                    addIconButton("MAGIA", iconWand, () -> {
                        selectedAttackType = "MAGIC_MENU";
                        isSelectingTarget = false;
                        updateButtons();
                    });
                    addIconButton("ZAINO", iconBackpack, () -> sendCommand("Zaino"));
                    addIconButton("PASSA", iconShield, () -> sendCommand("PASSA"));
                } else if (selectedAttackType.equals("PHYSIC_MENU")) {
                    addIconButton("ATT. LEGGERO", iconSword, () -> {
                        selectedAttackType = "L";
                        isSelectingTarget = true;
                        updateButtons();
                    });
                    addIconButton("ATT. NORMALE", iconSword, () -> {
                        selectedAttackType = "N";
                        isSelectingTarget = true;
                        updateButtons();
                    });
                    addIconButton("ATT. PESANTE", iconSword, () -> {
                        selectedAttackType = "P";
                        isSelectingTarget = true;
                        updateButtons();
                    });
                    addButton("ANNULLA", () -> {
                        selectedAttackType = "";
                        isSelectingTarget = false;
                        updateButtons();
                    });
                } else if (selectedAttackType.equals("MAGIC_MENU")) {
                    addIconButton("SFERA FUOCO", iconWand, () -> {
                        selectedAttackType = "M1";
                        isSelectingTarget = true;
                        updateButtons();
                    });
                    addIconButton("ESPLOS. ARCANA", iconWand, () -> {
                        selectedAttackType = "M2";
                        isSelectingTarget = true;
                        updateButtons();
                    });
                    addIconButton("CURA", iconWand, () -> {
                        selectedAttackType = "";
                        isSelectingTarget = false;
                        sendCommand("C"); // La cura non richiede bersaglio
                    });
                    addButton("ANNULLA", () -> {
                        selectedAttackType = "";
                        isSelectingTarget = false;
                        updateButtons();
                    });
                } else {
                    // Selezione dinamica dei bersagli
                    for (int i = 0; i < currentEnemies.size(); i++) {
                        final int targetIndex = i + 1;
                        EnemyInfo enemy = currentEnemies.get(i);
                        addButton(enemy.name.toUpperCase(), () -> {
                            String cmdType = selectedAttackType;
                            selectedAttackType = "";
                            isSelectingTarget = false;
                            
                            if (cmdType.equals("M1")) {
                                sendCommand("M 1 " + targetIndex);
                            } else if (cmdType.equals("M2")) {
                                sendCommand("M 2 " + targetIndex);
                            } else {
                                sendCommand("F " + cmdType + " " + targetIndex);
                            }
                        });
                    }
                    addButton("ANNULLA", () -> {
                        if (selectedAttackType.equals("M1") || selectedAttackType.equals("M2")) {
                            selectedAttackType = "MAGIC_MENU"; // Torna al menu magie
                        } else if (selectedAttackType.equals("L") || selectedAttackType.equals("N") || selectedAttackType.equals("P")) {
                            selectedAttackType = "PHYSIC_MENU"; // Torna al menu fisico
                        } else {
                            selectedAttackType = ""; // Torna al menu principale
                        }
                        isSelectingTarget = false;
                        updateButtons();
                    });
                }
                break;
            default:
                break;
        }
    }

    private void addIconButton(String text, Texture icon, Runnable action) {
        Button.ButtonStyle style = new Button.ButtonStyle();
        com.badlogic.gdx.scenes.scene2d.utils.Drawable bgDrawable = new Image(buttonBgTex).getDrawable();
        style.up = bgDrawable;
        style.over = skin.newDrawable(bgDrawable, new Color(0.8f, 0.8f, 0.8f, 1f));
        style.down = skin.newDrawable(bgDrawable, new Color(0.5f, 0.5f, 0.5f, 1f));

        Button button = new Button(style);

        Table t = new Table();
        t.add(new Image(icon)).size(40, 40).padRight(15);
        t.add(new Label(text, skin, "dialog"));
        button.add(t).pad(15);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });

        buttonTable.add(button).width(280).height(80).pad(10);
        if (buttonTable.getChildren().size % 2 == 0)
            buttonTable.row();
    }

    private void addButton(String text, Runnable action) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        com.badlogic.gdx.scenes.scene2d.utils.Drawable bgDrawable = new Image(buttonBgTex).getDrawable();
        style.up = bgDrawable;
        style.over = skin.newDrawable(bgDrawable, new Color(0.8f, 0.8f, 0.8f, 1f));
        style.down = skin.newDrawable(bgDrawable, new Color(0.5f, 0.5f, 0.5f, 1f));
        style.font = skin.getFont("default");
        style.fontColor = Color.WHITE;

        TextButton button = new TextButton(text, style);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        buttonTable.add(button).fill();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleKeyboardInput();

        if (!messageQueue.isEmpty()) {
            messageTimer -= delta;
            if (messageTimer <= 0) {
                String nextMsg = messageQueue.poll();
                messageLog.add(nextMsg);
                if (messageLog.size() > 5)
                    messageLog.remove(0);
                updateLog();
                messageTimer = 0.8f;
                if (messageQueue.isEmpty()) {
                    updateButtons();
                }
            }
        }

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    private void handleKeyboardInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1))
            sendKeyboardCommandIfValid("1");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2))
            sendKeyboardCommandIfValid("2");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3))
            sendKeyboardCommandIfValid("3");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_4))
            sendKeyboardCommandIfValid("4");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_5))
            sendKeyboardCommandIfValid("5");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_6))
            sendKeyboardCommandIfValid("6");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_7))
            sendKeyboardCommandIfValid("7");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_8))
            sendKeyboardCommandIfValid("8");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_9))
            sendKeyboardCommandIfValid("9");

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (currentState.equals("INVENTORY_MAIN") || currentState.equals("PLAYER_INFO"))
                sendCommand("Esci");
            if (currentState.equals("INVENTORY_ACTION"))
                sendCommand("Annulla");
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (currentState.equals("CONFIRM_SELECTION")) {
                sendCommand("Conferma");
            }
        }
    }

    private void sendKeyboardCommandIfValid(String command) {
        sendCommand(command);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    private Table createEquipSlot(final String slotName, String label) {
        Table slot = new Table();
        slot.setBackground(skin.newDrawable("white", new Color(0.15f, 0.15f, 0.15f, 0.9f)));

        Label slotLabel = new Label(label, skin);
        slotLabel.setFontScale(0.6f);
        slotLabel.setColor(Color.GRAY);
        slot.add(slotLabel).top().padTop(5).row();

        if (equippedMap.containsKey(slotName)) {
            ItemInfo item = equippedMap.get(slotName);

            Texture icon = getItemIcon(item);
            Image itemImg = new Image(icon);
            slot.add(itemImg).size(50, 50).pad(2).row();

            Label nameLabel = new Label(item.name.toUpperCase(), skin);
            nameLabel.setWrap(true);
            nameLabel.setAlignment(Align.center);
            nameLabel.setFontScale(0.7f);
            nameLabel.setColor(Color.CYAN);
            slot.add(nameLabel).width(140).pad(2).row();

            TextButton unequip = new TextButton("X", skin);
            unequip.getLabel().setColor(Color.RED);
            unequip.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    sendCommand("Disequipaggia " + slotName);
                }
            });
            slot.add(unequip).size(35, 35).bottom().right().pad(1);
        } else {
            Label empty = new Label("VUOTO", skin);
            empty.getColor().a = 0.2f;
            slot.add(empty).expand().center();
        }

        return slot;
    }

    private void parseBattleInfo(String battleData) {
        String[] parts = battleData.split(";");
        if (parts.length > 0) {
            String[] playerStats = parts[0].split(",");
            if (playerStats.length >= 3) {
                playerLevel = Integer.parseInt(playerStats[0]);
                playerHp = Integer.parseInt(playerStats[1]);
                playerMaxHp = Integer.parseInt(playerStats[2]);
                if (playerStats.length >= 12) {
                    playerStr = Integer.parseInt(playerStats[3]);
                    playerAgi = Integer.parseInt(playerStats[4]);
                    playerInt = Integer.parseInt(playerStats[5]);
                    playerPre = Integer.parseInt(playerStats[6]);
                    playerMana = Integer.parseInt(playerStats[7]);
                    playerManaMax = Integer.parseInt(playerStats[8]);
                    playerDef = Integer.parseInt(playerStats[9]);
                    playerCrit = Integer.parseInt(playerStats[10]);
                    playerDodge = Integer.parseInt(playerStats[11]);
                    if (playerStats.length >= 13) {
                        playerPa = Integer.parseInt(playerStats[12]);
                    }
                    if (playerStats.length >= 14) {
                        playerMoney = Integer.parseInt(playerStats[13]);
                    }
                }

            }
            currentEnemies.clear();
            for (int i = 1; i < parts.length; i++) {
                String[] enemyStats = parts[i].split(",");
                if (enemyStats.length == 3) {
                    EnemyInfo info = new EnemyInfo();
                    info.name = enemyStats[0];
                    info.hp = Integer.parseInt(enemyStats[1]);
                    info.maxHp = Integer.parseInt(enemyStats[2]);
                    currentEnemies.add(info);
                }
            }
        }
    }

    private Texture getItemIcon(ItemInfo item) {
        String name = item.name.toLowerCase();

        // Items Usabili / Consumabili
        if (item.type.equalsIgnoreCase("USABLE") || item.type.equalsIgnoreCase("HealthPotion") || item.type.equalsIgnoreCase("ManaPotion")) {
            if (name.contains("mana")) return iconManaPotion;
            if (name.contains("lancio") || name.contains("bomba") || name.contains("fiala") || name.contains("coltello")) return iconThrowingKnife;
            return iconHealthPotion; // default if usable
        }

        if (item.type.equalsIgnoreCase("Staff") || name.contains("catalizzatore") || name.contains("bastone") || name.contains("bacchetta") || name.contains("staffa"))
            return iconWand;

        String slot = item.slot.toLowerCase();

        // 1. Armors
        if (slot.contains("testa") || slot.contains("head"))
            return iconHelmet;
        if (slot.contains("torso") || slot.contains("chest"))
            return iconArmor;
        if (slot.contains("gambe") || slot.contains("legs"))
            return iconPants;
        if (slot.contains("piedi") || slot.contains("feet"))
            return iconBoots;

        // 2. Artefacts
        if (slot.contains("mantello")) return iconCloak;
        if (slot.contains("orecchini")) return iconEarrings;
        if (slot.contains("collana")) return iconNecklace;
        if (slot.contains("accessorio") || slot.contains("anello") || slot.contains("ring"))
            return iconRing;

        // 3. Specific Names
        if (name.contains("arco") || name.contains("balestra") || name.contains("bow") || name.contains("predator"))
            return iconBow;
        if (name.contains("pugnale") || name.contains("coltello") || name.contains("dagger") || name.contains("sting") || name.contains("tooth"))
            return iconDagger;
        if (name.contains("spadone") || name.contains("ascia") || name.contains("greatsword") || name.contains("martello") || name.contains("sundered"))
            return iconGreatsword;

        // 4. Shields
        if (name.contains("scudo") || name.contains("shield") || name.contains("colonne di ercole") || name.contains("great turtle shell"))
            return iconShield;
        if (slot.contains("sinistra") || slot.contains("scudo") || slot.contains("shield") || slot.contains("off") || slot.contains("secondaria"))
            return iconShield;

        return iconSword; // Default
    }

    private Texture getEnemyTexture(String enemyName) {
        String lowerName = enemyName.toLowerCase();
        if (lowerName.contains("orc") || lowerName.contains("orco")) {
            return orcTex;
        } else if (lowerName.contains("skeleton") || lowerName.contains("scheletro") || lowerName.contains("teschio")) {
            return skeletonTex;
        } else if (lowerName.contains("goblin")) {
            return goblinTex;
        } else if (lowerName.contains("witch") || lowerName.contains("strega")) {
            return witchTex;
        }
        return zombieTex; // Default fallback
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (networkManager != null) {
            networkManager.disconnect();
        }
        if (classTextures != null) {
            for (Texture t : classTextures) {
                if (t != null)
                    t.dispose();
            }
        }
        if (zombieTex != null) zombieTex.dispose();
        if (orcTex != null) orcTex.dispose();
        if (skeletonTex != null) skeletonTex.dispose();
        if (goblinTex != null) goblinTex.dispose();
        if (witchTex != null) witchTex.dispose();
    }
}
