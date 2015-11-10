package player;


import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * Created by Denis on 13.10.2015.
 */
public class MoviePlayer extends Application {


    public  static void main (String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Movie Player");
        Group root = new Group();

        Media media = new Media("file:///home/contragent/Desktop/MoviePlayer/trallers/1.mp4");
        MediaPlayer player = new MediaPlayer(media);
        MediaView view = new MediaView(player);

        Button btn_play, btn_pause, btn_stop;
        btn_play = new Button("Start");
        btn_pause = new Button("Pause");
        btn_stop = new Button("Stop");

        btn_play.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                player.play();
            }
        });

        btn_pause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                player.pause();
            }
        });

        btn_stop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                player.stop();
            }
        });

        System.out.print("media.width:"+ media.getWidth());

        final Timeline slideIn = new Timeline();
        final Timeline slideOut = new Timeline();

        root.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                slideOut.play();

            }
        });

        root.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                slideIn.play();

            }
        });

        final VBox vBox = new VBox();
        Slider slider = new Slider();
        vBox.getChildren().add(slider);

        final HBox hbox = new HBox(2);
        final HBox btnBox = new HBox(3);
        final int bands = player.getAudioSpectrumNumBands();
        final Rectangle[] rects = new Rectangle[bands];
        for (int i=0; i<rects.length; i++){
            rects[i] = new Rectangle();
            rects[i].setFill(Color.GREENYELLOW);
            hbox.getChildren().add(rects[i]);
        }

        vBox.getChildren().add(hbox);
        btnBox.getChildren().addAll(btn_play, btn_pause, btn_stop);



        root.getChildren().add(view);
        root.getChildren().add(btnBox);
        root.getChildren().add(vBox);

        Scene scene = new Scene(root, 400, 400, Color.BLACK);
        stage.setScene(scene);
        stage.show();

//        player.play();
        player.setOnReady(new Runnable() {
            @Override
            public void run() {
                int w = player.getMedia().getWidth();
                int h = player.getMedia().getHeight();

                hbox.setMinWidth(w);
                int bandWidth = w / rects.length;
                for (Rectangle r : rects) {
                    r.setWidth(bandWidth);
                    r.setHeight(2);
                }

                stage.setMinWidth(w);
                stage.setMinHeight(h);

                vBox.setMinSize(w, 100);
                vBox.setTranslateY(h - 100);

                slider.setMin(0.0);
                slider.setValue(0.0);
                slider.setMax(player.getTotalDuration().toSeconds());
//                slider.setValue(0.0);

                slideOut.getKeyFrames().addAll (
                        new KeyFrame(new Duration(0),
                                new KeyValue(vBox.translateYProperty(),h-100),
                                new KeyValue(vBox.opacityProperty(),0.9)
                        ),
                        new KeyFrame(new Duration(300),
                                new KeyValue(vBox.translateYProperty(),h),
                                new KeyValue(vBox.opacityProperty(),0.0)
                        )
                );

                slideIn.getKeyFrames().addAll (
                    new KeyFrame(new Duration(0),
                            new KeyValue(vBox.translateYProperty(),h),
                            new KeyValue(vBox.opacityProperty(),0.0)
                    ),
                    new KeyFrame(new Duration(300),
                            new KeyValue(vBox.translateYProperty(),h-100),
                            new KeyValue(vBox.opacityProperty(),0.9)
                    )
                );
                }
        });
        player.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration duration, Duration current) {
                slider.setValue(current.toSeconds());
            }
        });
        slider.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mauseEvent) {
                player.seek(Duration.seconds(slider.getValue()));
            }
        });
        player.setAudioSpectrumListener(new AudioSpectrumListener() {
            @Override
            public void spectrumDataUpdate(double v, double vl, float[] mags, float[] floats) {
                for (int i = 0; i < rects.length; i++) {
                    double h = mags[i] + 60;
                    if (h > 2) {
                        rects[i].setHeight(h);
                    }
                }

            }
        });
    }
}
