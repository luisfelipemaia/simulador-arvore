package simulador;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author luism
 */
public class ABB {

    protected No raiz;
    private int ranqueAtual;
    private final List<No> nosDesenho = new ArrayList<>();
    private final List<Passo> passos = new ArrayList<>();
    private boolean gravar = true;

    // Guarda uma cópia de cada passo para poder voltar depois
    public static class Passo {
        private final No raiz;
        public final String mensagem;

        private Passo( No raiz, String mensagem ) {
            this.raiz = raiz;
            this.mensagem = mensagem;
        }
    }

    public void setGravar( boolean gravar ) {
        this.gravar = gravar;
    }

    protected void iniciar( String mensagem ) {
        passos.clear();
        limparDestaques( raiz );
        registrar( mensagem, null, No.Destaque.NENHUM );
    }

    protected void registrar( String mensagem, No no, No.Destaque destaque ) {
        if ( gravar ) {
            if ( no != null ) {
                no.destaque = destaque;
            }
            passos.add( new Passo( copiar( raiz ), mensagem ) );
            if ( no != null ) {
                if ( destaque == No.Destaque.ENCONTRADO ) {
                    no.destaque = No.Destaque.ENCONTRADO;
                } else {
                    no.destaque = No.Destaque.VISITADO;
                }
            }
        }
    }

    protected void finalizar( String mensagem ) {
        limparDestaques( raiz );
        registrar( mensagem, null, No.Destaque.NENHUM );
    }

    private void limparDestaques( No no ) {
        if ( no != null ) {
            no.destaque = No.Destaque.NENHUM;
            limparDestaques( no.e );
            limparDestaques( no.d );
        }
    }

    private No copiar( No no ) {
        if ( no == null ) {
            return null;
        }
        No copia = new No( no.raio );
        copia.c = no.c;
        copia.altura = no.altura;
        copia.vermelho = no.vermelho;
        copia.colorido = no.colorido;
        copia.mostrarBalanceamento = no.mostrarBalanceamento;
        copia.destaque = no.destaque;
        copia.e = copiar( no.e );
        copia.d = copiar( no.d );
        return copia;
    }

    public List<Passo> getPassos() {
        return new ArrayList<>( passos );
    }

    // Mostra a cópia sem mexer na árvore que recebe as operações
    public ABB exibir( Passo passo ) {
        ABB exibicao = new ABB();
        exibicao.raiz = copiar( passo.raiz );
        return exibicao;
    }

    public void put( int c ) {
        iniciar( "Inserir a chave " + c + "." );
        raiz = put( raiz, c );
        finalizar( "Inserção concluída." );
    }

    protected No put( No no, int c ) {
        if ( no == null ) {
            no = criarNo( c );
            conectarNovo( no );
            registrar( "Posição encontrada: inserir " + c + ".", no, No.Destaque.ENCONTRADO );
            return no;
        }
        registrar( "Comparar " + c + " com " + no.c + ".", no, No.Destaque.ATUAL );
        if ( c < no.c ) {
            registrar( c + " é menor: seguir à esquerda.", no, No.Destaque.ATUAL );
            no.e = put( no.e, c );
        } else if ( c > no.c ) {
            registrar( c + " é maior: seguir à direita.", no, No.Destaque.ATUAL );
            no.d = put( no.d, c );
        } else {
            registrar( "A chave já existe. Não inserir duplicata.", no, No.Destaque.ENCONTRADO );
        }
        registrar( "Retornar ao nó " + no.c + ".", no, No.Destaque.RETORNO );
        return no;
    }

    protected No criarNo( int c ) {
        No no = new No( 23 );
        no.c = c;
        return no;
    }

    // Liga o novo nó antes de guardar o passo
    protected void conectarNovo( No novo ) {
        if ( raiz == null ) {
            raiz = novo;
            return;
        }
        No no = raiz;
        while ( true ) {
            if ( novo.c < no.c ) {
                if ( no.e == null ) {
                    no.e = novo;
                    return;
                }
                no = no.e;
            } else {
                if ( no.d == null ) {
                    no.d = novo;
                    return;
                }
                no = no.d;
            }
        }
    }

    // Atualiza o pai para a rotação aparecer corretamente no passo
    protected void substituir( No antigo, No novo ) {
        if ( raiz == antigo ) {
            raiz = novo;
        } else {
            substituir( raiz, antigo, novo );
        }
    }

    private boolean substituir( No pai, No antigo, No novo ) {
        if ( pai == null ) {
            return false;
        }
        if ( pai.e == antigo ) {
            pai.e = novo;
            return true;
        }
        if ( pai.d == antigo ) {
            pai.d = novo;
            return true;
        }
        return substituir( pai.e, antigo, novo ) || substituir( pai.d, antigo, novo );
    }

    public void delete( int c ) {
        iniciar( "Remover a chave " + c + "." );
        raiz = delete( raiz, c );
        finalizar( "Remoção concluída." );
    }

    public void remove( int c ) {
        delete( c );
    }

    protected No delete( No no, int c ) {
        if ( no == null ) {
            registrar( "Chegou a null: chave não encontrada.", null, No.Destaque.NENHUM );
            return null;
        }
        registrar( "Comparar " + c + " com " + no.c + ".", no, No.Destaque.ATUAL );
        if ( c < no.c ) {
            no.e = delete( no.e, c );
        } else if ( c > no.c ) {
            no.d = delete( no.d, c );
        } else {
            registrar( "Chave encontrada. Remover o nó " + c + ".", no, No.Destaque.REMOVER );
            if ( no.e == null || no.d == null ) {
                No filho;
                if ( no.e == null ) {
                    filho = no.d;
                } else {
                    filho = no.e;
                }
                substituir( no, filho );
                registrar( "Substituir pelo filho disponível (ou null para uma folha).", filho, No.Destaque.RETORNO );
                return filho;
            }
            No sucessor = minimo( no.d );
            registrar( "Dois filhos: usar o sucessor " + sucessor.c + ".", sucessor, No.Destaque.ENCONTRADO );
            no.c = sucessor.c;
            registrar( "Copiar a chave do sucessor e remover sua ocorrência original.", no, No.Destaque.REMOVER );
            no.d = delete( no.d, sucessor.c );
        }
        registrar( "Retornar ao nó " + no.c + ".", no, No.Destaque.RETORNO );
        return no;
    }

    protected No minimo( No no ) {
        while ( no.e != null ) {
            registrar( "Procurar o menor: seguir à esquerda de " + no.c + ".", no, No.Destaque.ATUAL );
            no = no.e;
        }
        return no;
    }

    public boolean buscar( int c ) {
        iniciar( "Buscar a chave " + c + "." );
        boolean achou = buscar( raiz, c );
        if ( achou ) {
            registrar( "Busca concluída: chave encontrada.", null, No.Destaque.NENHUM );
        } else {
            registrar( "Busca concluída: chave não encontrada.", null, No.Destaque.NENHUM );
        }
        return achou;
    }

    private boolean buscar( No no, int c ) {
        if ( no == null ) {
            registrar( "Chegou a null: chave não encontrada.", null, No.Destaque.NENHUM );
            return false;
        }
        registrar( "Comparar " + c + " com " + no.c + ".", no, No.Destaque.ATUAL );
        if ( c == no.c ) {
            registrar( "Encontrou a chave " + c + ".", no, No.Destaque.ENCONTRADO );
            return true;
        }
        boolean achou;
        if ( c < no.c ) {
            achou = buscar( no.e, c );
        } else {
            achou = buscar( no.d, c );
        }
        registrar( "Retornar pela busca ao nó " + no.c + ".", no, No.Destaque.RETORNO );
        return achou;
    }

    public boolean contem( int c ) {
        No no = raiz;
        while ( no != null ) {
            if ( c == no.c ) {
                return true;
            }
            if ( c < no.c ) {
                no = no.e;
            } else {
                no = no.d;
            }
        }
        return false;
    }

    public void limpar() {
        iniciar( "Limpar a árvore." );
        raiz = null;
        finalizar( "Árvore vazia." );
        nosDesenho.clear();
    }

    public List<Integer> chavesEmOrdem() {
        List<Integer> chaves = new ArrayList<>();
        chavesEmOrdem( raiz, chaves );
        return chaves;
    }

    private void chavesEmOrdem( No no, List<Integer> chaves ) {
        if ( no != null ) {
            chavesEmOrdem( no.e, chaves );
            chaves.add( no.c );
            chavesEmOrdem( no.d, chaves );
        }
    }

    public void emOrdem() {
        System.out.println( chavesEmOrdem() );
    }

    public int tamanho() {
        return chavesEmOrdem().size();
    }

    public int altura() {
        return alturaReal( raiz );
    }

    private int alturaReal( No no ) {
        if ( no == null ) {
            return -1;
        } else {
            return 1 + Math.max( alturaReal( no.e ), alturaReal( no.d ) );
        }
    }

    public void atualizarNos( double espHor, double espVert, int margemHor, int margemVert ) {
        ranqueAtual = 0;
        nosDesenho.clear();
        atualizarNos( raiz, 0, espHor, espVert, margemHor, margemVert );
    }

    private void atualizarNos( No no, int nivel, double espHor, double espVert, int margemHor, int margemVert ) {
        if ( no != null ) {
            atualizarNos( no.e, nivel + 1, espHor, espVert, margemHor, margemVert );
            no.nivel = nivel;
            no.ranque = ranqueAtual++;
            no.pos.x = margemHor + espHor * no.ranque;
            no.pos.y = margemVert + espVert * no.nivel;
            no.raio = (int) Math.max( 8, Math.min( 23, Math.min( espHor * 0.40, espVert * 0.27 ) ) );
            nosDesenho.add( no );
            atualizarNos( no.d, nivel + 1, espHor, espVert, margemHor, margemVert );
        }
    }

    public void desenhar( EngineFrame e ) {
        for ( No no : nosDesenho ) {
            if ( no.e != null ) {
                e.drawLine( no.pos, no.e.pos, Cores.TEXTO_SECUNDARIO );
            }
            if ( no.d != null ) {
                e.drawLine( no.pos, no.d.pos, Cores.TEXTO_SECUNDARIO );
            }
        }
        for ( No no : nosDesenho ) {
            no.desenhar( e );
        }
    }
}
