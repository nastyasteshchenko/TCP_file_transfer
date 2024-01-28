package networks.lab2;

class InputException extends  Exception{
    private InputException(String message) {
        super(message);
    }
    static InputException notValidIp() {
        return new InputException("Ip is not valid");
    }
    static InputException notValidPort() {
        return new InputException("Port is not valid");
    }
    static InputException notValidFile() {
        return new InputException("File is not valid");
    }
}
