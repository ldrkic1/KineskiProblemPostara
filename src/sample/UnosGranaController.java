package sample;

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
import javafx.util.Pair;

import java.util.*;

public class UnosGranaController {
    public VBox unosVbox;
    public Button pronadjiRjesenjeButton;
    private int brojGrana, brojCvorova;
    private ObservableList<Integer> cvorovi = null;
    private int brojacOdabranihCvorova=0;
    private boolean tezinaIspravna = false;
    private Graf graf;
    private Set<Cvor> posjeceniCvorovi = new HashSet<>();
    private Set<Cvor> neposjeceniCvorovi = new HashSet<>();
    private Map<Cvor, Cvor> prethodnici = new HashMap<>();
    //od pocetnog do cvora u mapi
    private Map<Cvor, Integer> udaljenosti = new HashMap<>();
    private Map<Pair<Cvor, Cvor>, Integer> udaljenostiIzmedjuCvorova = new HashMap<>();
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
                    if(!pocetniChoice.getStyleClass().contains("odabran")) {
                        pocetniChoice.getStyleClass().removeAll("nijeOdabran");
                        pocetniChoice.getStyleClass().add("odabran");
                        brojacOdabranihCvorova++;
                    }
                }
            });
            krajnjiChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                    if(!krajnjiChoice.getStyleClass().contains("odabran")) {
                        krajnjiChoice.getStyleClass().removeAll("nijeOdabran");
                        krajnjiChoice.getStyleClass().add("odabran");
                        brojacOdabranihCvorova++;
                    }
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
    private ArrayList<Cvor> dajSusjedneCvorove(Cvor cvor) {
        ArrayList<Cvor> susjedi = new ArrayList<>();
        for(Grana grana: graf.getGrane()) {
            //neusmjereni graf
            if(grana.getKrajnjiCvor().equals(cvor)) susjedi.add(grana.getPocetniCvor());
            else if (grana.getPocetniCvor().equals(cvor)) susjedi.add(grana.getKrajnjiCvor());
        }

        return susjedi;
    }
    private boolean postojiUdaljenost(Cvor cvor) {
        for(Map.Entry<Cvor, Integer> entry: udaljenosti.entrySet()) {
            if(entry.getKey().getOznaka().equals(cvor.getOznaka())) return true;
        }
        return false;
    }
    private void findMinimalDistances(Cvor node) {
        ArrayList<Cvor> susjedni = dajSusjedneNeposjeceneCvorove(node);
       // System.out.println("Prolazimo korz susjedne neposjecene cvorove cvora " + node.getOznaka());
        for (Cvor krajni : susjedni) {
         //   System.out.println("susjed neposjecen od " + node.getOznaka() + " je "+ krajni.getOznaka());
           // System.out.println("provjera da li je getShortestDistance od "+krajni.getOznaka() + " vece od getShortestDistance od "+node.getOznaka() + " + tezina grane izmedju " + node.getOznaka() + " i " + krajni.getOznaka() );
           // System.out.println(getShortestDistance(krajni) + " > " + (getShortestDistance(node) + tezinaGrane(node, krajni)));
            if (getShortestDistance(krajni) > getShortestDistance(node) + tezinaGrane(node, krajni)) {
             //   System.out.println("Prvo je vece");
               // System.out.println("provjeravamo da li vec postoji put od pocetnog do " + krajni.getOznaka());
                if(postojiUdaljenost(krajni)) {
                 //   System.out.println("postoji udaljenost");
                    for(Map.Entry<Cvor, Integer> entry: udaljenosti.entrySet()) {
                        if(entry.getKey().getOznaka().equals(krajni.getOznaka())) {
                            if(entry.getValue() > getShortestDistance(node) + tezinaGrane(node, krajni)) {
                   //             System.out.println("ova sto smo sad nasli je manja od one vec spasene ");
                                entry.setValue(getShortestDistance(node) + tezinaGrane(node, krajni));
                     //           System.out.println("nova duzina je " + entry.getValue());
                                for(Map.Entry<Pair<Cvor, Cvor>, Integer> entry1: udaljenostiIzmedjuCvorova.entrySet()) {
                                    if(entry1.getKey().getKey().getOznaka().equals(node.getOznaka()) && entry1.getKey().getValue().getOznaka().equals(krajni.getOznaka())) {
                                        entry1.setValue(getShortestDistance(node) + tezinaGrane(node, krajni));
                                    }
                                }
                                //prethodnici.put(krajni, node);
                                //neposjeceniCvorovi.add(krajni);
                            }
                        }
                    }
                }
                else {
                 //   System.out.println("u pampi ne postoji put od pocetnog do " + krajni.getOznaka() + " pa ga dodajemo");
                    udaljenosti.put(krajni,  (getShortestDistance(node) + tezinaGrane(node, krajni)));
                   // System.out.println("put od " + node.getOznaka() + " do " + krajni.getOznaka() +" je " + (getShortestDistance(node) + tezinaGrane(node, krajni)));
                    udaljenostiIzmedjuCvorova.put(new Pair<>(node, krajni), (getShortestDistance(node) + tezinaGrane(node, krajni)));
                    prethodnici.put(krajni, node);
                   // System.out.println("u neposjecen dodaje " + krajni.getOznaka());
                    neposjeceniCvorovi.add(krajni);
                }
            }
        }

    }

    private int tezinaGrane(Cvor pocetni, Cvor krajnji) {
        for (Grana grana: graf.getGrane()) {
            if (grana.getPocetniCvor().equals(pocetni) && grana.getKrajnjiCvor().equals(krajnji)) {
               // System.out.print("Duzina direktne grane izmedju " + pocetni.getOznaka() + " i " + krajnji.getOznaka() + " je " + grana.getTezinaGrane());
                return grana.getTezinaGrane();
            }
            //graf neusmjeren
            if (grana.getPocetniCvor().equals(krajnji) && grana.getKrajnjiCvor().equals(pocetni)) {
               // System.out.print("Duzina direktne grane izmedju " + pocetni.getOznaka() + " i " + krajnji.getOznaka() + " je " + grana.getTezinaGrane());
                return grana.getTezinaGrane();
            }
        }
       // System.out.println("Nema direktne grane izmedju cvorova " + pocetni.getOznaka() + " i " + krajnji.getOznaka() + " pa je tezina 0");
        return 0;
    }

    private ArrayList<Cvor> dajSusjedneNeposjeceneCvorove(Cvor cvor) {
        ArrayList<Cvor> susjedniCvorovi = new ArrayList<>();
        for (Grana grana : graf.getGrane()) {
            if (grana.getPocetniCvor().equals(cvor) && !posjecen(grana.getKrajnjiCvor())) {
                susjedniCvorovi.add(grana.getKrajnjiCvor());
            }
            else if(grana.getKrajnjiCvor().equals(cvor) && !posjecen(grana.getPocetniCvor())) {
                susjedniCvorovi.add(grana.getPocetniCvor());
            }
        }
        //System.out.print("Susjedni neposjeceni cvorovi cvora " + cvor.getOznaka());
       // for(Cvor cvor1: susjedniCvorovi) {
         //   System.out.print( cvor1.getOznaka() + " ");
        //}
       // System.out.println();
        return susjedniCvorovi;
    }

    private Cvor getMinimum(Set<Cvor> vertexes) {
       // System.out.println("trazi onaj iz neposjecenih cija je udaljenost od pocetnog je najmanja ");
        Cvor minimum = null;
        for (Cvor vertex : vertexes) {
            if (minimum == null) {
                minimum = vertex;
            } else {
                if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
                    minimum = vertex;
                }
            }
        }
        //System.out.println("cvor sa minimalnom udaljenost iz neposjecenih je " + minimum.getOznaka());
        return minimum;
    }

    private boolean posjecen(Cvor cvor) {
        //System.out.println("Provjeri jel " + cvor.getOznaka() + " u posjecenim, a posjeceni su");
        for(Cvor cvor1: posjeceniCvorovi) {
          //  System.out.print(cvor1.getOznaka() + " ");
        }
        //System.out.println();
        for(Cvor cvor1: posjeceniCvorovi) {
            if(cvor1.getOznaka().equals(cvor.getOznaka())){
          //      System.out.println("Jeste");
                return true;
            }
        }
        //System.out.println("Nije");
        return false;
    }

    private int getShortestDistance(Cvor krajnji) {
        //System.out.println("Daj najkracu udaljenost od zadanog pocetnog do " + krajnji.getOznaka());
        Integer d = udaljenosti.get(krajnji);
        if (d == null) {
          //  System.out.println("beskonacna udaljenost");
            return Integer.MAX_VALUE;
        } else {
            // System.out.println("udaljenos je " + d);
            return d;
        }
    }

    /*
     * This method returns the path from the source to the selected target and
     * NULL if no path exists
     */
    public LinkedList<Cvor> getPath(Cvor target) {
        LinkedList<Cvor> path = new LinkedList<Cvor>();
        Cvor step = target;
        // check if a path exists
        if (prethodnici.get(step) == null) {
            return null;
        }
        path.add(step);
        while (prethodnici.get(step) != null) {
            step = prethodnici.get(step);
            path.add(step);
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }

    private int najkraciPutDijkstra(Cvor pocetni, Cvor kranji) {
        neposjeceniCvorovi = new HashSet<>();
        posjeceniCvorovi = new HashSet<>();
        udaljenosti = new HashMap<>();
        ArrayList<Integer> duzine = new ArrayList<>();
        ArrayList<Cvor> susjedniPocetnog = dajSusjedneCvorove(pocetni);
        //System.out.print("Susjedi zadanog pocetnog cvora    ");
        //for(Cvor cvor: susjedniPocetnog) {
          //  System.out.print(" " + cvor.getOznaka());
       // }
       // System.out.println();
        udaljenosti.put(pocetni, 0);
        //System.out.println("dodau u udaljenosti " + pocetni.getOznaka() + " sa value " + 0);
        neposjeceniCvorovi.add(pocetni);
        //System.out.println("Dodao u neposjecene " + pocetni.getOznaka());
        while (neposjeceniCvorovi.size() > 0) {
            Cvor cvor = getMinimum(neposjeceniCvorovi);
          //  System.out.println("Minimum je " + cvor.getOznaka());
            posjeceniCvorovi.add(cvor);
           // System.out.println("U posjecene dodao " +  cvor.getOznaka());
            neposjeceniCvorovi.remove(cvor);
           // System.out.println("Uklpnio minimum iz neposjecenih i ide traziti minimal distance za " + cvor.getOznaka());
            findMinimalDistances(cvor);
        }
        //for(Map.Entry<Cvor, Integer> entry: udaljenosti.entrySet()) {
          //  System.out.println("pocetni " + pocetni.getOznaka() + " krajnji " + entry.getKey().getOznaka() + " duzina "+ entry.getValue());
        //}
        for(Map.Entry<Cvor, Integer> entry: udaljenosti.entrySet()) {
            if(entry.getKey().getOznaka().equals(kranji.getOznaka())) return entry.getValue();
        }
        //ne postoji
        return Integer.MAX_VALUE;
    }
    private void algoritamEdmondsJohnson() {
        //kreiramo listu cvorova neparnog stepena
        ArrayList<Cvor> cvoroviNepranogStepena = new ArrayList<>();
        for (Cvor cvor: graf.getCvorovi()) {
            if(cvor.getStepen() % 2 == 1) cvoroviNepranogStepena.add(cvor);
        }
        //kreiramo listu svih parova cvorova neparnog stepena
        ArrayList<Pair<Cvor, Cvor>> paroviCvorovaNeparnogStepena = new ArrayList<>();
        for(int i = 0; i < cvoroviNepranogStepena.size(); i++) {
            for(int j = i + 1; j < cvoroviNepranogStepena.size(); j++) {
                paroviCvorovaNeparnogStepena.add(new Pair(cvoroviNepranogStepena.get(i), cvoroviNepranogStepena.get(j)));
            }
        }
        //pronalazimo najkrace puteve izmedju cvorova koji cine jedan par
        for (Pair<Cvor,Cvor> par: paroviCvorovaNeparnogStepena) {
            System.out.print("Najkraci put od " + par.getKey().getOznaka() + " do " + par.getValue().getOznaka());
            int put = najkraciPutDijkstra(par.getKey(),par.getValue());
            System.out.println(" duzina " + put);

        }
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
                            granaGrafa.setTezinaGrane(Integer.parseInt(((TextField) hboxChild).getText()));
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
            if(brojCvorovaNeparnogStepena != 0) {
                algoritamEdmondsJohnson();
            }
            else {

            }
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
