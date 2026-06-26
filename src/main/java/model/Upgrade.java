package model;

public abstract class Upgrade {

    protected String id;
    protected String nome;
    protected long custo;
    protected boolean comprado;
    protected double multiplicador;

    public abstract String getGrupo();

    public abstract String getDescricao();

    public Upgrade(String id, String nome, long custo, double multiplicador) {
        this.id = id;
        this.nome = nome;
        this.custo = custo;
        this.comprado = false;
        this.multiplicador = multiplicador;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public long getCusto() {
        return custo;
    }

    public boolean isComprado() {
        return comprado;
    }

    public double getMultiplicador() {
        return multiplicador;
    }

    public void comprar() {
        comprado = true;
    }
}
