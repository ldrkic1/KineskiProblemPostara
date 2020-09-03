package sample;

public class Grana {
    private int id;
    private Cvor pocetniCvor;
    private Cvor krajnjiCvor;
    private int tezinaGrane;
    private boolean suprotanSmjer=false;
    private boolean sadrzanaUKonturi = false;
    public Grana() {
    }

    public Grana(int id, Cvor pocetniCvor, Cvor krajnjiCvor, int tezinaGrane) {
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

    public int getTezinaGrane() {
        return tezinaGrane;
    }

    public void setTezinaGrane(int tezinaGrane) {
        this.tezinaGrane = tezinaGrane;
    }

    public boolean isSuprotanSmjer() {
        return suprotanSmjer;
    }

    public void setsuprotSmjeru(boolean uSuprotnomSmjeru) {
        this.suprotanSmjer = uSuprotnomSmjeru;
    }

    public boolean isSadrzanaUKonturi() {
        return sadrzanaUKonturi;
    }

    public void setSadrzanaUKonturi(boolean sadrzanaUKonturi) {
        this.sadrzanaUKonturi = sadrzanaUKonturi;
    }

    @Override
    public boolean equals(Object obj) {
        Grana grana = (Grana) obj;
        return grana.id == id && grana.pocetniCvor.equals(pocetniCvor) && grana.krajnjiCvor.equals(krajnjiCvor) && grana.tezinaGrane == tezinaGrane;
    }

    @Override
    public String toString() {
        return "Grana: " +
                "id = " + id +
                ", pocetniCvor = " + pocetniCvor +
                ", krajnjiCvor = " + krajnjiCvor +
                ", tezinaGrane = " + tezinaGrane;
    }
}
