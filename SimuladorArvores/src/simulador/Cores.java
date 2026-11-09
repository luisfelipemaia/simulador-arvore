package simulador;

import br.com.davidbuzatto.jsge.imgui.GuiTheme;
import br.com.davidbuzatto.jsge.imgui.GuiComponent;
import br.com.davidbuzatto.jsge.imgui.GuiList;
import br.com.davidbuzatto.jsge.imgui.GuiScrollBar;
import br.com.davidbuzatto.jsge.imgui.GuiSlider;
import java.awt.Color;

/**
 *
 * @author enzov
 */
// Cores usadas no menu e nas árvores
public class Cores {

    public static final Color FUNDO = new Color( 15, 23, 42 );
    public static final Color PAINEL = new Color( 30, 41, 59 );
    public static final Color BOTAO = new Color( 51, 65, 85 );
    public static final Color BORDA = new Color( 71, 85, 105 );
    public static final Color TEXTO = new Color( 248, 250, 252 );
    public static final Color TEXTO_SECUNDARIO = new Color( 203, 213, 225 );
    public static final Color AZUL_CLARO = new Color( 147, 197, 253 );
    public static final Color AZUL = new Color( 37, 99, 235 );
    public static final Color VERDE = new Color( 34, 197, 94 );

    public static synchronized void configurarGUI() {
        GuiTheme tema = GuiTheme.buildDarkTheme();
        tema.backgroundColor = BOTAO;
        tema.borderColor = BOTAO;
        tema.textColor = TEXTO;
        tema.containerBackgroundColor = FUNDO;
        tema.containerBorderColor = BORDA;
        tema.containerTextColor = TEXTO;
        tema.listContainerBackgroundColor = FUNDO;
        tema.mouseOverBackgroundColor = BOTAO;
        tema.mouseOverBorderColor = BOTAO;
        tema.mouseOverTextColor = TEXTO;
        tema.mouseDownBackgroundColor = AZUL;
        tema.mouseDownBorderColor = AZUL_CLARO;
        tema.mouseDownTextColor = TEXTO;
        tema.disabledBackgroundColor = PAINEL;
        tema.disabledBorderColor = BORDA;
        tema.disabledTextColor = TEXTO_SECUNDARIO;
        tema.install();
    }

    public static void desenhar( GuiComponent componente ) {
        desenhar( componente, componente::draw );
    }

    // Mantém a cor normal ao passar o mouse e deixa o clique com a JSGE
    static synchronized void desenhar( GuiComponent componente, Runnable desenho ) {
        Color fundoAnterior = GuiComponent.MOUSE_OVER_BACKGROUND_COLOR;
        Color bordaAnterior = GuiComponent.MOUSE_OVER_BORDER_COLOR;
        Color textoAnterior = GuiComponent.MOUSE_OVER_TEXT_COLOR;
        try {
            GuiComponent.MOUSE_OVER_BACKGROUND_COLOR = componente.getBackgroundColor();
            GuiComponent.MOUSE_OVER_BORDER_COLOR = componente.getBorderColor();
            GuiComponent.MOUSE_OVER_TEXT_COLOR = componente.getTextColor();
            if ( componente instanceof GuiList ) {
                GuiComponent.MOUSE_OVER_BORDER_COLOR = componente.getBackgroundColor();
            } else if ( componente instanceof GuiSlider || componente instanceof GuiScrollBar ) {
                GuiComponent.MOUSE_OVER_BACKGROUND_COLOR = BOTAO;
                GuiComponent.MOUSE_OVER_BORDER_COLOR = BOTAO;
            }
            desenho.run();
        } finally {
            GuiComponent.MOUSE_OVER_BACKGROUND_COLOR = fundoAnterior;
            GuiComponent.MOUSE_OVER_BORDER_COLOR = bordaAnterior;
            GuiComponent.MOUSE_OVER_TEXT_COLOR = textoAnterior;
        }
    }
}
