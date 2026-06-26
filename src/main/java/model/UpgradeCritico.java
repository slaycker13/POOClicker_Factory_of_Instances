package model;

import service.MotorJogo;

public class UpgradeCritico extends Upgrade implements Aplicavel {

    private double bonusChanceCritico;

    public UpgradeCritico(String id, String nome, long custo, double multiplicadorDanoCritico, double bonusChanceCritico) {
        super(id, nome, custo, multiplicadorDanoCritico);
        this.bonusChanceCritico = bonusChanceCritico;
    }

    @Override
    public void aplicar(MotorJogo motorJogo) {
        motorJogo.multiplicarDanoCritico(multiplicador);
        motorJogo.aumentarChanceCritico(bonusChanceCritico);
    }

    @Override
    public String getGrupo() {
        return "Critico";
    }

    @Override
    public String getDescricao() {
        return "[Critico] " + nome + " | dano x" + multiplicador + " | chance +" + formatarPercentual(bonusChanceCritico);
    }

    public double getBonusChanceCritico() {
        return bonusChanceCritico;
    }

    private String formatarPercentual(double valor) {
        return String.format("%.0f%%", valor * 100);
    }
}
