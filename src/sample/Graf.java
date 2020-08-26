package sample;

import java.util.ArrayList;

public class Graf {
    private ArrayList<Cvor> cvorovi;
    private ArrayList<Grana> grane;

    public Graf() {
        cvorovi = new ArrayList<>();
        grane = new ArrayList<>();
    }

    public Graf(ArrayList<Cvor> cvorovi, ArrayList<Grana> grane) {
        this.cvorovi = cvorovi;
        this.grane = grane;
    }

    public ArrayList<Cvor> getCvorovi() {
        return cvorovi;
    }

    public void setCvorovi(ArrayList<Cvor> cvorovi) {
        this.cvorovi = cvorovi;
    }

    public ArrayList<Grana> getGrane() {
        return grane;
    }

    public void setGrane(ArrayList<Grana> grane) {
        this.grane = grane;
    }
}
