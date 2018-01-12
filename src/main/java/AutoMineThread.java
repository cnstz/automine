import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class AutoMineThread extends Thread {
//    TODO INTERRUPTED_EXCEPTION
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            String CLAYMORE_ETH_MINER_PATH = getClass().getResource("claymore_eth_miner").getPath();
            String CLAYMORE_ETH_MINER_EXEC = CLAYMORE_ETH_MINER_PATH + "/start.bat";

            ProcessBuilder processBuilder = new ProcessBuilder(CLAYMORE_ETH_MINER_EXEC);
            processBuilder.directory(new File(CLAYMORE_ETH_MINER_PATH));
            processBuilder.redirectErrorStream(true);

            Process process;

            try {
                process = processBuilder.start();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while (true) {
                    line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
