package service;

import model.*;
import exception.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MotorJogo {

    public static final long LIMITE_VITORIA = Integer.MAX_VALUE;
    private EstadoJogo estadoJogo;
    private List<Construcao> construcoes;
    private List<Upgrade> upgrades;
    private Random random;
    private boolean ultimoCliqueCritico;
    private long ultimoValorClique;
    private double multiplicadorClique = 1.0;
    private double chanceCritico = 0.05;
    private double multiplicadorCritico = 10.0;


    public MotorJogo() {
        random = new Random();
        estadoJogo = new EstadoJogo();
        construcoes = new ArrayList<>();
        upgrades = new ArrayList<>();

        inicializarConstrucoes();
        inicializarUpgrades();
    }

    private void inicializarConstrucoes() {
        construcoes.add(new AutoClick());
        construcoes.add(new Estagiario());
        construcoes.add(new ClasseBasica());
        construcoes.add(new HerancaSimples());
        construcoes.add(new Interface());
        construcoes.add(new ClasseAbstrata());
        construcoes.add(new Polimorfismo());
        construcoes.add(new FrameworkProprio());
    }

    public List<Upgrade> getUpgrades() {
        return upgrades;
    }

    private void inicializarUpgrades() {
        upgrades.add(new UpgradeClique(
                        "upgrade_click_1",
                        "Mouse Gamer RGB",
                        100,
                        2.0
        ));

        upgrades.add(new UpgradeClique(
                        "upgrade_click_2",
                        "Macro Configurada",
                        500,
                        3.0
        ));

        upgrades.add(new UpgradeClique(
                        "upgrade_click_3",
                        "Butterfly Click",
                        2_500,
                        5.0
        ));
        upgrades.add(new UpgradeCritico(
                        "upgrade_critico_1",
                        "Café Extra",
                        2_500,
                        1.5,
                        0.10
        ));

        upgrades.add(new UpgradeCritico(
                        "upgrade_critico_2",
                        "Overflow de Dopamina",
                        25_000,
                        2.0,
                        0.10
        ));

        upgrades.add(new UpgradeCritico(
                        "upgrade_critico_3",
                        "Programação às 3 da Manhã",
                        250_000,
                        2.5,
                        0.10
        ));
        upgrades.add(new UpgradeProducao(
                        "upgrade_autoclick_1",
                        "Loop Infinito",
                        100,
                        2.0,
                        "AutoClick()"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_autoclick_2",
                        "while(true)",
                        500,
                        3.0,
                        "AutoClick()"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_autoclick_3",
                        "Bug que é Feature",
                        2_500,
                        5.0,
                        "AutoClick()"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_estagiario_1",
                        "Cafeína para Estagiários",
                        1_000,
                        2.0,
                        "Estagiário"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_estagiario_2",
                        "ChatGPT Enterprise",
                        5_000,
                        3.0,
                        "Estagiário"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_estagiario_3",
                        "Home Office",
                        25_000,
                        5.0,
                        "Estagiário"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_classe_basica_1",
                        "Livro de POO",
                        20_000,
                        2.0,
                        "Classe Básica"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_classe_basica_2",
                        "IDE Colorida",
                        100_000,
                        3.0,
                        "Classe Básica"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_classe_basica_3",
                        "Getter e Setter Automáticos",
                        500_000,
                        5.0,
                        "Classe Básica"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_heranca_simples_1",
                        "extends Animal",
                        500_000,
                        2.0,
                        "Classe Básica"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_heranca_simples_2",
                        "UML Impressa",
                        2_500_000,
                        3.0,
                        "Classe Básica"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_heranca_simples_3",
                        "Sobrescrita de Métodos",
                        10_000_000,
                        5.0,
                        "Classe Básica"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_interface_1",
                        "implements Trabalhador",
                        10_000_000,
                        2.0,
                        "Interface"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_interface_2",
                        "Injeção de Dependência",
                        50_000_000,
                        3.0,
                        "Interface"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_interface_3",
                        "Acoplamento Fraco",
                        250_000_000,
                        5.0,
                        "Interface"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_classe_abstrata_1",
                        "Método Abstrato",
                        250_000_000,
                        2.0,
                        "Classe Abstrata"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_classe_abstrata_2",
                        "Template Method",
                        1_000_000_000,
                        3.0,
                        "Classe Abstrata"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_classe_abstrata_3",
                        "Blueprint Definitivo",
                        5_000_000_000L,
                        5.0,
                        "Classe Abstrata"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_polimorfismo_1",
                        "Overloading",
                        5_000_000_000L,
                        2.0,
                        "Polimorfismo"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_polimorfismo_2",
                        "Overriding",
                        25_000_000_000L,
                        3.0,
                        "Polimorfismo"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_polimorfismo_3",
                        "Dispatch Dinâmico",
                        100_000_000_000L,
                        5.0,
                        "Polimorfismo"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_framework_proprio_1",
                        "Core Modular",
                        100_000_000_000L,
                        2.0,
                        "Framework Proprio"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_framework_proprio_2",
                        "Documentação Inexistente",
                        500_000_000_000L,
                        3.0,
                        "Framework Proprio"
                )
        );
        upgrades.add(new UpgradeProducao(
                        "upgrade_framework_proprio_3",
                        "Open Source",
                        2_000_000_000_000L,
                        5.0,
                        "Framework Proprio"
                )
        );
    }

    public void aplicarUpgradeCarregado(Upgrade upgrade) {
        if (upgrade instanceof Aplicavel aplicavel) {
            aplicavel.aplicar(this);
        }
    }

    public void modoTesteVitoria() {
        estadoJogo.adicionarInstancias(Integer.MAX_VALUE-3);
    }

    public long clicar() {
        long ganho = getPoderCliqueAtual();
        ultimoCliqueCritico = false;
        estadoJogo.registrarClique();
        if (random.nextDouble() < chanceCritico) {
            ganho = Math.round(ganho * multiplicadorCritico);
            ultimoCliqueCritico = true;
        }
        ultimoValorClique = ganho;
        estadoJogo.adicionarInstancias(ganho);

        return ganho;
    }

    public void multiplicarPoderClique(double multiplicador) {
        multiplicadorClique *= multiplicador;
    }

    public void multiplicarDanoCritico(double multiplicador) {
        multiplicadorCritico *= multiplicador;
    }

    public void aumentarChanceCritico(double bonusChance) {
        chanceCritico = Math.min(1.0, chanceCritico + bonusChance);
    }

    public long getPoderCliqueAtual() {
        return Math.max(1, Math.round(multiplicadorClique));
    }

    public double getMultiplicadorClique() {
        return multiplicadorClique;
    }

    public double getChanceCritico() {
        return chanceCritico;
    }

    public double getMultiplicadorCritico() {
        return multiplicadorCritico;
    }

    public boolean deveMostrarErro() {
        return random.nextDouble() < 0.05;
    }

    public boolean isUltimoCliqueCritico() {
        return ultimoCliqueCritico;
    }

    public long getUltimoValorClique() {
        return ultimoValorClique;
    }

    public long getInstancias() {
        return estadoJogo.getInstancias();
    }

    public List<Construcao> getConstrucoes() {
        return construcoes;
    }

    public void comprarConstrucao(Construcao construcao) throws SaldoInsuficienteException {

        long custo = construcao.getCustoAtual();
        if (estadoJogo.getInstancias() < custo) {
            throw new SaldoInsuficienteException("Instâncias insuficientes para comprar " + construcao.getNome());
        }
        estadoJogo.removerInstancias(custo);
        construcao.adicionarUma();
    }

    public void processarTick() {
        estadoJogo.adicionarInstancias(getProducaoTotal());
    }

    public boolean verificarVitoria() {
        return estadoJogo.getInstancias() >= LIMITE_VITORIA;
    }

    public Construcao buscarConstrucaoPorNome(String nome) throws ConstrucaoNaoEncontradaException {
        for (Construcao construcao : construcoes) {
            if (construcao.getNome().equalsIgnoreCase(nome)) {
                return construcao;
            }
        }
        throw new ConstrucaoNaoEncontradaException("Construção não encontrada: " + nome);
    }

    public EstadoJogo getEstadoJogo() {
        return estadoJogo;
    }

    public void comprarUpgrade(Upgrade upgrade) throws UpgradeJaCompradoException, SaldoInsuficienteException {
        if (upgrade.isComprado()) {
            throw new UpgradeJaCompradoException("Upgrade já comprado.");
        }

        if (estadoJogo.getInstancias() < upgrade.getCusto()) {
            throw new SaldoInsuficienteException("Instâncias insuficientes para comprar " +  upgrade.getNome());
        }

        estadoJogo.removerInstancias(upgrade.getCusto());
        upgrade.comprar();
        if (upgrade instanceof Aplicavel aplicavel) {
            aplicavel.aplicar(this);
        }
    }

    public long calcularBonusBug() {
        return Math.max(1000, getProducaoTotal() * 60);
    }

    public long getProducaoTotal() {
        long total = 0;
        for (Construcao construcao : construcoes) {
            total += construcao.getProducaoTotal();
        }
        return total;
    }

    public void adicionarInstancias(double quantidade) {
        long instancias = (long)(estadoJogo.getInstancias() + quantidade);
        estadoJogo.setInstancias(instancias);
    }

    public void comprarUpgradeDebug(Upgrade upgrade) {
        if (upgrade.isComprado()) {
            return;
        }
        upgrade.comprar();
        if (upgrade instanceof Aplicavel aplicavel) {
            aplicavel.aplicar(this);
        }
    }

    public void comprarConstrucaoDebug(Construcao construcao) {
        construcao.adicionarUma();
    }

    public boolean removerConstrucao(Construcao construcao) {
        boolean removida = construcoes.remove(construcao);
        if (removida) {
            upgrades.removeIf(upgrade -> upgrade instanceof UpgradeProducao && upgrade.getGrupo().equalsIgnoreCase(construcao.getNome()));
        }
        return removida;
    }

    public boolean removerUpgrade(Upgrade upgrade) {
        return upgrades.remove(upgrade);
    }

    public boolean removerUpgradePorId(String id) {
        return upgrades.removeIf(upgrade -> upgrade.getId().equalsIgnoreCase(id));
    }

    public Construcao removerConstrucaoPorNome(String nome) throws ConstrucaoNaoEncontradaException {
        Construcao construcao = buscarConstrucaoPorNome(nome);
        removerConstrucao(construcao);
        return construcao;
    }
}
