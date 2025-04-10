package com.orbsofaegir;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.graphics.Color;

public class MenuScreen implements Screen {
    private final MainGame game;
    private WSManager conn;
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private Stage stage;
    private Skin skin;
    private Label playersLabel;
    private Label.LabelStyle labelStyle;
    private Texture orbTexture;

    public MenuScreen(MainGame game) {

        this.game = game;
        conn = WSManager.getInstance();
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/VT323.ttf"));

        // Fuente titulo
        FreeTypeFontGenerator.FreeTypeFontParameter titleParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParam.size = (int) (Gdx.graphics.getWidth() * 0.08f);
        titleFont = generator.generateFont(titleParam);

        // Fuente boton
        FreeTypeFontGenerator.FreeTypeFontParameter buttonParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        buttonParam.size = (int) (Gdx.graphics.getWidth() * 0.025f);
        BitmapFont buttonFont = generator.generateFont(buttonParam);

        generator.dispose();

        // Stage y Skin
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("skin/flat-earth-ui.json"));

        // Estilo de botón personalizado
        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.up = skin.newDrawable("button-c", new Color(0x8e62fcFF));
        buttonStyle.down = skin.newDrawable("button-p", new Color(0x4923a8FF));
        buttonStyle.over = skin.newDrawable("button-h", new Color(0x4923a8FF));
        buttonStyle.font = buttonFont;
        buttonStyle.fontColor = Color.WHITE;

        labelStyle = new Label.LabelStyle();
        labelStyle.font = buttonFont;
        labelStyle.fontColor = Color.WHITE;


        // Label con número de jugadores
        if(conn.gameState != null) {
            playersLabel = new Label("Players: "+conn.gameState.get("players").size, labelStyle);
        }
        else {
            playersLabel = new Label("Connecting...", labelStyle);
        }


        // Posición del label (encima del botón)
        float labelWidth = Gdx.graphics.getWidth() * 0.4f;
        float labelHeight = Gdx.graphics.getHeight() * 0.05f;
        playersLabel.setSize(labelWidth, labelHeight);
        playersLabel.setPosition(
            Gdx.graphics.getWidth() * 0.07f,
            Gdx.graphics.getHeight() * 0.7f
        );
        stage.addActor(playersLabel);

        // Boton Start Game
        TextButton startButton = new TextButton("Start Game", buttonStyle);

        float buttonWidth = Gdx.graphics.getWidth() * 0.2f;
        float buttonHeight = Gdx.graphics.getHeight() * 0.15f;
        startButton.setSize(buttonWidth, buttonHeight);

        startButton.setPosition(
            Gdx.graphics.getWidth() * 0.07f,
            Gdx.graphics.getHeight() * 0.1f
        );

        startButton.getLabel().setWrap(true);
        startButton.getLabel().setFontScale(1.0f);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                conn.testHttpConnection();
                game.setScreen(new GameScreen(game));
            }
        });

        stage.addActor(startButton);

        orbTexture = new Texture(Gdx.files.internal("game_assets/items/orb.png"));

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        batch.begin();

        if (conn.gameState != null && conn.gameState.has("players")) {
            int numPlayers = conn.gameState.get("players").size; // Asegúrate que 'players' sea un array
            playersLabel.setText("Players: " + numPlayers);
        } else {
            playersLabel.setText("Connecting...");
        }

        String title = "Orbs of Aegir";

        // Título más a la izquierda y adaptado al tamaño de pantalla
        float x = Gdx.graphics.getWidth() * 0.07f;
        float y = Gdx.graphics.getHeight() * 0.9f;
        titleFont.draw(batch, title, x, y);

        TextureRegion orbRegion = new TextureRegion(orbTexture, 0, 0, 16, orbTexture.getHeight());
        batch.draw(
            orbRegion,
            Gdx.graphics.getWidth()*0.6f,
            Gdx.graphics.getHeight() / 2f,
            400,
            400
        );

        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }


    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        titleFont.dispose();
        stage.dispose();
        skin.dispose();
    }
}
