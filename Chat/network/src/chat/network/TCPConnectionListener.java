package chat.network;

public interface TCPConnectionListener {

    void onConnectionReady(TCPConnection tcpConnection); // готовое соединение
    void onReciveString(TCPConnection tcpConnection , String value); // принимает строчку
    void onDisconnect(TCPConnection tcpConnection);
    void onException(TCPConnection tcpConnection,Exception e);




}
