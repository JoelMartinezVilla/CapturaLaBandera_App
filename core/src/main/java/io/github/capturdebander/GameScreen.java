package io.github.capturdebander;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen implements Screen {
    private WebSocketManager webSocketManager;
    private SpriteBatch batch;
    private BitmapFont font;

    public GameScreen() {
        // Instanciamos el WebSocketManager. En su constructor se conecta automáticamente.
        webSocketManager = new WebSocketManager();
        batch = new SpriteBatch();
        font = new BitmapFont();
    }

    @Override
    public void show() {
        // Si deseas probar la conexión HTTP, puedes invocar el método:
        // webSocketManager.testHttpConnection();
    }

    @Override
    public void render(float delta) {
        // Limpiar la pantalla con color negro
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Iniciar el batch para dibujar
        batch.begin();

        // Verificamos el estado de conexión y definimos el mensaje a mostrar
        String estadoConexion = webSocketManager.isConnected() ? "Conexión establecida" : "Sin conexión";
        font.draw(batch, estadoConexion, 20, Gdx.graphics.getHeight() - 20);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Ajustes en el cambio de tamaño, si son necesarios
    }

    @Override
    public void pause() {
        // Acciones cuando se pausa la aplicación
    }

    @Override
    public void resume() {
        // Acciones cuando se reanuda la aplicación
    }

    @Override
    public void hide() {
        // Acciones cuando la pantalla es oculta
    }

    @Override
    public void dispose() {
        // Liberar recursos gráficos
        batch.dispose();
        font.dispose();
    }
}
