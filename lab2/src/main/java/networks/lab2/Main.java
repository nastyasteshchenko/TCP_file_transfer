package networks.lab2;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("You need to choose the mode:\n" +
                "'s' - server mode\n" +
                "'c' - client mode\n");
        System.out.print("Enter mode: ");
        String mode = in.next();
        while (!mode.equals("s") && !mode.equals("c")) {
            System.out.println("Mode is not exist, try again: ");
            mode = in.next();
        }
        App app = new App(mode, in);
        app.startApp();
        in.close();
    }
}