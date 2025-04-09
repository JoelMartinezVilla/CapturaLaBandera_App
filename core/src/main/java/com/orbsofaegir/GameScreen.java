package com.orbsofaegir;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.Json;

import java.util.HashMap;
import java.util.Map;

public class GameScreen implements Screen {
    private Game game;
    private WSManager conn;
    private Stage stage;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private TextButton exitButton;
    private Texture backgroundTexture;


    Texture orbTexture;

    Rectangle up;
    Rectangle down;
    Rectangle left;
    Rectangle right;

    // Propiedades del "player" (cuadrado)
    private float playerSize = 50f;

    // Propiedades del item (opcional)
    private float itemX, itemY;
    private float orbSize = 100f;

    public GameScreen(Game game) {
        this.game = game;
        // Conecta al servidor mediante WebSocket y asigna este objeto como listener
        conn = WSManager.getInstance();
        up = new Rectangle(0, Gdx.graphics.getHeight() * 2f / 3f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 3f);
        down = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 3f);
        left = new Rectangle(0, 0, Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight());
        right = new Rectangle(Gdx.graphics.getWidth() * 2f / 3f, 0, Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
        orbTexture = new Texture("game_assets/items/orb.png");

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Cargar Skin para la interfaz
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        // Cargar el background
        backgroundTexture = new Texture("game_assets/backgrounds/background.png");
        
        font = new BitmapFont();

        // Configurar botón "Menu" para regresar al MainMenuScreen
        exitButton = new TextButton("Menu", skin);
        exitButton.setPosition(Gdx.graphics.getWidth() * 0.9f, Gdx.graphics.getHeight() * 0.9f);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MenuScreen((MainGame)game));
            }
        });
        stage.addActor(exitButton);

    }

    @Override
    public void render(float delta) {
        // Limpiar pantalla con un color de fondo oscuro
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        gameLogic();

        draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
        if (stage != null) stage.dispose();
    }

    private void gameLogic() {
        String direction = virtualJoystickControl();
        conn.sendData("{\"type\":\"direction\", \"value\":\""+direction+"\"}");

    }

    private void draw() {
        if(conn.gameState == null) {
            return;
        }

        // Iniciar SpriteBatch para el fondo y las texturas
        batch.begin();

        // Dibuja el fondo primero
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Dibuja el orbe si existe
        if(conn.gameState.has("flagPos")) {
            TextureRegion orbRegion = new TextureRegion(orbTexture, 0, 0, 16, orbTexture.getHeight());

            float orbX = conn.gameState.get("flagPos").getFloat("dx") * Gdx.graphics.getWidth();
            float orbY = conn.gameState.get("flagPos").getFloat("dy") * Gdx.graphics.getHeight();
            batch.draw(orbRegion, orbX, orbY, orbSize, orbSize);
        }

        String connectionStatus = conn.isConnected() ? "Connected" : "NO CONNECTION STABLISHED";
        font.draw(batch, connectionStatus, 20, Gdx.graphics.getHeight() - 20);

        batch.end();

        // Dibuja los jugadores con ShapeRenderer
        if (conn.gameState.has("players")) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.BLUE);

            for (JsonValue player : conn.gameState.get("players")) {
                float playerX = player.getFloat("x") * Gdx.graphics.getWidth();
                float playerY = (1f - player.getFloat("y")) * Gdx.graphics.getHeight();
                shapeRenderer.rect(playerX, playerY, playerSize, playerSize);
            }

            shapeRenderer.end();
        }

        // Dibujar UI por encima
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }


    protected String virtualJoystickControl() {
        for (int i = 0; i < 10; i++) {
            if (Gdx.input.isTouched(i)) {
                Vector3 touchPos = new Vector3();
                touchPos.set(Gdx.input.getX(i), Gdx.input.getY(i), 0);

                touchPos.y = Gdx.graphics.getHeight() - touchPos.y;

                if (up.contains(touchPos.x, touchPos.y)) return "up";
                if (down.contains(touchPos.x, touchPos.y)) return "down";
                if (left.contains(touchPos.x, touchPos.y)) return "left";
                if (right.contains(touchPos.x, touchPos.y)) return "right";
            }
        }
        return "none";
    }
}
