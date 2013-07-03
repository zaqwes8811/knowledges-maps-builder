import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class MyApp extends Application {
  public void start(Stage stage) {
    Scene scene = new Scene(new Group());  // Может не то нужно передать констр.?
    // Загружаем css-файл
    scene.getStylesheets().add("test.css");

    Rectangle rect = new Rectangle(100,100);
    rect.setLayoutX(50);
    rect.setLayoutY(50);
    rect.getStyleClass().add("my-rect");
    ((Group)scene.getRoot()).getChildren().add(rect);

    final Rectangle rect_2 = new Rectangle(100,100);
    rect_2.setLayoutX(0);
    rect_2.setLayoutY(50);
    rect_2.getStyleClass().add("my-rect-2");
    ((Group)scene.getRoot()).getChildren().add(rect_2);

    // Обработчики событий
    rect.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
          if(mouseEvent.getClickCount() == 1){
            System.out.println("Double clicked");
            rect_2.toFront();
          }
        }
      }
    });

    stage.setTitle("JavaFX Application");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    Application.launch(MyApp.class, args);
  }
}