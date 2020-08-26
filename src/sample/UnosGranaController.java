package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class UnosGranaController {
    public VBox unosVbox;
    public Button pronadjiRjesenjeButton;
    private int brojGrana, brojCvorova;
    private ObservableList<Integer> cvorovi = null;
    private boolean poljaIspravna = false;
    public UnosGranaController(int brojCvorova, int brojGrana) {
        this.brojCvorova = brojCvorova;
        this.brojGrana= brojGrana;
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 1; i <= brojCvorova; i++) {
            list.add(i);
        }
        cvorovi = FXCollections.observableArrayList(list);
    }

    @FXML
    public void initialize() {
        for(int i = 0; i < brojGrana; i++) {
            HBox granaHbox = new HBox();
            granaHbox.setAlignment(Pos.CENTER);
            granaHbox.setSpacing(5);
            Label početniLabel = new Label("Početni čvor:");
            Separator separator1 = new Separator();
            separator1.setOrientation(Orientation.VERTICAL);
            Separator separator2 = new Separator();
            separator2.setOrientation(Orientation.VERTICAL);
            ChoiceBox pocetniChoice = new ChoiceBox();
            pocetniChoice.setItems(cvorovi);
            pocetniChoice.setPrefWidth(100);
            Label krajnjiLabel = new Label("Krajnji čvor:");
            ChoiceBox krajnjiChoice = new ChoiceBox();
            krajnjiChoice.setPrefWidth(100);
            krajnjiChoice.setItems(cvorovi);
            Label tezinaLabel = new Label("Tezina:");
            TextField tezinaField = new TextField();
            tezinaField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                    if(Controller.isInt(newValue)) {
                        if(Integer.parseInt(newValue) > 0) {
                            tezinaField.getStyleClass().removeAll("neispravnoPolje");
                            tezinaField.getStyleClass().add("ispravnoPolje");
                            poljaIspravna = true;
                        }
                        else {
                            tezinaField.getStyleClass().removeAll("ispravnoPolje");
                            tezinaField.getStyleClass().add("neispravnoPolje");
                            poljaIspravna = false;
                        }
                    }
                    else {
                        tezinaField.getStyleClass().removeAll("ispravnoPolje");
                        tezinaField.getStyleClass().add("neispravnoPolje");
                        poljaIspravna = false;
                    }
                }
            });
            granaHbox.getChildren().addAll(početniLabel, pocetniChoice, separator1, krajnjiLabel, krajnjiChoice, separator2, tezinaLabel, tezinaField);
            unosVbox.getChildren().add(granaHbox);
        }
    }

    public void pronadjiRjesenjeAction(ActionEvent actionEvent) {

    }
}
