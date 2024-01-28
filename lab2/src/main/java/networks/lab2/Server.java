package networks.lab2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class Server {
    private final ServerSocket serverSocket;
    private boolean isRunning;

    private Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    static Server create(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        return new Server(serverSocket);
    }

    //Несколько клиентов обрабатываются несколькими потоками, теперь потоков больше чем 3
    void start() throws IOException {
        isRunning = true;
        while (isRunning) {
            Socket clientSocket = serverSocket.accept();
            FileReceiver fileReceivers = new FileReceiver(clientSocket);
            fileReceivers.start();
        }
        serverSocket.close();
    }

    void stopRunning() {
        isRunning = false;
    }

}
