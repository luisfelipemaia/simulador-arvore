package simulador;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.math.Vector2;
import java.awt.Color;

/**
 *
 * @author enzov
 */
public class No {

    // Chave e filhos do nó, como na estrutura da aula
    int c;
    No e;
    No d;
    Vector2 pos;
    int nivel;
    int ranque;
    int raio;
    int altura;
    boolean vermelho;
    boolean colorido;
    boolean mostrarBalanceamento = true;
    Destaque destaque = Destaque.NENHUM;

    public enum Destaque {
        NENHUM, ATUAL, VISITADO, ENCONTRADO, REMOVER, RETORNO, AJUSTE
    }

    public No( int raio ) {
        pos = new Vector2();
        this.raio = raio;
    }

    public void desenhar( EngineFrame engine ) {
        Color halo = null;
        if ( destaque == Destaque.ATUAL ) {
            halo = EngineFrame.GOLD;
        } else if ( destaque == Destaque.VISITADO ) {
            halo = EngineFrame.SKYBLUE;
        } else if ( destaque == Destaque.ENCONTRADO ) {
            halo = EngineFrame.GREEN;
        } else if ( destaque == Destaque.REMOVER ) {
            halo = EngineFrame.RED;
        } else if ( destaque == Destaque.RETORNO ) {
            halo = EngineFrame.ORANGE;
        } else if ( destaque == Destaque.AJUSTE ) {
            halo = EngineFrame.PURPLE;
        }
        if ( halo != null ) {
            engine.fillCircle( pos, raio + 5, halo );
        }
        Color preenchimento;
        Color corTexto;
        if ( colorido ) {
            corTexto = EngineFrame.WHITE;
            if ( vermelho ) {
                preenchimento = EngineFrame.MAROON;
            } else {
                preenchimento = EngineFrame.BLACK;
            }
        } else {
            preenchimento = EngineFrame.WHITE;
            corTexto = EngineFrame.BLACK;
        }
        engine.fillCircle( pos, raio, preenchimento );
        engine.drawCircle( pos, raio, Cores.TEXTO_SECUNDARIO );
        String label = String.valueOf( c );
        int fonte = Math.max( 7, Math.min( 16, (int) ( raio * 2.5 / label.length() ) ) );
        int largura = engine.measureText( label, fonte );
        engine.drawText( label, pos.x - largura / 2, pos.y - fonte / 2, fonte, corTexto );
        if ( mostrarBalanceamento ) {
            // Calcula a legenda deste passo sem mudar a altura usada pela AVL
            int ae = alturaVisual( e );
            int ad = alturaVisual( d );
            int h = 1 + Math.max( ae, ad );
            String info = "h=" + h + " fb=" + ( ae - ad );
            int tamanho = Math.max( 9, Math.min( 11, raio / 2 ) );
            engine.drawText( info, pos.x - engine.measureText( info, tamanho ) / 2,
                    pos.y + raio + 5, tamanho, Cores.AZUL_CLARO );
        }
    }

    private int alturaVisual( No no ) {
        if ( no == null ) {
            return -1;
        } else {
            return 1 + Math.max( alturaVisual( no.e ), alturaVisual( no.d ) );
        }
    }

    @Override
    public String toString() {
        return String.format( "c: %d, n: %d, r: %d", c, nivel, ranque );
    }
}
