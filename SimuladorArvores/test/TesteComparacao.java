package simulador;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

// Confere as operações e os passos das três árvores
public class TesteComparacao {

    private static void conferir( boolean condicao, String mensagem ) {
        if ( !condicao ) {
            throw new AssertionError( mensagem );
        }
    }

    private static void verificarResultado( Comparacao comparacao, TreeSet<Integer> esperado ) {
        comparacao.mostrarPasso( comparacao.quantidadePassos() - 1 );
        for ( Comparacao.Painel painel : comparacao.paineis ) {
            conferir( painel.passos.size() == comparacao.quantidadePassos(), "Reproduções com tamanhos diferentes" );
            conferir( painel.arvore.chavesEmOrdem().equals( new ArrayList<>( esperado ) ), "Operação diferente em uma árvore" );
            conferir( painel.exibicao.chavesEmOrdem().equals( new ArrayList<>( esperado ) ), "Último passo incorreto" );
        }
        comparacao.mostrarPasso( -1 );
        conferir( comparacao.getPassoAtual() == 0, "Limite do primeiro passo" );
        for ( int i = 0; i < comparacao.quantidadePassos(); i++ ) {
            comparacao.mostrarPasso( i );
            for ( Comparacao.Painel painel : comparacao.paineis ) {
                conferir( painel.mensagem != null && painel.exibicao != null, "Painel sem passo" );
            }
        }
        comparacao.mostrarPasso( Integer.MAX_VALUE );
        conferir( comparacao.getPassoAtual() == comparacao.quantidadePassos() - 1, "Limite do último passo" );
    }

    public static void main( String[] args ) {
        Comparacao comparacao = new Comparacao();
        TreeSet<Integer> esperado = new TreeSet<>();
        comparacao.carregar( new int[]{ 1, 2, 3, 4, 5, 6, 7 } );
        esperado.addAll( List.of( 1, 2, 3, 4, 5, 6, 7 ) );
        verificarResultado( comparacao, esperado );
        conferir( comparacao.paineis[0].arvore.altura() == 6, "ABB deve mostrar a sequência inclinada" );
        conferir( comparacao.paineis[1].arvore.altura() < 6, "AVL deve permanecer balanceada" );
        conferir( comparacao.paineis[2].arvore.altura() < 6, "AVP deve permanecer balanceada" );
        Random random = new Random( 94 );
        for ( int i = 0; i < 120; i++ ) {
            int c = random.nextInt( 25 ) - 12;
            int operacao = random.nextInt( 3 );
            if ( operacao == 0 ) {
                comparacao.executar( "put", c );
                esperado.add( c );
            } else if ( operacao == 1 ) {
                comparacao.executar( "delete", c );
                esperado.remove( c );
            } else {
                comparacao.executar( "buscar", c );
            }
            verificarResultado( comparacao, esperado );
        }
        comparacao.executar( "limpar", 0 );
        esperado.clear();
        verificarResultado( comparacao, esperado );
        comparacao.carregar( new int[0] );
        verificarResultado( comparacao, esperado );
        System.out.println( "Comparação: operações, passos compartilhados e resultados das três árvores OK." );
    }
}
