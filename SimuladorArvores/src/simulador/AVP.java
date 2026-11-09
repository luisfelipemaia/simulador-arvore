package simulador;

/**
 *
 * @author enzov
 */
public class AVP extends ABB {

    private boolean vermelho( No no ) {
        return no != null && no.vermelho;
    }

    @Override
    public void put( int c ) {
        iniciar( "Inserir a chave " + c + " na árvore vermelha e preta." );
        raiz = put( raiz, c );
        registrar( "Garantir que a raiz fique preta.", raiz, No.Destaque.AJUSTE );
        raiz.vermelho = false;
        finalizar( "Inserção concluída: raiz pintada de preto." );
    }

    @Override
    protected No put( No no, int c ) {
        if ( no == null ) {
            no = new No( 20 );
            no.c = c;
            no.vermelho = true;
            no.colorido = true;
            conectarNovo( no );
            registrar( "Inserir " + c + " como nó vermelho.", no, No.Destaque.ENCONTRADO );
            return no;
        }
        registrar( "Comparar " + c + " com " + no.c + ".", no, No.Destaque.ATUAL );
        if ( c < no.c ) {
            no.e = put( no.e, c );
        } else if ( c > no.c ) {
            no.d = put( no.d, c );
        } else {
            registrar( "Chave já existente: não inserir duplicata.", no, No.Destaque.ENCONTRADO );
        }
        registrar( "Retornar e conferir as cores em " + no.c + ".", no, No.Destaque.RETORNO );
        return balancear( no );
    }

    private No rotacionarEsquerda( No no ) {
        registrar( "Rotação à esquerda em " + no.c + ": ajustar ligação vermelha.", no, No.Destaque.AJUSTE );
        No novo = no.d;
        no.d = novo.e;
        novo.e = no;
        novo.vermelho = no.vermelho;
        no.vermelho = true;
        substituir( no, novo );
        registrar( "Rotação à esquerda concluída.", novo, No.Destaque.AJUSTE );
        return novo;
    }

    private No rotacionarDireita( No no ) {
        registrar( "Rotação à direita em " + no.c + ": reorganizar ligações vermelhas.", no, No.Destaque.AJUSTE );
        No novo = no.e;
        no.e = novo.d;
        novo.d = no;
        novo.vermelho = no.vermelho;
        no.vermelho = true;
        substituir( no, novo );
        registrar( "Rotação à direita concluída.", novo, No.Destaque.AJUSTE );
        return novo;
    }

    private void inverterCores( No no ) {
        registrar( "Inverter as cores de " + no.c + " e de seus filhos.", no, No.Destaque.AJUSTE );
        no.vermelho = !no.vermelho;
        if ( no.e != null ) {
            no.e.vermelho = !no.e.vermelho;
        }
        if ( no.d != null ) {
            no.d.vermelho = !no.d.vermelho;
        }
        registrar( "Inversão de cores concluída.", no, No.Destaque.AJUSTE );
    }

    private No balancear( No no ) {
        if ( vermelho( no.d ) && !vermelho( no.e ) ) {
            no = rotacionarEsquerda( no );
        }
        if ( vermelho( no.e ) && vermelho( no.e.e ) ) {
            no = rotacionarDireita( no );
        }
        if ( vermelho( no.e ) && vermelho( no.d ) ) {
            inverterCores( no );
        }
        return no;
    }

    private No moverVermelhoEsquerda( No no ) {
        inverterCores( no );
        if ( no.d != null && vermelho( no.d.e ) ) {
            no.d = rotacionarDireita( no.d );
            no = rotacionarEsquerda( no );
            inverterCores( no );
        }
        return no;
    }

    private No moverVermelhoDireita( No no ) {
        inverterCores( no );
        if ( no.e != null && vermelho( no.e.e ) ) {
            no = rotacionarDireita( no );
            inverterCores( no );
        }
        return no;
    }

    @Override
    public void delete( int c ) {
        if ( !contem( c ) ) {
            buscar( c );
            return;
        }
        iniciar( "Remover " + c + "; preparar as cores durante a descida." );
        if ( !vermelho( raiz.e ) && !vermelho( raiz.d ) ) {
            raiz.vermelho = true;
            registrar( "Pintar temporariamente a raiz de vermelho.", raiz, No.Destaque.AJUSTE );
        }
        raiz = delete( raiz, c );
        if ( raiz != null ) {
            raiz.vermelho = false;
        }
        finalizar( "Remoção concluída. Raiz preta e propriedades restauradas." );
    }

    @Override
    protected No delete( No no, int c ) {
        registrar( "Comparar " + c + " com " + no.c + ".", no, No.Destaque.ATUAL );
        if ( c < no.c ) {
            if ( !vermelho( no.e ) && !vermelho( no.e.e ) ) {
                no = moverVermelhoEsquerda( no );
            }
            no.e = delete( no.e, c );
        } else {
            if ( vermelho( no.e ) ) {
                no = rotacionarDireita( no );
            }
            if ( c == no.c && no.d == null ) {
                registrar( "Remover a folha " + c + ".", no, No.Destaque.REMOVER );
                substituir( no, null );
                registrar( "Folha removida.", null, No.Destaque.NENHUM );
                return null;
            }
            if ( !vermelho( no.d ) && !vermelho( no.d.e ) ) {
                no = moverVermelhoDireita( no );
            }
            if ( c == no.c ) {
                no.c = minimo( no.d ).c;
                registrar( "Copiar o sucessor " + no.c + " e remover a ocorrência original.", no, No.Destaque.REMOVER );
                no.d = deleteMinimo( no.d );
            } else {
                no.d = delete( no.d, c );
            }
        }
        registrar( "Retornar e restaurar as cores em " + no.c + ".", no, No.Destaque.RETORNO );
        return balancear( no );
    }

    private No deleteMinimo( No no ) {
        if ( no.e == null ) {
            registrar( "Remover o sucessor " + no.c + ".", no, No.Destaque.REMOVER );
            substituir( no, null );
            registrar( "Sucessor removido.", null, No.Destaque.NENHUM );
            return null;
        }
        if ( !vermelho( no.e ) && !vermelho( no.e.e ) ) {
            no = moverVermelhoEsquerda( no );
        }
        no.e = deleteMinimo( no.e );
        return balancear( no );
    }
}
