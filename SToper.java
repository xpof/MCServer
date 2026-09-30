import java.io.OutputStream;
import java.io.IOException;

public class SToper {
    private final Process process;
    private final OutputStream processOutput;
    private final long processId;
    
    private int onlinePlayers = 0;
    private boolean isPaused = false;

    public SToper(Process process) {
        this.process = process;
        this.processOutput = (process != null) ? process.getOutputStream() : null;
        this.processId = (process != null) ? process.pid() : -1;
    }

    public synchronized void onPlayerJoin(String playerName) {
        onlinePlayers++;
        System.out.println("[SToper] プレイヤーが参加しました: " + playerName + " (現在: " + onlinePlayers + "人)");
        
        if (isPaused) {
            resumeServer();
        }
    }

    public synchronized void onPlayerLeave(String playerName) {
        onlinePlayers = Math.max(0, onlinePlayers - 1);
        System.out.println("[SToper] プレイヤーが退出しました: " + playerName + " (現在: " + onlinePlayers + "人)");

        if (onlinePlayers == 0 && !isPaused) {
            pauseServer();
        }
    }

    public synchronized void pauseServer() {
        if (process == null || !process.isAlive() || isPaused) return;

        System.out.println("[SToper] プレイヤーが0人になりました。サーバーを一時停止(Suspend)します... [PID: " + processId + "]");
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {

                new ProcessBuilder("powershell", "-Command", 
                    "[Threading.Thread]::Sleep(100); " +
                    "$p = Get-Process -Id " + processId + "; " +
                    "$p.Threads | ForEach-Object { $Win32 = Add-Type -memberDefinition '[DllImport(\"kernel32.dll\")] public static extern IntPtr OpenThread(int dwDesiredAccess, bool bInheritHandle, int dwThreadId); [DllImport(\"kernel32.dll\")] public static extern uint SuspendThread(IntPtr hThread);' -Name 'Win32' -Namespace Win32 -PassThru; $h = $Win32::OpenThread(2, $false, $_.Id); $Win32::SuspendThread($h); }").start();
            } else {
                new ProcessBuilder("kill", "-STOP", String.valueOf(processId)).start();
            }
            isPaused = true;
            System.out.println("[SToper] サーバープロセスを一時停止しました。CPU/メモリ負荷を最小化中。");
        } catch (IOException e) {
            System.err.println("[SToper] 一時停止処理に失敗しました: " + e.getMessage());
        }
    }

    public synchronized void resumeServer() {
        if (process == null || !isPaused) return;

        System.out.println("[SToper] プレイヤーの接続を検知。サーバーを再開(Resume)します... [PID: " + processId + "]");
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {

                new ProcessBuilder("powershell", "-Command", 
                    "$p = Get-Process -Id " + processId + "; " +
                    "$p.Threads | ForEach-Object { $Win32 = Add-Type -memberDefinition '[DllImport(\"kernel32.dll\")] public static extern IntPtr OpenThread(int dwDesiredAccess, bool bInheritHandle, int dwThreadId); [DllImport(\"kernel32.dll\")] public static extern uint ResumeThread(IntPtr hThread);' -Name 'Win32' -Namespace Win32 -PassThru; $h = $Win32::OpenThread(2, $false, $_.Id); $Win32::ResumeThread($h); }").start();
            } else {
                new ProcessBuilder("kill", "-CONT", String.valueOf(processId)).start();
            }
            isPaused = false;
            System.out.println("[SToper] サーバープロセスが正常に再開されました。");
        } catch (IOException e) {
            System.err.println("[SToper] 再開処理に失敗しました: " + e.getMessage());
        }
    }

    public synchronized void stopServer() {
        if (isPaused) {
            resumeServer(); 
        }
        if (process != null && process.isAlive()) {
            try {
                System.out.println("[SToper] サーバーにシャットダウンコマンド(stop)を送信中...");
                processOutput.write("stop\n".getBytes());
                processOutput.flush();
                process.waitFor();
                System.out.println("[SToper] サーバーが安全に終了しました。");
            } catch (Exception e) {
                process.destroyForcibly();
            }
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }
}