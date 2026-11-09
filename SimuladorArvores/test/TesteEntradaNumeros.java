package simulador;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

// Confere o sorteio e a leitura dos números usados no menu
public class TesteEntradaNumeros {

    private static void conferir( boolean condicao, String mensagem ) {
        if ( !condicao ) {
            throw new AssertionError( mensagem );
        }
    }

    public static void main( String[] args ) {
        for ( int quantidade : new int[]{ 1, 10, 35, 99, 200 } ) {
            int[] numeros = EntradaNumeros.sortear( quantidade, new Random( 42 ) );
            conferir( numeros.length == quantidade, "Quantidade sorteada incorreta" );
            Set<Integer> usados = new HashSet<>();
            for ( int c : numeros ) {
                conferir( c >= 1 && c <= 9999, "Número fora do intervalo" );
                conferir( usados.add( c ), "Número repetido no sorteio" );
            }
            int[] leitura = EntradaNumeros.ler( EntradaNumeros.formatar( numeros ) );
            conferir( Arrays.equals( numeros, leitura ), "A sequência perdeu números ou mudou a ordem" );
        }
        for ( int quantidade : new int[]{ -1, 0, 201 } ) {
            boolean rejeitou = false;
            try {
                EntradaNumeros.sortear( quantidade, new Random( 1 ) );
            } catch ( IllegalArgumentException exc ) {
                rejeitou = true;
            }
            conferir( rejeitou, "Quantidade inválida foi aceita" );
        }
        conferir( Arrays.equals( EntradaNumeros.ler( "-9999, 0; 9999 5" ),
                new int[]{ -9999, 0, 9999, 5 } ), "Leitura de separadores e números negativos" );
        for ( String texto : new String[]{ "", ",;", "abc", "1 2.5", "10000", "-10000" } ) {
            boolean rejeitou = false;
            try {
                EntradaNumeros.ler( texto );
            } catch ( IllegalArgumentException exc ) {
                rejeitou = true;
            }
            conferir( rejeitou, "Sequência inválida foi aceita" );
        }
        System.out.println( "Menu: sorteio de 1 a 200 números, sem repetição, leitura e validação OK" );
    }
}
