package dashboard;

import javafx.application.Application;
import javafx.stage.Stage;

import static com.sun.javafx.application.PlatformImpl.startup;

public class Starter {

    public static void main(String[] args) {
        Starter main = new Starter();
        main.startDashboard();
    }

    void startDashboard() {
        Application application = new Dashboard();
        startup(() -> {
            try {
                application.start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
