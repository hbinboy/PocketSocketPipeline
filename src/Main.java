import com.hb.pocket.server.Server;
import com.hb.utils.log.MyLog;

import java.util.Scanner;

/**
 * Created by hb on 04/07/2018.
 */
public class Main {

    private static String TAG = Main.class.getSimpleName();

    public static void main(String[] args) {

        MyLog.d(TAG, "Hello Pocket Socket Pipeline!");

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
        MyLog.i(TAG, "Main exit.");

    }
}
