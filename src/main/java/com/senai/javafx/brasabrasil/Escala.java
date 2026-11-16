package com.senai.javafx.brasabrasil;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 * Contêiner raiz de todas as telas: faz o conteúdo escalar proporcionalmente
 * conforme o tamanho da janela, para o layout ficar igual (sem aperto e sem
 * cortes) em qualquer resolução ou escala de tela do Windows.
 *
 * O conteúdo é desenhado numa área "base" de 1280 x 700. A escala é a maior que
 * ainda cabe na janela; o espaço que sobra na outra direção é devolvido ao
 * layout (que é fluido), então nunca aparecem faixas vazias.
 */
public class Escala extends Pane {

    public static final double LARGURA_BASE = 1280;
    public static final double ALTURA_BASE = 700;

    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }

        double escala = Math.min(w / LARGURA_BASE, h / ALTURA_BASE);
        double larguraLogica = w / escala;
        double alturaLogica = h / escala;

        for (Node filho : getChildren()) {
            if (filho instanceof Region) {
                ((Region) filho).resize(larguraLogica, alturaLogica);
            }
            filho.setScaleX(escala);
            filho.setScaleY(escala);
            // a escala é aplicada a partir do centro do nó
            filho.setLayoutX((w - larguraLogica) / 2);
            filho.setLayoutY((h - alturaLogica) / 2);
        }
    }

    @Override
    protected double computePrefWidth(double altura) {
        return LARGURA_BASE;
    }

    @Override
    protected double computePrefHeight(double largura) {
        return ALTURA_BASE;
    }

    @Override
    protected double computeMinWidth(double altura) {
        return 0;
    }

    @Override
    protected double computeMinHeight(double largura) {
        return 0;
    }
}
