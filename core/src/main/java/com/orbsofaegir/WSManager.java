package com.orbsofaegir;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;

public class WSManager {
    // Dirección del host y puerto
    private String address = "bandera4.ieti.site";
    private int port = 443;
    private WebSocket socket;
    private boolean isConnected = false;

    public JsonValue gameState;
    public String playerId;

    private static WSManager instance;

    private WSManager() {

        String wsUrl = "wss://" + address + "/test";

        // Socket creation
        socket = WebSockets.newSocket(wsUrl);

        socket.setSendGracefully(false);
        socket.addListener(new WSListener());

        socket.connect();
    }

    public static WSManager getInstance() {
        if(instance == null) {
            instance = new WSManager();
        }
        return instance;
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

    public void testHttpConnection() {
        System.out.println("Intentando conexión HTTP...");
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        String fullUrl = "https://" + address + ":" + port + "/test";

        Net.HttpRequest request = requestBuilder.newRequest()
            .method(Net.HttpMethods.GET)
            .url(fullUrl)
            .build();

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String response = httpResponse.getResultAsString();
                System.out.println("Respuesta del servidor HTTP: " + response);
            }

            @Override
            public void failed(Throwable t) {
                System.out.println("Error al conectar con el servidor HTTP: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                System.out.println("Petición HTTP cancelada.");
            }
        });
    }

    // WS Handler
    private class WSListener implements WebSocketListener {
        @Override
        public boolean onOpen(WebSocket webSocket) {
            isConnected = true;
            Gdx.app.log("WS", "Conexión WebSocket abierta.");
            return true;
        }

        @Override
        public boolean onClose(WebSocket webSocket, int closeCode, String reason) {
            isConnected = false;
            return true;
        }

        @Override
        public boolean onMessage(WebSocket webSocket, String packet) {
            JsonReader reader = new JsonReader();
            JsonValue response = reader.parse(packet);

            if(response.has("type")) {
                if(response.getString("type").equals("update")) {
                    if(response.has("gameState")) {
                        gameState = response.get("gameState");
                    }
                }else if(response.getString("type").equals("welcome")) {
                    playerId = response.getString("id");
                    Gdx.app.log("ID", playerId);
                }
            }

            return true;
        }

        @Override
        public boolean onMessage(WebSocket webSocket, byte[] packet) {
            return true;
        }

        @Override
        public boolean onError(WebSocket webSocket, Throwable error) {
            return true;
        }
    }
}
