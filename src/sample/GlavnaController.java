package sample;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.io.IOException;
import java.util.*;
import static javafx.scene.control.PopupControl.USE_COMPUTED_SIZE;
public class GlavnaController {
    public VBox unosVbox;
    public Button pronadjiRjesenjeButton;
    public ImageView slika;
    private int brojGrana, brojCvorova;
    private ArrayList<ArrayList<Integer>> listaSusjedstva = new ArrayList<>();
    private ObservableList<Integer> cvorovi = null;
    private int brojacOdabranihCvorova=0;
    private boolean tezinaIspravna = false;
    private Graf graf;
    private Set<Cvor> posjeceniCvorovi = new HashSet<>();
    private Set<Cvor> sljedeciCvorovi = new HashSet<>();
    private Map<String, String> prethodnici = new HashMap<>();
    /* udaljenosti od zadanog pocetnog do cvora koji predstavlja key mape */
    private Map<Cvor, Integer> udaljenosti = new HashMap<>();
    /* kljuc - par kojeg cine par cvorova nepranog stepena i duzina puta izmedju njih, value - lista grana koje cine put */
    private Map<Pair<Pair<Cvor,Cvor>, Integer>, ArrayList<Grana>> puteviIzmedjuCvorova = new HashMap<>();
    private Map<Pair<Cvor,Cvor>,ArrayList<Grana>> obrnuteGraneUPutu = new HashMap<>();
    public GlavnaController(int brojCvorova, int brojGrana) {
        this.brojCvorova = brojCvorova;
        this.brojGrana= brojGrana;
        graf = new Graf();
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 1; i <= brojCvorova; i++) {
            list.add(i);
            graf.getCvorovi().add(new Cvor(graf.getCvorovi().size() + 1, String.valueOf(i)));
        }
        cvorovi = FXCollections.observableArrayList(list);
        for(int i = 0; i < brojCvorova; i++) {
            listaSusjedstva.add(new ArrayList<>());
        }
    }
    public void dodajPoljaZaUnosGrane() {
        /* kreiranje polja za unos podataka za odgovarajuci broj grana */
        Image slikaPostar = new Image("/images/razmislja.png");
        slika.setImage(slikaPostar);
        slika.setFitHeight(377);
        slika.setFitWidth(289);
        tezinaIspravna = false;
        HBox granaHbox = new HBox();
        granaHbox.setAlignment(Pos.CENTER);
        granaHbox.setSpacing(5);
        Label pocetniLabel = new Label("Početni čvor:");
        pocetniLabel.setFont(new Font(16));
        Separator separator1 = new Separator();
        separator1.setOrientation(Orientation.VERTICAL);
        Separator separator2 = new Separator();
        separator2.setOrientation(Orientation.VERTICAL);
        ChoiceBox pocetniChoice = new ChoiceBox();
        pocetniChoice.setItems(cvorovi);
        pocetniChoice.setPrefWidth(100);
        pocetniChoice.getStyleClass().add("nijeOdabran");
        Label krajnjiLabel = new Label("Krajnji čvor:");
        krajnjiLabel.setFont(new Font(16));
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
        Label tezinaLabel = new Label("Težina grane:");
        tezinaLabel.setFont(new Font(16));
        TextField tezinaField = new TextField();
        tezinaField.getStyleClass().add("neispravnoPolje");
        if(tezinaField.getStyleClass().contains("neispravnoPolje")) tezinaIspravna = false;
        tezinaField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if(PocetnaController.isInt(newValue)) {
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
        granaHbox.getChildren().addAll(pocetniLabel, pocetniChoice, separator1, krajnjiLabel, krajnjiChoice, separator2, tezinaLabel, tezinaField);
        unosVbox.getChildren().add(granaHbox);
    }
    @FXML
    public void initialize() {
        for(int i = 0; i < brojGrana; i++) {
            dodajPoljaZaUnosGrane();
        }
    }
    /* Da li je graf povezan provjerava se DFS pretragom */
    private void DFS(int i, boolean[] posjecen) {
        /* Oznacava trenutni cvor kao posjecen */
        posjecen[i] = true;
        for (int j : listaSusjedstva.get(i)) {
            if(!posjecen[j]) DFS(j,posjecen);
        }
    }
    private int dajBrojPovezanihKomponentiGrafa() {
        int brojPovezanihKomponenti = 0;
        /* sve cvorove oznaci kao neposjecene */
        boolean[] posjecen = new boolean[brojCvorova];
        for(int i = 0; i < brojCvorova; i++) posjecen[i] = false;
        for(int i = 0; i < brojCvorova; ++i) {
            if(!posjecen[i]) {
                DFS(i,posjecen);
                brojPovezanihKomponenti++;
            }
        }
        return brojPovezanihKomponenti;
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
            /* Neusmjereni graf */
            if(grana.getKrajnjiCvor().equals(cvor)) susjedi.add(grana.getPocetniCvor());
            else if (grana.getPocetniCvor().equals(cvor)) susjedi.add(grana.getKrajnjiCvor());
        }
        return susjedi;
    }
    private boolean postojiUdaljenost(Cvor cvor) {
        /* Provjerava da li vec postoji put do cvora */
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
        /* Trazimo susjede zadanog cvora za koje nije pronađena udaljenost */
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
    private void pronadjiNajkracePuteve(Cvor node) {
        ArrayList<Cvor> susjedni = dajSusjedneNeposjeceneCvorove(node);
        for (Cvor krajni : susjedni) {
            if (dajNajkracuUdaljenost(krajni) > dajNajkracuUdaljenost(node) + tezinaGrane(node, krajni)) {
                if(postojiUdaljenost(krajni)) {
                    for(Map.Entry<Cvor, Integer> entry: udaljenosti.entrySet()) {
                        if(entry.getKey().getOznaka().equals(krajni.getOznaka())) {
                            if(entry.getValue() > dajNajkracuUdaljenost(node) + tezinaGrane(node, krajni)) {
                                entry.setValue(dajNajkracuUdaljenost(node) + tezinaGrane(node, krajni));
                                prethodnici.put(krajni.getOznaka(), node.getOznaka());
                            }
                        }
                    }
                }
                else {
                    udaljenosti.put(krajni,  (dajNajkracuUdaljenost(node) + tezinaGrane(node, krajni)));
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
            if (grana.getPocetniCvor().equals(krajnji) && grana.getKrajnjiCvor().equals(pocetni)) {
                return grana.getTezinaGrane();
            }
        }
        return 0;
    }
    private Cvor dajCvorNajkraceUdaljenosti(Set<Cvor> cvorovi) {
        Cvor minimum = null;
        for (Cvor cvor : cvorovi) {
            if (minimum == null) {
                minimum = cvor;
            } else {
                if (dajNajkracuUdaljenost(cvor) < dajNajkracuUdaljenost(minimum)) {
                    minimum = cvor;
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
    private int dajNajkracuUdaljenost(Cvor cvor) {
        /* provjeravamo da li je vec pronadjen put od zadanog pocetnog cvora do cvor */
        Integer d = udaljenosti.get(cvor);
        if (d == null) {
            return Integer.MAX_VALUE;
        } else {
            return d;
        }
    }
    public LinkedList<String> dajNajkraciPut(Cvor krajnji) {
        LinkedList<String> put = new LinkedList<>();
        String cvor = krajnji.getOznaka();
        if (prethodnici.get(cvor) == null) {
            return null;
        }
        put.add(cvor);
        while (prethodnici.get(cvor) != null) {
            cvor = prethodnici.get(cvor);
            put.add(cvor);
        }
        Collections.reverse(put);
        return put;
    }

    private void najkraciPutDijkstra(Cvor pocetni, Cvor kranji) {
        sljedeciCvorovi = new HashSet<>();
        posjeceniCvorovi = new HashSet<>();
        udaljenosti = new HashMap<>();
        prethodnici = new HashMap<>();
        udaljenosti.put(pocetni, 0);
        sljedeciCvorovi.add(pocetni);
        while (sljedeciCvorovi.size() > 0) {
            Cvor cvor = dajCvorNajkraceUdaljenosti(sljedeciCvorovi);
            posjeceniCvorovi.add(cvor);
            sljedeciCvorovi.remove(cvor);
            pronadjiNajkracePuteve(cvor);
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
    private boolean sadrziUparivanje(ArrayList<ArrayList<Pair<Cvor,Cvor>>> uparivanja, ArrayList<Pair<Cvor,Cvor>> novoUparivanje) {
        for(ArrayList<Pair<Cvor,Cvor>> lista: uparivanja) {
            int brojac = 0;
            for(Pair<Cvor, Cvor> parListe: lista) {
                for(Pair<Cvor, Cvor> par: novoUparivanje) {
                    if(parListe.getKey().getOznaka().equals(par.getKey().getOznaka()) && parListe.getValue().getOznaka().equals(par.getValue().getOznaka())) brojac++;
                }
            }
            if(brojac == novoUparivanje.size()) {
                return true;
            }
        }
        return false;
    }
    private ArrayList<Pair<Cvor,Cvor>> dajMogucaUparivanjaGrana(ArrayList<Pair<Cvor, Cvor>> paroviCvorova) {
        ArrayList<ArrayList<Pair<Cvor,Cvor>>> pronadjenaUparivanja = new ArrayList<>();
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
        int pocetniIndex = 0;
        for(int i = 0; i < paroviCvorova.size(); i++) {
            pomocnaLista = new ArrayList<>();
            brojac = 0;
            parovi = dajKopiju(paroviCvorova);
            pomocnaLista.add(paroviCvorova.get(i));
            brojac++;
            ukloniIskoristeneCvorove(parovi, paroviCvorova.get(i));
            int pocetniJ = 0;
            for(int j = pocetniIndex; j < parovi.size(); j++) {
                pomocnaLista.add(parovi.get(j));
                brojac++;
                ukloniIskoristeneCvorove(parovi, parovi.get(j));
                j = -1;
                if (brojac == brojDupliciranihPuteva) {
                    boolean sadrzanoUparivanje = false;
                    if(uparivanjePutevi.size() == 0) {
                        /* prvo uparivanje */
                        pronadjenaUparivanja.add(dajKopiju(pomocnaLista));
                        brojacUparivanja++;
                    }
                    if(uparivanjePutevi.size() != 0) {
                        sadrzanoUparivanje = sadrziUparivanje(pronadjenaUparivanja, pomocnaLista);
                        if(!sadrzanoUparivanje)  {
                            /* pronadjeno bolje uparivanje */
                            pronadjenaUparivanja.add(dajKopiju(pomocnaLista));
                            brojacUparivanja++;
                            j = parovi.size();

                        }
                    }
                }
            }
            if (brojac == brojDupliciranihPuteva) {
                if (uparivanjePutevi.size() == 0) {
                    sumaTezina = dajSumuTezinaPuteva(pomocnaLista);
                    uparivanjePutevi = pomocnaLista;
                } else {
                    if (sumaTezina > dajSumuTezinaPuteva(pomocnaLista)) {
                        uparivanjePutevi = pomocnaLista;
                        sumaTezina = dajSumuTezinaPuteva(pomocnaLista);
                    }
                }
            }
            if(brojacUparivanja == brojMogucihUparivanja){
                break;
            }
            if(brojacUparivanja != brojMogucihUparivanja && i == paroviCvorova.size() - 1) {
                /* ukoliko nisu pronadjena sva moguća uparivanja grana - vracamo se na pocetak
                i trazimo drugim redoslijedom */
                i = -1;
                pocetniIndex++;
                if(pocetniIndex == paroviCvorova.size()) break;
            }
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
    private void dupicirajGraneUGrafu(ArrayList<Pair<Cvor, Cvor>> grane) {
        for(Pair<Cvor, Cvor> par: grane) {
            Grana grana = new Grana();
            grana.setId(graf.getGrane().size() + 1);
            grana.setPocetniCvor(par.getKey());
            grana.setKrajnjiCvor(par.getValue());
            grana.setTezinaGrane(tezinaGrane(grana.getPocetniCvor(), grana.getKrajnjiCvor()));
            graf.getGrane().add(grana);
            brojGrana++;
        }
    }
    private void algoritamEdmondsJohnson() {
        /* kreira listu cvorova neparnog stepena */
        ArrayList<Cvor> cvoroviNepranogStepena = new ArrayList<>();
        for (Cvor cvor: graf.getCvorovi()) {
            if(cvor.getStepen() % 2 == 1) cvoroviNepranogStepena.add(cvor);
        }
        /* kreira listu svih parova cvorova neparnog stepena */
        ArrayList<Pair<Cvor, Cvor>> paroviCvorovaNeparnogStepena = new ArrayList<>();
        for(int i = 0; i < cvoroviNepranogStepena.size(); i++) {
            for(int j = i + 1; j < cvoroviNepranogStepena.size(); j++) {
                paroviCvorovaNeparnogStepena.add(new Pair(cvoroviNepranogStepena.get(i), cvoroviNepranogStepena.get(j)));
            }
        }
        int duzinaPuta = 0;

        /* pronalazi najkrace puteve izmedju cvorova koji cine jedan par */
        for (Pair<Cvor,Cvor> par: paroviCvorovaNeparnogStepena) {
            najkraciPutDijkstra(par.getKey(),par.getValue());
        }

        for(Map.Entry<Pair<Pair<Cvor, Cvor>, Integer>, ArrayList<Grana>> entry: puteviIzmedjuCvorova.entrySet()) {
            int brojGrana = entry.getValue().size();
            for(int i = 0; i < brojGrana; i++) {
                /* provjeravamo da li za razmatrani par cvorova trenutna grana u obrnutom smjeru */
                if (daLiJeObrnutaGranaUPutu(entry.getKey().getKey(),entry.getValue().get(i))) {
                    if (i == brojGrana - 1) System.out.println(entry.getValue().get(i).getKrajnjiCvor().getOznaka() + "-" + entry.getValue().get(i).getPocetniCvor().getOznaka());
                    else System.out.print(entry.getValue().get(i).getKrajnjiCvor().getOznaka() + "-");
                } else {
                    if (i == brojGrana - 1) System.out.println(entry.getValue().get(i).getPocetniCvor().getOznaka() + "-" + entry.getValue().get(i).getKrajnjiCvor().getOznaka());
                    else System.out.print(entry.getValue().get(i).getPocetniCvor().getOznaka() + "-");
                }
            }
        }
        /* trazimo najbolje uparivanje */
        ArrayList<Pair<Cvor, Cvor>> najboljeUparivanje = dajMogucaUparivanjaGrana(paroviCvorovaNeparnogStepena);
        System.out.println("Uparivanja:");
        for(int i = 0; i < najboljeUparivanje.size(); i++) {
            System.out.println(najboljeUparivanje.get(i).getKey().getOznaka() + " " + najboljeUparivanje.get(i).getValue().getOznaka());
        }

        /* dupiciranje grana */
        dupicirajGraneUGrafu(najboljeUparivanje);
        System.out.println("Grane u grafu:");
        for(Grana grana: graf.getGrane()) System.out.println(grana.toString());
    }
    /* pronalazi drugi cvor neposjecene grane */
    private Cvor dajDrugiCvorIzNeposjecene(Cvor cvor) {
        for(Grana grana: graf.getGrane()) {
            if(grana.isSadrzanaUKonturi()) {
                continue;
            }
            else {
                if(grana.getPocetniCvor().getOznaka().equals(cvor.getOznaka())) {
                    grana.setSadrzanaUKonturi(true);
                    return grana.getKrajnjiCvor();
                }
                if(grana.getKrajnjiCvor().getOznaka().equals(cvor.getOznaka())) {
                    grana.setSadrzanaUKonturi(true);
                    return grana.getPocetniCvor();
                }
            }
        }
        return null;
    }
    private Cvor dajCvorIzNeposjeceneGrane() {
        for(Grana grana: graf.getGrane()) {
            if(grana.isSadrzanaUKonturi()) {
                continue;
            }
            else {
                return grana.getPocetniCvor();
            }
        }
        return null;
    }
    private int indexCvoraUKonturi(ArrayList<Cvor> cvorovi, Cvor cvor) {
        int index = 0;
        for(int i = 0; i < cvorovi.size(); i++) {
            if(cvorovi.get(i).getOznaka().equals(cvor.getOznaka())) {
                index = i;
            }
        }
        return index;
    }
    private ArrayList<Cvor> nadjiEulerovuKonturu() {
    /* Pronalazak Eulerove konture pomoću Hierholzerovog algoritma */
        Cvor pocetni = graf.getCvorovi().get(0);
        ArrayList<Cvor> cvoroviUKonturi = new ArrayList<>();
        cvoroviUKonturi.add(pocetni);
        int brojGranaUKonturi = 0;
        Cvor trenutni = pocetni;
        while (brojGranaUKonturi != brojGrana) {
            ArrayList<Cvor> zatvoreniPut = new ArrayList<>();
            zatvoreniPut.add(trenutni);
            trenutni = dajDrugiCvorIzNeposjecene(trenutni);
            zatvoreniPut.add(trenutni);
            brojGranaUKonturi++;
            while (!trenutni.getOznaka().equals(pocetni.getOznaka())) {
                trenutni = dajDrugiCvorIzNeposjecene(trenutni);
                zatvoreniPut.add(trenutni);
                brojGranaUKonturi++;
            }
            int index = indexCvoraUKonturi(cvoroviUKonturi, pocetni);
            index++;
            for(int i = 1; i < zatvoreniPut.size(); i++) {
                if(index != cvoroviUKonturi.size()) cvoroviUKonturi.add(index, zatvoreniPut.get(i));
                else cvoroviUKonturi.add(zatvoreniPut.get(i));
                index++;
            }
            pocetni = dajCvorIzNeposjeceneGrane();
            trenutni = pocetni;

        }
        return cvoroviUKonturi;
    }
    public void pronadjiRjesenjeAction(ActionEvent actionEvent) throws IOException {
        if (provjeriIspravnostUnesenihTezina() && brojacOdabranihCvorova == brojGrana * 2) {
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
                    listaSusjedstva.get(Integer.parseInt(granaGrafa.getPocetniCvor().getOznaka()) - 1).add(Integer.parseInt(granaGrafa.getKrajnjiCvor().getOznaka()) - 1);
                    listaSusjedstva.get(Integer.parseInt(granaGrafa.getKrajnjiCvor().getOznaka()) - 1).add(Integer.parseInt(granaGrafa.getPocetniCvor().getOznaka()) - 1);
                }
            }
            int brojKomponenti = dajBrojPovezanihKomponentiGrafa();
            System.out.println("Broj komponenti = " + brojKomponenti);
            if (brojKomponenti == 1) {
                izracunajStepeneCvorova();
                int brojCvorovaNeparnogStepena = dajBrojCvorovaNeparnogStepena();
                System.out.println("Broj cvorova neparnog stepena " + brojCvorovaNeparnogStepena);
                if (brojCvorovaNeparnogStepena != 0) {
                    algoritamEdmondsJohnson();
                }
                /* Eulerova kontura */
                ArrayList<Cvor> postarovPut = nadjiEulerovuKonturu();
                /* Ispis puta */
                System.out.println("Optimalna poštarova ruta je: ");
                for(int i = 0; i < postarovPut.size(); i++) {
                    if( i == postarovPut.size() - 1) System.out.print(postarovPut.get(i).getOznaka());
                    else System.out.print(postarovPut.get(i).getOznaka()+ "-");
                }
                Stage trenutniStage = (Stage) unosVbox.getScene().getWindow();
                Stage rjesenjeStage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/rjesenje.fxml"));
                RjesenjeContoller rjesenjeContoller = new RjesenjeContoller(postarovPut);
                loader.setController(rjesenjeContoller);
                Parent root = loader.load();
                rjesenjeStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
                rjesenjeStage.setTitle("Rješenje kineskog problema poštara");
                trenutniStage.close();
                rjesenjeStage.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Greška");
                alert.setHeaderText(null);
                alert.setContentText("Graf koji ste unijeli nije povezan, te nije moguće pronaći optimalno rješenje kineskog problema poštara!");
                alert.showAndWait();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Greška");
            alert.setHeaderText(null);
            alert.setContentText("Unesite tražene podatke ispravno!");
            alert.showAndWait();
        }
    }
    public void pocetakAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) unosVbox.getScene().getWindow();
        PocetnaController ctrl = new PocetnaController();
        FXMLLoader loader = new  FXMLLoader(getClass().getResource("/fxml/pocetna.fxml"));
        loader.setController(ctrl);
        Parent root = loader.load();
        Stage pocetna = new Stage();
        pocetna.setTitle("Kineski problem poštara");
        pocetna.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.close();
        pocetna.show();
    }
    public void dodajGranuAction(ActionEvent actionEvent) {
        dodajPoljaZaUnosGrane();
        brojGrana++;
    }
}