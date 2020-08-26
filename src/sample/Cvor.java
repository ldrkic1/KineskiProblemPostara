package sample;

public class Cvor {
    private int id;
    private String oznaka;
    private int stepen;

    public Cvor() {
    }

    public Cvor(int id, String oznaka) {
        this.id = id;
        this.oznaka = oznaka;
    }

    public Cvor(int id, String oznaka, int stepen) {
        this.id = id;
        this.oznaka = oznaka;
        this.stepen = stepen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOznaka() {
        return oznaka;
    }

    public void setOznaka(String oznaka) {
        this.oznaka = oznaka;
    }

    public int getStepen() {
        return stepen;
    }

    public void setStepen(int stepen) {
        this.stepen = stepen;
    }

    @Override
    public String toString() {
        return oznaka;
    }

    @Override
    public boolean equals(Object obj) {
        Cvor cvor = (Cvor) obj;
        return cvor.id == id && cvor.oznaka.equals(oznaka);
    }
}
