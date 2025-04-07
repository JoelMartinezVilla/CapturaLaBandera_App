package io.github.capturdebander;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {

    private Stage stage;
    private Skin skin;

    public MainMenuScreen() {
        // Inicializamos el Stage con un viewport
        stage = new Stage(new ScreenViewport());
        // Establecemos el stage como procesador de entrada
        Gdx.input.setInputProcessor(stage);

        // Cargamos un Skin. Puedes usar el "uiskin.json" que viene en los ejemplos de LibGDX.
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Creamos una tabla para organizar los elementos de forma sencilla.
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Creamos el título y el botón
        Label titleLabel = new Label("Captura la bandera", skin);
        TextButton dummyButton = new TextButton("Botón", skin);

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
        // Limpiamos la pantalla con un color (en este caso negro)
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
