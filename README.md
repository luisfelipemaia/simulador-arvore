# Simulador de Árvores

Projeto Java 17 ou superior com JSGE 1.9.1, continuando a base disponibilizada pelo professor.
Abre um menu para escolher os números, o tipo de árvore e a ordem da saída.
Você pode abrir ABB, AVL, Vermelha e Preta (AVP, variante LLRB) ou comparar as três.
Todos os controles da interface usam a GUI da JSGE. O desenho também é feito pela engine.

## Executar

É necessário ter um **JDK 17 ou superior** instalado e os comandos `java` e `javac` no PATH.

- **Windows:** abra `iniciar.cmd`. O JAR já está incluído em `dist`, junto das bibliotecas.
- **Apache NetBeans:** use Arquivo > Abrir Projeto, selecione esta pasta e execute
  `simulador.Main` (F6). O menu aparece primeiro. A compilação foi verificada com o
  Ant incluído no Apache NetBeans IDE 25.
- **Compilar novamente:** execute `compilar.cmd`.
- **Compilar e testar:** execute `testar.cmd`.
- **Uma janela apenas:** no terminal, use `iniciar.cmd ABB`, `iniciar.cmd AVL` ou `iniciar.cmd AVP`.

Não é necessário baixar bibliotecas. Preserve a pasta `lib` ao mover o projeto e a pasta
`dist/lib` junto do JAR. A JSGE utilizada é a que acompanha o projeto do professor.

## Usar o simulador

No menu inicial:

1. Digite os números separados por vírgulas, ponto e vírgula ou espaços.
   Para gerar uma sequência, informe a **Quantidade** e clique em **Sortear números**.
   O padrão é 10, com limite de 1 a 200 números únicos, sorteados de 1 a 9999.
   O sorteio preenche o campo e mantém os números na ordem sorteada, sem ordenar antes.
   **Ver sequência** abre uma lista com rolagem da JSGE para consultar todos os valores.
2. Escolha Binária de busca, AVL, Vermelha e Preta ou Comparar as três.
3. Escolha a saída Crescente ou Decrescente e clique em Iniciar simulação.

Passar o mouse não muda as cores dos controles. O destaque aparece ao clicar ou
selecionar, inclusive nas linhas da sequência, nos campos e no controle de velocidade.

Para editar a quantidade, clique no campo e digite o novo valor: o primeiro clique
seleciona o conteúdo inteiro. Backspace ou Delete apagam a seleção; Ctrl+A seleciona
tudo novamente. As setas, Home e End permitem editar pelo cursor. Enter no campo
Quantidade sorteia os números; nos outros campos do menu, inicia a simulação.
Ctrl+V permite colar texto numérico. Sequências longas acompanham o cursor dentro
do campo, sem limitar a edição ao trecho visível.

A árvore recebe os números na sequência digitada, e a montagem é reproduzida passo
a passo. A opção crescente/decrescente define o percurso usado para mostrar a saída;
as regras de cada árvore permanecem as mesmas. Duplicatas aparecem uma vez na saída.
O menu permanece aberto para iniciar outras simulações. Carregar exemplo restaura
20, 10, 15, 5 e 30, e Limpar números esvazia o campo. O limite é de 200 números.

Em **Comparar as três**, uma única janela mostra ABB, AVL e Vermelha e Preta lado a
lado. Os botões e o campo de chave aplicam a mesma operação às três árvores.
Anterior, Próximo, Pausar/Continuar, Pular e velocidade controlam os três painéis.
Cada painel tem sua própria explicação, altura e sequência ordenada. A reprodução
aguarda as três árvores terminarem uma inserção antes de começar o próximo número.
As opções individuais continuam abrindo somente a árvore escolhida.

Dentro de cada simulador:

1. Clique no campo, digite uma chave inteira de **-9999 a 9999** e escolha Inserir, Remover ou Buscar.
2. Enter também insere. Duplicatas são ignoradas e entradas inválidas exibem uma mensagem.
3. Acompanhe os passos e as explicações na parte inferior.
4. Use Anterior e Próximo para navegar com a reprodução pausada, Pausar/Continuar para
   controlar a reprodução e Pular para mostrar o resultado final.
5. O controle de velocidade varia entre 0,5 e 6 passos por segundo.
6. Aleatória insere uma chave disponível entre 1 e 99. Limpar esvazia apenas a janela atual.
7. **Voltar ao menu**, no canto superior direito, fecha a janela da simulação atual
   e traz o menu para frente, preservando os números escolhidos no menu. Se o menu foi fechado,
   o botão abre um novo. A opção existe tanto na comparação quanto nos modos individuais.

No último passo, Continuar reproduz a operação desde o início. Uma nova operação usa o
resultado final da anterior, mesmo quando sua reprodução está pausada no meio.
Fechar uma janela preserva as outras; o programa encerra quando a última janela é fechada.

As cores dos halos indicam comparação (amarelo), caminho visitado (azul), inserção ou
resultado da busca (verde), retorno da recursão (laranja), rotação/inversão de cores (roxo)
e remoção (vermelho). Na AVP, o preenchimento vermelho ou preto indica a cor do próprio nó.
Nas três árvores, cada nó mostra a altura `h` e o fator `fb = altura(esquerda) - altura(direita)`.
Esses valores são calculados pela estrutura do passo exibido. Na ABB e na AVP, o fator
é informativo; a regra de manter `fb` entre -1 e 1 pertence à AVL.
A altura da árvore vazia é -1 e a de uma folha é 0.

## Organização do código

- `No.java`: chave, filhos, altura, cor, destaque e desenho do nó.
- `ABB.java`: inserção, remoção, busca, percurso em ordem, distribuição por ranque e cópias dos passos.
- `AVL.java`: reaproveita a ABB e acrescenta as rotações e a atualização das alturas.
- `AVP.java`: inserção e remoção LLRB, com rotações e inversões de cores.
- `Simulador.java`: entrada, botões, velocidade, reprodução e desenho da janela.
- `Comparador.java`: janela com as três árvores lado a lado e controles compartilhados.
- `Comparacao.java`: operações das três árvores e sincronização dos passos por número.
- `Menu.java`: escolha da sequência, da árvore e da ordem da saída.
- `EntradaNumeros.java`: validação e leitura da sequência digitada.
- `Janelas.java`: controle do encerramento quando todas as janelas são fechadas.
- `Cores.java`: paleta escura e cores dos componentes da JSGE.
- `ListaSimples.java`: lista da JSGE com destaque de seleção, sem mudar a cor ao passar o mouse.
- `CampoNumerico.java`: extensão do campo da JSGE com seleção, teclado e edição de sequências longas.
- `Main.java`: abertura do menu inicial.
- `test/TesteArvores.java`: testes das estruturas e das cópias dos passos, sem bibliotecas adicionais.
- `test/TesteComparacao.java`: testes das operações compartilhadas e da navegação dos três painéis.
- `test/TesteEntradaNumeros.java`: testes do sorteio, quantidades, números únicos e leitura da sequência.

Os métodos recursivos mantêm a estrutura do código da aula. Antes de gravar uma inserção,
remoção ou rotação, a ligação com o pai é atualizada. Assim a cópia inclui a subárvore
correta mesmo antes da volta da recursão. Os passos guardam cópias separadas e não alteram
a árvore real quando o usuário volta na reprodução.
As decisões no código usam `if` e `else`, sem operadores ternários ou `switch`.
Os comentários foram mantidos curtos, em português e sem ponto final.

## Uso da JSGE

As telas Menu, Simulador e Comparador estendem `EngineFrame`. Os campos usam
`GuiTextField`, os botões usam `GuiButton`, a velocidade usa `GuiSlider` e `GuiLabel`,
e a consulta da sequência usa `GuiList`, todos da JSGE 1.9.1 incluída no projeto.
`CampoNumerico` estende `GuiTextField` para corrigir a edição e usa as entradas de
teclado e mouse e as funções de desenho da própria engine.
Árvores, linhas, círculos, legendas, recortes e cores são desenhados pela engine.
Não há botões, campos ou painéis Swing substituindo os componentes da JSGE.
As classes do Java são usadas nos algoritmos, sorteio, cores e eventos das janelas;
`SwingUtilities` organiza o retorno ao menu na thread gráfica da janela da engine.

A estrutura da aula foi preservada: `No`, filhos `e` e `d`, métodos recursivos,
`put`, `delete` e posicionamento por nível e ranque. A organização segue métodos
separados para cada ação, nomes em português e decisões com `if` e `else`.

## Verificação e limites

Os testes executam 12.000 operações aleatórias e outras 500 com gravação por árvore,
com semente fixa. Comparam as chaves com `TreeSet`, conferem as alturas e o balanceamento
AVL, a raiz preta, a ausência de vermelhos consecutivos, a inclinação à esquerda e a
igualdade da altura preta na AVP. Também verificam ciclos, perda de subárvores nas fotos,
imutabilidade dos passos antigos, duplicatas, remoções ausentes e esvaziamento.
Os testes também cobrem o sorteio até 200 números, a ausência de repetições, as
quantidades inválidas e a manutenção de todos os números ao formatar e ler a sequência.

A interface ajusta o espaçamento para caber na janela. Muitas chaves diminuem a
legibilidade, especialmente em uma ABB muito inclinada. Há um limite de 200 nós na
interface para controlar o custo das cópias. O percurso em ordem é abreviado quando
não cabe no rodapé. Na remoção com dois filhos, duas chaves iguais podem aparecer em
um passo, mostrando a cópia do sucessor antes da remoção de sua ocorrência original.

Referências: tutorial em PDF fornecido pelo aluno, projeto anterior fornecido como
referência de organização e [repositório da JSGE](https://github.com/davidbuzatto/JSGE).
A autoria da base do professor foi preservada nos arquivos originais e neste texto.
