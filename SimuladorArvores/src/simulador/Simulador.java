package simulador;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.imgui.GuiButton;
import br.com.davidbuzatto.jsge.imgui.GuiLabel;
import br.com.davidbuzatto.jsge.imgui.GuiSlider;
import br.com.davidbuzatto.jsge.imgui.GuiTextField;
import java.awt.Color;
import java.util.List;
import java.util.Random;


/**
 *
 * @author enzov
 */
public class Simulador extends EngineFrame {

    private boolean decrescente;

    private ABB arvore;
    private ABB exibicao;
    private List<ABB.Passo> passos;
    private int passoAtual;
    private boolean pausado;
    private double tempo;
    private String mensagem;
    private Random aleatorio;

    // Campo da chave e botões das operações
    private GuiTextField campoChave;
    private GuiButton btnInserir;
    private GuiButton btnRemover;
    private GuiButton btnBuscar;
    private GuiButton btnAleatoria;
    private GuiButton btnLimpar;
    private GuiButton btnAnterior;
    private GuiButton btnPausar;
    private GuiButton btnProximo;
    private GuiButton btnPular;
    private GuiButton btnMenu;
    private GuiSlider velocidade;
    private GuiLabel lblVelocidade;
    private List<GuiButton> botoes;

    public Simulador( String tipo ) {
        super( 1000, 720, "Simulador de Árvores - " + tipo, 60, true );
    }

    public Simulador( String tipo, int[] numeros, boolean decrescente ) {
        this( tipo );
        carregar( numeros, decrescente );
    }

    // Junta os passos para mostrar a montagem da árvore inteira
    public synchronized void carregar( int[] numeros, boolean decrescente ) {
        this.decrescente = decrescente;
        arvore.setGravar( false );
        arvore.limpar();
        arvore.setGravar( true );
        List<ABB.Passo> montagem = new java.util.ArrayList<>();
        for ( int c : numeros ) {
            arvore.put( c );
            montagem.addAll( arvore.getPassos() );
        }
        if ( montagem.isEmpty() ) {
            arvore.iniciar( "Árvore vazia." );
            montagem.addAll( arvore.getPassos() );
        }
        passos = montagem;
        pausado = false;
        mostrarPasso( 0 );
        atualizarPosicoes();
    }

    public synchronized List<Integer> numerosOrdenados() {
        List<Integer> numeros = exibicao.chavesEmOrdem();
        if ( decrescente ) {
            java.util.Collections.reverse( numeros );
        }
        return numeros;
    }

    @Override
    public void create() {
        // A JSGE chama create() antes de terminar o construtor da janela
        if ( getTitle().endsWith( "AVL" ) ) {
            arvore = new AVL();
        } else if ( getTitle().endsWith( "AVP" ) ) {
            arvore = new AVP();
        } else {
            arvore = new ABB();
        }
        Janelas.registrar( this );
        Cores.configurarGUI();
        setDefaultFontSize( 16 );
        aleatorio = new Random();
        arvore.setGravar( false );
        for ( int c : new int[]{ 20, 10, 15, 5, 30 } ) {
            arvore.put( c );
        }
        arvore.setGravar( true );
        arvore.iniciar( "Árvore inicial carregada: 20, 10, 15, 5 e 30." );
        passos = arvore.getPassos();
        passoAtual = passos.size() - 1;
        pausado = true;
        mensagem = "Digite uma chave entre -9999 e 9999 e escolha a operação.";
        exibicao = arvore.exibir( passos.get( passoAtual ) );

        campoChave = new CampoNumerico( 24, 62, 150, 34, "", false, this );
        btnInserir = new GuiButton( 186, 62, 140, 34, "Inserir (put)", this );
        btnRemover = new GuiButton( 338, 62, 150, 34, "Remover (delete)", this );
        btnBuscar = new GuiButton( 500, 62, 110, 34, "Buscar", this );
        btnAleatoria = new GuiButton( 622, 62, 110, 34, "Aleatória", this );
        btnLimpar = new GuiButton( 744, 62, 110, 34, "Limpar", this );
        btnAnterior = new GuiButton( 24, 114, 110, 30, "Anterior", this );
        btnPausar = new GuiButton( 146, 114, 110, 30, "Continuar", this );
        btnProximo = new GuiButton( 268, 114, 110, 30, "Próximo", this );
        btnPular = new GuiButton( 390, 114, 110, 30, "Pular", this );
        btnMenu = new GuiButton( getScreenWidth() - 200, 8, 176, 28, "Voltar ao menu", this );
        btnMenu.setBackgroundColor( Cores.AZUL );
        btnMenu.setBorderColor( Cores.AZUL );
        velocidade = new GuiSlider( 650, 122, 210, 14, 2, 0.5, 6, this );
        lblVelocidade = new GuiLabel( 520, 110, 130, 34, "2.0 passos/s", this );
        botoes = List.of( btnInserir, btnRemover, btnBuscar, btnAleatoria, btnLimpar,
                btnAnterior, btnPausar, btnProximo, btnPular, btnMenu );
        atualizarPosicoes();
    }

    @Override
    // Evita desenhar a árvore enquanto outra thread está atualizando
    public synchronized void update( double delta ) {
        campoChave.update( delta );
        for ( GuiButton botao : botoes ) {
            botao.update( delta );
        }
        velocidade.update( delta );
        lblVelocidade.setText( String.format( java.util.Locale.ROOT, "%.1f passos/s", velocidade.getValue() ) );

        if ( btnMenu.isMousePressed() ) {
            voltarAoMenu();
        } else if ( btnInserir.isMousePressed() || isKeyPressed( KEY_ENTER ) ) {
            executar( "put" );
        } else if ( btnRemover.isMousePressed() ) {
            executar( "delete" );
        } else if ( btnBuscar.isMousePressed() ) {
            executar( "buscar" );
        } else if ( btnAleatoria.isMousePressed() ) {
            inserirAleatoria();
        } else if ( btnLimpar.isMousePressed() ) {
            arvore.limpar();
            iniciarReproducao();
        } else if ( btnAnterior.isMousePressed() ) {
            pausado = true;
            mostrarPasso( passoAtual - 1 );
        } else if ( btnProximo.isMousePressed() ) {
            pausado = true;
            mostrarPasso( passoAtual + 1 );
        } else if ( btnPular.isMousePressed() ) {
            pausado = true;
            mostrarPasso( passos.size() - 1 );
        } else if ( btnPausar.isMousePressed() ) {
            if ( passoAtual == passos.size() - 1 ) {
                mostrarPasso( 0 );
            }
            pausado = !pausado;
            tempo = 0;
        }

        if ( !pausado ) {
            tempo += delta;
            if ( tempo >= 1.0 / velocidade.getValue() ) {
                mostrarPasso( passoAtual + 1 );
            }
            if ( passoAtual == passos.size() - 1 ) {
                pausado = true;
            }
        }
        if ( pausado ) {
            btnPausar.setText( "Continuar" );
        } else {
            btnPausar.setText( "Pausar" );
        }
        atualizarPosicoes();
    }

    private void voltarAoMenu() {
        pausado = true;
        tempo = 0;
        Janelas.voltarAoMenu( this );
    }

    // Confere a chave antes de mexer na árvore
    private void executar( String operacao ) {
        try {
            int c = Integer.parseInt( campoChave.getValue().trim() );
            if ( c < -9999 || c > 9999 ) {
                mensagem = "Informe uma chave entre -9999 e 9999.";
                pausado = true;
                return;
            }
            if ( operacao.equals( "put" ) ) {
                if ( arvore.tamanho() >= 200 && !arvore.contem( c ) ) {
                    mensagem = "Limite de 200 nós para a gravação. Remova uma chave ou limpe a árvore.";
                    pausado = true;
                    return;
                }
                arvore.put( c );
            } else if ( operacao.equals( "delete" ) ) {
                arvore.delete( c );
            } else {
                arvore.buscar( c );
            }
            iniciarReproducao();
        } catch ( NumberFormatException exc ) {
            mensagem = "Entrada inválida. Digite uma chave inteira entre -9999 e 9999.";
            pausado = true;
        }
    }

    private void inserirAleatoria() {
        // Separa as chaves livres para não repetir número no sorteio
        java.util.ArrayList<Integer> livres = new java.util.ArrayList<>();
        for ( int c = 1; c <= 99; c++ ) {
            if ( !arvore.contem( c ) ) {
                livres.add( c );
            }
        }
        if ( livres.isEmpty() ) {
            mensagem = "Todas as chaves de 1 a 99 já estão na árvore.";
            pausado = true;
        } else {
            campoChave.setValue( String.valueOf( livres.get( aleatorio.nextInt( livres.size() ) ) ) );
            executar( "put" );
        }
    }

    private void iniciarReproducao() {
        passos = arvore.getPassos();
        pausado = false;
        mostrarPasso( 0 );
    }

    private void mostrarPasso( int indice ) {
        passoAtual = Math.max( 0, Math.min( passos.size() - 1, indice ) );
        ABB.Passo passo = passos.get( passoAtual );
        exibicao = arvore.exibir( passo );
        mensagem = passo.mensagem;
        tempo = 0;
    }

    private void atualizarPosicoes() {
        // Posiciona os nós pelo ranque, como no código da aula
        int quantidade = exibicao.tamanho();
        double espHor = Math.min( 95, ( getScreenWidth() - 100.0 ) / Math.max( 1, quantidade - 1 ) );
        double espVert = Math.min( 85, ( getScreenHeight() - 400.0 ) / Math.max( 1, exibicao.altura() ) );
        int margemHor = (int) ( ( getScreenWidth() - ( quantidade - 1 ) * espHor ) / 2 );
        exibicao.atualizarNos( espHor, espVert, margemHor, 230 );
    }

    private String abreviar( String texto ) {
        while ( measureText( texto, 14 ) > getScreenWidth() - 60 && texto.length() > 5 ) {
            texto = texto.substring( 0, texto.length() - 4 ) + "...";
        }
        return texto;
    }

    @Override
    public synchronized void draw() {
        clearBackground( Cores.FUNDO );
        fillRectangle( 0, 0, getScreenWidth(), 45, Cores.PAINEL );
        String nome;
        if ( arvore instanceof AVP ) {
            nome = "Árvore Vermelha e Preta (LLRB)";
        } else if ( arvore instanceof AVL ) {
            nome = "Árvore AVL";
        } else {
            nome = "Árvore Binária de Busca";
        }
        drawText( nome, 24, 12, 22, Cores.TEXTO );
        Cores.desenhar( campoChave );
        for ( GuiButton botao : botoes ) {
            Cores.desenhar( botao );
        }
        Cores.desenhar( velocidade );
        Cores.desenhar( lblVelocidade );
        desenharLegenda();
        beginScissorMode( 0, 195, getScreenWidth(), getScreenHeight() - 305 );
        exibicao.desenhar( this );
        if ( exibicao.tamanho() == 0 ) {
            drawText( "Árvore vazia", getScreenWidth() / 2 - 55, 320, Cores.TEXTO_SECUNDARIO );
        }
        endScissorMode();
        int y = getScreenHeight() - 100;
        drawLine( 24, y - 12, getScreenWidth() - 24, y - 12, Cores.BORDA );
        String estado;
        if ( pausado ) {
            estado = "Pausado";
        } else {
            estado = "Reproduzindo";
        }
        drawText( "Passo " + ( passoAtual + 1 ) + " de " + passos.size() + "   |   "
                + estado, 24, y, 16, Cores.AZUL_CLARO );
        drawText( abreviar( mensagem ), 24, y + 25, 14, Cores.TEXTO );
        String ordem;
        if ( decrescente ) {
            ordem = "Decrescente: ";
        } else {
            ordem = "Crescente: ";
        }
        drawText( abreviar( ordem + numerosOrdenados() ),
                24, y + 49, 14, Cores.TEXTO_SECUNDARIO );
        drawText( "Nós: " + exibicao.tamanho() + "   Altura: " + exibicao.altura()
                + " (vazia: -1)   |   Enter: inserir   |   h = altura, fb = esq. - dir.",
                24, y + 73, 12, Cores.TEXTO_SECUNDARIO );
    }

    private void desenharLegenda() {
        Color[] cores = { GOLD, SKYBLUE, GREEN, ORANGE, PURPLE, RED };
        String[] textos = { "Comparação", "Visitado", "Novo / achou", "Retorno", "Ajuste", "Remoção" };
        int x = 30;
        for ( int i = 0; i < cores.length; i++ ) {
            fillCircle( x, 173, 6, cores[i] );
            drawText( textos[i], x + 12, 167, 12, Cores.TEXTO_SECUNDARIO );
            x += 150;
        }
    }
}
