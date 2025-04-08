package com.orbsofaegir;

import com.badlogic.gdx.Game;

public class MainGame extends Game {
    @Override
    public void create() {
        this.setScreen(new MenuScreen(this));
    }

    public void startMenu() {
        this.setScreen(new MenuScreen(this));
    }

    public void startGame() {
        this.setScreen(new GameScreen(this));
    }
}
