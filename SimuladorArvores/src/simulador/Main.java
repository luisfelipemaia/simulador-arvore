package simulador;

/**
 *
 * @author enzov
 */
public class Main {

    public static void main( String[] args ) {
        if ( args.length > 0 ) {
            String tipo = args[0].toUpperCase( java.util.Locale.ROOT );
            if ( !java.util.List.of( "ABB", "AVL", "AVP" ).contains( tipo ) ) {
                throw new IllegalArgumentException( "Use ABB, AVL ou AVP." );
            }
            new Simulador( tipo );
        } else {
            new Menu();
        }
    }
}
