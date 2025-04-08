package io.github.capturdebander;

import com.badlogic.gdx.Game;

public class Main extends Game {

    @Override
    public void create() {
        // Se pasa "this" para poder cambiar de pantalla desde MainMenuScreen
        setScreen(new MainMenuScreen(this));
    }
}
