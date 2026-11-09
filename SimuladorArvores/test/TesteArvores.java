package simulador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

// Testes das árvores sem JUnit
public class TesteArvores {

    private static void conferir( boolean condicao, String mensagem ) {
        if ( !condicao ) {
            throw new AssertionError( mensagem );
        }
    }

    private static int verificar( No no, long minimo, long maximo, boolean avl, boolean avp, Set<No> vistos ) {
        if ( no == null ) {
            if ( avp ) {
                return 1;
            } else {
                return -1;
            }
        }
        conferir( vistos.add( no ), "Ciclo ou nó compartilhado" );
        conferir( no.c > minimo && no.c < maximo, "Ordem da ABB" );
        int e = verificar( no.e, minimo, no.c, avl, avp, vistos );
        int d = verificar( no.d, no.c, maximo, avl, avp, vistos );
        if ( avl ) {
            conferir( Math.abs( e - d ) <= 1, "Balanceamento AVL em " + no.c );
            conferir( no.altura == 1 + Math.max( e, d ), "Altura armazenada" );
        }
        if ( avp ) {
            conferir( no.d == null || !no.d.vermelho, "Ligação vermelha à direita" );
            conferir( !no.vermelho || ( ( no.e == null || !no.e.vermelho )
                    && ( no.d == null || !no.d.vermelho ) ), "Vermelhos consecutivos" );
            conferir( e == d, "Altura preta diferente" );
            if ( no.vermelho ) {
                return e;
            } else {
                return e + 1;
            }
        }
        return 1 + Math.max( e, d );
    }

    private static void validar( ABB arvore, TreeSet<Integer> esperado ) {
        conferir( arvore.chavesEmOrdem().equals( new ArrayList<>( esperado ) ), "Chaves diferentes do TreeSet" );
        boolean avp = arvore instanceof AVP;
        conferir( !avp || arvore.raiz == null || !arvore.raiz.vermelho, "Raiz vermelha" );
        verificar( arvore.raiz, Long.MIN_VALUE, Long.MAX_VALUE, arvore instanceof AVL, avp,
                Collections.newSetFromMap( new IdentityHashMap<>() ) );
    }

    private static String estrutura( No no, Set<No> vistos ) {
        if ( no == null ) {
            return ".";
        }
        conferir( vistos.add( no ), "Ciclo na cópia de um passo" );
        return no.c + ":" + no.vermelho + ":" + no.altura + ":" + no.destaque
                + "(" + estrutura( no.e, vistos ) + "," + estrutura( no.d, vistos ) + ")";
    }

    private static String estrutura( ABB arvore ) {
        return estrutura( arvore.raiz, Collections.newSetFromMap( new IdentityHashMap<>() ) );
    }

    private static void testar( ABB arvore ) {
        arvore.setGravar( false );
        TreeSet<Integer> esperado = new TreeSet<>();
        Random random = new Random( 20261109 );
        for ( int i = 0; i < 12000; i++ ) {
            int c = random.nextInt( 401 ) - 200;
            if ( random.nextBoolean() ) {
                arvore.put( c );
                esperado.add( c );
            } else {
                arvore.delete( c );
                esperado.remove( c );
            }
            validar( arvore, esperado );
            conferir( arvore.contem( c ) == esperado.contains( c ), "Busca incorreta" );
        }
        for ( int c : new ArrayList<>( esperado ) ) {
            arvore.delete( c );
            esperado.remove( c );
            validar( arvore, esperado );
        }
        for ( int c : new int[]{ Integer.MIN_VALUE, 0, Integer.MAX_VALUE } ) {
            arvore.put( c );
            esperado.add( c );
        }
        validar( arvore, esperado );
        arvore.limpar();
        esperado.clear();

        // Confere também os passos das rotações e remoções
        arvore.setGravar( true );
        random = new Random( 73 );
        List<ABB.Passo> anteriores = null;
        String fotoAnterior = null;
        for ( int i = 0; i < 500; i++ ) {
            int c = random.nextInt( 40 );
            if ( random.nextBoolean() ) {
                arvore.put( c );
                esperado.add( c );
            } else {
                arvore.delete( c );
                esperado.remove( c );
            }
            validar( arvore, esperado );
            List<ABB.Passo> passos = arvore.getPassos();
            conferir( !passos.isEmpty(), "Operação sem passos" );
            for ( ABB.Passo passo : passos ) {
                ABB foto = arvore.exibir( passo );
                estrutura( foto );
                List<Integer> chaves = foto.chavesEmOrdem();
                for ( int j = 1; j < chaves.size(); j++ ) {
                    conferir( chaves.get( j - 1 ) <= chaves.get( j ), "Foto fora de ordem" );
                }
                // A cópia do sucessor pode repetir uma chave por um passo, mas não pode perder os filhos
                for ( int chave : esperado ) {
                    if ( chave != c ) {
                        conferir( foto.contem( chave ), "Foto perdeu a chave " + chave );
                    }
                }
            }
            ABB ultima = arvore.exibir( passos.get( passos.size() - 1 ) );
            conferir( ultima.chavesEmOrdem().equals( new ArrayList<>( esperado ) ), "Última foto incorreta" );
            if ( anteriores != null ) {
                conferir( fotoAnterior.equals( estrutura( arvore.exibir( anteriores.get( 0 ) ) ) ), "Foto antiga foi alterada" );
            }
            anteriores = passos;
            fotoAnterior = estrutura( arvore.exibir( passos.get( 0 ) ) );
        }
        conferir( arvore.buscar( 999 ) == esperado.contains( 999 ), "Busca ausente" );
        arvore.limpar();
        conferir( arvore.altura() == -1 && arvore.tamanho() == 0, "Limpeza" );
        System.out.println( arvore.getClass().getSimpleName() + ": OK (12.000 operações + 500 operações com gravação)" );
    }

    public static void main( String[] args ) {
        testar( new ABB() );
        testar( new AVL() );
        testar( new AVP() );
    }
}
