package model;

public abstract class Construcao implements Compravel, Produtor {

    protected String nome;
    protected long custoBase;
    protected long producaoBase;
    protected int quantidade;
    protected String id;
    protected double multiplicadorProducao;

    public Construcao(String id, String nome, long custoBase, long producaoBase) {
        this.id = id;
        this.nome = nome;
        this.custoBase = custoBase;
        this.producaoBase = producaoBase;
        this.quantidade = 0;
        this.multiplicadorProducao = 1.0;
    }

    public double getMultiplicadorProducao() {
        return multiplicadorProducao;
    }

    public void setMultiplicadorProducao(double multiplicadorProducao) {
        this.multiplicadorProducao = multiplicadorProducao;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public long getProducaoBase() {
        return producaoBase;
    }

    public long getProducaoAtual() {
        return (long)(producaoBase * multiplicadorProducao);
    }

    public long getCustoBase() {
        return custoBase;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public void adicionarUma() {
        quantidade++;
    }

    public long getCustoAtual() {
        return (long) (custoBase * Math.pow(1.15, quantidade));
    }

    public long getProducaoTotal() {
        return (long)(producaoBase * quantidade * multiplicadorProducao);
    }

    public void multiplicarProducao(double multiplicador) {
        multiplicadorProducao *= multiplicador;
    }
}