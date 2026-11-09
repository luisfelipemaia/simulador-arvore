package simulador;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.imgui.GuiButton;
import br.com.davidbuzatto.jsge.imgui.GuiList;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author enzov
 */
// Menu para escolher os números e a árvore
public class Menu extends EngineFrame {

    private CampoNumerico campoNumeros;
    private CampoNumerico campoQuantidade;
    private GuiButton btnSortear;
    private GuiButton btnVerSequencia;
    private GuiButton btnFecharLista;
    private GuiList listaNumeros;
    private boolean mostrarLista;
    private Random aleatorio;
    private List<GuiButton> botoesModo;
    private GuiButton btnCrescente;
    private GuiButton btnDecrescente;
    private GuiButton btnIniciar;
    private GuiButton btnExemplo;
    private GuiButton btnLimpar;
    private String modo;
    private boolean decrescente;
    private String mensagem;

    public Menu() {
        super( 1000, 610, "Simulador de Árvores - Menu", 60, true );
    }

    @Override
    public void create() {
        Janelas.registrar( this );
        Cores.configurarGUI();
        setDefaultFontSize( 16 );
        modo = "ABB";
        aleatorio = new Random();
        mensagem = "Escolha os números e o tipo de árvore para começar.";
        campoNumeros = new CampoNumerico( 32, 115, 930, 38, "20, 10, 15, 5, 30", true, this );
        campoQuantidade = new CampoNumerico( 150, 195, 90, 34, "10", false, this );
        btnSortear = new GuiButton( 254, 195, 205, 34, "Sortear números", this );
        btnVerSequencia = new GuiButton( 757, 195, 205, 34, "Ver sequência", this );
        btnFecharLista = new GuiButton( 832, 81, 110, 30, "Fechar lista", this );
        botoesModo = List.of(
                new GuiButton( 32, 285, 205, 38, "Binária de busca", this ),
                new GuiButton( 249, 285, 150, 38, "AVL", this ),
                new GuiButton( 411, 285, 235, 38, "Vermelha e Preta", this ),
                new GuiButton( 658, 285, 304, 38, "Comparar as três", this ) );
        btnCrescente = new GuiButton( 32, 385, 205, 38, "Crescente", this );
        btnDecrescente = new GuiButton( 249, 385, 205, 38, "Decrescente", this );
        btnIniciar = new GuiButton( 32, 465, 280, 42, "Iniciar simulação", this );
        btnExemplo = new GuiButton( 324, 465, 205, 42, "Carregar exemplo", this );
        btnLimpar = new GuiButton( 541, 465, 205, 42, "Limpar números", this );
        btnIniciar.setBackgroundColor( Cores.VERDE );
        btnIniciar.setBorderColor( Cores.VERDE );
        btnIniciar.setTextColor( Cores.FUNDO );
    }

    @Override
    public synchronized void update( double delta ) {
        if ( mostrarLista ) {
            listaNumeros.update( delta );
            btnFecharLista.update( delta );
            if ( btnFecharLista.isMousePressed() ) {
                mostrarLista = false;
            }
            return;
        }
        campoNumeros.update( delta );
        campoQuantidade.update( delta );
        String[] modos = { "ABB", "AVL", "AVP", "TODAS" };
        for ( int i = 0; i < botoesModo.size(); i++ ) {
            GuiButton botao = botoesModo.get( i );
            botao.update( delta );
            if ( botao.isMousePressed() ) {
                modo = modos[i];
            }
        }
        for ( GuiButton botao : List.of( btnCrescente, btnDecrescente, btnIniciar, btnExemplo, btnLimpar,
                btnSortear, btnVerSequencia ) ) {
            botao.update( delta );
        }
        if ( btnSortear.isMousePressed() ) {
            sortear();
        } else if ( btnVerSequencia.isMousePressed() ) {
            verSequencia();
        } else if ( btnCrescente.isMousePressed() ) {
            decrescente = false;
        } else if ( btnDecrescente.isMousePressed() ) {
            decrescente = true;
        } else if ( btnExemplo.isMousePressed() ) {
            campoNumeros.setValue( "20, 10, 15, 5, 30" );
        } else if ( btnLimpar.isMousePressed() ) {
            campoNumeros.setValue( "" );
        } else if ( isKeyPressed( KEY_ENTER ) && campoQuantidade.estaFocado() ) {
            sortear();
        } else if ( btnIniciar.isMousePressed() || isKeyPressed( KEY_ENTER ) ) {
            iniciar();
        }
    }

    private void sortear() {
        try {
            int quantidade = Integer.parseInt( campoQuantidade.getValue().trim() );
            int[] numeros = EntradaNumeros.sortear( quantidade, aleatorio );
            campoNumeros.setValue( EntradaNumeros.formatar( numeros ) );
            mensagem = quantidade + " números sorteados sem repetição. Use Ver sequência para consultar todos.";
        } catch ( NumberFormatException exc ) {
            mensagem = "Digite uma quantidade inteira entre 1 e 200.";
        } catch ( IllegalArgumentException exc ) {
            mensagem = exc.getMessage();
        }
    }

    private void verSequencia() {
        try {
            int[] numeros = EntradaNumeros.ler( campoNumeros.getValue() );
            List<String> linhas = new ArrayList<>();
            for ( int inicio = 0; inicio < numeros.length; inicio += 15 ) {
                int fim = Math.min( inicio + 15, numeros.length );
                StringBuilder linha = new StringBuilder();
                linha.append( inicio + 1 ).append( " a " ).append( fim ).append( ":  " );
                for ( int i = inicio; i < fim; i++ ) {
                    if ( i > inicio ) {
                        linha.append( ", " );
                    }
                    linha.append( numeros[i] );
                }
                linhas.add( linha.toString() );
            }
            listaNumeros = new ListaSimples( 48, 130, 884, 360, linhas, this );
            mostrarLista = true;
        } catch ( IllegalArgumentException exc ) {
            mensagem = exc.getMessage();
        }
    }

    private void iniciar() {
        try {
            int[] numeros = EntradaNumeros.ler( campoNumeros.getValue() );
            if ( modo.equals( "TODAS" ) ) {
                new Comparador( numeros, decrescente );
            } else {
                Simulador janela = new Simulador( modo, numeros, decrescente );
                janela.setLocation( 50, 40 );
            }
            mensagem = "Simulação aberta. Você pode escolher outra sequência neste menu.";
        } catch ( IllegalArgumentException exc ) {
            mensagem = exc.getMessage();
        }
    }

    private void desenharOpcao( GuiButton botao, boolean selecionado ) {
        if ( selecionado ) {
            botao.setBackgroundColor( Cores.AZUL );
            botao.setBorderColor( Cores.AZUL );
        } else {
            botao.setBackgroundColor( Cores.BOTAO );
            botao.setBorderColor( Cores.BOTAO );
        }
        Cores.desenhar( botao );
        if ( selecionado ) {
            drawRectangle( botao.getX() - 2, botao.getY() - 2,
                    botao.getWidth() + 4, botao.getHeight() + 4, Cores.AZUL );
        }
    }

    @Override
    public synchronized void draw() {
        clearBackground( Cores.PAINEL );
        fillRectangle( 0, 0, getScreenWidth(), 56, Cores.PAINEL );
        drawText( "Simulador de Árvores - Menu inicial", 32, 16, 22, Cores.TEXTO );
        drawText( "1. Números a inserir", 32, 83, 18, Cores.AZUL_CLARO );
        // Recorta o texto para ele não sair do campo quando a lista for grande
        beginScissorMode( 32, 115, 931, 39 );
        Cores.desenhar( campoNumeros );
        endScissorMode();
        drawText( "Separe por vírgula, ponto e vírgula ou espaço. Exemplo: 8, 3, 10, 1, 6", 32, 163, 13, Cores.TEXTO_SECUNDARIO );
        drawText( "Quantidade:", 32, 206, 14, Cores.AZUL_CLARO );
        Cores.desenhar( campoQuantidade );
        Cores.desenhar( btnSortear );
        Cores.desenhar( btnVerSequencia );
        drawText( "1 a 200 números; valores de 1 a 9999.", 476, 207, 12, Cores.TEXTO_SECUNDARIO );
        drawText( "2. Tipo de árvore (modo da simulação)", 32, 255, 18, Cores.AZUL_CLARO );
        String[] modos = { "ABB", "AVL", "AVP", "TODAS" };
        for ( int i = 0; i < botoesModo.size(); i++ ) {
            desenharOpcao( botoesModo.get( i ), modo.equals( modos[i] ) );
        }
        drawText( "3. Ordem da saída", 32, 355, 18, Cores.AZUL_CLARO );
        desenharOpcao( btnCrescente, !decrescente );
        desenharOpcao( btnDecrescente, decrescente );
        drawText( "A árvore recebe os números na ordem digitada. Chaves repetidas aparecem uma vez.", 32, 435, 13, Cores.TEXTO_SECUNDARIO );
        Cores.desenhar( btnIniciar );
        Cores.desenhar( btnExemplo );
        Cores.desenhar( btnLimpar );
        drawText( mensagem, 32, 530, 14, Cores.AZUL_CLARO );
        if ( mostrarLista ) {
            fillRectangle( 32, 65, 936, 480, Cores.PAINEL );
            drawRectangle( 32, 65, 936, 480, Cores.BORDA );
            drawText( "Sequência completa, na ordem de inserção", 48, 87, 16, Cores.TEXTO );
            listaNumeros.draw();
            Cores.desenhar( btnFecharLista );
            drawText( "Use a rolagem para consultar todos os números.", 48, 510, 13, Cores.AZUL_CLARO );
        }
    }
}
