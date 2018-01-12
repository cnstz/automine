import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GpuThread extends Thread {
    private final AutoMineThread autoMineThread;

    public GpuThread(AutoMineThread autoMineThread) {
        this.autoMineThread = autoMineThread;
    }

    public void run() {
        ProcessBuilder processBuilder = new ProcessBuilder(Scheduler.CMD_COMMAND, "/c", Scheduler.GPU_TEMP_COMMAND);
        processBuilder.redirectErrorStream(true);
        Process process = null;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                line = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (line == null) {
                break;
            }
            double gpuTemp = Double.parseDouble(line);
            if (gpuTemp >= 60 && autoMineThread.isAlive()){
                autoMineThread.interrupt();}
            else if(gpuTemp < 60 && (autoMineThread.isInterrupted() || !autoMineThread.isAlive()))
                autoMineThread.start();
            try {
                // TODO SLEEP FOR 300000 ms (5 minutes).
                Thread.sleep(1000);
            } catch (Exception ex) {
            }
        }
    }
}
