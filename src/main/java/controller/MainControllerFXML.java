package controller;

import javafx.animation.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import exception.ConstrucaoNaoEncontradaException;
import exception.SaldoInsuficienteException;
import exception.UpgradeJaCompradoException;
import model.Construcao;
import model.Upgrade;
import persistence.*;
import service.*;
import util.*;

public class MainControllerFXML {

    private static final boolean ATUALIZAR_AREA_VISUAL_NO_LOOP = true;

    private Timeline timeline;
    private MotorJogo motorJogo;
    private final Random random = new Random();
    private GerenciadorSave gerenciadorSave;
    private Stage janelaDebug;
    private boolean vitoriaJaProcessada = false;
    private final Map<String, Image> imagemCache = new HashMap<>();
    private final List<CardConstrucao> cardsConstrucoes = new ArrayList<>();
    private final List<CardUpgrade> cardsUpgrades = new ArrayList<>();
    private VBox debugListaConstrucoes;
    private VBox debugListaUpgrades;
    private TextField debugCampoBuscaConstrucao;
    private TextField debugCampoBuscaUpgrade;
    private Label debugInfo;

    @FXML private Label lblInstancias;
    @FXML private Label lblProducao;
    @FXML private Label lblStatus;
    @FXML private Label lblCritico;
    @FXML private AnchorPane areaClique;

    @FXML private AnchorPane areaAutoClick;
    @FXML private AnchorPane areaEstagiario;
    @FXML private AnchorPane areaClasseBasica;
    @FXML private AnchorPane areaHeranca;
    @FXML private AnchorPane areaInterface;
    @FXML private AnchorPane areaClasseAbstrata;
    @FXML private AnchorPane areaPolimorfismo;
    @FXML private AnchorPane areaFrameworkProprio;

    @FXML private VBox painelConstrucoes;
    @FXML private VBox painelUpgrades;
    @FXML private AnchorPane areaJogo;
    @FXML private Button btnErro;

    @FXML private MenuItem salvarSlot1;
    @FXML private MenuItem salvarSlot2;
    @FXML private MenuItem salvarSlot3;
    @FXML private MenuItem carregarSlot1;
    @FXML private MenuItem carregarSlot2;
    @FXML private MenuItem carregarSlot3;

    @FXML private MenuButton menuSalvar;
    @FXML private MenuButton menuCarregar;

    @FXML private TextFlow flowCodigo;
    @FXML private StackPane cardClique;

    @FXML private ScrollPane scrollVisual;
    @FXML private ScrollPane scrollUpgrades;
    @FXML private ScrollPane scrollConstrucoes;

    private record CardConstrucao(Construcao construcao, Button card, Label producao, Label preco, Label quantidade) {}
    private record CardUpgrade(Upgrade upgrade, Button card, Label titulo, Label preco) {}

    @FXML
    public void initialize() {
        gerenciadorSave = new GerenciadorSave();
        salvarSlot1.setOnAction(e -> salvar(1));
        salvarSlot2.setOnAction(e -> salvar(2));
        salvarSlot3.setOnAction(e -> salvar(3));

        carregarSlot1.setOnAction(e -> carregar(1));
        carregarSlot2.setOnAction(e -> carregar(2));
        carregarSlot3.setOnAction(e -> carregar(3));

        motorJogo = new MotorJogo();
        montarLoja();
        configurarFundos();
        atualizarTela();
        atualizarAreaVisual();
        iniciarLoopJogo();
        painelUpgrades.setFillWidth(true);
        painelConstrucoes.setFillWidth(true);
        lblCritico.setRotate(-20);
        btnErro.setVisible(false);

        btnErro.setOnAction(event -> {
            long bonus = motorJogo.calcularBonusBug();
            motorJogo.getEstadoJogo().adicionarInstancias(bonus);
            lblStatus.setText("Bug corrigido! +" + FormatadorNumero.formatar(bonus) + " Instâncias");
            btnErro.setVisible(false);
            atualizarTela();
        });

        lblStatus.setText("Sistema iniciado.");
        estilizarMenusTopo();
        estilizarScrolls();
        montarCodigoBotao();
    }

    private void estilizarMenusTopo() {
        Platform.runLater(() -> {
            if (menuSalvar.lookup(".arrow") != null) {
                menuSalvar.lookup(".arrow").setStyle("-fx-background-color: white;");
            }
            if (menuCarregar.lookup(".arrow") != null) {
                menuCarregar.lookup(".arrow").setStyle("-fx-background-color: white;");
            }
        });
    }

    private void salvar(int slot) {
        try {
            gerenciadorSave.salvar(motorJogo, "save" + slot);
            lblStatus.setText("Jogo salvo no Slot " + slot);
        } catch (IOException e) {
            lblStatus.setText("Erro ao salvar.");
            e.printStackTrace();
        }
    }

    private void carregar(int slot) {
        try {
            gerenciadorSave.carregar(motorJogo, "save" + slot);
            atualizarTela();
            atualizarAreaVisual();
            lblStatus.setText("Slot " + slot + " carregado.");
        } catch (IOException e) {
            lblStatus.setText("Erro ao carregar.");
            e.printStackTrace();
        }
    }

    private void atualizarTela() {
        verificarVitoria();
        lblInstancias.setText("Instâncias: " + FormatadorNumero.formatar(motorJogo.getInstancias()));
        lblProducao.setText("Produção/s: " + FormatadorNumero.formatar(motorJogo.getProducaoTotal()) + " | Clique: +" + FormatadorNumero.formatar(motorJogo.getPoderCliqueAtual()));
        atualizarConstrucoes();
        atualizarUpgrades();
    }

    @FXML
    private void clicar(MouseEvent event) {
        motorJogo.clicar();
        mostrarTextoClique(motorJogo.getUltimoValorClique(), motorJogo.isUltimoCliqueCritico(), event.getX(), event.getY());
        atualizarTela();
        if (motorJogo.isUltimoCliqueCritico()) {
            mostrarCritico();
        }
    }

    private void mostrarTextoClique(long valor, boolean critico, double mouseX, double mouseY) {
        Label texto = new Label("+" + FormatadorNumero.formatar(valor));
        texto.setMouseTransparent(true);
        if (critico) {
            texto.setStyle(
                    "-fx-text-fill: #ff5555;" +
                    "-fx-font-size: 24px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-effect: dropshadow(gaussian, black, 8, 0.45, 0, 2);"
            );
        } else {
            texto.setStyle(
                    "-fx-text-fill: #55d17a;" +
                    "-fx-font-size: 20px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-effect: dropshadow(gaussian, black, 6, 0.35, 0, 2);"
            );
        }
        double x = cardClique.getLayoutX() + mouseX - 12;
        double y = cardClique.getLayoutY() + mouseY - 28;
        texto.setLayoutX(x);
        texto.setLayoutY(y);
        areaClique.getChildren().add(texto);
        FadeTransition fade = new FadeTransition(Duration.seconds(0.9), texto);
        fade.setFromValue(1);
        fade.setToValue(0);
        TranslateTransition mover = new TranslateTransition(Duration.seconds(0.9), texto);
        mover.setByY(-45);
        ParallelTransition animacao = new ParallelTransition(fade, mover);
        animacao.setOnFinished(e -> areaClique.getChildren().remove(texto));
        animacao.play();
    }

    private void mostrarCritico() {
        lblCritico.setStyle("-fx-text-fill: #ff5555;" + "-fx-font-size: 32px;" + "-fx-font-weight: bold;" + "-fx-effect: dropshadow(gaussian, black, 10, 0.55, 0, 3);");
        lblCritico.setVisible(true);
        Timeline esconder = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> lblCritico.setVisible(false)));
        esconder.setCycleCount(1);
        esconder.play();
    }

    private void mostrarErroAleatorio() {
        double largura = areaJogo.getWidth();
        double altura = areaJogo.getHeight();
        double x = random.nextDouble() * (largura - 180);
        double y = random.nextDouble() * (altura - 50);
        btnErro.setLayoutX(x);
        btnErro.setLayoutY(y);
        btnErro.setStyle(
                "-fx-background-color: #8b1e2d;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: #ff5555;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 4;" +
                "-fx-border-radius: 4;" +
                "-fx-effect: dropshadow(gaussian, black, 10, 0.45, 0, 3);"
        );
        btnErro.setVisible(true);
        Timeline esconder = new Timeline(new KeyFrame(Duration.seconds(2), e -> btnErro.setVisible(false)));
        esconder.setCycleCount(1);
        esconder.play();
    }

    private void iniciarLoopJogo() {
        timeline = new Timeline(new KeyFrame(
                        Duration.seconds(1), event -> {
                            motorJogo.processarTick();
                            if (!btnErro.isVisible() && motorJogo.deveMostrarErro()) {
                                mostrarErroAleatorio();
                            }
                            atualizarTela();
                            if (ATUALIZAR_AREA_VISUAL_NO_LOOP) {
                                atualizarAreaVisual();
                            }
                        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void montarLoja() {
        montarConstrucoes();
        montarUpgrades();
    }

    private void montarConstrucoes() {
        cardsConstrucoes.clear();
        painelConstrucoes.getChildren().clear();
        for (var construcao : motorJogo.getConstrucoes()) {
            CardConstrucao card = criarCardConstrucao(construcao);
            cardsConstrucoes.add(card);
            painelConstrucoes.getChildren().add(card.card());
        }
    }

    private CardConstrucao criarCardConstrucao(Construcao construcao) {
        Button card = new Button();
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefWidth(410);
        card.setPrefHeight(104);
        card.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        card.setFocusTraversable(false);

        HBox conteudo = new HBox();
        conteudo.setPrefWidth(390);
        conteudo.setSpacing(12);
        conteudo.setAlignment(Pos.CENTER_LEFT);

        StackPane imagemBox = new StackPane();
        imagemBox.setPrefWidth(76);
        imagemBox.setPrefHeight(76);
        imagemBox.setAlignment(Pos.CENTER);
        imagemBox.setStyle("-fx-background-color: #22242a;" + "-fx-border-color: #42444d;" + "-fx-border-width: 1;");

        ImageView imagem = new ImageView();
        imagem.setFitWidth(58);
        imagem.setFitHeight(58);
        imagem.setPreserveRatio(true);
        imagem.setImage(obterImagemConstrucao(construcao.getNome()));
        imagemBox.getChildren().add(imagem);

        VBox infoBox = new VBox();
        infoBox.setSpacing(3);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setPrefWidth(205);

        Label nome = new Label(construcao.getNome());
        nome.setStyle("-fx-text-fill: white;" + "-fx-font-size: 15px;" + "-fx-font-weight: bold;");
        Label producao = new Label();
        producao.setStyle("-fx-text-fill: #9ca0aa;" + "-fx-font-size: 12px;");
        Label preco = new Label();
        infoBox.getChildren().addAll(nome, producao, preco);

        Region espacador = new Region();
        HBox.setHgrow(espacador, Priority.ALWAYS);

        Label quantidadeBox = new Label();
        quantidadeBox.setAlignment(Pos.CENTER);
        quantidadeBox.setPrefWidth(62);
        quantidadeBox.setStyle("-fx-text-fill: #8b8f9b;" + "-fx-font-size: 38px;" + "-fx-font-weight: bold;");

        conteudo.getChildren().addAll(imagemBox, infoBox, espacador, quantidadeBox);
        card.setGraphic(conteudo);

        CardConstrucao holder = new CardConstrucao(construcao, card, producao, preco, quantidadeBox);
        card.hoverProperty().addListener((obs, antigo, novo) -> atualizarCardConstrucao(holder));
        card.setOnAction(event -> comprarConstrucaoLoja(construcao));
        atualizarCardConstrucao(holder);
        return holder;
    }

    private void atualizarCardConstrucao(CardConstrucao card) {
        Construcao construcao = card.construcao();
        boolean podeComprar = motorJogo.getInstancias() >= construcao.getCustoAtual();
        card.producao().setText("Produz: " + FormatadorNumero.formatar(construcao.getProducaoAtual()) + "/s");
        card.preco().setText("Preço: " + FormatadorNumero.formatar(construcao.getCustoAtual()));
        card.preco().setStyle(podeComprar ? "-fx-text-fill: #55d17a;" + "-fx-font-size: 13px;" + "-fx-font-weight: bold;" : "-fx-text-fill: #ff6666;" + "-fx-font-size: 13px;" + "-fx-font-weight: bold;");
        card.quantidade().setText(String.valueOf(construcao.getQuantidade()));
        card.card().setStyle(estiloCardLoja(podeComprar, false, card.card().isHover()));
    }

    private void atualizarConstrucoes() {
        if (cardsConstrucoes.isEmpty()) { montarConstrucoes(); }
        for (var card : cardsConstrucoes) { atualizarCardConstrucao(card); }
    }

    private void montarUpgrades() {
        cardsUpgrades.clear();
        painelUpgrades.getChildren().clear();
        for (var upgrade : motorJogo.getUpgrades()) {
            CardUpgrade card = criarCardUpgrade(upgrade);
            cardsUpgrades.add(card);
            painelUpgrades.getChildren().add(card.card());
        }
    }

    private CardUpgrade criarCardUpgrade(Upgrade upgrade) {
        Button card = new Button();
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefWidth(410);
        card.setPrefHeight(112);
        card.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        card.setFocusTraversable(false);

        HBox conteudo = new HBox();
        conteudo.setPrefWidth(390);
        conteudo.setSpacing(12);
        conteudo.setAlignment(Pos.CENTER_LEFT);

        StackPane imagemBox = new StackPane();
        imagemBox.setPrefWidth(76);
        imagemBox.setPrefHeight(76);
        imagemBox.setAlignment(Pos.CENTER);
        imagemBox.setStyle("-fx-background-color: #22242a;" + "-fx-border-color: #42444d;" + "-fx-border-width: 1;");

        ImageView imagem = new ImageView();
        imagem.setFitWidth(58);
        imagem.setFitHeight(58);
        imagem.setPreserveRatio(true);
        imagem.setImage(obterImagemUpgrade(upgrade.getId()));
        imagemBox.getChildren().add(imagem);

        VBox infoBox = new VBox();
        infoBox.setSpacing(3);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setPrefWidth(285);

        Label titulo = new Label(upgrade.getNome());
        titulo.setStyle("-fx-text-fill: white;" + "-fx-font-size: 15px;" + "-fx-font-weight: bold;");
        Label descricao = new Label(upgrade.getDescricao());
        descricao.setWrapText(true);
        descricao.setMaxWidth(285);
        descricao.setStyle("-fx-text-fill: #d0d3dc;" + "-fx-font-size: 11px;");
        Label buff = new Label("Buff: x" + upgrade.getMultiplicador());
        buff.setStyle("-fx-text-fill: #8cc8ff;" + "-fx-font-size: 12px;" + "-fx-font-weight: bold;");
        Label preco = new Label();
        infoBox.getChildren().addAll(titulo, descricao, buff, preco);
        conteudo.getChildren().addAll(imagemBox, infoBox);
        card.setGraphic(conteudo);

        CardUpgrade holder = new CardUpgrade(upgrade, card, titulo, preco);
        card.hoverProperty().addListener((obs, antigo, novo) -> atualizarCardUpgrade(holder));
        card.setOnAction(event -> comprarUpgradeLoja(upgrade));
        atualizarCardUpgrade(holder);
        return holder;
    }

    private void atualizarCardUpgrade(CardUpgrade card) {
        Upgrade upgrade = card.upgrade();
        boolean comprado = upgrade.isComprado();
        boolean podeComprar = motorJogo.getInstancias() >= upgrade.getCusto() && !comprado;
        card.titulo().setText(comprado ? upgrade.getNome() + " ✓" : upgrade.getNome());
        card.preco().setText(comprado ? "COMPRADO" : "Preço: " + FormatadorNumero.formatar(upgrade.getCusto()));
        card.preco().setStyle(comprado ? "-fx-text-fill: #8fa8ff;" + "-fx-font-size: 12px;" + "-fx-font-weight: bold;" : podeComprar ? "-fx-text-fill: #55d17a;" + "-fx-font-size: 12px;" + "-fx-font-weight: bold;" : "-fx-text-fill: #ff6666;" + "-fx-font-size: 12px;" + "-fx-font-weight: bold;");
        card.card().setStyle(estiloCardLoja(podeComprar, comprado, card.card().isHover()));
    }

    private void atualizarUpgrades() {
        if (cardsUpgrades.isEmpty()) { montarUpgrades(); }
        for (var card : cardsUpgrades) { atualizarCardUpgrade(card); }
    }

    private Image obterImagemConstrucao(String nome) {
        String arquivo;
        switch (nome) {
            case "AutoClick()":
                arquivo = "autoclick.png";
                break;
            case "Estagiário":
                arquivo = "estagiario.png";
                break;
            case "Classe Básica":
                arquivo = "classe_basica.png";
                break;
            case "Herança Simples":
                arquivo = "heranca.png";
                break;
            case "Interface":
                arquivo = "interface.png";
                break;
            case "Classe Abstrata":
                arquivo = "classe_abstrata.png";
                break;
            case "Polimorfismo":
                arquivo = "polimorfismo.png";
                break;
            case "Framework Próprio":
                arquivo = "framework.png";
                break;
            default:
                arquivo = "default.png";
        }
        return carregarImagem("/images/" + arquivo);
    }

    private Image obterImagemUpgrade(String id) {
        return carregarImagem("/images/upgrades/" + id + ".png");
    }

    private Image carregarImagem(String caminho) {
        return imagemCache.computeIfAbsent(caminho, key -> {
            InputStream stream = getClass().getResourceAsStream(key);
            if (stream == null) {
                stream = getClass().getResourceAsStream("/images/default.png");
            }
            return new Image(stream);
        });
    }

    private void atualizarAreaVisual() {
        gerarIcones("AutoClick()", areaAutoClick, "autoclickIcon.png", 25);
        gerarIcones("Estagiário", areaEstagiario, "estagiarioIcon.png", 25);
        gerarIcones("Classe Básica", areaClasseBasica, "classe_basicaIcon.png", 25);
        gerarIcones("Herança Simples", areaHeranca, "herancaIcon.png", 25);
        gerarIcones("Interface", areaInterface, "interfaceIcon.png", 25);
        gerarIcones("Classe Abstrata", areaClasseAbstrata, "classe_abstrataIcon.png", 25);
        gerarIcones("Polimorfismo", areaPolimorfismo, "polimorfismoIcon.png", 25);
        gerarIcones("Framework Próprio", areaFrameworkProprio, "frameworkIcon.png", 25);
    }

    private void gerarIcones(String nomeConstrucao, AnchorPane area, String arquivoImagem, int limite) {
        area.getChildren().clear();
        try {
            int quantidade = motorJogo.buscarConstrucaoPorNome(nomeConstrucao).getQuantidade();
            quantidade = Math.min(quantidade, limite);
            Image imagem = carregarImagem("/images/" + arquivoImagem);
            for (int i = 0; i < quantidade; i++) {
                ImageView icone = new ImageView(imagem);
                icone.setFitWidth(30);
                icone.setFitHeight(30);
                icone.setLayoutX(random.nextInt(410));
                icone.setLayoutY(random.nextInt(100));
                area.getChildren().add(icone);
            }
        } catch (ConstrucaoNaoEncontradaException e) {
            return;
        }
    }

    private void definirFundo(AnchorPane area, String arquivo) {
        area.setStyle(
                "-fx-background-color: #17181e;" +
                "-fx-background-image: url('/images/backgrounds/" + arquivo + "');" +
                "-fx-background-size: cover;" +
                "-fx-background-position: center;" +
                "-fx-background-repeat: no-repeat;" +
                "-fx-border-color: #42444d;" +
                "-fx-border-width: 4;"
        );
    }

    private void configurarFundos() {
        definirFundo(areaAutoClick, "autoclick_bg.png");
        definirFundo(areaEstagiario, "estagiario_bg.png");
        definirFundo(areaClasseBasica, "classe_basica_bg.png");
        definirFundo(areaHeranca, "heranca_bg.png");
        definirFundo(areaInterface, "interface_bg.png");
        definirFundo(areaClasseAbstrata, "classe_abstrata_bg.png");
        definirFundo(areaPolimorfismo, "polimorfismo_bg.png");
        definirFundo(areaFrameworkProprio, "framework_bg.png");
    }

    private void verificarVitoria() {
        if (vitoriaJaProcessada) {
            return;
        }
        if (!motorJogo.verificarVitoria()) {
            return;
        }
        vitoriaJaProcessada = true;
        if (timeline != null) {
            timeline.stop();
        }
        fecharDebugSeAberto();
        btnErro.setVisible(false);
        bloquearFechamentoJanelaPrincipal();
        simularTravamentoAntesDoErro();
    }

    private void bloquearFechamentoJanelaPrincipal() {
        if (areaJogo.getScene() == null) {
            return;
        }
        Window window = areaJogo.getScene().getWindow();
        if (window instanceof Stage stagePrincipal) {
            stagePrincipal.setOnCloseRequest(event -> {
                if (vitoriaJaProcessada) {
                    event.consume();
                }
            });
        }
    }

    private void encerrarPrograma(Stage popupErro) {
        if (timeline != null) {
            timeline.stop();
        }
        if (popupErro != null && popupErro.isShowing()) {
            popupErro.close();
        }
        fecharDebugSeAberto();
        if (areaJogo.getScene() != null && areaJogo.getScene().getWindow() instanceof Stage stagePrincipal) {
            stagePrincipal.setOnCloseRequest(null);
            stagePrincipal.close();
        }
        Platform.exit();
        System.exit(0);
    }

    private void simularTravamentoAntesDoErro() {
        StackPane overlayTravamento = new StackPane();
        overlayTravamento.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.28);"
        );
        overlayTravamento.setPickOnBounds(true);
        overlayTravamento.setCursor(Cursor.WAIT);
        Label lblTravando = new Label("POO Clicker não está respondendo...");
        lblTravando.setStyle(
                "-fx-text-fill: #222222;" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-color: rgba(240, 240, 240, 0.85);" +
                "-fx-padding: 12 20 12 20;" +
                "-fx-border-color: #888888;" +
                "-fx-border-width: 1;"
        );
        overlayTravamento.getChildren().add(lblTravando);
        AnchorPane.setTopAnchor(overlayTravamento, 0.0);
        AnchorPane.setBottomAnchor(overlayTravamento, 0.0);
        AnchorPane.setLeftAnchor(overlayTravamento, 0.0);
        AnchorPane.setRightAnchor(overlayTravamento, 0.0);
        areaJogo.getChildren().add(overlayTravamento);

        if (areaJogo.getScene() != null) {
            areaJogo.getScene().setCursor(Cursor.WAIT);
        }
        PauseTransition atraso = new PauseTransition(Duration.seconds(10));
        atraso.setOnFinished(e -> {
            if (areaJogo.getScene() != null) {
                areaJogo.getScene().setCursor(Cursor.DEFAULT);
            }
            areaJogo.getChildren().remove(overlayTravamento);
            Platform.runLater(() -> {
                mostrarPopupErroVitoria();
            });
        });
        atraso.play();
    }

    private void mostrarPopupErroVitoria() {
        Stage popup = new Stage();
        Window janelaPrincipal = areaJogo.getScene().getWindow();
        popup.initOwner(janelaPrincipal);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("java.lang.OutOfMemoryError");
        popup.setResizable(false);
        VBox root = new VBox();
        root.setSpacing(0);
        root.setStyle(
                "-fx-background-color: #f0f0f0;" +
                "-fx-border-color: #8a8a8a;" +
                "-fx-border-width: 1;"
        );
        Label barraTitulo = new Label("java.lang.OutOfMemoryError");
        barraTitulo.setPrefWidth(460);
        barraTitulo.setPrefHeight(34);
        barraTitulo.setStyle(
                "-fx-background-color: #ffffff;" +
                "-fx-text-fill: #111111;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 0 0 0 10;" +
                "-fx-border-color: #d0d0d0;" +
                "-fx-border-width: 0 0 1 0;"
        );
        HBox conteudo = new HBox();
        conteudo.setSpacing(14);
        conteudo.setPadding(new Insets(18));
        conteudo.setAlignment(Pos.TOP_LEFT);
        Label iconeErro = new Label("✖");
        iconeErro.setAlignment(Pos.CENTER);
        iconeErro.setPrefWidth(42);
        iconeErro.setPrefHeight(42);
        iconeErro.setStyle(
                "-fx-background-color: #c62828;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 50;"
        );
        VBox textos = new VBox();
        textos.setSpacing(8);
        Label titulo = new Label("Limite de memória atingido");
        titulo.setStyle(
                "-fx-text-fill: #111111;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"
        );
        Label mensagem = new Label(
                "O sistema colapsou.\n\n" +
                "Você gerou instâncias demais.\n\n" +
                "Exception in thread \"JavaFX Application Thread\"\n" +
                "java.lang.OutOfMemoryError: Java heap space\n\n" +
                "Total de instâncias: " +
                FormatadorNumero.formatar(motorJogo.getInstancias())
        );
        mensagem.setWrapText(true);
        mensagem.setPrefWidth(340);
        mensagem.setStyle("-fx-text-fill: #222222;" + "-fx-font-size: 13px;");
        textos.getChildren().addAll(titulo, mensagem);
        conteudo.getChildren().addAll(iconeErro, textos);
        HBox botoes = new HBox();
        botoes.setAlignment(Pos.CENTER_RIGHT);
        botoes.setPadding(new Insets(0, 14, 14, 14));
        Button btnOk = new Button("Encerrar processo");
        btnOk.setPrefWidth(140);
        btnOk.setStyle(
                "-fx-background-color: #e1e1e1;" +
                "-fx-text-fill: #111111;" +
                "-fx-border-color: #777777;" +
                "-fx-border-width: 1;" +
                "-fx-background-radius: 0;" +
                "-fx-border-radius: 0;"
        );
        btnOk.setOnAction(e -> encerrarPrograma(popup));
        botoes.getChildren().add(btnOk);
        root.getChildren().addAll(barraTitulo, conteudo, botoes);
        Scene scene = new Scene(root, 460, 300);
        popup.setScene(scene);
        popup.setOnCloseRequest(e -> e.consume());
        popup.show();
    }

    @FXML
    private void abrirDebug() {
        if (janelaDebug != null && janelaDebug.isShowing()) { janelaDebug.toFront(); return; }
        janelaDebug = new Stage();
        Stage janela = janelaDebug;
        janela.setTitle("Painel Debug");
        janela.setOnCloseRequest(e -> {
            janelaDebug = null;
            limparReferenciasDebug();
        });
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));

        VBox boxCheats = new VBox();
        boxCheats.setSpacing(8);
        boxCheats.setPadding(new Insets(10));

        Label lblInfo = new Label("Instâncias: " + FormatadorNumero.formatar(motorJogo.getInstancias()) + "\nProdução/s: " + FormatadorNumero.formatar(motorJogo.getProducaoTotal()));
        debugInfo = lblInfo;

        Button btn10k = new Button("+10K Instâncias");
        Button btn100k = new Button("+100K Instâncias");
        Button btn1m = new Button("+1M Instâncias");

        btn10k.setOnAction(e -> adicionarInstanciasDebug(10_000, lblInfo));
        btn100k.setOnAction(e -> adicionarInstanciasDebug(100_000, lblInfo));
        btn1m.setOnAction(e -> adicionarInstanciasDebug(1_000_000, lblInfo));

        boxCheats.getChildren().addAll(lblInfo, btn10k, btn100k, btn1m);
        TitledPane tpCheats = new TitledPane("Cheats de Instâncias", boxCheats);
        tpCheats.setExpanded(true);

        VBox boxConstrucoes = new VBox(8);
        boxConstrucoes.setPadding(new Insets(10));

        TextField campoBuscaConstrucao = new TextField();
        campoBuscaConstrucao.setPromptText("Buscar construção...");
        debugCampoBuscaConstrucao = campoBuscaConstrucao;

        VBox listaConstrucoesDebug = new VBox(8);
        debugListaConstrucoes = listaConstrucoesDebug;

        boxConstrucoes.getChildren().addAll(campoBuscaConstrucao, listaConstrucoesDebug);
        atualizarListaConstrucoesDebug(listaConstrucoesDebug, "", lblInfo);
        campoBuscaConstrucao.textProperty().addListener((obs, antigo, novo) -> atualizarListaConstrucoesDebug(listaConstrucoesDebug, novo, lblInfo));
        TitledPane tpConstrucoes = new TitledPane("Construções", boxConstrucoes);

        tpConstrucoes.setExpanded(false);

        VBox boxUpgrades = new VBox();
        boxUpgrades.setSpacing(8);
        boxUpgrades.setPadding(new Insets(10));

        TextField campoBuscaUpgrade = new TextField();
        campoBuscaUpgrade.setPromptText("Buscar por construção ou upgrade...");
        debugCampoBuscaUpgrade = campoBuscaUpgrade;

        VBox listaUpgradesDebug = new VBox();
        listaUpgradesDebug.setSpacing(8);
        debugListaUpgrades = listaUpgradesDebug;

        boxUpgrades.getChildren().addAll(campoBuscaUpgrade, listaUpgradesDebug);

        atualizarListaUpgradesDebug(listaUpgradesDebug, "", lblInfo);

        campoBuscaUpgrade.textProperty().addListener((obs, antigo, novo) -> {
            atualizarListaUpgradesDebug(listaUpgradesDebug, novo, lblInfo);
        });

        TitledPane tpUpgrades = new TitledPane("Upgrades", boxUpgrades);
        tpUpgrades.setExpanded(false);

        VBox boxSistema = new VBox();
        boxSistema.setSpacing(8);
        boxSistema.setPadding(new Insets(10));
        Button btnVitoria = new Button("Forçar Vitória");
        Button btnFechar = new Button("Fechar Debug");
        btnVitoria.setOnAction(e -> {
            motorJogo.modoTesteVitoria();
            atualizarTela();
            atualizarInfoDebug(lblInfo);
        });
        btnFechar.setOnAction(e -> {
            janela.close();
            janelaDebug = null;
            limparReferenciasDebug();
        });
        boxSistema.getChildren().addAll(btnVitoria, btnFechar);
        TitledPane tpSistema = new TitledPane("Sistema", boxSistema);
        tpSistema.setExpanded(false);

        root.getChildren().addAll(tpCheats, tpConstrucoes, tpUpgrades, tpSistema);
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        Scene scene = new Scene(scroll, 520, 600);
        janela.setScene(scene);
        janela.show();
    }

    private void atualizarListaConstrucoesDebug(VBox lista, String filtro, Label lblInfo) {
        lista.getChildren().clear();

        String busca = filtro == null ? "" : filtro.toLowerCase().trim();

        for (var construcao : motorJogo.getConstrucoes()) {
            if (!construcao.getNome().toLowerCase().contains(busca)) { continue; }

            HBox linha = new HBox(10);
            linha.setAlignment(Pos.CENTER_LEFT);

            Label nome = new Label(construcao.getNome());
            nome.setPrefWidth(210);

            Label quantidade = new Label("Qtd: " + construcao.getQuantidade());
            quantidade.setPrefWidth(70);

            Button btnMais = new Button("+1");
            Button btnMenos = new Button("-1");
            Button btnRemover = new Button("Remover");

            btnMais.setOnAction(e -> {
                motorJogo.comprarConstrucaoDebug(construcao);
                quantidade.setText("Qtd: " + construcao.getQuantidade());
                atualizarTela();
                atualizarInfoDebug(lblInfo);
            });
            btnMenos.setOnAction(e -> {
                if (construcao.getQuantidade() > 0) {
                    construcao.setQuantidade(construcao.getQuantidade() - 1);
                }
                quantidade.setText("Qtd: " + construcao.getQuantidade());
                atualizarTela();
                atualizarAreaVisual();
                atualizarInfoDebug(lblInfo);
            });
            btnRemover.setOnAction(e -> {
                if (motorJogo.removerConstrucao(construcao)) {
                    montarLoja();
                    atualizarTela();
                    atualizarAreaVisual();
                    atualizarPainelDebugAtual();
                }
            });
            linha.getChildren().addAll(
                    nome,
                    quantidade,
                    btnMais,
                    btnMenos,
                    btnRemover
            );
            lista.getChildren().add(linha);
        }
    }

    private void atualizarInfoDebug(Label lblInfo) {
        lblInfo.setText("Instâncias: " + FormatadorNumero.formatar(motorJogo.getInstancias()) + "\nProdução/s: " + FormatadorNumero.formatar(motorJogo.getProducaoTotal()));
    }

    private void atualizarPainelDebugAtual() {
        if (debugInfo != null) {
            atualizarInfoDebug(debugInfo);
        }
        if (debugListaConstrucoes != null && debugCampoBuscaConstrucao != null && debugInfo != null) {
            atualizarListaConstrucoesDebug(debugListaConstrucoes, debugCampoBuscaConstrucao.getText(), debugInfo);
        }
        if (debugListaUpgrades != null && debugCampoBuscaUpgrade != null && debugInfo != null) {
            atualizarListaUpgradesDebug(debugListaUpgrades, debugCampoBuscaUpgrade.getText(), debugInfo);
        }
    }

    private void limparReferenciasDebug() {
        debugListaConstrucoes = null;
        debugListaUpgrades = null;
        debugCampoBuscaConstrucao = null;
        debugCampoBuscaUpgrade = null;
        debugInfo = null;
    }

    private void atualizarListaUpgradesDebug(VBox lista, String filtro, Label lblInfo) {
        lista.getChildren().clear();

        String filtroNormalizado = filtro == null ? "" : filtro.toLowerCase().trim();

        motorJogo.getUpgrades().stream()
                .sorted(Comparator.comparing(Upgrade::getGrupo).thenComparing(Upgrade::getNome))
                .filter(upgrade -> upgrade.getGrupo().toLowerCase().contains(filtroNormalizado) || upgrade.getNome().toLowerCase().contains(filtroNormalizado))
                .forEach(upgrade -> {
                    HBox linha = new HBox(10);
                    linha.setAlignment(Pos.CENTER_LEFT);

                    Label nome = new Label("[" + upgrade.getGrupo() + "] " + upgrade.getNome());
                    nome.setPrefWidth(260);

                    Label status = new Label(upgrade.isComprado() ? "Comprado" : "Disponível");
                    status.setPrefWidth(90);

                    Button btnComprar = new Button("Comprar");
                    btnComprar.setDisable(upgrade.isComprado());
                    Button btnRemover = new Button("Remover");

                    btnComprar.setOnAction(e -> {
                        motorJogo.comprarUpgradeDebug(upgrade);
                        status.setText("Comprado");
                        btnComprar.setDisable(true);
                        atualizarTela();
                        atualizarInfoDebug(lblInfo);
                    });
                    btnRemover.setOnAction(e -> {
                        if (motorJogo.removerUpgrade(upgrade)) {
                            montarLoja();
                            atualizarTela();
                            atualizarPainelDebugAtual();
                        }
                    });

                    linha.getChildren().addAll(nome, status, btnComprar, btnRemover);
                    lista.getChildren().add(linha);
                });
    }

    private void fecharDebugSeAberto() {
        if (janelaDebug != null && janelaDebug.isShowing()) {
            janelaDebug.close();
            janelaDebug = null;
        }
    }

    private void adicionarInstanciasDebug(long quantidade, Label lblInfo) {
        motorJogo.getEstadoJogo().adicionarInstancias(quantidade);
        atualizarTela();
        atualizarInfoDebug(lblInfo);
    }

    private void comprarConstrucaoLoja(Construcao construcao) {
        try {
            motorJogo.comprarConstrucao(construcao);
            lblStatus.setText("Comprado: " + construcao.getNome());
            atualizarTela();
        } catch (SaldoInsuficienteException e) {
            lblStatus.setText(e.getMessage());
        }
    }

    private void comprarUpgradeLoja(Upgrade upgrade) {
        try {
            motorJogo.comprarUpgrade(upgrade);
            lblStatus.setText("Upgrade comprado: " + upgrade.getNome());
            atualizarTela();
        } catch (SaldoInsuficienteException | UpgradeJaCompradoException e) {
            lblStatus.setText(e.getMessage());
        }
    }

    private void montarCodigoBotao() {
        flowCodigo.getChildren().clear();
        Text t1 = new Text("public ");
        t1.setStyle("-fx-fill: #c792ea; -fx-font-size: 11px; -fx-font-family: 'Consolas';");
        Text t2 = new Text("class ");
        t2.setStyle("-fx-fill: #c792ea; -fx-font-size: 11px; -fx-font-family: 'Consolas';");
        Text t3 = new Text("HelloWorld ");
        t3.setStyle("-fx-fill: #82aaff; -fx-font-size: 11px; -fx-font-family: 'Consolas';");
        Text t4 = new Text("{\n");
        t4.setStyle("-fx-fill: #e8e8e8; -fx-font-size: 11px; -fx-font-family: 'Consolas';");
        Text t5 = new Text("    public static void ");
        t5.setStyle("-fx-fill: #c792ea; -fx-font-size: 11px; -fx-font-family: 'Consolas';");
        Text t6 = new Text("main");
        t6.setStyle("-fx-fill: #ffcb6b; -fx-font-size: 11px; -fx-font-family: 'Consolas';");
        Text t7 = new Text("(String[] args) ");
        t7.setStyle("-fx-fill: #f78c6c; -fx-font-size: 11px; -fx-font-family: 'Consolas';");
        Text t8 = new Text("{\n");
        t8.setStyle("-fx-fill: #e8e8e8; -fx-font-size: 11px; -fx-font-family: 'Consolas';");
        Text t9 = new Text("        System.out.println");
        t9.setStyle("-fx-fill: #e8e8e8; -fx-font-size: 11px; -fx-font-family: 'Consolas';");
        Text t10 = new Text("(\"Hello, World!\");\n");
        t10.setStyle("-fx-fill: #89d185; -fx-font-size: 11px; -fx-font-family: 'Consolas';");
        Text t11 = new Text("    }\n");
        t11.setStyle("-fx-fill: #e8e8e8; -fx-font-size: 11px; -fx-font-family: 'Consolas';");
        Text t12 = new Text("}");
        t12.setStyle("-fx-fill: #e8e8e8; -fx-font-size: 11px; -fx-font-family: 'Consolas';");
        flowCodigo.getChildren().addAll(
                t1, t2, t3, t4,
                t5, t6, t7, t8,
                t9, t10, t11, t12
        );
    }

    private void estilizarScrolls() {
        estilizarScroll(scrollVisual);
        estilizarScroll(scrollUpgrades);
        estilizarScroll(scrollConstrucoes);
    }

    private void estilizarScroll(ScrollPane scrollPane) {
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle(
                "-fx-background-color: #22242a;" +
                "-fx-border-color: transparent;" +
                "-fx-background-insets: 0;" +
                "-fx-padding: 0;"
        );
        if (scrollPane.getContent() != null) {
            scrollPane.getContent().setStyle("-fx-background-color: #22242a;");
        }
        Platform.runLater(() -> {
            var viewport = scrollPane.lookup(".viewport");
            if (viewport != null) {
                viewport.setStyle("-fx-background-color: #22242a;");
            }
            var corner = scrollPane.lookup(".corner");
            if (corner != null) {
                corner.setStyle("-fx-background-color: #22242a;");
            }
            for (var barra : scrollPane.lookupAll(".scroll-bar")) {
                barra.setStyle(
                        "-fx-background-color: #17181e;" + "-fx-background-radius: 0;"
                );
                for (var track : barra.lookupAll(".track")) {
                    track.setStyle(
                            "-fx-background-color: #17181e;" + "-fx-background-radius: 0;"
                    );
                }
                for (var thumb : barra.lookupAll(".thumb")) {
                    thumb.setStyle(
                            "-fx-background-color: #42444d;" + "-fx-background-radius: 6;"
                    );
                }
                for (var button : barra.lookupAll(".increment-button")) {
                    button.setStyle(
                            "-fx-background-color: #17181e;" + "-fx-padding: 0;"
                    );
                }
                for (var button : barra.lookupAll(".decrement-button")) {
                    button.setStyle(
                            "-fx-background-color: #17181e;" + "-fx-padding: 0;"
                    );
                }
                for (var arrow : barra.lookupAll(".increment-arrow")) {
                    arrow.setStyle("-fx-background-color: #6b6f7a;");
                }
                for (var arrow : barra.lookupAll(".decrement-arrow")) {
                    arrow.setStyle("-fx-background-color: #6b6f7a;");
                }
            }
        });
    }

    private String estiloCardLoja(boolean podeComprar, boolean comprado, boolean hover) {
        String fundo;
        if (comprado) {
            fundo = hover ? "#343a4c" : "#303544";
        } else {
            fundo = hover ? "#343740" : "#2c2e35";
        }
        String borda;
        if (comprado) {
            borda = "#6f83c9";
        } else if (podeComprar) {
            borda = "#55d17a";
        } else {
            borda = "#8b1e2d";
        }
        return "-fx-background-color: " + fundo + ";" +
                "-fx-border-color: " + borda + ";" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 0;" +
                "-fx-border-radius: 0;" +
                "-fx-padding: 8;" +
                "-fx-alignment: center-left;" +
                "-fx-cursor: hand;";
    }
}
