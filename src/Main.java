/**
 * Created by Nithesh on 4/29/2016.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main
        extends Application {

    private Text actionStatus;
    private Text filepath;
    private Stage savedStage;
    private Button Split_btn = new Button("Split .WAV file...");
    private  Button Refresh_btn = new Button(" Refresh ");
    private static final String titleTxt = "Audio Processor";
    private Label label = new Label("Choose a .wav File");
    File selectedFile;
    int duration = 6;
    public static void main(String [] args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle(titleTxt);

        // Window label

        label.setTextFill(Color.SALMON);
        label.setFont(Font.font("Cambria", FontWeight.BOLD, 28));
        HBox labelHb = new HBox();
        labelHb.setAlignment(Pos.CENTER);
        labelHb.getChildren().add(label);

        // Buttons
        Button btn1 = new Button("Choose a .WAV file...");
        btn1.setOnAction(new SingleFcButtonListener());

        Split_btn.setOnAction(new SplitListener());
        HBox buttonHb1 = new HBox(20);
        buttonHb1.setAlignment(Pos.CENTER);
        buttonHb1.getChildren().addAll(btn1,Split_btn);
        Split_btn.setDisable(true);

        Refresh_btn.setOnAction(new RefreshListener());
        HBox refreshspace = new HBox(10);
        refreshspace.setAlignment(Pos.BOTTOM_RIGHT);
        refreshspace.getChildren().addAll(Refresh_btn);


        // Status message text
        actionStatus = new Text();
        actionStatus.setFont(Font.font("Cambria", FontWeight.NORMAL, 20));
        actionStatus.setFill(Color.BLACK);

        //Path
        filepath = new Text();
        filepath.setFont(Font.font("Cambria", FontWeight.NORMAL, 20));
        filepath.setFill(Color.BLACK);

        // Vbox
        VBox vbox = new VBox(30);
        vbox.setPadding(new Insets(25, 25, 25, 25));;
        vbox.getChildren().addAll(labelHb, buttonHb1, actionStatus, filepath, refreshspace);

        // Scene
        Scene scene = new Scene(vbox, 500, 350); // w x h
        primaryStage.setScene(scene);
        primaryStage.show();

        savedStage = primaryStage;
    }

    private class SingleFcButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {

            showSingleFileChooser();
        }
    }

    private class RefreshListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent r) {


            Platform.runLater(() -> {
                actionStatus.setText(" ");
                filepath.setText("");
                Split_btn.setDisable(true);
                label.setText("Choose a .WAV file...");
                selectedFile = null;
            });
        }
    }

    private class SplitListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent s) {

            System.out.println("Split Initiated!");

            String fname = selectedFile.getName() ;
            fname = fname.replaceAll("\\s", "").replaceAll("[^a-zA-Z]+", "");
            String dirName =  selectedFile.getParent() + "/"+ fname +"_split";
            System.out.println(dirName);
            File dir = new File(dirName);
            boolean successful = dir.mkdir();
            System.out.println(successful);


            StringBuilder command = new StringBuilder();
            command.append("ffmpeg");
            command.append(" -i " + selectedFile.getAbsolutePath());
            command.append(" -c copy -map 0");
            command.append(" -segment_time " + duration + " -f segment ");
            command.append(selectedFile.getParent()+"/"+fname+"_split"+"/"+fname+"%03d.wav");
            //command.append(selectedFile.getParent()+"/"+fname+"%03d.wav");
            String commands = command.toString();
            System.out.println(commands);
            Process p = null;
            try {
                p = Runtime.getRuntime().exec(commands);
                BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                actionStatus.setText(" ");
                filepath.setText("");
                Split_btn.setDisable(true);
                // Split_btn.fire();
                label.setText("Split .wav File");
            });




        }
    }


    private void showSingleFileChooser() {

        FileChooser fileChooser = new FileChooser();
        selectedFile = fileChooser.showOpenDialog(null);


        if (selectedFile != null)
        {
            Platform.runLater(() -> {
                actionStatus.setText("File selected: " + selectedFile.getName());
                filepath.setText("File Path: " + selectedFile.getAbsolutePath());
                Split_btn.setDisable(false);
               // Split_btn.fire();
                label.setText("Split .wav File");
            });







        }
        else {
            actionStatus.setText("File selection cancelled.");
        }
    }




}