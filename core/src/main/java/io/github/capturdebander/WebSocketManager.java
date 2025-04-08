package io.github.capturdebander;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;

public class WebSocketManager {
    private WebSocket socket;
    private String address = "bandera4.ieti.site";
    private int port = 443;
    private boolean isConnected = false;

    public WebSocketManager() {
        System.out.println("Iniciando NetworkManager...");

        socket = WebSockets.newSocket(WebSockets.toSecureWebSocketUrl(address, port));

        socket.setSendGracefully(false);
        socket.addListener(new MyWebSocketListener());

        socket.connect();
    }

    public void sendData(String data) {
        if (isConnected && socket != null && socket.isOpen()) {
            socket.send(data);
            System.out.println("Datos enviados: " + data);
        } else {
            System.out.println("No conectado. No se pueden enviar datos.");
        }
    }

    public void disconnect() {
        if (socket != null && socket.isOpen()) {
            socket.close();
            System.out.println("Desconectado del servidor.");
        }
        isConnected = false;
    }

    public boolean isConnected() {
        return isConnected;
    }

    private class MyWebSocketListener implements WebSocketListener {
        @Override
        public boolean onOpen(WebSocket webSocket) {
            System.out.println("Conexión WebSocket abierta.");
            isConnected = true;
            socket.send("Hola servidor desde MiniFlag!");
            return true;
        }

        @Override
        public boolean onClose(WebSocket webSocket, int closeCode, String reason) {
            System.out.println("Conexión cerrada. Código: " + closeCode + ", Razón: " + reason);
            isConnected = false;
            return true;
        }

        @Override
        public boolean onMessage(WebSocket webSocket, String packet) {
            System.out.println("Mensaje recibido: " + packet);
            return true;
        }

        @Override
        public boolean onMessage(WebSocket webSocket, byte[] packet) {
            System.out.println("Mensaje recibido (bytes). Longitud: " + packet.length);
            return true;
        }

        @Override
        public boolean onError(WebSocket webSocket, Throwable error) {
            System.out.println("ERROR en WebSocket: " + error.getMessage());
            return true;
        }
    }
}
