package model;

import exception.ConstrucaoNaoEncontradaException;
import service.MotorJogo;

public class UpgradeProducao extends Upgrade implements Aplicavel {
    private String construcaoAlvo;

    public UpgradeProducao(String id, String nome, long custo, double multiplicador, String construcaoAlvo) {
        super(id, nome, custo, multiplicador);
        this.construcaoAlvo = construcaoAlvo;
    }

    @Override
    public void aplicar(MotorJogo motorJogo) {
        try {
            Construcao construcao = motorJogo.buscarConstrucaoPorNome(construcaoAlvo);
            construcao.multiplicarProducao(multiplicador);
        } catch (ConstrucaoNaoEncontradaException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDescricao() {
        return "[" + construcaoAlvo + "] " + nome + " | x" + multiplicador;
    }

    @Override
    public String getGrupo() {
        return construcaoAlvo;
    }

    public String getConstrucaoAlvo() {
        return construcaoAlvo;
    }
}
