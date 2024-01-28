package networks.lab2;

import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicLong;

class SpeedCounter extends Thread {
    private static final long SPEED_OUTPUT_INTERVAL = 3000L;
    private static final int MS_IN_SEC = 1000;
    private static final int MB_IN_BYTE = 1024 * 1024;
    private final AtomicLong totalBytesReceived = new AtomicLong(0L);
    private final AtomicLong bytesReceived = new AtomicLong(0L);
    private volatile boolean isRunning;
    private long totalTime = 0L;
    private final InetAddress clientAddress;
    private final String fileName;
    private int countZeroSpeed = 0;

    SpeedCounter(InetAddress clientAddress, String fileName) {
        this.clientAddress = clientAddress;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        isRunning = true;
        long timeSpent = 0;
        try {
            long start = System.currentTimeMillis();
            while (true) {
                if (countZeroSpeed > 3) {
                    break;
                }
                long currentTime = System.currentTimeMillis();
                timeSpent += currentTime - start - totalTime;
                totalTime = currentTime - start;

                if (timeSpent >= SPEED_OUTPUT_INTERVAL) {
                    countAndPrintSpeed(timeSpent);
                    bytesReceived.set(0L);
                    timeSpent = 0;
                } else if (!isRunning) {
                    countAndPrintSpeed(timeSpent);
                    break;
                }
            }
        } catch (Exception ignored) {
        }
    }

    void stopRunning() {
        isRunning = false;
    }

    void changeTotalBytesReceived(long count) {
        totalBytesReceived.set(count);
    }

    void changeBytesReceived(long count) {
        bytesReceived.set(count + bytesReceived.get());
    }

    //Сделан вывод в MB
    private void countAndPrintSpeed(long timeSpent) {
        DecimalFormat df = new DecimalFormat("#.00000");
        double totalSpeed = ((double) totalBytesReceived.get() / MB_IN_BYTE) / totalTime * MS_IN_SEC;
        double currentSpeed = ((double) bytesReceived.get() / MB_IN_BYTE) / timeSpent * MS_IN_SEC;
        if (currentSpeed == 0) {
            countZeroSpeed++;
        } else {
            countZeroSpeed = 0;
        }
        System.out.println(fileName + " : client with ip: " + clientAddress.getHostAddress() +
                " | Total speed: " + df.format(totalSpeed) + " mb/sec" +
                " | Current speed: " + df.format(currentSpeed) + " mb/sec\n");
    }
}