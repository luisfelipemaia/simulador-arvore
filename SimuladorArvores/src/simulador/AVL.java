package simulador;
/**
 *
 * @author enzov
 */
public class AVL extends ABB {

    @Override
    protected No criarNo( int c ) {
        No no = super.criarNo( c );
        no.mostrarBalanceamento = true;
        return no;
    }

    private int altura( No no ) {
        if ( no == null ) {
            return -1;
        } else {
            return no.altura;
        }
    }

    private int fator( No no ) {
        return altura( no.e ) - altura( no.d );
    }

    private void atualizarAltura( No no ) {
        no.altura = 1 + Math.max( altura( no.e ), altura( no.d ) );
        no.mostrarBalanceamento = true;
    }

    private No rotacionarDireita( No no ) {
        registrar( "Rotação à direita em " + no.c + ".", no, No.Destaque.AJUSTE );
        No novo = no.e;
        no.e = novo.d;
        novo.d = no;
        atualizarAltura( no );
        atualizarAltura( novo );
        substituir( no, novo );
        registrar( "Rotação concluída: " + novo.c + " assume a subárvore.", novo, No.Destaque.AJUSTE );
        return novo;
    }

    private No rotacionarEsquerda( No no ) {
        registrar( "Rotação à esquerda em " + no.c + ".", no, No.Destaque.AJUSTE );
        No novo = no.d;
        no.d = novo.e;
        novo.e = no;
        atualizarAltura( no );
        atualizarAltura( novo );
        substituir( no, novo );
        registrar( "Rotação concluída: " + novo.c + " assume a subárvore.", novo, No.Destaque.AJUSTE );
        return novo;
    }

    private No balancear( No no ) {
        if ( no == null ) {
            return null;
        }
        atualizarAltura( no );
        registrar( "Conferir o balanceamento de " + no.c + ": FB=" + fator( no ) + ".", no, No.Destaque.RETORNO );
        if ( fator( no ) > 1 ) {
            if ( fator( no.e ) < 0 ) {
                no.e = rotacionarEsquerda( no.e );
            }
            return rotacionarDireita( no );
        }
        if ( fator( no ) < -1 ) {
            if ( fator( no.d ) > 0 ) {
                no.d = rotacionarDireita( no.d );
            }
            return rotacionarEsquerda( no );
        }
        return no;
    }

    @Override
    protected No put( No no, int c ) {
        return balancear( super.put( no, c ) );
    }

    @Override
    protected No delete( No no, int c ) {
        return balancear( super.delete( no, c ) );
    }
}
