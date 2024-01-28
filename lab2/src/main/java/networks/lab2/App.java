package networks.lab2;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.Scanner;

class App {

    private final String mode;
    private final Scanner input;

    App(String mode, Scanner input) {
        this.mode = mode;
        this.input = input;
    }

    void startApp() {
        try {
            switch (mode) {
                case "s" -> startServer();
                case "c" -> startClient();
            }
        } catch (Exception e) {
            input.close();
            System.err.println(e.getMessage());
        }
    }

    private void startClient() throws IOException, InputException {
        System.out.print("Enter server port: ");
        int port = Integer.parseInt(input.next());
        CheckingInputUtils.checkValidPort(port);

        System.out.print("Enter server ip: ");
        String address = input.next();
        CheckingInputUtils.checkValidIp(address);
        InetAddress serverAddress = InetAddress.getByName(address);

        System.out.print("Enter path to file to send: ");
        Path path = Path.of(input.next());
        CheckingInputUtils.checkValidPath(path);

        Client client = Client.create(port, serverAddress, path);
        client.start();
    }

    private void startServer() throws IOException, InputException {
        System.out.print("Enter port: ");
        int port = Integer.parseInt(input.next());
        CheckingInputUtils.checkValidPort(port);

        Server server = Server.create(port);
        server.start();
    }
}
