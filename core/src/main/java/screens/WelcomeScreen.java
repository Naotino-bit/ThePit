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
    private Texture emptySlotPanelTex;
    private Texture parchmentTex;
    private Texture resetButtonTex;
    private Texture logoTextTex;
    private Texture uiBoxTex;

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
        bgTex = new Texture(Gdx.files.internal("bg_welcomeScreen.png"));
        logoTex = new Texture(Gdx.files.internal("the_pit_logo.png"));
        logoTextTex = new Texture(Gdx.files.internal("the_pit_text.png"));
        slotPanelTex = new Texture(Gdx.files.internal("selection_bg.png"));
        emptySlotPanelTex = new Texture(Gdx.files.internal("bg_empty_slot.png"));
        parchmentTex = new Texture(Gdx.files.internal("leaderboard_bg.png"));
        resetButtonTex = new Texture(Gdx.files.internal("reset_button_bg.png"));
        uiBoxTex = new Texture(Gdx.files.internal("ui_box.png"));

        warriorTex = new Texture(Gdx.files.internal("classes/warrior.png"));
        archerTex = new Texture(Gdx.files.internal("classes/archer.png"));
        assassinTex = new Texture(Gdx.files.internal("classes/assassin.png"));
        mageTex = new Texture(Gdx.files.internal("classes/mage.png"));
        tankTex = new Texture(Gdx.files.internal("classes/tank.png"));
    }

    private TextureRegionDrawable createBackground(Texture tex) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(tex);
        drawable.setMinWidth(0);
        drawable.setMinHeight(0);
        return drawable;
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
        parameter.size = 24;
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.minFilter = Texture.TextureFilter.Linear;
        BitmapFont font = generator.generateFont(parameter);
        skin.add("default", font);

        // Status font
        parameter.size = 18;
        BitmapFont statusFont = generator.generateFont(parameter);
        skin.add("status", statusFont);

        // Leaderboard font
        parameter.size = 20;
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
        Table logoTable = new Table();
        Image logoImg = new Image(logoTex);
        logoImg.setScaling(com.badlogic.gdx.utils.Scaling.fit);
        Image textImg = new Image(logoTextTex);
        textImg.setScaling(com.badlogic.gdx.utils.Scaling.fit);

        // Usiamo padTop e padBottom negativi per ingrandire le immagini SENZA spingere via il resto dello schermo
        logoTable.add(logoImg).size(300, 300).padTop(-50).padBottom(-50).row();
        logoTable.add(textImg).size(1600, 500).padTop(-175).padBottom(-175);
        rootTable.add(logoTable).padBottom(10).row();
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

        // FONDAMENTALE: Assicurati che la tabella principale riempia lo schermo
        rootTable.setFillParent(true);

        // Logo
        Table logoTable = new Table();
        Image logoImg = new Image(logoTex);
        logoImg.setScaling(com.badlogic.gdx.utils.Scaling.fit);
        Image textImg = new Image(logoTextTex);
        textImg.setScaling(com.badlogic.gdx.utils.Scaling.fit);

        // Usiamo padTop e padBottom negativi per ingrandire le immagini SENZA spingere via il resto dello schermo
        logoTable.add(logoImg).size(300, 300).padTop(-50).padBottom(-50).row();
        logoTable.add(textImg).size(1600, 500).padTop(-175).padBottom(-175);
        rootTable.add(logoTable).padBottom(10).row();

        // Slots container (Lo sfondo grande con le rune)
        Table slotsContainer = new Table();
        slotsContainer.setBackground(createBackground(slotPanelTex));
        // Padding per lasciare spazio ai bordi runici
        slotsContainer.pad(60, 40, 60, 40);

        String[] slots = slotsInfo.split("\\|");
        for (String slotStr : slots) {
            if (slotStr.isEmpty()) continue;
            String[] sInfo = slotStr.split(";");
            if (sInfo.length >= 4) {
                String id = sInfo[0];
                String clazz = sInfo[1];
                String lvl = sInfo[2];
                String date = sInfo[3];

                // Tabella del singolo slot — NESSUN padding qui, la dimensione la mettiamo sulla cella
                Table slotTable = new Table() {
                    @Override
                    public com.badlogic.gdx.scenes.scene2d.Actor hit(float x, float y, boolean touchable) {
                        // Ignoriamo i click nell'area trasparente "gigante" causata dal padding negativo
                        // Il padding è -375 (top/bottom) e -200 (left/right).
                        if (x < 200 || x > getWidth() - 200 || y < 350 || y > getHeight() - 350) {
                            return null;
                        }
                        return super.hit(x, y, touchable);
                    }
                };

                if (clazz.equals("Vuoto")) {
                    // Sfondo dello slot vuoto
                    slotTable.setBackground(createBackground(emptySlotPanelTex));
                    Label textLabel = new Label("Nuova Partita (Slot " + id + ")", skin);

                    slotTable.add(textLabel).expand().center();

                    slotTable.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);
                    slotTable.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            networkManager.sendCommand("NEW_GAME " + id);
                            statusLabel.setText("Avvio Nuova Partita nello Slot " + id + "...");
                        }
                    });

                    slotsContainer.add(slotTable).size(1500, 850).pad(-375,-200, -375, -200).row();

                } else {
                    // Sfondo dello slot occupato
                    slotTable.setBackground(createBackground(emptySlotPanelTex));

                    Image classImg = new Image(getClassTexture(clazz));
                    classImg.setScaling(com.badlogic.gdx.utils.Scaling.fit);

                    Table textTable = new Table();
                    Label nameLabel = new Label("Carica Slot " + id + " - " + clazz + " Lvl " + lvl, skin);
                    Label dateLabel = new Label("(" + date + ")", skin, "status");

                    nameLabel.setAlignment(Align.left);
                    dateLabel.setAlignment(Align.left);

                    textTable.add(nameLabel).expandX().fillX().row();
                    textTable.add(dateLabel).padTop(5).expandX().fillX();

                    TextButton.TextButtonStyle resetStyle = new TextButton.TextButtonStyle();
                    resetStyle.up = createBackground(resetButtonTex);
                    resetStyle.font = skin.getFont("default");
                    TextButton resetBtn = new TextButton("Reset", resetStyle);
                    resetBtn.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            event.stop(); // Previene conflitti con il listener dello slot
                            networkManager.sendCommand("RESET_SLOT " + id);
                            statusLabel.setText("Resettando lo slot " + id + "...");
                        }
                    });

                    // Layout interno dello slot
                    slotTable.add(classImg).size(80, 80).padLeft(75);
                    slotTable.add(textTable);
                    slotTable.add(resetBtn).size(280, 140);

                    slotTable.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);
                    slotTable.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            if (event.getTarget() != resetBtn && !resetBtn.isAscendantOf(event.getTarget())) {
                                networkManager.sendCommand("LOAD_GAME " + id);
                                statusLabel.setText("Caricamento in corso...");
                            }
                        }
                    });

                    slotsContainer.add(slotTable).size(1500, 850).pad(-375,-200, -375, -200).row();
                }
            }
        }

        rootTable.add(slotsContainer).width(900).padBottom(20).row();

        // Classifica
        Table leaderboardTable = new Table();
        leaderboardTable.setBackground(createBackground(parchmentTex));
        leaderboardTable.pad(40, 60, 40, 60);

        Label leaderboardLabel = new Label(topRunsInfo, skin, "black");
        leaderboardLabel.setAlignment(Align.center);
        leaderboardTable.add(leaderboardLabel).expand().center();

        rootTable.add(leaderboardTable).width(1200).height(900).padTop(-400).padBottom(-300).row();

        // Testo in basso
        statusLabel.setText("Scegli un'opzione per iniziare");
        rootTable.add(statusLabel).padBottom(10).row();
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
        uiBoxTex.dispose();
        warriorTex.dispose();
        archerTex.dispose();
        assassinTex.dispose();
        mageTex.dispose();
        tankTex.dispose();
        skin.dispose();
    }
}
