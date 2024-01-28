package networks.lab2;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

//Сделан внешним
class FileReceiver extends Thread {
    private static final Path PATH_TO_SAVE = Path.of("./uploads");
    private static final int MAX_AMOUNT_OF_BYTES = 1024 * 4;
    private static final long MAX_FILE_SIZE = 1024L * 1024L * 1024L * 1024L;
    private static final long MAX_FILENAME_SIZE = 4096L;
    private final Socket clientSocket;

    FileReceiver(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    //Сделана проверка на превышение размеров
    public void run() {
        try {
            if (Files.notExists(PATH_TO_SAVE)) {
                Files.createDirectory(PATH_TO_SAVE);
            }

            try (OutputStream outputStream = clientSocket.getOutputStream();
                 InputStream inputStream = clientSocket.getInputStream()) {

                int fileNameSize = recvFileNameSize(inputStream);

                long fileSize = recvFileSize(inputStream);

                String fileName = recvFileName(fileNameSize, inputStream);

                Path newFile = createFileToReceiveIn(fileName);

                if (fileSize > MAX_FILE_SIZE || fileNameSize > MAX_FILENAME_SIZE) {
                    notifyClient(outputStream, "n");
                } else {
                    notifyClient(outputStream, "y");
                }

                if (recvFile(newFile, fileSize, inputStream, fileName)) {
                    notifyClient(outputStream, "s");
                } else {
                    notifyClient(outputStream, "f");
                }

                System.out.println("Receiving '" + fileName + "' from " +
                        clientSocket.getInetAddress().getHostAddress() + " ended");
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private boolean recvFile(Path newFile, long fileSize, InputStream inputStream, String fileName) throws IOException {
        SpeedCounter speedCounter = new SpeedCounter(clientSocket.getInetAddress(), fileName);

        try (OutputStream output = Files.newOutputStream(newFile)) {

            byte[] buf = new byte[MAX_AMOUNT_OF_BYTES];
            long length = 0;
            int count;
            speedCounter.start();
            int countErrorsWithConnection = 0;
            while (length != fileSize) {
                if ((count = inputStream.read(buf)) == -1) {
                    break;
                }
                if (count == 0) {
                    countErrorsWithConnection++;
                }

                if (countErrorsWithConnection > 3) {
                    break;
                }

                speedCounter.changeBytesReceived(count);
                length += count;
                speedCounter.changeTotalBytesReceived(length);
                output.write(buf, 0, count);
            }
            speedCounter.stopRunning();
            speedCounter.join();
            return fileSize == length;
        } catch (InterruptedException ignored) {

        }
        return false;
    }

    private void notifyClient(OutputStream outputStream, String message) throws IOException {
        byte[] toSend = message.getBytes(StandardCharsets.UTF_8);
        outputStream.write(toSend);
    }

    private Path createFileToReceiveIn(String fileName) throws IOException {
        Path newFile = Path.of(PATH_TO_SAVE.toString(), fileName);
        if (Files.exists(newFile)) {
            newFile = Path.of(PATH_TO_SAVE.toString(), fileName + Math.random());
        }
        Files.createFile(newFile);
        return newFile;
    }

    private String recvFileName(int fileNameSize, InputStream inputStream) throws IOException {
        byte[] fileNameInBytes = new byte[fileNameSize];
        int length = 0;
        while (length != fileNameSize) {
            length += inputStream.read(fileNameInBytes, length, fileNameSize - length);
        }
        return new String(fileNameInBytes, StandardCharsets.UTF_8);
    }

    private long recvFileSize(InputStream inputStream) throws IOException {
        byte[] fileSizeInBytes = new byte[Long.BYTES];
        int length = 0;
        while (length != Long.BYTES) {
            length += inputStream.read(fileSizeInBytes, length, Long.BYTES - length);
        }
        return Longs.fromByteArray(fileSizeInBytes);
    }

    private int recvFileNameSize(InputStream inputStream) throws IOException {
        byte[] fileNameSizeInBytes = new byte[Integer.BYTES];
        int length = 0;
        while (length != Integer.BYTES) {
            length += inputStream.read(fileNameSizeInBytes, length, Integer.BYTES - length);
        }
        return Ints.fromByteArray(fileNameSizeInBytes);
    }
}