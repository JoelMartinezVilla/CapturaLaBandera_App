package io.github.capturdebander;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private Game game; // Referencia al juego principal

    public MainMenuScreen(Game game) {
        this.game = game;
        // Inicializamos el Stage con un viewport
        stage = new Stage(new ScreenViewport());
        // Establecemos el stage como procesador de entrada
        Gdx.input.setInputProcessor(stage);

        // Cargamos el Skin
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Creamos una tabla para organizar los elementos
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Creamos el título y el botón
        Label titleLabel = new Label("Captura la bandera", skin);
        TextButton dummyButton = new TextButton("Ir a GameScreen", skin);

        // Agregamos un listener para detectar el clic en el botón
        dummyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Al pulsar el botón, cambiamos la pantalla a GameScreen
                game.setScreen(new GameScreen());
            }
        });

        // Agregamos los elementos a la tabla
        table.add(titleLabel).padBottom(20);
        table.row();
        table.add(dummyButton);

        // Añadimos la tabla al stage
        stage.addActor(table);
    }

    @Override
    public void show() {
        // Se llama cuando la pantalla se hace visible.
    }

    @Override
    public void render(float delta) {
        // Limpiamos la pantalla con un color negro
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualizamos y dibujamos el stage
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // Acciones al pausar la pantalla
    }

    @Override
    public void resume() {
        // Acciones al reanudar la pantalla
    }

    @Override
    public void hide() {
        // Se llama cuando la pantalla deja de ser visible.
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
