package com.orbsofaegir;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class GameScreen implements Screen {
    private final Game game;
    private final WSManager conn;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private Stage stage;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private Texture backgroundTexture;

    private ArrayList<Texture> idleCharacters;
    private ArrayList<Texture> runCharacters;
    private Animation<TextureRegion>[][] idleAnimations;
    private Animation<TextureRegion>[][] runAnimations;

    Texture orbTexture;
    Animation<TextureRegion> orbAnimation;

    Rectangle up;
    Rectangle down;
    Rectangle left;
    Rectangle right;

    private float playerSize = 150f;
    private float orbSize = 70f;

    private float stateTime = 0;

    private final String[] COLORS = {"blue", "red", "green", "yellow"};
    private final String[] DIRECTIONS = {"down", "right", "up", "left"};
    private final float FRAME_DURATION = 0.1f;
    private final float SCREEN_WIDTH = 1920f;
    private final float SCREEN_HEIGHT = 1080f;
    private final float WORLD_WIDTH = 3000f;
    private final float WORLD_HEIGHT = 3000f;

    public GameScreen(Game game) {
        this.game = game;

        // Prepare camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera);
        viewport.apply();

        // Conecta al servidor mediante WebSocket y asigna este objeto como listener
        conn = WSManager.getInstance();
        up = new Rectangle(0, Gdx.graphics.getHeight() * 2f / 3f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 3f);
        down = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 3f);
        left = new Rectangle(0, 0, Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight());
        right = new Rectangle(Gdx.graphics.getWidth() * 2f / 3f, 0, Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
        // Characters load
        idleCharacters = new ArrayList<>();
        runCharacters = new ArrayList<>();
        idleAnimations = new Animation[4][4];
        runAnimations = new Animation[4][4];
        for (int i = 0; i < COLORS.length; i++) {
            String color = COLORS[i];

            // Carga las texturas de sprites
            Texture idleTexture = new Texture("game_assets/sprites/spritesheet_idle_" + color + ".png");
            Texture runTexture = new Texture("game_assets/sprites/spritesheet_run_" + color + ".png");

            idleCharacters.add(idleTexture);
            runCharacters.add(runTexture);

            TextureRegion[][] idleTmp = TextureRegion.split(idleTexture, 32, 32);
            TextureRegion[][] runTmp = TextureRegion.split(runTexture, 32, 32);

            for (int j = 0; j < 4; j++) { // Cada dirección
                TextureRegion[] idleFrames = new TextureRegion[8];
                TextureRegion[] runFrames = new TextureRegion[8];

                for (int k = 0; k < 8; k++) { // Cada frame de animación
                    idleFrames[k] = idleTmp[j][k];
                    runFrames[k] = runTmp[j][k];
                }

                idleAnimations[i][j] = new Animation<>(FRAME_DURATION, idleFrames);
                runAnimations[i][j] = new Animation<>(FRAME_DURATION, runFrames);
            }
        }

        // Orb load
        orbTexture = new Texture("game_assets/items/orb.png");
        TextureRegion[][] orbTmp = TextureRegion.split(orbTexture, 16, 16);
        // Gdx.app.log("ORBTMP", String.valueOf(orbTmp.length));
        TextureRegion[] orbFrames = new TextureRegion[28];
        for(int i = 0; i < 28; i++) {
            orbFrames[i] = orbTmp[0][i];
        }
        orbAnimation = new Animation<>(FRAME_DURATION, orbFrames);

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer = new ShapeRenderer();
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

        // Cargar Skin para la interfaz
        FileHandle f = Gdx.files.internal("uiskin.json");
        Skin skin = new Skin(f);

        // Cargar el background
        backgroundTexture = new Texture("game_assets/backgrounds/background.png");

        font = new BitmapFont();

        // Configurar botón "Menu" para regresar al MainMenuScreen
        TextButton exitButton = new TextButton("Menu", skin);
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
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        stateTime += Gdx.graphics.getDeltaTime();

        gameLogic();

        draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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

        batch.begin();

        batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        // DRAW ORB
        if(conn.gameState.has("flagPos")) {
            float orbX = conn.gameState.get("flagPos").getFloat("dx") * Gdx.graphics.getWidth();
            float orbY = conn.gameState.get("flagPos").getFloat("dy") * Gdx.graphics.getHeight();

            TextureRegion currentFrame = orbAnimation.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, orbX, orbY, orbSize, orbSize);
        }

        // DRAW PLAYERS
        if (conn.gameState.has("players")) {
            JsonValue players = conn.gameState.get("players");
            for(int i = 0; i < players.size; i++) {
                JsonValue player = players.get(i);
                drawPlayer(player, COLORS[i]);
            }

            shapeRenderer.end();
        }

        batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    private void drawPlayer(JsonValue player, String color) {
        if(!player.has("direction") || !player.has("moving")) {
            return;
        }
        float playerX = player.getFloat("x") * Gdx.graphics.getWidth();
        float playerY = (1f - player.getFloat("y")) * Gdx.graphics.getHeight();

        camera.position.set(playerX, playerY, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        String direction = player.getString("direction");
        boolean moving = player.getBoolean("moving");
        TextureRegion currentFrame = null;
        for(int i = 0; i < COLORS.length; i++) {
            for(int j = 0; j < DIRECTIONS.length; j++) {
                if(DIRECTIONS[j].equals(direction) && COLORS[i].equals(color)) {
                    if(moving) {
                        currentFrame = runAnimations[i][j].getKeyFrame(stateTime, true);
                    }else {
                        currentFrame = idleAnimations[i][j].getKeyFrame(stateTime, true);
                    }
                }
            }
        }
        if(currentFrame == null) {
            return;
        }
        batch.draw(currentFrame, playerX, playerY, playerSize, playerSize);

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
