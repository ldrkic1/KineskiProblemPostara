package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


import java.io.IOException;

import static javafx.scene.control.PopupControl.USE_COMPUTED_SIZE;

public class Controller {
    public TextField brojCvorovaField;
    public TextField brojGranaField;
    public ImageView slika;
    private boolean poljaIspravna = false;
    public Controller() {
    }
    public static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    @FXML
    public void initialize() {
        Image postarSlika = new Image("/images/zbunjen.jpg");
        slika.setImage(postarSlika);
        slika.setFitWidth(249);
        slika.setFitHeight(358);
        brojCvorovaField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if(isInt(newValue)) {
                    if(Integer.parseInt(newValue) > 0) {
                        brojCvorovaField.getStyleClass().removeAll("neispravnoPolje");
                        brojCvorovaField.getStyleClass().add("ispravnoPolje");
                        poljaIspravna = true;
                    }
                    else {
                        brojCvorovaField.getStyleClass().removeAll("ispravnoPolje");
                        brojCvorovaField.getStyleClass().add("neispravnoPolje");
                        poljaIspravna = false;
                    }
                }
                else {
                    brojCvorovaField.getStyleClass().removeAll("ispravnoPolje");
                    brojCvorovaField.getStyleClass().add("neispravnoPolje");
                    poljaIspravna = false;
                }
            }
        });
        brojGranaField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if(isInt(newValue)) {
                    if(Integer.parseInt(newValue) > 0) {
                        brojGranaField.getStyleClass().removeAll("neispravnoPolje");
                        brojGranaField.getStyleClass().add("ispravnoPolje");
                        poljaIspravna = true;
                    }
                    else {
                        brojGranaField.getStyleClass().removeAll("ispravnoPolje");
                        brojGranaField.getStyleClass().add("neispravnoPolje");
                        poljaIspravna = false;
                    }
                }
                else {
                    brojGranaField.getStyleClass().removeAll("ispravnoPolje");
                    brojGranaField.getStyleClass().add("neispravnoPolje");
                    poljaIspravna = false;
                }
            }
        });
    }

    public void nastaviAction(ActionEvent actionEvent) throws IOException {
        if(poljaIspravna) {
            UnosGranaController ctrl = new UnosGranaController(Integer.parseInt(brojCvorovaField.getText()), Integer.parseInt(brojGranaField.getText()));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/unosGrana.fxml"));
            loader.setController(ctrl);
            Parent root =  loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setTitle("Kineski problem poštara");
            Stage trenutniStage = (Stage) brojCvorovaField.getScene().getWindow();
            trenutniStage.close();
            stage.setResizable(false);
            stage.show();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Neispravan unos");
            alert.setHeaderText(null);
            if (brojGranaField.getStyleClass().contains("neispravnoPolje") && brojCvorovaField.getStyleClass().contains("poljeIspravno")) {
                alert.setContentText("Unesite ispravan broj grana!");
            }
            else if (brojGranaField.getStyleClass().contains("poljeIspravno") && brojCvorovaField.getStyleClass().contains("neispravnoPolje")) {
                alert.setContentText("Unesite ispravan broj čvorova!");
            }
            else {
                alert.setContentText("Unesite ispravan broj čvorova i grana!");
            }
            alert.showAndWait();
        }
    }
}
