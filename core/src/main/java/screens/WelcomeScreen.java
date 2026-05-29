package screens;

import client.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import network.NetworkManager;

public class WelcomeScreen extends ScreenAdapter {

    private Main game;
    private Stage stage;
    private NetworkManager networkManager;
    private Table rootTable;
    private Label statusLabel;
    
    // UI Assets
    private Texture bgTex;
    private Texture logoTex;
    private Texture slotPanelTex;
    private Texture parchmentTex;
    private Texture silhouetteTex;
    
    // Class textures
    private Texture warriorTex, archerTex, assassinTex, mageTex, tankTex;
    
    private Skin skin;

    public WelcomeScreen(Main game) {
        this.game = game;
        stage = new Stage(new FitViewport(1920, 1080));
        Gdx.input.setInputProcessor(stage);

        loadAssets();
        createBasicSkin();

        // Main Layout
        Table mainLayout = new Table();
        mainLayout.setFillParent(true);
        mainLayout.setBackground(new TextureRegionDrawable(bgTex));
        stage.addActor(mainLayout);

        rootTable = new Table();
        mainLayout.add(rootTable).expand().fill();

        statusLabel = new Label("Connessione al server in corso...", skin, "status");
        statusLabel.setAlignment(Align.center);
        
        buildConnectingMenu();

        networkManager = new NetworkManager("127.0.0.1", 8083, new NetworkManager.NetworkListener() {
            @Override
            public void onMessageReceived(String message) {
                Gdx.app.postRunnable(() -> handleMessage(message));
            }

            @Override
            public void onConnectionError(String error) {
                Gdx.app.postRunnable(() -> statusLabel.setText("Errore: " + error));
            }
        });
        networkManager.connect();
    }

    private void loadAssets() {
        bgTex = new Texture(Gdx.files.internal("welcome_bg.png"));
        logoTex = new Texture(Gdx.files.internal("the_pit_icon.png")); // Using window icon as logo
        slotPanelTex = new Texture(Gdx.files.internal("slot_panel.png"));
        parchmentTex = new Texture(Gdx.files.internal("parchment.png"));
        silhouetteTex = new Texture(Gdx.files.internal("silhouette.png"));

        warriorTex = new Texture(Gdx.files.internal("classes/warrior.png"));
        archerTex = new Texture(Gdx.files.internal("classes/archer.png"));
        assassinTex = new Texture(Gdx.files.internal("classes/assassin.png"));
        mageTex = new Texture(Gdx.files.internal("classes/mage.png"));
        tankTex = new Texture(Gdx.files.internal("classes/tank.png"));
    }

    private Texture getClassTexture(String className) {
        switch (className.toLowerCase()) {
            case "warrior": case "guerriero": return warriorTex;
            case "archer": case "arciere": return archerTex;
            case "assassin": case "assassino": return assassinTex;
            case "mage": case "mago": return mageTex;
            case "tank": return tankTex;
            default: return warriorTex;
        }
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
        pixmap.dispose();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        // Standard font
        parameter.size = 32;
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.minFilter = Texture.TextureFilter.Linear;
        BitmapFont font = generator.generateFont(parameter);
        skin.add("default", font);

        // Status font
        parameter.size = 24;
        BitmapFont statusFont = generator.generateFont(parameter);
        skin.add("status", statusFont);

        // Leaderboard font
        parameter.size = 26;
        parameter.color = Color.BLACK;
        BitmapFont blackFont = generator.generateFont(parameter);
        skin.add("black", blackFont);

        generator.dispose();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        Label.LabelStyle statusStyle = new Label.LabelStyle();
        statusStyle.font = statusFont;
        statusStyle.fontColor = Color.LIGHT_GRAY;
        skin.add("status", statusStyle);

        Label.LabelStyle blackStyle = new Label.LabelStyle();
        blackStyle.font = blackFont;
        blackStyle.fontColor = Color.valueOf("222222"); // Dark ink color
        skin.add("black", blackStyle);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("button_up");
        textButtonStyle.over = skin.newDrawable("button_over");
        textButtonStyle.down = skin.newDrawable("button_down");
        textButtonStyle.font = font;
        skin.add("default", textButtonStyle);
    }

    private void buildConnectingMenu() {
        rootTable.clear();
        Image logo = new Image(logoTex);
        rootTable.add(logo).size(300, 300).padBottom(50).row();
        rootTable.add(statusLabel).pad(20).row();
    }

    private void handleMessage(String msg) {
        if (msg.startsWith("{WELCOME_INFO:")) {
            String data = msg.substring(14, msg.length() - 1); // remove {WELCOME_INFO: and }
            String[] parts = data.split("\\|\\|\\|");
            String slotsInfo = parts.length > 0 ? parts[0] : "";
            String topRunsInfo = parts.length > 1 ? parts[1].replace("\\n", "\n") : "";
            buildMenu(slotsInfo, topRunsInfo);
        } else if (msg.contains("{END:MENU}") || msg.contains("{END:IDLE}") || msg.contains("{END:IN_GAME}")) {
            // Pass to PlayScreen
            PlayScreen playScreen = new PlayScreen(game, networkManager);
            game.setScreen(playScreen);
            playScreen.handleServerMessage(msg);
        } else if (msg.startsWith("{MSG:")) {
            String text = msg.substring(5, msg.indexOf("}"));
            statusLabel.setText(text);
        }
    }

    private void buildMenu(String slotsInfo, String topRunsInfo) {
        rootTable.clear();
        
        // Logo
        Image logo = new Image(logoTex);
        rootTable.add(logo).size(250, 250).padBottom(40).row();

        // Slots container
        Table slotsContainer = new Table();

        String[] slots = slotsInfo.split("\\|");
        for (String slotStr : slots) {
            if (slotStr.isEmpty()) continue;
            String[] sInfo = slotStr.split(";");
            if (sInfo.length >= 4) {
                String id = sInfo[0];
                String clazz = sInfo[1];
                String lvl = sInfo[2];
                String date = sInfo[3];
                
                // Base slot table with background
                Table slotTable = new Table();
                slotTable.setBackground(new TextureRegionDrawable(slotPanelTex));
                slotTable.pad(15);
                
                if (clazz.equals("Vuoto")) {
                    // Empty slot
                    Image silImage = new Image(silhouetteTex);
                    Label textLabel = new Label("Nuova Partita (Slot " + id + ")", skin);
                    
                    slotTable.add(silImage).size(80, 80).padRight(20);
                    slotTable.add(textLabel).expandX().center();
                    
                    slotTable.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            networkManager.sendCommand("NEW_GAME " + id);
                            statusLabel.setText("Avvio Nuova Partita nello Slot " + id + "...");
                        }
                    });
                    
                    slotsContainer.add(slotTable).width(700).height(120).padBottom(20).row();
                    
                } else {
                    // Occupied slot
                    Image classImg = new Image(getClassTexture(clazz));
                    
                    Table textTable = new Table();
                    Label nameLabel = new Label("Carica Slot " + id + " - " + clazz + " Lvl " + lvl, skin);
                    Label dateLabel = new Label("(" + date + ")", skin, "status");
                    nameLabel.setAlignment(Align.center);
                    dateLabel.setAlignment(Align.center);
                    textTable.add(nameLabel).row();
                    textTable.add(dateLabel).padTop(5);
                    
                    TextButton resetBtn = new TextButton("Reset", skin);
                    resetBtn.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            networkManager.sendCommand("RESET_SLOT " + id);
                            statusLabel.setText("Resettando lo slot " + id + "...");
                        }
                    });
                    
                    // The row inside the slot
                    slotTable.add(classImg).size(80, 80).padRight(20);
                    slotTable.add(textTable).expandX().center();
                    slotTable.add(resetBtn).width(120).height(70).padLeft(20);
                    
                    // Add click listener to the table to load game (but not on the reset button)
                    slotTable.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);
                    slotTable.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            // Only trigger load if the click wasn't on the reset button
                            if (event.getTarget() != resetBtn && !resetBtn.isAscendantOf(event.getTarget())) {
                                networkManager.sendCommand("LOAD_GAME " + id);
                                statusLabel.setText("Caricamento in corso...");
                            }
                        }
                    });
                    
                    slotsContainer.add(slotTable).width(700).height(120).padBottom(20).row();
                }
            }
        }
        
        rootTable.add(slotsContainer).padBottom(30).row();

        // Leaderboard with parchment background
        Table leaderboardTable = new Table();
        leaderboardTable.setBackground(new TextureRegionDrawable(parchmentTex));
        leaderboardTable.pad(40, 60, 40, 60); // Inner padding for the scroll
        
        Label leaderboardLabel = new Label("| ===== CLASSIFICA (MIGLIORI RUN) ===== |\n" + topRunsInfo, skin, "black");
        leaderboardLabel.setAlignment(Align.center);
        leaderboardTable.add(leaderboardLabel);

        rootTable.add(leaderboardTable).minWidth(600).padBottom(40).row();

        // Bottom status label
        statusLabel.setText("Scegli un'opzione per iniziare");
        rootTable.add(statusLabel).padBottom(20).row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        bgTex.dispose();
        logoTex.dispose();
        slotPanelTex.dispose();
        parchmentTex.dispose();
        silhouetteTex.dispose();
        warriorTex.dispose();
        archerTex.dispose();
        assassinTex.dispose();
        mageTex.dispose();
        tankTex.dispose();
        skin.dispose();
    }
}
