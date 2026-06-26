package model;

public class EstadoJogo {

    private long instancias;
    private long totalCliques;

    public EstadoJogo() {
        this.instancias = 0;
        this.totalCliques = 0;
    }

    public long getInstancias() {
        return instancias;
    }

    public long getTotalCliques() {
        return totalCliques;
    }

    public void setInstancias(long instancias) {
        this.instancias = instancias;
    }

    public void adicionarInstancias(long quantidade) {
        instancias += quantidade;
    }

    public void removerInstancias(long quantidade) {
        instancias -= quantidade;
    }

    public void registrarClique() {
        totalCliques++;
    }
}