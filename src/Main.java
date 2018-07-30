import com.hb.pocket.server.Server;
import com.hb.utils.log.MyLog;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by hb on 04/07/2018.
 */
public class Main {

    private static String TAG = Main.class.getSimpleName();

    public static void main(String[] args) {

        MyLog.d(TAG, "Hello Pocket Socket Pipeline!");
//        startServerV1();
        startServerV2();

        MyLog.i(TAG, "Main exit.");

    }

    /**
     * Start server version 2.0.0
     */
    private static void startServerV2() {
        com.hb.pocket.serverv2.Server server = com.hb.pocket.serverv2.Server.getInstance();
        try {
            server.init();
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner sc = new Scanner(System.in);
        sc.useDelimiter("\n");
        while (sc.hasNext()) {
            String str = sc.next();
            if (str.equals("Exit".toLowerCase())) {
                try {
                    server.shutDownServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            if (str.equals("ClientCount".toLowerCase())) {
                MyLog.i(TAG, "ClientCount number: " + server.getClientCount());
            }
            if (str.startsWith("SendBroadMsg".toLowerCase())) {
                int index = str.indexOf(' ');
                if (index == -1) {
                    MyLog.i(TAG, "Command can not execute.");
                    continue;
                } else {
                    String msg = str.substring(index);
                    server.sendBroadMessage(msg + "\n");
                }
            }
            if (str.equals("Clear".toLowerCase())) {
                server.clearAllClients();
                MyLog.i(TAG, "Remove all the clients.");
            }
        }
        MyLog.i(TAG, "Main thread is exited.");
    }

    /**
     * Start server version 1.0.0
     */
    private static void startServerV1() {
        Server server = Server.getInstance();
        server.startServer();
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String str = sc.next();
            if (str.equals("Exit".toLowerCase())) {
                break;
            }
            if (str.equals("ClientCount".toLowerCase())) {
                MyLog.i(TAG, "ClientCount number: " + server.getClientCount());
            }
            if (str.equals("List".toLowerCase())) {

            }
        }
        server.shutDownServer();
    }
}
