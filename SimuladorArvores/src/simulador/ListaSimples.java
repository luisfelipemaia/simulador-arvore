package simulador;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.imgui.GuiList;
import java.util.List;


/**
 *
 * @author enzov
 */
// Lista da JSGE que destaca a linha apenas quando ela é selecionada
public class ListaSimples extends GuiList {

    public ListaSimples( double x, double y, double largura, double altura,
            List<String> linhas, EngineFrame engine ) {
        super( x, y, largura, altura, linhas, engine );
    }

    @Override
    public void draw() {
        if ( !isVisible() ) {
            return;
        }
        Cores.desenhar( this, () -> super.draw() );
        if ( scrollBar.isEnabled() ) {
            Cores.desenhar( scrollBar );
        }
        // Mantém a borda da lista igual quando o mouse passa pelas linhas
        engine.drawRectangle( getX(), getY(), getWidth() + scrollBar.getWidth(), getHeight(), getBorderColor() );
    }
}
