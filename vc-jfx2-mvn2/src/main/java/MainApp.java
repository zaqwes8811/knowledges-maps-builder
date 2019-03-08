import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sun.applet.Main;

// intall editor
// https://www.oracle.com/technetwork/java/javase/downloads/javafxscenebuilder-1x-archive-2199384.html
// java -jar target/vc-1.0-SNAPSHOT-jar-with-dependencies.jar

// fxml + controller + app
// https://stackoverflow.com/questions/33881046/how-to-connect-fx-controller-with-main-app
public class MainApp extends Application {

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        String fxmlFile = "/fxml/hello.fxml";
        FXMLLoader loader = new FXMLLoader();


        Parent root = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));

//        // https://stackoverflow.com/questions/42497507/javafx-fxml-controller-event-handler-and-initialization-best-practice
//        MainController ctrl = new MainController();
//        //ctrl.setModel(this.model);
//        //ctrl.setUp();
//        ctrl.hwBt.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent e) {
//                //label.setText("Accepted");
//                System.out.println("Hello");
//            }
//        });
        MainController controller = (MainController) loader.getController();
        controller.stage = stage;


        stage.setTitle("JavaFX and Maven");
        stage.setScene(new Scene(root));
        stage.show();
    }
}