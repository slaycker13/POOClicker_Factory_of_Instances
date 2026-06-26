package model;

import service.MotorJogo;

public class UpgradeClique extends Upgrade implements Aplicavel {

    public UpgradeClique(String id, String nome, long custo, double multiplicador) {
        super(id, nome, custo, multiplicador);
    }

    @Override
    public void aplicar(MotorJogo motorJogo) {
        motorJogo.multiplicarPoderClique(multiplicador);
    }

    @Override
    public String getGrupo() {
        return "Clique";
    }

    @Override
    public String getDescricao() {
        return "[Clique] " + nome + " | x" + multiplicador;
    }
}
