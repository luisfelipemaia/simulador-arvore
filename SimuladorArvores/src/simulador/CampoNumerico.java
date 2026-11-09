package simulador;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.imgui.GuiComponentMouseState;
import br.com.davidbuzatto.jsge.imgui.GuiTextField;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import javax.swing.SwingUtilities;

// Campo da JSGE com seleção, apagamento e espaço para a sequência inteira
public class CampoNumerico extends GuiTextField {

    private boolean focado;
    private boolean tudoSelecionado;
    private boolean lista;
    private int cursor;
    private int inicioVisivel;
    private int ultimaTecla = EngineFrame.KEY_NULL;
    private double tempoRepeticao;
    private double tempoCursor;

    public CampoNumerico( double x, double y, double largura, double altura,
            String valor, boolean lista, EngineFrame engine ) {
        super( x, y, largura, altura, valor, engine );
        this.lista = lista;
    }

    @Override
    public void setValue( String valor ) {
        super.setValue( valor );
        cursor = getValue().length();
        inicioVisivel = 0;
        tudoSelecionado = false;
    }

    public boolean estaFocado() {
        return focado;
    }

    @Override
    public void update( double delta ) {
        if ( !isEnabled() || !isVisible() ) {
            focado = false;
            tudoSelecionado = false;
            return;
        }
        boolean dentro = engine.getMouseX() >= getX() && engine.getMouseX() <= getX() + getWidth()
                && engine.getMouseY() >= getY() && engine.getMouseY() <= getY() + getHeight();
        mouseState = GuiComponentMouseState.MOUSE_OUT;
        if ( engine.isMouseButtonPressed( EngineFrame.MOUSE_BUTTON_LEFT ) ) {
            boolean jaEstavaFocado = focado;
            focado = dentro;
            tudoSelecionado = false;
            tempoCursor = 0;
            ultimaTecla = EngineFrame.KEY_NULL;
            if ( focado ) {
                if ( !jaEstavaFocado ) {
                    SwingUtilities.invokeLater( () -> engine.getContentPane().getComponent( 0 ).requestFocusInWindow() );
                    // O primeiro clique permite trocar o valor sem apagar dígito por dígito
                    tudoSelecionado = true;
                    cursor = getValue().length();
                } else {
                    int posicao = inicioVisivel + (int) Math.round( ( engine.getMouseX() - getX() - 5 ) / larguraLetra() );
                    cursor = Math.max( 0, Math.min( getValue().length(), posicao ) );
                }
            }
        }
        if ( !focado ) {
            return;
        }
        tempoCursor += delta;
        if ( engine.isKeyDown( EngineFrame.KEY_CONTROL ) ) {
            if ( engine.isKeyPressed( EngineFrame.KEY_A ) ) {
                tudoSelecionado = true;
                cursor = getValue().length();
            } else if ( engine.isKeyPressed( EngineFrame.KEY_V ) ) {
                colar();
            } else if ( tudoSelecionado && engine.isKeyPressed( EngineFrame.KEY_BACKSPACE ) ) {
                editar( EngineFrame.KEY_BACKSPACE );
            } else if ( tudoSelecionado && engine.isKeyPressed( EngineFrame.KEY_DELETE ) ) {
                editar( EngineFrame.KEY_DELETE );
            }
            ultimaTecla = EngineFrame.KEY_NULL;
            return;
        }
        int[] edicao = { EngineFrame.KEY_BACKSPACE, EngineFrame.KEY_DELETE, EngineFrame.KEY_LEFT,
            EngineFrame.KEY_RIGHT, EngineFrame.KEY_HOME, EngineFrame.KEY_END, EngineFrame.KEY_TAB };
        for ( int tecla : edicao ) {
            if ( repetir( tecla, delta ) ) {
                editar( tecla );
                return;
            }
        }
        for ( int i = 0; i <= 9; i++ ) {
            if ( repetir( EngineFrame.KEY_ZERO + i, delta ) || repetir( EngineFrame.KEY_KP_0 + i, delta ) ) {
                inserir( String.valueOf( i ) );
                return;
            }
        }
        if ( repetir( EngineFrame.KEY_MINUS, delta ) || repetir( EngineFrame.KEY_KP_SUBTRACT, delta ) ) {
            inserir( "-" );
        } else if ( lista && repetir( EngineFrame.KEY_COMMA, delta ) ) {
            inserir( "," );
        } else if ( lista && repetir( EngineFrame.KEY_SEMICOLON, delta ) ) {
            inserir( ";" );
        } else if ( lista && repetir( EngineFrame.KEY_SPACE, delta ) ) {
            inserir( " " );
        }
    }

    private boolean repetir( int tecla, double delta ) {
        if ( engine.isKeyPressed( tecla ) ) {
            ultimaTecla = tecla;
            tempoRepeticao = 0;
            return true;
        }
        if ( ultimaTecla == tecla ) {
            if ( !engine.isKeyDown( tecla ) ) {
                ultimaTecla = EngineFrame.KEY_NULL;
                tempoRepeticao = 0;
            } else {
                tempoRepeticao += delta;
                if ( tempoRepeticao >= 0.4 ) {
                    tempoRepeticao -= 0.05;
                    return true;
                }
            }
        }
        return false;
    }

    private void editar( int tecla ) {
        String texto = getValue();
        if ( tecla == EngineFrame.KEY_BACKSPACE || tecla == EngineFrame.KEY_DELETE ) {
            if ( tudoSelecionado ) {
                setValue( "" );
            } else if ( tecla == EngineFrame.KEY_BACKSPACE && cursor > 0 ) {
                super.setValue( texto.substring( 0, cursor - 1 ) + texto.substring( cursor ) );
                cursor--;
            } else if ( tecla == EngineFrame.KEY_DELETE && cursor < texto.length() ) {
                super.setValue( texto.substring( 0, cursor ) + texto.substring( cursor + 1 ) );
            }
        } else if ( tecla == EngineFrame.KEY_HOME ) {
            cursor = 0;
        } else if ( tecla == EngineFrame.KEY_END ) {
            cursor = texto.length();
        } else if ( tecla == EngineFrame.KEY_LEFT ) {
            if ( tudoSelecionado ) {
                cursor = 0;
            } else {
                cursor = Math.max( 0, cursor - 1 );
            }
        } else if ( tecla == EngineFrame.KEY_RIGHT ) {
            if ( tudoSelecionado ) {
                cursor = texto.length();
            } else {
                cursor = Math.min( texto.length(), cursor + 1 );
            }
        } else if ( tecla == EngineFrame.KEY_TAB ) {
            focado = false;
        }
        tudoSelecionado = false;
        tempoCursor = 0;
    }

    private void inserir( String trecho ) {
        String texto = getValue();
        int posicao = cursor;
        if ( tudoSelecionado ) {
            texto = "";
            posicao = 0;
        }
        int limite = 12;
        if ( lista ) {
            limite = 1600;
        }
        if ( texto.length() + trecho.length() <= limite ) {
            super.setValue( texto.substring( 0, posicao ) + trecho + texto.substring( posicao ) );
            cursor = posicao + trecho.length();
            tudoSelecionado = false;
            tempoCursor = 0;
        }
    }

    private void colar() {
        try {
            String texto = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData( DataFlavor.stringFlavor );
            for ( int i = 0; i < texto.length(); i++ ) {
                char c = texto.charAt( i );
                if ( !Character.isDigit( c ) && c != '-' && !( lista && ( c == ',' || c == ';' || Character.isWhitespace( c ) ) ) ) {
                    return;
                }
            }
            inserir( texto );
        } catch ( Exception exc ) {
            // Mantém o campo como está se não houver texto disponível para colar
        }
    }

    private double larguraLetra() {
        return Math.max( 1, engine.measureText( "0", FONT_SIZE ) );
    }

    @Override
    public void draw() {
        if ( !isVisible() ) {
            return;
        }
        int capacidade = Math.max( 1, (int) ( ( getWidth() - 12 ) / larguraLetra() ) );
        if ( focado ) {
            if ( cursor < inicioVisivel ) {
                inicioVisivel = cursor;
            } else if ( cursor > inicioVisivel + capacidade ) {
                inicioVisivel = cursor - capacidade;
            }
        }
        inicioVisivel = Math.max( 0, Math.min( inicioVisivel, getValue().length() ) );
        int fim = Math.min( getValue().length(), inicioVisivel + capacidade );
        String texto = getValue().substring( inicioVisivel, fim );
        Color fundo = getBackgroundColor();
        Color borda = getBorderColor();
        if ( focado ) {
            borda = Cores.AZUL_CLARO;
        }
        engine.fillRectangle( bounds, fundo );
        engine.drawRectangle( bounds, borda );
        double y = getY() + getHeight() / 2 - 3;
        engine.beginScissorMode( getX() + 2, getY() + 2, getWidth() - 4, getHeight() - 4 );
        Color corTexto = getTextColor();
        if ( focado && tudoSelecionado && !texto.isEmpty() ) {
            engine.fillRectangle( getX() + 5, y - 3, engine.measureText( texto, FONT_SIZE ), FONT_SIZE + 6, Cores.AZUL_CLARO );
            corTexto = Cores.FUNDO;
        }
        engine.drawText( texto, getX() + 5, y, FONT_SIZE, corTexto );
        if ( focado && !tudoSelecionado && tempoCursor % 1 < 0.5 ) {
            double x = getX() + 5 + ( cursor - inicioVisivel ) * larguraLetra();
            engine.drawLine( x, getY() + 5, x, getY() + getHeight() - 5, Cores.TEXTO );
        }
        engine.endScissorMode();
    }
}
