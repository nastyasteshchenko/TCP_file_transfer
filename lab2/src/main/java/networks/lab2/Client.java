package networks.lab2;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

class Client {
    //подумать какой правильный размер нужен
    private static final int MAX_AMOUNT_OF_BYTES = 1460;
    private final Socket clientSocket;
    private final Path pathToSendingFile;

    private Client(Socket clientSocket, Path pathToFile) {
        this.clientSocket = clientSocket;
        this.pathToSendingFile = pathToFile;
    }

    static Client create(int serverPort, InetAddress serverAddress, Path pathToFile) throws IOException {
        Socket clientSocket = new Socket(serverAddress, serverPort);
        return new Client(clientSocket, pathToFile);
    }

    //Надо было подумать, что будет, если сервер умрет\будет занят другим клиентом
    void start() throws IOException {

        try (OutputStream outputStream = clientSocket.getOutputStream();
             InputStream inputStream = clientSocket.getInputStream()) {

            sendInfoAboutSendingFile(outputStream);

            if (!isServerAgreeToRecvFile(inputStream)) {
                System.out.println("Receiving file: failed");
                clientSocket.close();
                return;
            }

            sendFile(outputStream);

            waitResultFromServer(inputStream);
        }

        clientSocket.close();
    }

    private void sendInfoAboutSendingFile(OutputStream outputStream) throws IOException {
        byte[] buf = createInfoAboutFileToSend();
        outputStream.write(buf);
    }

    private byte[] createInfoAboutFileToSend() throws IOException {
        long fileSize = Files.size(pathToSendingFile);
        byte[] fileSizeInBytes = Longs.toByteArray(fileSize);

        String fileName = pathToSendingFile.getFileName().toString();
        byte[] fileNameSizeInBytes = Ints.toByteArray(fileName.length());

        byte[] fileNameInBytes = fileName.getBytes(StandardCharsets.UTF_8);

        return Bytes.concat(fileNameSizeInBytes, fileSizeInBytes, fileNameInBytes);
    }

    private void sendFile(OutputStream outputStream) throws IOException {
        try (InputStream i = Files.newInputStream(pathToSendingFile)) {
            int length;
            byte[] buf = new byte[MAX_AMOUNT_OF_BYTES];
            while ((length = i.read(buf)) != -1) {
                outputStream.write(buf, 0, length);
            }
        }
    }

    private void waitResultFromServer(InputStream inputStream) throws IOException {
        while (true) {
            byte[] answer = new byte[1];
            int length = 0;
            while (length != 1) {
                length += inputStream.read(answer, length, 1 - length);
            }
            if (new String(answer, StandardCharsets.UTF_8).equals("f")) {
                System.out.println("Receiving file: failed");
                break;
            } else if (new String(answer, StandardCharsets.UTF_8).equals("s")) {
                System.out.println("Receiving file: success");
                break;
            }
        }
    }

    private boolean isServerAgreeToRecvFile(InputStream inputStream) throws IOException {
        while (true) {
            byte[] answer = new byte[1];
            int length = 0;
            while (length != 1) {
                length += inputStream.read(answer, length, 1 - length);
            }
            if (new String(answer, StandardCharsets.UTF_8).equals("y")) {
                return true;
            } else if (new String(answer, StandardCharsets.UTF_8).equals("n")) {
                return false;
            }
        }
    }
}
