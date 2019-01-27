package chat.server;

import chat.network.TCPConnection;
import chat.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener { // одновременно и чат сервер и TCL
    public static void main(String[] args) {

        new ChatServer();

    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer(){
        System.out.println("Server running");
       try (ServerSocket serverSocket = new ServerSocket(8081)) // слушает порт и принимает входящее соединение
       {
            while (true){
                try{
                    new TCPConnection(this,serverSocket.accept()); // ждет соединения и когда соед уст, то возвращает объект сокета
                } catch (IOException e){ // передаём этот объект в конструктор тсп коннектион и создаем его экземпляр
                    System.out.println("Connection exception");
                }
            }
    }catch (IOException e){
           throw new RuntimeException(e);
       }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendAllCoonections("Client connected: "+ tcpConnection);
    }

    @Override
    public synchronized void onReciveString(TCPConnection tcpConnection, String value) {
            sendAllCoonections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
            connections.remove(tcpConnection);
        sendAllCoonections("Client disconnected: "+ tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("Exception");
    }

    private  void sendAllCoonections(String value){
        System.out.println(value);
        for(int i=0;i<connections.size();i++){
            connections.get(i).sendString(value);
        }
    }
}
