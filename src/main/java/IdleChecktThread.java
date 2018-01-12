import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class IdleChecktThread extends Thread {
    private final GpuThread gpuThread;

    public IdleChecktThread(GpuThread gpuThread) {
        this.gpuThread = gpuThread;
    }

    public interface Kernel32 extends StdCallLibrary {
        Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);

        int GetTickCount();
    }

    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);

        class LASTINPUTINFO extends Structure {
            public int cbSize = 8;
            public int dwTime;

            protected List<String> getFieldOrder() {
                return Arrays.asList(new String[]{"cbSize", "dwTime"});
            }
        }

        boolean GetLastInputInfo(LASTINPUTINFO result);
    }


    public static int getIdleTimeMillisWin32() {
        User32.LASTINPUTINFO lastInputInfo = new User32.LASTINPUTINFO();
        User32.INSTANCE.GetLastInputInfo(lastInputInfo);
        return Kernel32.INSTANCE.GetTickCount() - lastInputInfo.dwTime;
    }

    enum State {
        UNKNOWN, ONLINE, IDLE, AWAY
    }

    public void run() {
        if (!System.getProperty("os.name").contains("Windows")) {
            System.err.println("ERROR: Only implemented on Windows");
            System.exit(1);
        }
        State state = State.UNKNOWN;
        DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println(getIdleTimeMillisWin32());
            int idleSec = getIdleTimeMillisWin32() / 1000;
            State newState =
                    idleSec < 30 ? State.ONLINE :
                            idleSec > 5 * 60 ? State.AWAY : State.IDLE;
            if (newState != state) {
                state = newState;
//                System.out.println(dateFormat.format(new Date()) + " # " + state);
            }
            state = State.AWAY;
            if (state == State.AWAY || state == State.IDLE) {
                if (!gpuThread.isAlive())
                    gpuThread.start();
            } else {
                if (!gpuThread.isInterrupted())
                    gpuThread.interrupt();
            }
            try {
                // TODO SLEEP FOR 300000 ms (5 minutes).
                Thread.sleep(1000);
            } catch (Exception ex) {
            }
        }
    }
}