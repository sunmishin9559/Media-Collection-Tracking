package application;
	
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class Main extends Application {
	private static ArrayList<MediaItem> collection = new ArrayList<>();

    public static void main(String[] args) {

        readCollection();

        Application.launch(args);

        storeCollection();
    }

    public static void readCollection() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(new File("media.dat")));) {
            collection = (ArrayList<MediaItem>) ois.readObject();
        } catch (Exception e) {
            System.out.println("Error: Unable to load your collection!");
        }
    }

    public static void storeCollection() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(new File("media.dat")));) {
            oos.writeObject(collection);
        } catch (Exception e) {
            System.out.println("Error: Unable to save the updates to your "
                    + "collection!");
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //make a Border Pane
        BorderPane rootPane = new BorderPane();

        //make listview
        ListView<MediaItem> myList = new ListView();
        Collections.sort(collection);
        myList.setItems(FXCollections.observableList(collection));
        myList.setPrefSize(400, 500);
        
        //create a radio button
        RadioButton byTitle = new RadioButton("By title");
        GridPane sortGridPane = sortGrid(myList, byTitle);

        //loan an item
        GridPane loanGridPane = loanGrid(myList, byTitle);

        //add an Item
        GridPane addGridPane = addPane(myList, byTitle);

        //Buttons for remove and return
        Button removeButton = new Button("Remove");
        removeButton.setPrefWidth(65);
        removeButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                MediaItem item = myList.getSelectionModel().getSelectedItem();

                if (item != null) {
                    collection.remove(item);
                   // rearrange(byTitle);
                    myList.setItems(FXCollections.observableList(collection));
                    myList.refresh();
                } else {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error: remove");
                    alert.setContentText("There was nothing to remove");
                    alert.showAndWait();
                }
            }
        });

        Button returnButton = new Button("Return");
        returnButton.setPrefWidth(65);
        returnButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                MediaItem item = myList.getSelectionModel().getSelectedItem();

                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error: return");

                if (item != null) {
                    if (item.getLoanedTo() != null) {
                        item.returnItem();
                        rearrange(byTitle);
                        myList.setItems(FXCollections.observableList(collection));
                        myList.refresh();
                    } else {
                        alert.setContentText("This item is not on loan");
                        alert.showAndWait();
                    }
                } else {
                    alert.setContentText("There was nothing to return");
                    alert.showAndWait();
                }
            }
        });

        //make a FlowPane
        FlowPane flowPane = new FlowPane(Orientation.VERTICAL, 10, 0);
        flowPane.setPrefSize(100, 500);
        flowPane.setPadding(new Insets(15));
        flowPane.setVgap(15);
        flowPane.getChildren().add(addGridPane);
        flowPane.getChildren().add(removeButton);
        flowPane.getChildren().add(returnButton);
        flowPane.getChildren().add(loanGridPane);
        flowPane.getChildren().add(sortGridPane);

        //in BorderPane
        rootPane.setCenter(myList);
        rootPane.setRight(flowPane);

        //Create a scene
        Scene scene = new Scene(rootPane);
        primaryStage.setTitle("Media Collection");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public GridPane addPane(ListView<MediaItem> myList, RadioButton byTitle) {

        GridPane pane = new GridPane();

        pane.setStyle("-fx-border-border: solid inside;"
                + "-fx-border-radius: 5;"
                + "-fx-border-color: black");
        pane.setPadding(new Insets(10, 10, 10, 10));
        pane.setHgap(5);
        pane.setVgap(10);

        Label titleLabel = new Label("Title:");
        TextField titleText = new TextField();

        Label formatLabel = new Label("Format:");
        TextField formatText = new TextField();

        Button add = new Button("Add");

        add.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                String title = titleText.getText();
                String format = formatText.getText();

                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error: add");

                if (title.trim().length() == 0 || format.trim().length() == 0) {

                    alert.setContentText("Title or Format is empty");

                    alert.showAndWait();
                } else {
                    MediaItem newItem = new MediaItem(title, format);

                    if (!collection.contains(newItem)) {
                        collection.add(newItem);
                        rearrange(byTitle);

                        myList.setItems(FXCollections.observableList(collection));
                        myList.refresh();

                    } else {
                        alert.setContentText("We already have an item with "
                                + "this same title");
                        alert.showAndWait();
                    }
                }
            }
        });

        pane.add(titleLabel, 0, 0);
        pane.add(titleText, 1, 0);
        pane.add(formatLabel, 0, 1);
        pane.add(formatText, 1, 1);
        pane.add(add, 0, 2);

        return pane;
    }

    public GridPane loanGrid(ListView<MediaItem> myList, RadioButton byTitle) {

        GridPane pane = new GridPane();

        pane.setStyle("-fx-border-radius: 5;"
                + "-fx-border-color:black");
        pane.setPadding(new Insets(10));
        pane.setHgap(5);
        pane.setVgap(10);

        Label loanedTo = new Label("Loaned To:");
        TextField loanedToText = new TextField();

        Label loanedOn = new Label("Loaned On:");
        TextField loanedOnText = new TextField();

        Button loan = new Button("Loan");
        loan.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                MediaItem item = myList.getSelectionModel().getSelectedItem();
                String toText = loanedToText.getText();
                String onText = loanedOnText.getText();

                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error: loan");

                if (item == null) {
                    alert.setContentText("There was nothing to loan");
                    alert.showAndWait();
                } else {
                    if (toText.trim().length() == 0 || onText.trim().length() == 0) {
                        alert.setContentText("\"Loaned on\" or \"Loaned to\" "
                                + "is empty");
                        alert.showAndWait();
                    } else {
                        try {
                            Date loanedOn = new SimpleDateFormat("MM-dd-yyyy").parse(onText);

                            if (item.getLoanedTo() != null) {
                                alert.setContentText("This item is already loan on to someone");
                                alert.showAndWait();
                            } else {
                                item.loan(toText, loanedOn);
                                rearrange(byTitle);
                                myList.setItems(FXCollections.observableList(collection));
                                myList.refresh();
                            }

                        } catch (ParseException ex) {
                            alert.setContentText("The date is not a valid date");
                            alert.showAndWait();
                        }
                    }
                }
            }
        });

        pane.add(loanedTo, 0, 0);
        pane.add(loanedToText, 1, 0);
        pane.add(loanedOn, 0, 1);
        pane.add(loanedOnText, 1, 1);
        pane.add(loan, 0, 2);

        return pane;
    }

    public GridPane sortGrid(ListView<MediaItem> myList, RadioButton byTitle) {

        GridPane pane = new GridPane();
        pane.setVgap(10);

        ToggleGroup sort = new ToggleGroup();

        Label sortLabel = new Label("Sort");

        byTitle.setToggleGroup(sort);
        byTitle.setSelected(true);

        byTitle.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Collections.sort(collection);
                myList.setItems(FXCollections.observableList(collection));
                myList.refresh();
            }
        }
        );

        RadioButton byData = new RadioButton("By data loaned");
        byData.setToggleGroup(sort);
        byData.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Collections.sort(collection, new ByDataComparator());
                myList.setItems(FXCollections.observableList(collection));
                myList.refresh();
            }

        });

        pane.add(sortLabel, 0, 0);
        pane.add(byTitle, 0, 1);
        pane.add(byData, 0, 2);

        return pane;
    }

    public void rearrange(RadioButton byTitle) {
        if (byTitle.isSelected()) {
            Collections.sort(collection);
        } else {
            Collections.sort(collection, new ByDataComparator());
        }
    }

    class ByDataComparator implements Comparator<MediaItem> {

        @Override
        public int compare(MediaItem t1, MediaItem t2) {

            if (t1.getLoanedOn() == null && t2.getLoanedOn() == null) {
                int tNum = t1.getTitle().compareTo(t2.getTitle());

                if (tNum > 0) {
                    return 1;
                } else {
                    return -1;
                }

            } else {
                // at least one media is loaned    
                if (t1.getLoanedOn() == null) {
                    return 1;
                } else if (t2.getLoanedOn() == null) {
                    return -1;
                } else {
                    int dNum = t1.getLoanedOn().compareTo(t2.getLoanedOn());

                    if (dNum > 0) {
                        return 1;
                    } else if (dNum < 0) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            }
        }
    }

}
