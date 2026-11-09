package simulador;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author enzov
 */
// Aplica a mesma operação nas três árvores e junta os passos
public class Comparacao {

    static class Painel {
        final String nome;
        final ABB arvore;
        final List<ABB.Passo> passos = new ArrayList<>();
        ABB exibicao;
        String mensagem;

        Painel( String nome, ABB arvore ) {
            this.nome = nome;
            this.arvore = arvore;
        }
    }

    final Painel[] paineis;
    private int passoAtual;

    public Comparacao() {
        paineis = new Painel[]{
            new Painel( "ABB - Binária de Busca", new ABB() ),
            new Painel( "AVL", new AVL() ),
            new Painel( "Vermelha e Preta", new AVP() )
        };
        carregar( new int[0] );
    }

    public void carregar( int[] numeros ) {
        for ( Painel painel : paineis ) {
            painel.arvore.setGravar( false );
            painel.arvore.limpar();
            painel.arvore.setGravar( true );
            painel.passos.clear();
        }
        for ( int c : numeros ) {
            adicionarOperacao( "put", c );
        }
        if ( numeros.length == 0 ) {
            adicionarOperacao( "limpar", 0 );
        }
        mostrarPasso( 0 );
    }

    public void executar( String operacao, int c ) {
        for ( Painel painel : paineis ) {
            painel.passos.clear();
        }
        adicionarOperacao( operacao, c );
        mostrarPasso( 0 );
    }

    private void adicionarOperacao( String operacao, int c ) {
        List<List<ABB.Passo>> gravacoes = new ArrayList<>();
        int maior = 0;
        for ( Painel painel : paineis ) {
            if ( operacao.equals( "put" ) ) {
                painel.arvore.put( c );
            } else if ( operacao.equals( "delete" ) ) {
                painel.arvore.delete( c );
            } else if ( operacao.equals( "buscar" ) ) {
                painel.arvore.buscar( c );
            } else {
                painel.arvore.limpar();
            }
            List<ABB.Passo> passos = painel.arvore.getPassos();
            gravacoes.add( passos );
            maior = Math.max( maior, passos.size() );
        }
        // Espera as três terminarem antes de inserir o próximo número
        for ( int i = 0; i < paineis.length; i++ ) {
            List<ABB.Passo> passos = gravacoes.get( i );
            for ( int j = 0; j < maior; j++ ) {
                int indice = Math.min( j, passos.size() - 1 );
                paineis[i].passos.add( passos.get( indice ) );
            }
        }
    }

    public void mostrarPasso( int indice ) {
        passoAtual = Math.max( 0, Math.min( quantidadePassos() - 1, indice ) );
        for ( Painel painel : paineis ) {
            ABB.Passo passo = painel.passos.get( passoAtual );
            painel.exibicao = painel.arvore.exibir( passo );
            painel.mensagem = passo.mensagem;
        }
    }

    public int getPassoAtual() {
        return passoAtual;
    }

    public int quantidadePassos() {
        return paineis[0].passos.size();
    }

    public boolean contem( int c ) {
        return paineis[0].arvore.contem( c );
    }

    public int tamanho() {
        return paineis[0].arvore.tamanho();
    }
}
