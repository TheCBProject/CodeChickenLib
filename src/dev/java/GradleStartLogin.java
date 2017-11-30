import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by covers1624 on 21/11/2017.
 */
public class GradleStartLogin extends GradleStart {

    private static String user = null;
    private static String pass = null;

    public static void main(String[] args) throws Throwable {
        // hack natives.
        Method m = GradleStart.class.getDeclaredMethod("hackNatives");
        m.setAccessible(true);
        m.invoke(null);

        File file = new File(args[0], "login_meta.dat");
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String ln = null;
                for (int l_num = 0; (ln = reader.readLine()) != null; l_num++) {
                    if (l_num == 0) {
                        user = ln;
                    } else if (l_num == 1) {
                        pass = ln;
                    } else {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // launch
        new GradleStartLogin().launch(args);
    }

    @Override
    protected void setDefaultArguments(Map<String, String> argMap) {
        super.setDefaultArguments(argMap);
        argMap.put("username", user);
        argMap.put("password", pass);
        user = null;
        pass = null;
    }
}
