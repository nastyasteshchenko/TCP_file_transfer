package networks.lab2;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;

class CheckingInputUtils {
    private static final int MIN_USER_PORT = 1024;
    private static final int MAX_USER_PORT = 49151;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    static void checkValidIp(String address) throws InputException {
        try {
            InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            throw InputException.notValidIp();
        }
    }

    static void checkValidPort(int port) throws InputException {
        if (port < MIN_USER_PORT || port > MAX_USER_PORT) {
            throw InputException.notValidPort();
        }
    }

    static void checkValidPath(Path path) throws InputException {
        if (Files.notExists(path) || !Files.isRegularFile(path)) {
            throw InputException.notValidFile();
        }
    }
}
