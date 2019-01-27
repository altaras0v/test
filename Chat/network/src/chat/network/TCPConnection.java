package chat.network;

import java.io.*;
import java.net.Socket;

public class TCPConnection {
    private final Socket socket;
    private final Thread rxThread;
    private final BufferedReader in;
    private final BufferedWriter out;

    private final TCPConnectionListener eventListener;


    // сокет создают внутри по айпи и порту
    public TCPConnection(TCPConnectionListener eventListener, String ipAdr,int port) throws IOException{
         this(eventListener, new Socket(ipAdr, port));
    }

        // cокет создают снаружи
    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException {  // примет объект сокета и с этим сокетом создаст соединение
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader( new InputStreamReader((socket.getInputStream()))); // получили простой поток ввода
                                                                                     // на его основе создали ISR, который может делать более сложные вещи
                                                                                    //и на основе него создали экземляр класса BR, который умеет читать и писать строчки
        out = new BufferedWriter((new OutputStreamWriter(socket.getOutputStream())));
                // cоздаём поток, который будет слушать всё входящее
        rxThread =  new Thread(new Runnable() {  // cкрытый класс
            @Override
            public void run() { // слушать вхожящее соеденение
                try {
                    eventListener.onConnectionReady( TCPConnection.this);
                    while (!rxThread.isInterrupted()){ // пока поток не прерван, получаем строчку и отдаём ее ЕL
                        String mag = in.readLine();
                        eventListener.onReciveString(TCPConnection.this, in.readLine());
                    }

                } catch (IOException e){
                        eventListener.onException(TCPConnection.this,e);

                } finally {  // когда клиенту приходит сообщение - оно у него в окне, когда не серевер, то у всех
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThread.start();  // чтобы поток заработал, нужно ему передать экземляр класса от Runnble
        }

        // synchronized - чтобы безопасно обращаться к ним с разных потоков
        public synchronized void sendString(String value)// cпрашивает строчку для отправления
        {
                try {
                    out.write(value+"\r\n"); // чтобы знать, когда строка закончится , добавляем r и n
                    out.flush();// строка может сохраниться в буффере но не передаться, поэтому исп flush
                } catch (IOException e){
                    eventListener.onException(TCPConnection.this , e); // оповещаем листенер
                    disconect();
                }
        }
        public synchronized void disconect(){
            rxThread.interrupt();
            try {
                socket.close();
            } catch (IOException e){
                eventListener.onException(TCPConnection.this, e);
            }
        }

    @Override
    public String toString() { // для логов
        return "TCPCOnnection: " + socket.getInetAddress() + ": "+socket.getPort();
    }
}
