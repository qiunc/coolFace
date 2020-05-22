package registerandlogin;

import org.litepal.LitePalApplication;

public class MyApplication extends LitePalApplication {

    private String userName;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
