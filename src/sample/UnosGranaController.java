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
    private Set<Cvor> sljedeciCvorovi = new HashSet<>();
    private Map<String, String> prethodnici = new HashMap<>();
    private Map<Cvor, Integer> udaljenosti = new HashMap<>(); /*od zadanog pocetnog do cvora koji predstavlja key mape*/
    private Map<Pair<Pair<Cvor,Cvor>, Integer>, ArrayList<Grana>> puteviIzmedjuCvorova = new HashMap<>(); /*kljuc - par kojeg cine par cvorova nepranog stepena i duzina puta izmedju njih, value - grane koje cine put*/
    private Map<Pair<Cvor,Cvor>,ArrayList<Grana>> obrnuteGraneUPutu = new HashMap<>();
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
            if(grana.getKrajnjiCvor().equals(cvor)) susjedi.add(grana.getPocetniCvor());
            else if (grana.getPocetniCvor().equals(cvor)) susjedi.add(grana.getKrajnjiCvor()); //neusmjereni graf
        }

        return susjedi;
    }

    private boolean postojiUdaljenost(Cvor cvor) {
        //provjera da li već postoji put do cvora
        for(Map.Entry<Cvor, Integer> entry: udaljenosti.entrySet()) {
            if(entry.getKey().getOznaka().equals(cvor.getOznaka())) return true;
        }
        return false;
    }

    private Cvor dajCvor(String oznaka) {
        for(Cvor cvor: graf.getCvorovi()) {
            if(cvor.getOznaka().equals(oznaka)) return cvor;
        }
        return null;
    }

    private ArrayList<Cvor> dajSusjedneNeposjeceneCvorove(Cvor cvor) {
        //trazimo susjede zadanog cvora za koje nije pronađena
        ArrayList<Cvor> susjedniCvorovi = new ArrayList<>();
        for (Grana grana : graf.getGrane()) {
            if (grana.getPocetniCvor().equals(cvor) && !posjecen(grana.getKrajnjiCvor())) {
                susjedniCvorovi.add(grana.getKrajnjiCvor());
            } else if (grana.getKrajnjiCvor().equals(cvor) && !posjecen(grana.getPocetniCvor())) {
                susjedniCvorovi.add(grana.getPocetniCvor());
            }
        }
        return susjedniCvorovi;
    }

    private void findMinimalDistances(Cvor node) {
        ArrayList<Cvor> susjedni = dajSusjedneNeposjeceneCvorove(node);
        for (Cvor krajni : susjedni) {
            if (getShortestDistance(krajni) > getShortestDistance(node) + tezinaGrane(node, krajni)) {
                if(postojiUdaljenost(krajni)) {
                    for(Map.Entry<Cvor, Integer> entry: udaljenosti.entrySet()) {
                        if(entry.getKey().getOznaka().equals(krajni.getOznaka())) {
                            if(entry.getValue() > getShortestDistance(node) + tezinaGrane(node, krajni)) {
                                entry.setValue(getShortestDistance(node) + tezinaGrane(node, krajni));
                                prethodnici.put(krajni.getOznaka(), node.getOznaka());
                            }
                        }
                    }
                }
                else {
                    udaljenosti.put(krajni,  (getShortestDistance(node) + tezinaGrane(node, krajni)));
                    prethodnici.put(krajni.getOznaka(), node.getOznaka());
                    sljedeciCvorovi.add(krajni);
                }
            }
        }
    }

    private int tezinaGrane(Cvor pocetni, Cvor krajnji) {
        for (Grana grana: graf.getGrane()) {
            if (grana.getPocetniCvor().equals(pocetni) && grana.getKrajnjiCvor().equals(krajnji)) {
                return grana.getTezinaGrane();
            }
            //graf neusmjeren
            if (grana.getPocetniCvor().equals(krajnji) && grana.getKrajnjiCvor().equals(pocetni)) {
                return grana.getTezinaGrane();
            }
        }
        return 0;
    }

    private Cvor getMinimum(Set<Cvor> vertexes) {
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
        return minimum;
    }

    private boolean posjecen(Cvor cvor) {
        for(Cvor cvor1: posjeceniCvorovi) {
            if(cvor1.getOznaka().equals(cvor.getOznaka())){
                return true;
            }
        }
        return false;
    }

    private int getShortestDistance(Cvor cvor) {
        //provjeravamo da li je već pronađen put od zadanog pocetnog cvora do cvor
        Integer d = udaljenosti.get(cvor);
        if (d == null) {
            return Integer.MAX_VALUE;
        } else {
            return d;
        }
    }

    public LinkedList<String> dajNajkraciPut(Cvor krajnji) {
        LinkedList<String> path = new LinkedList<>();
        String step = krajnji.getOznaka();
        if (prethodnici.get(step) == null) {
            return null;
        }
        path.add(step);
        while (prethodnici.get(step) != null) {
            step = prethodnici.get(step);
            path.add(step);
        }
        Collections.reverse(path);
        return path;
    }

    private void najkraciPutDijkstra(Cvor pocetni, Cvor kranji) {
        sljedeciCvorovi = new HashSet<>();
        posjeceniCvorovi = new HashSet<>();
        udaljenosti = new HashMap<>();
        prethodnici = new HashMap<>();
        ArrayList<Integer> duzine = new ArrayList<>();
        ArrayList<Cvor> susjedniPocetnog = dajSusjedneCvorove(pocetni);
        udaljenosti.put(pocetni, 0);
        sljedeciCvorovi.add(pocetni);
        while (sljedeciCvorovi.size() > 0) {
            Cvor cvor = getMinimum(sljedeciCvorovi);
            posjeceniCvorovi.add(cvor);
            sljedeciCvorovi.remove(cvor);
            findMinimalDistances(cvor);
        }
        Pair<Cvor, Cvor> pocetniKrajnjiPar = new Pair<>(pocetni, kranji);
        LinkedList<String> cvoroviNaPutu = dajNajkraciPut(kranji);
        ArrayList<Grana> graneOdPocetnogDoKrajnjeg = new ArrayList<>();
        for( int i = 0; i < cvoroviNaPutu.size() - 1; i++) {
            Grana grana = dajGranu(pocetni.getOznaka(), kranji.getOznaka(), cvoroviNaPutu.get(i), cvoroviNaPutu.get(i + 1));
            graneOdPocetnogDoKrajnjeg.add(grana);
        }

        for(Map.Entry<Cvor, Integer> entry: udaljenosti.entrySet()) {
            if(entry.getKey().getOznaka().equals(kranji.getOznaka())) {
                puteviIzmedjuCvorova.put(new Pair(pocetniKrajnjiPar,entry.getValue()), graneOdPocetnogDoKrajnjeg);
            }
        }
    }
    private void dodajObrnutuGranuZaParCvorova(String cvor1, String cvor2, Grana g) {
        boolean dodana = false;
        for(Map.Entry<Pair<Cvor,Cvor>, ArrayList<Grana>> entry: obrnuteGraneUPutu.entrySet()) {
            if(entry.getKey().getKey().getOznaka().equals(cvor1) && entry.getKey().getValue().getOznaka().equals(cvor2)) {
                entry.getValue().add(g);
                dodana = true;
            }
        }
        if(!dodana) {
            ArrayList<Grana> listaGrana = new ArrayList<>();
            listaGrana.add(g);
            obrnuteGraneUPutu.put(new Pair(dajCvor(cvor1), dajCvor(cvor2)), listaGrana);
        }
    }
    private Grana dajGranu(String pocetniPuta, String krajnjiPuta, String cvor1, String cvor2) {
        /* pronalazi granu u grafu sa zadanim pocetnim i krajnjim cvorom */
        for (Grana grana: graf.getGrane()) {
            if(grana.getPocetniCvor().getOznaka().equals(cvor1) && grana.getKrajnjiCvor().getOznaka().equals(cvor2)) {
                return grana;
            }
            else if(grana.getPocetniCvor().getOznaka().equals(cvor2) && grana.getKrajnjiCvor().getOznaka().equals(cvor1)) {
                /* kod neusmjerenih grafova - za par cvorova cuvamo posebno grane suprote orijentacije */
                dodajObrnutuGranuZaParCvorova(pocetniPuta, krajnjiPuta, grana);
                return grana;
            }
        }
        return null;
    }

    private boolean daLiJeObrnutaGranaUPutu(Pair<Cvor,Cvor> par, Grana grana) {
        for(Map.Entry<Pair<Cvor,Cvor>, ArrayList<Grana>> entry: obrnuteGraneUPutu.entrySet()) {
            if(entry.getKey().getKey().getOznaka().equals(par.getKey().getOznaka()) && entry.getKey().getValue().getOznaka().equals(par.getValue().getOznaka())) {
                for (Grana grana1 : entry.getValue()) {

                    if (grana.getId() == grana1.getId()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void vratiGraneNaPocetnoStanje() {
        for(Grana grana: graf.getGrane()) {
            grana.setsuprotSmjeru(false);
        }
    }

    private Grana dajGranu(Cvor cvor1, Cvor cvor2) {
        for (Grana grana: graf.getGrane()) {
            if(grana.getPocetniCvor().getOznaka().equals(cvor1) && grana.getKrajnjiCvor().getOznaka().equals(cvor2)) {
                return grana;
            }
            else if(grana.getPocetniCvor().getOznaka().equals(cvor2) && grana.getKrajnjiCvor().getOznaka().equals(cvor1)) {
                return grana;
            }
        }
        return null;
    }
    private int dajTezinuPuta(Cvor pocetni, Cvor zavrsni) {
        for(Map.Entry<Pair<Pair<Cvor, Cvor>, Integer>, ArrayList<Grana>> entry: puteviIzmedjuCvorova.entrySet()) {
            if(entry.getKey().getKey().getKey().getOznaka().equals(pocetni.getOznaka()) && entry.getKey().getKey().getValue().getOznaka().equals(zavrsni.getOznaka())) {
                return entry.getKey().getValue();
            }
        }
        return 0;
    }
    private void ukloniIskoristeneCvorove(ArrayList<Pair<Cvor, Cvor>> parovi, Pair<Cvor,Cvor> par) {
        for(int i = 0; i < parovi.size(); i++) {
            if(parovi.get(i).getKey().getOznaka().equals(par.getKey().getOznaka()) || parovi.get(i).getValue().getOznaka().equals(par.getValue().getOznaka()) || parovi.get(i).getKey().getOznaka().equals(par.getValue().getOznaka()) || parovi.get(i).getValue().getOznaka().equals(par.getKey().getOznaka())) {
                parovi.remove(i);
                i--;
            }
        }
    }
    private int dajSumuTezinaPuteva(ArrayList<Pair<Cvor, Cvor>> parovi) {
        int suma = 0;
        for(int i = 0; i < parovi.size(); i++) {
            suma = suma + dajTezinuPuta(parovi.get(i).getKey(), parovi.get(i).getValue());
        }
        return suma;
    }
    private ArrayList<Pair<Cvor,Cvor>> dajKopiju(ArrayList<Pair<Cvor,Cvor>> parovi) {
        ArrayList<Pair<Cvor,Cvor>> kopija = new ArrayList<>();
        for(Pair<Cvor,Cvor> par: parovi) kopija.add(new Pair<>(par.getKey(), par.getValue()));
        return kopija;
    }
    private ArrayList<Pair<Cvor,Cvor>> dajMogucaUparivanjaGrana(ArrayList<Pair<Cvor, Cvor>> paroviCvorova) {
        int brojCvorovaNeparnogStepena = dajBrojCvorovaNeparnogStepena();
        int brojDupliciranihPuteva = brojCvorovaNeparnogStepena/2;
        ArrayList<Pair<Cvor,Cvor>> uparivanjePutevi = new ArrayList<>();
        ArrayList<Pair<Cvor,Cvor>> pomocnaLista = new ArrayList<>();
        ArrayList<Pair<Cvor,Cvor>> parovi = dajKopiju(paroviCvorova);
        int brojMogucihUparivanja = 1;
        for(int i = 1; i < brojCvorovaNeparnogStepena; i += 2) {
            brojMogucihUparivanja = brojMogucihUparivanja * i;
        }
        int brojac = 0, brojacUparivanja = 0, sumaTezina = 0;
        ArrayList<Pair<Cvor, Cvor>> temp = paroviCvorova;
        for(int i = 0; i < paroviCvorova.size(); i++) {
            pomocnaLista = new ArrayList<>();
            brojac = 0;
            parovi = dajKopiju(paroviCvorova);
            pomocnaLista.add(paroviCvorova.get(i));
            brojac++;
            ukloniIskoristeneCvorove(parovi, paroviCvorova.get(i));
            for(int j = 0; j < parovi.size(); j++) {
                pomocnaLista.add(parovi.get(j));
                brojac++;
                ukloniIskoristeneCvorove(parovi, parovi.get(j));
                if (brojac == brojDupliciranihPuteva) j = parovi.size();
            }
            if(brojac == brojDupliciranihPuteva) {
                if(uparivanjePutevi.size() == 0) {
                    sumaTezina = dajSumuTezinaPuteva(pomocnaLista);
                    uparivanjePutevi = pomocnaLista;
                }
                else {
                    if(sumaTezina > dajSumuTezinaPuteva(pomocnaLista)) {
                        uparivanjePutevi = pomocnaLista;
                        sumaTezina = dajSumuTezinaPuteva(pomocnaLista);
                    }

                }
            }
            brojacUparivanja++;
            if(brojacUparivanja == brojMogucihUparivanja) break;
        }
        ArrayList<Pair<Cvor, Cvor>> graneKojeSeDupliciraju = new ArrayList<>();
        for(Pair<Cvor,Cvor> par: uparivanjePutevi) {
            for(Map.Entry<Pair<Pair<Cvor,Cvor>,Integer>, ArrayList<Grana>> entry: puteviIzmedjuCvorova.entrySet()) {
                if(entry.getKey().getKey().getKey().getOznaka().equals(par.getKey().getOznaka()) && entry.getKey().getKey().getValue().getOznaka().equals(par.getValue().getOznaka())) {
                    for(Grana grana: entry.getValue()) {
                        graneKojeSeDupliciraju.add(new Pair<>(grana.getPocetniCvor(), grana.getKrajnjiCvor()));
                    }
                }
            }
        }
        return graneKojeSeDupliciraju;
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
        int duzinaPuta = 0;

        //pronalazimo najkrace puteve izmedju cvorova koji cine jedan par
        for (Pair<Cvor,Cvor> par: paroviCvorovaNeparnogStepena) {
            najkraciPutDijkstra(par.getKey(),par.getValue());
        }

        for(Map.Entry<Pair<Pair<Cvor, Cvor>, Integer>, ArrayList<Grana>> entry: puteviIzmedjuCvorova.entrySet()) {
            int brojGrana = entry.getValue().size();
            for(int i = 0; i < brojGrana; i++) {
                //probjeravamo da li za razmatrani par cvorova trenutna grana u obrnutom smjeru
                if (daLiJeObrnutaGranaUPutu(entry.getKey().getKey(),entry.getValue().get(i))) {
                    if (i == brojGrana - 1) System.out.println(entry.getValue().get(i).getKrajnjiCvor().getOznaka() + "-" + entry.getValue().get(i).getPocetniCvor().getOznaka());
                    else System.out.print(entry.getValue().get(i).getKrajnjiCvor().getOznaka() + "-");
                } else {
                    if (i == brojGrana - 1) System.out.println(entry.getValue().get(i).getPocetniCvor().getOznaka() + "-" + entry.getValue().get(i).getKrajnjiCvor().getOznaka());
                    else System.out.print(entry.getValue().get(i).getPocetniCvor().getOznaka() + "-");
                }
            }
        }
        ArrayList<Pair<Cvor, Cvor>> uparivanja = dajMogucaUparivanjaGrana(paroviCvorovaNeparnogStepena);
        System.out.println("Uparivanja:");
        for(int i = 0; i < uparivanja.size(); i++) {
            System.out.println(uparivanja.get(i).getKey().getOznaka() + " " + uparivanja.get(i).getValue().getOznaka());
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
            int brojCvorovaNeparnogStepena = dajBrojCvorovaNeparnogStepena();
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
