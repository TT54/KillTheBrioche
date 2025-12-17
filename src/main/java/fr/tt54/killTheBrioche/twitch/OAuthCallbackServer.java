package fr.tt54.killTheBrioche.twitch;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class OAuthCallbackServer {

    private static HttpServer server;

    public static void launchServer(){
        new Thread(() -> {
            try {
                runCallbackServer();
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    public static void stopServer(){
        if(server != null){
            server.stop(1);
            server = null;
        }
    }

    private static void runCallbackServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/callback", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            System.out.println("Callback reçu : " + query);

            try {
                String code = query.split("code=")[1].split("&")[0];
                TwitchBridge.instance.retrieveTwitchToken(code);
            } catch (Exception e){
                e.printStackTrace();
            }

            String response = "Autorisation réussie ! Tu peux fermer cette page.";
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        });

        server.start();
        System.out.println("Serveur OAuth en écoute sur http://localhost:8080/callback");
    }
}