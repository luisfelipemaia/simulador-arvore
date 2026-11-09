package simulador;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.SwingUtilities;
/**
 *
 * @author enzov
 */
public class Janelas {

    private static int abertas;

    public static void voltarAoMenu( EngineFrame origem ) {
        SwingUtilities.invokeLater( () -> {
            Menu menu = null;
            for ( Window janela : Window.getWindows() ) {
                if ( janela instanceof Menu && janela.isDisplayable() ) {
                    menu = (Menu) janela;
                    break;
                }
            }
            if ( menu == null ) {
                menu = new Menu();
            }
            menu.setExtendedState( EngineFrame.NORMAL );
            menu.setVisible( true );
            menu.toFront();
            menu.requestFocus();
            if ( origem != menu && origem.isDisplayable() ) {
                origem.dispatchEvent( new WindowEvent( origem, WindowEvent.WINDOW_CLOSING ) );
            }
        } );
    }

    public static synchronized void registrar( EngineFrame janela ) {
        abertas++;
        janela.setDefaultCloseOperation( EngineFrame.DISPOSE_ON_CLOSE );
        janela.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosed( WindowEvent evento ) {
                fechar();
            }
        } );
    }

    private static synchronized void fechar() {
        abertas--;
        if ( abertas == 0 ) {
            System.exit( 0 );
        }
    }
}
