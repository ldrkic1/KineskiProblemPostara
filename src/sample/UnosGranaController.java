package sample;

import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class UnosGranaController {
    public VBox unosVbox;
    public Button pronadjiRjesenjeButton;
    private int brojGrana, brojCvorova;
    private ObservableList<Integer> cvorovi = null;
    private int brojacOdabranihCvorova=0;
    private boolean tezinaIspravna = false;
    private Graf graf;
    public UnosGranaController(int brojCvorova, int brojGrana) {
        this.brojCvorova = brojCvorova;
        this.brojGrana= brojGrana;
        graf = new Graf();
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 1; i <= brojCvorova; i++) {
            list.add(i);
            graf.getCvorovi().add(new Cvor(graf.getCvorovi().size() + 1, String.valueOf(i)));
        }
        cvorovi = FXCollections.observableArrayList(list);

    }

    @FXML
    public void initialize() {
        for(int i = 0; i < brojGrana; i++) {
            tezinaIspravna = false;
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
            pocetniChoice.getStyleClass().add("nijeOdabran");
            Label krajnjiLabel = new Label("Krajnji čvor:");
            ChoiceBox krajnjiChoice = new ChoiceBox();
            krajnjiChoice.setPrefWidth(100);
            krajnjiChoice.setItems(cvorovi);
            krajnjiChoice.getStyleClass().add("nijeOdabran");
            pocetniChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                    pocetniChoice.getStyleClass().removeAll("nijeOdabran");
                    pocetniChoice.getStyleClass().add("odabran");
                    brojacOdabranihCvorova++;
                }
            });
            krajnjiChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                    krajnjiChoice.getStyleClass().removeAll("nijeOdabran");
                    krajnjiChoice.getStyleClass().add("odabran");
                    brojacOdabranihCvorova++;
                }
            });
            Label tezinaLabel = new Label("Tezina:");
            TextField tezinaField = new TextField();
            tezinaField.getStyleClass().add("neispravnoPolje");
            if(tezinaField.getStyleClass().contains("neispravnoPolje")) tezinaIspravna = false;
            tezinaField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                    if(Controller.isInt(newValue)) {
                        if(Integer.parseInt(newValue) > 0) {
                            tezinaField.getStyleClass().removeAll("neispravnoPolje");
                            tezinaField.getStyleClass().add("ispravnoPolje");
                            tezinaIspravna = true;
                        }
                        else {
                            tezinaField.getStyleClass().removeAll("ispravnoPolje");
                            tezinaField.getStyleClass().add("neispravnoPolje");
                            tezinaIspravna = false;
                        }
                    }
                    else {
                        tezinaField.getStyleClass().removeAll("ispravnoPolje");
                        tezinaField.getStyleClass().add("neispravnoPolje");
                        tezinaIspravna = false;
                    }
                }
            });
            granaHbox.getChildren().addAll(početniLabel, pocetniChoice, separator1, krajnjiLabel, krajnjiChoice, separator2, tezinaLabel, tezinaField);
            unosVbox.getChildren().add(granaHbox);
        }
    }
    private int dajIndexCvora(String oznaka) {
        for(Cvor cvor: graf.getCvorovi()) {
            if(cvor.getOznaka().equals(oznaka)) return cvor.getId();
        }
        return 0;
    }
    private boolean provjeriIspravnostUnesenihTezina() {
        boolean ispravno = true;
        ObservableList<Node> grane = unosVbox.getChildren();
        for(Node grana: grane) {
            if (grana instanceof HBox) {
                for (Node hboxChild : ((HBox) grana).getChildren()) {
                    if (hboxChild instanceof TextField) {
                        if (hboxChild.getStyleClass().contains("neispravnoPolje")) return false;
                    }
                }
            }
        }
        return ispravno;
    }
    private void izracunajStepeneCvorova() {
        for(Cvor cvor: graf.getCvorovi()) {
            int brojac = 0;
            for(Grana grana: graf.getGrane()) {
                if(grana.getPocetniCvor().equals(cvor)) brojac++;
                else if(grana.getKrajnjiCvor().equals(cvor)) brojac++;
            }
            cvor.setStepen(brojac);
        }
    }
    private int dajBrojCvorovaNeparnogStepena() {
        int brojCvorovaNeparnogStepena = 0;
        for(Cvor cvor: graf.getCvorovi()) {
            if(cvor.getStepen() % 2 == 1) brojCvorovaNeparnogStepena++;
        }
        return brojCvorovaNeparnogStepena;
    }
    public void pronadjiRjesenjeAction(ActionEvent actionEvent) {
        if(provjeriIspravnostUnesenihTezina() && brojacOdabranihCvorova == brojGrana*2) {
            ObservableList<Node> grane = unosVbox.getChildren();
            for (Node grana : grane) {
                if (grana instanceof HBox) {
                    Grana granaGrafa = new Grana();
                    granaGrafa.setId(graf.getGrane().size() + 1);
                    for (Node hboxChild : ((HBox) grana).getChildren()) {
                        if (hboxChild instanceof TextField) {
                            granaGrafa.setTezinaGrane(Double.parseDouble(((TextField) hboxChild).getText()));
                        }
                        if (hboxChild instanceof ChoiceBox) {
                            int labelIndex = ((HBox) grana).getChildren().indexOf(hboxChild) - 1;
                            if (((HBox) grana).getChildren().get(labelIndex) instanceof Label) {
                                if (((Label) ((HBox) grana).getChildren().get(labelIndex)).getText().equals("Početni čvor:")) {
                                    Cvor pocetni = new Cvor();
                                    pocetni.setOznaka(((ChoiceBox) hboxChild).getSelectionModel().getSelectedItem().toString());
                                    pocetni.setId(dajIndexCvora(pocetni.getOznaka()));
                                    granaGrafa.setPocetniCvor(pocetni);
                                } else {
                                    Cvor krajnji = new Cvor();
                                    krajnji.setOznaka(((ChoiceBox) hboxChild).getSelectionModel().getSelectedItem().toString());
                                    krajnji.setId(dajIndexCvora(krajnji.getOznaka()));
                                    granaGrafa.setKrajnjiCvor(krajnji);
                                }
                            }
                        }
                    }
                    graf.getGrane().add(granaGrafa);
                }
            }
            izracunajStepeneCvorova();
            /*for(Cvor cvor: graf.getCvorovi()) {
                System.out.println("id " + cvor.getId() + " oznaka " + cvor.getOznaka() + " stepen " + cvor.getStepen());
            }*/
            int brojCvorovaNeparnogStepena = dajBrojCvorovaNeparnogStepena();
            //System.out.println(brojCvorovaNeparnogStepena);
        }

        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Neispravan unos");
            alert.setHeaderText(null);
            alert.setContentText("Unesite trazene podatke ispravno!");
            alert.showAndWait();
        }
    }
}
