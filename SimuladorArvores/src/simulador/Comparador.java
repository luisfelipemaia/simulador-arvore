package simulador;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.imgui.GuiButton;
import br.com.davidbuzatto.jsge.imgui.GuiLabel;
import br.com.davidbuzatto.jsge.imgui.GuiSlider;
import br.com.davidbuzatto.jsge.imgui.GuiTextField;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// Mostra as três árvores na mesma janela da JSGE
public class Comparador extends EngineFrame {

    private Comparacao comparacao;
    private boolean decrescente;
    private boolean pausado;
    private double tempo;
    private String aviso;
    private Random aleatorio;
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

    public Comparador( int[] numeros, boolean decrescente ) {
        super( 1280, 760, "Simulador de Árvores - Comparar as três", 60, true );
        carregar( numeros, decrescente );
    }

    private synchronized void carregar( int[] numeros, boolean decrescente ) {
        this.decrescente = decrescente;
        comparacao.carregar( numeros );
        pausado = false;
        atualizarPosicoes();
    }

    @Override
    public void create() {
        Janelas.registrar( this );
        Cores.configurarGUI();
        setDefaultFontSize( 16 );
        comparacao = new Comparacao();
        aleatorio = new Random();
        pausado = true;
        aviso = "A mesma operação será aplicada às três árvores.";
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
            comparacao.executar( "limpar", 0 );
            iniciarReproducao();
        } else if ( btnAnterior.isMousePressed() ) {
            pausado = true;
            mostrarPasso( comparacao.getPassoAtual() - 1 );
        } else if ( btnProximo.isMousePressed() ) {
            pausado = true;
            mostrarPasso( comparacao.getPassoAtual() + 1 );
        } else if ( btnPular.isMousePressed() ) {
            pausado = true;
            mostrarPasso( comparacao.quantidadePassos() - 1 );
        } else if ( btnPausar.isMousePressed() ) {
            if ( comparacao.getPassoAtual() == comparacao.quantidadePassos() - 1 ) {
                mostrarPasso( 0 );
            }
            pausado = !pausado;
            tempo = 0;
        }
        if ( !pausado ) {
            tempo += delta;
            if ( tempo >= 1.0 / velocidade.getValue() ) {
                mostrarPasso( comparacao.getPassoAtual() + 1 );
            }
            if ( comparacao.getPassoAtual() == comparacao.quantidadePassos() - 1 ) {
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

    private void executar( String operacao ) {
        try {
            int c = Integer.parseInt( campoChave.getValue().trim() );
            if ( c < -9999 || c > 9999 ) {
                aviso = "Informe uma chave entre -9999 e 9999.";
                pausado = true;
                return;
            }
            if ( operacao.equals( "put" ) && comparacao.tamanho() >= 200 && !comparacao.contem( c ) ) {
                aviso = "Limite de 200 nós. Remova uma chave ou limpe as árvores.";
                pausado = true;
                return;
            }
            comparacao.executar( operacao, c );
            iniciarReproducao();
        } catch ( NumberFormatException exc ) {
            aviso = "Entrada inválida. Digite uma chave inteira entre -9999 e 9999.";
            pausado = true;
        }
    }

    private void inserirAleatoria() {
        List<Integer> livres = new ArrayList<>();
        for ( int c = 1; c <= 99; c++ ) {
            if ( !comparacao.contem( c ) ) {
                livres.add( c );
            }
        }
        if ( livres.isEmpty() ) {
            aviso = "Todas as chaves de 1 a 99 já estão nas árvores.";
            pausado = true;
        } else {
            campoChave.setValue( String.valueOf( livres.get( aleatorio.nextInt( livres.size() ) ) ) );
            executar( "put" );
        }
    }

    private void iniciarReproducao() {
        aviso = "A mesma operação será aplicada às três árvores.";
        pausado = false;
        mostrarPasso( 0 );
    }

    private void mostrarPasso( int indice ) {
        comparacao.mostrarPasso( indice );
        tempo = 0;
    }

    private double larguraPainel() {
        return ( getScreenWidth() - 56.0 ) / 3;
    }

    private void atualizarPosicoes() {
        double largura = larguraPainel();
        for ( int i = 0; i < comparacao.paineis.length; i++ ) {
            ABB arvore = comparacao.paineis[i].exibicao;
            double x = 16 + i * ( largura + 12 );
            double espHor = Math.min( 80, ( largura - 66 ) / Math.max( 1, arvore.tamanho() - 1 ) );
            double espVert = Math.min( 80, ( getScreenHeight() - 485.0 ) / Math.max( 1, arvore.altura() ) );
            int margem = (int) ( x + ( largura - ( arvore.tamanho() - 1 ) * espHor ) / 2 );
            arvore.atualizarNos( espHor, espVert, margem, 270 );
        }
    }

    private String abreviar( String texto, double largura ) {
        while ( measureText( texto, 12 ) > largura && texto.length() > 5 ) {
            texto = texto.substring( 0, texto.length() - 4 ) + "...";
        }
        return texto;
    }

    private void desenharMensagem( String mensagem, double x, double y, double largura ) {
        String linha = "";
        int numeroLinha = 0;
        String[] palavras = mensagem.split( " " );
        for ( int i = 0; i < palavras.length; i++ ) {
            String palavra = palavras[i];
            if ( measureText( linha + palavra, 12 ) > largura && !linha.isEmpty() ) {
                drawText( linha, x, y + numeroLinha * 17, 12, Cores.TEXTO );
                linha = "";
                numeroLinha++;
            }
            if ( numeroLinha == 2 ) {
                String restante = "";
                for ( int j = i; j < palavras.length; j++ ) {
                    restante += palavras[j] + " ";
                }
                drawText( abreviar( restante, largura ),
                        x, y + numeroLinha * 17, 12, Cores.TEXTO );
                return;
            }
            linha += palavra + " ";
        }
        drawText( linha, x, y + numeroLinha * 17, 12, Cores.TEXTO );
    }

    private void desenharPainel( int indice ) {
        Comparacao.Painel painel = comparacao.paineis[indice];
        double largura = larguraPainel();
        double x = 16 + indice * ( largura + 12 );
        drawRectangle( x, 198, largura, getScreenHeight() - 253, Cores.BORDA );
        fillRectangle( x + 1, 199, largura - 2, 35, Cores.PAINEL );
        drawText( painel.nome, x + 12, 208, 16, Cores.TEXTO );
        beginScissorMode( x + 1, 240, largura - 2, getScreenHeight() - 405 );
        painel.exibicao.desenhar( this );
        if ( painel.exibicao.tamanho() == 0 ) {
            drawText( "Árvore vazia", x + largura / 2 - 55, 360, Cores.TEXTO_SECUNDARIO );
        }
        endScissorMode();
        double y = getScreenHeight() - 153;
        drawText( "Nós: " + painel.exibicao.tamanho() + "  Altura: " + painel.exibicao.altura(),
                x + 12, y, 13, Cores.AZUL_CLARO );
        List<Integer> numeros = painel.exibicao.chavesEmOrdem();
        String ordem = "Crescente: ";
        if ( decrescente ) {
            Collections.reverse( numeros );
            ordem = "Decrescente: ";
        }
        drawText( abreviar( ordem + numeros, largura - 24 ), x + 12, y + 24, 12, Cores.TEXTO_SECUNDARIO );
        desenharMensagem( painel.mensagem, x + 12, y + 51, largura - 24 );
    }

    @Override
    public synchronized void draw() {
        clearBackground( Cores.FUNDO );
        fillRectangle( 0, 0, getScreenWidth(), 45, Cores.PAINEL );
        drawText( "Comparação: ABB, AVL e Vermelha e Preta", 24, 12, 22, Cores.TEXTO );
        Cores.desenhar( campoChave );
        for ( GuiButton botao : botoes ) {
            Cores.desenhar( botao );
        }
        Cores.desenhar( velocidade );
        Cores.desenhar( lblVelocidade );
        drawText( "h: altura | fb: esq. - dir.", 900, 125, 12, Cores.AZUL_CLARO );
        Color[] cores = { GOLD, SKYBLUE, GREEN, ORANGE, PURPLE, RED };
        String[] textos = { "Comparação", "Visitado", "Novo / achou", "Retorno", "Ajuste", "Remoção" };
        for ( int i = 0; i < cores.length; i++ ) {
            fillCircle( 30 + i * 150, 173, 6, cores[i] );
            drawText( textos[i], 42 + i * 150, 167, 12, Cores.TEXTO_SECUNDARIO );
        }
        for ( int i = 0; i < comparacao.paineis.length; i++ ) {
            desenharPainel( i );
        }
        drawText( "Passo " + ( comparacao.getPassoAtual() + 1 ) + " de " + comparacao.quantidadePassos(),
                24, getScreenHeight() - 33, 14, Cores.AZUL_CLARO );
        drawText( aviso, 240, getScreenHeight() - 33, 12, Cores.TEXTO_SECUNDARIO );
    }
}
