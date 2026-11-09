package simulador;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author enzov
 */
public class EntradaNumeros {

    public static int[] sortear( int quantidade, Random aleatorio ) {
        if ( quantidade < 1 || quantidade > 200 ) {
            throw new IllegalArgumentException( "A quantidade deve estar entre 1 e 200." );
        }
        int[] numeros = new int[quantidade];
        Set<Integer> usados = new HashSet<>();
        int indice = 0;
        while ( indice < quantidade ) {
            int numero = aleatorio.nextInt( 9999 ) + 1;
            if ( usados.add( numero ) ) {
                numeros[indice] = numero;
                indice++;
            }
        }
        return numeros;
    }

    public static String formatar( int[] numeros ) {
        StringBuilder texto = new StringBuilder();
        for ( int i = 0; i < numeros.length; i++ ) {
            if ( i > 0 ) {
                texto.append( ", " );
            }
            texto.append( numeros[i] );
        }
        return texto.toString();
    }

    public static int[] ler( String texto ) {
        if ( texto == null || texto.trim().isEmpty() ) {
            throw new IllegalArgumentException( "Digite os números antes de iniciar." );
        }
        String[] partes = texto.trim().split( "[\\s,;]+" );
        if ( partes.length == 0 ) {
            throw new IllegalArgumentException( "Digite pelo menos um número." );
        }
        if ( partes.length > 200 ) {
            throw new IllegalArgumentException( "Use no máximo 200 números por sequência." );
        }
        int[] numeros = new int[partes.length];
        for ( int i = 0; i < partes.length; i++ ) {
            try {
                numeros[i] = Integer.parseInt( partes[i] );
            } catch ( NumberFormatException exc ) {
                throw new IllegalArgumentException( "Valor inválido: " + partes[i] + ". Use números inteiros." );
            }
            if ( numeros[i] < -9999 || numeros[i] > 9999 ) {
                throw new IllegalArgumentException( "As chaves devem estar entre -9999 e 9999." );
            }
        }
        return numeros;
    }
}
