import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    static String CMD_COMMAND = "cmd.exe";
    static String GPU_TEMP_COMMAND = "nvidia-smi --query-gpu=temperature.gpu --format=csv,noheader -l 5";

    public static void main(String[] args) throws IOException, InterruptedException {

        List<Thread> threads = new ArrayList<Thread>();

        AutoMineThread autoMineThread = new AutoMineThread();
        GpuThread gpuThread = new GpuThread(autoMineThread);
        IdleChecktThread idleChecktThread = new IdleChecktThread(gpuThread);

        threads.add(idleChecktThread);
        threads.add(gpuThread);
        threads.add(autoMineThread);

        idleChecktThread.start();

        for (int i = 0; i < threads.size(); i++) {
            threads.get(i).join();
        }
    }
}
