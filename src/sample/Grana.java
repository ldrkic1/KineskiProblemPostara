package sample;

public class Grana {
    private int id;
    private Cvor pocetniCvor;
    private Cvor krajnjiCvor;
    private double tezinaGrane;

    public Grana() {
    }

    public Grana(int id, Cvor pocetniCvor, Cvor krajnjiCvor, double tezinaGrane) {
        this.id = id;
        this.pocetniCvor = pocetniCvor;
        this.krajnjiCvor = krajnjiCvor;
        this.tezinaGrane = tezinaGrane;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cvor getPocetniCvor() {
        return pocetniCvor;
    }

    public void setPocetniCvor(Cvor pocetniCvor) {
        this.pocetniCvor = pocetniCvor;
    }

    public Cvor getKrajnjiCvor() {
        return krajnjiCvor;
    }

    public void setKrajnjiCvor(Cvor krajnjiCvor) {
        this.krajnjiCvor = krajnjiCvor;
    }

    public double getTezinaGrane() {
        return tezinaGrane;
    }

    public void setTezinaGrane(double tezinaGrane) {
        this.tezinaGrane = tezinaGrane;
    }

    @Override
    public boolean equals(Object obj) {
        Grana grana = (Grana) obj;
        return grana.id == id && grana.pocetniCvor.equals(pocetniCvor) && grana.krajnjiCvor.equals(krajnjiCvor) && grana.tezinaGrane == tezinaGrane;
    }
}
