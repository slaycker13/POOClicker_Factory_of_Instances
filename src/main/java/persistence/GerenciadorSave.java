package persistence;

import model.Construcao;
import model.Upgrade;
import service.MotorJogo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class GerenciadorSave {

    public void salvar(MotorJogo jogo, String slot) throws IOException {
        Files.createDirectories(Path.of("saves"));

        Properties propriedades = new Properties();
        propriedades.setProperty("instancias", String.valueOf(jogo.getEstadoJogo().getInstancias()));

        for (Construcao construcao : jogo.getConstrucoes()) {
            propriedades.setProperty(construcao.getId(), String.valueOf(construcao.getQuantidade()));
        }

        for (Upgrade upgrade : jogo.getUpgrades()) {
            propriedades.setProperty(upgrade.getId(), String.valueOf(upgrade.isComprado()));
        }

        try (FileOutputStream arquivo = new FileOutputStream("saves/" + slot + ".properties")) {
            propriedades.store(arquivo, "Save do OOP Clicker");
        }
    }

    public void carregar(MotorJogo jogo, String slot) throws IOException {
        Properties propriedades = new Properties();

        try (FileInputStream arquivo = new FileInputStream("saves/" + slot + ".properties")) {
            propriedades.load(arquivo);
        }

        jogo.getEstadoJogo().setInstancias(Long.parseLong(propriedades.getProperty("instancias", "0")));

        for (Construcao construcao : jogo.getConstrucoes()) {
            String valor = propriedades.getProperty(construcao.getId(), "0");
            construcao.setQuantidade(Integer.parseInt(valor));
        }

        for (Upgrade upgrade : jogo.getUpgrades()) {
            String valor = propriedades.getProperty(upgrade.getId());
            if (valor != null && Boolean.parseBoolean(valor) && !upgrade.isComprado()) {
                upgrade.comprar();
                jogo.aplicarUpgradeCarregado(upgrade);
            }
        }
    }
}
