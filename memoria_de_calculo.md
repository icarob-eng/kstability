# Memória de cálculo de kstability

Modificado por último: 16/04/2023

Neste documento serão transcritos os procedimentos de cálculo e fórmulas
utlizadas pelo projeto kstability.

## Algebra linear

As operações de algebra linear foram definidas na classe [`Vector`](https://github.com/icarob-eng/kstability/blob/main/src/commonMain/kotlin/com/kstabilty/Models.kt), transcrita abaixo:

```kotlin
data class Vector(val x: Float, val y: Float) {
    operator fun plus(other: Vector) = Vector(this.x + other.x, this.y + other.y)
    operator fun minus(other: Vector) = Vector(this.x - other.x, this.y - other.y)
    operator fun times(other: Float) = Vector(x * other, y * other)
    operator fun div(other: Float) = Vector(x / other, y / other)

    fun modulus() = sqrt(this*this)  // = sqrt(x**2 + y**2)

    fun normalize() = this/modulus()

    operator fun times(other: Vector) = this.x * other.x + this.y * other.y  // dot product

    fun crossModule(other: Vector) = this.x * other.y - this.y * other.x

    fun getOrthogonal() = Vector(-y, x).normalize()
}
```

Em notação matemática, as operações listadas podem ser transcritas, respectivamente, como:

$\vec{A} + \vec{B} = \langle A_x + B_x; A_y + B_y \rangle$

$\vec{A} - \vec{B} = \langle A_x - B_x; A_y - B_y \rangle$

$\vec{A} \times c = \langle A_x \times c; A_y \times c \rangle$

$\vec{A} \div c = \langle A_x \div c; A_y \div c \rangle$

$|\vec{A}| = \sqrt{\vec{A} \cdot \vec{A}}$

$\hat{A} = \frac{\vec{A}}{|\vec{A}|}$

Produto escalar:

$\vec{A} \cdot \vec{B} = A_x \times B_x + A_y \times B_y$

Para o produto vetorial, se obtêm apenas seu módulo, uma vez que o projeto se limita a duas dimensões

$|\vec{A} \times \vec{B}| = A_x \times B_x - A_y \times B_y$

Para obter um vetor $\hat{B}$ tal que $\hat{B} \perp \vec{A}$ a função `getOrthogonal()` faz:

$getOrthogonal(\vec{A}) = \langle - A_y; A_x\rangle = \hat{B}$

## Considerações de modelo

Na presente concepção do modelo do projeto, vale-se destacar alguns aspectos:

Um nó é onde ficam armazenados os dados das barras atreladas a ele; cargas distirbuidas e pontuais; 
um único suporte, que pode ser de primeiro, segundo ou terceiro gênero; e um nome, 
que pode ser definido pelo usuário.

*Ainda não foi definida a forma de interação entre múltiplas barras, de modo que devem funcionar 
como rótulas.*

Uma barra tem como única propriedade atrelada a ela, seus nós, assim, as cargas precisam ser 
atreladas às barras posteriomente, no momento de se definir os gráficos de esforços.

Cargas distribuidas tem como prorpiedades o nó inicial, o final e sua norma. É obtida uma força 
pontual equivalente por um método definido na seção de considerações físicas.

Todos os nós são armazenados numa classe `Structure`, que representa um projeto como um todo sendo usada como base para calcular os parâmetros físicos.


## Considerações físicas

### Teste de estabilidade

Até o momento, o teste de estabilidade de uma estrutura é feito apenas pela somatória dos gêneros 
dos suportes da estrutura, comparando-os com $3$. Isto foi feito desta forma, pois nem todos os nós 
possuem barras e não foi determinada forma de se especificar a ligação entre as barras. 
Necessita-se discutir a utilidade da funcionalidade deste teste. 


### Carga pontual equivalente a carga distirbuida

Para este cáculo se faz necessário obter dois valores: o ponto de aplicação da força equivalente e 
o vetor desta: $\vec{P_e}, \vec{F_e}$.

Defina-se $\vec{P_1}, \vec{P_2}, N$ como respectivamente, o ponto inicial da carga distribuída, o 
ponto final e sua norma. Com isto, temos que:

$\vec{P_e} = \frac{\vec{P_1} + \vec{P_2}}{2}$

e

$\vec{F_e} = (getOrthogonal(\vec{P_2} - \vec{P_1}) \cdot |\vec{P_2} - \vec{P_1}|) \times N$

A implementação das equações foi esta:

```kotlin
fun getEqvLoad() = Load(
        Knot(knot1.name + knot2.name,
            (knot1.pos + knot2.pos) / 2F,  // midpoint
            null),
        (knot2.pos - knot1.pos).getOrthogonal() * (knot2.pos - knot1.pos).modulus() * norm
        )
```

### Somatório de cargas e momentos fletores

O somatório das cargas foi definido como o somatório vetorial $\Sigma \vec{F}$, 
para todas as cargas pontuais e equivalentes.

O somatório dos momentos fletores foi definido como $\Sigma |\vec{F} \times \vec{P}|$, onde 
$\vec{P}$ é definido como a diferença vetorial entre o ponto de aplicação da força e
o nó que serve como eixo de rotação.

## Cáculo dos esforços nas barras

### Determinação das cargas resultantes
As cargas resultantes são determinadas da seguinte forma:
- Se calcula a força resultante, o que é feito pelo somatório de todas as cargas concentradas e distribuídas adicionadas na estrutura;
- O momento fletor é calculado pela soma das cargas de momento nos nós somadas ao momento fletor de todas as cargas da estrutura. O momento fletor de uma carga é calculado em relação a um ponto arbitrário, geralmente, a origem do sistema de coordenadas;

### Cálculo das reações de apoio
Para o cálculo das forças de reação, se emprega dois algoritmos diferentes, para as duas estruturas que o sistema suporta no momento:
- Determinação de reações de força e momento fletor para uma única viga engastada: basta determinar as resultantes e adicionar cargas opostas;
- Determinação de forças de reação para uma única viga bi-apoiada em um suporte de primeiro e outro de segundo gênero: o algoritmo para determinar as reações nesta estrutura é o descrito no arquivo `algebraic_formulations.ipybn`;

Após o cálculo, as reações de apoio são adicionadas à estrutura, tornando-a estável. O último passo do sistema é gerar os gráficos de esforços cortantes, valores dos esforços normais e gráficos de momento fletor, para a(s) barra(s) da estrutura. Atualmente só suporta-se uma barra.

## Diagramas de esforço cortante, esforço normal e momento fletor

O algorítmo usado para encontrar a lista de pontos do diagrama de uma barra consiste nos passos mostrados a seguir:
1. É criada uma cópia da estrutura, com a barra em análise sendo rotacionada para sua inclinação ($\frac{\Delta y}{\Delta x}$) ser $0$, assim, todos os demais vetores são também rotacionados;
2. A barra é dividida em uma série de segmentos (horizontais, tal como a barra), com cada segmento sendo representado por um nó em seu início, consequentemente, cada segmento terá apenas uma carga a mais em relação aos outros a sua esqueda;
3. Cada segmento possui um polinômio, que é calculado pela soma das cargas a esquerda do segmento de análise, usando principio da superposição. Desta forma, temos uma série de poliômios que representam partes de uma função de Macaulay;
4. Com a função de Macaulay, e tendo uma lista de valores ao longo do eixo $x$, basta calcular os valores no eixo $y$ e então rotacionar os pontos para que o novo eixo $x$ tenha a mesma inclinação (e alinhamento) da barra original;

### 1. Rotação dos vetores
Os vetores são rotacionados para que o sistema possa ser resolvido de forma linear. Usando a inclinação $i$ da barra como argumento, para um vetor $\langle x, y \rangle$ e sua rotação $\langle x', y'\rangle$, temos que:

$$ \left \{\begin{cases}
x' = x cos(arctg(i)) - y sin(arctg(i)) \\
y' = x sin(arctg(i)) + y cos(arctg(i))
\end{cases}$$

As funções trigonométricas podem ser simplificadas, sabendo que:

$$cos(arctg(i)) = \frac{1}{\sqrt{i^2 + 1}}, sin(arctg(i)) = \frac{i}{\sqrt{i^2 + 1}}$$

Para barras verticais ($i \to \infty$), precisamos também estabelecer alguns limites:

$$lim_{x \to - \infty} \frac{x}{\sqrt{x^2 + 1}} = -1, lim_{x \to \infty} \frac{x}{\sqrt{x^2 + 1}} = 1$$ 

E

$$lim_{x \to \pm \infty} \frac{1}{\sqrt{x^2 + 1}} = 0$$

Basta então aplicar esta transformada para todos os vetores a serem usados.

### 2. Divisão de segmentos
Como, até o momento não existe atribuição de nós e cargas a barras específicas, o sistema trata todas as cargas como sendo aplicadas à barra em estudo, desta forma, todos os nós representam uma quebra de segmento. Dessa forma, basta mapear cada nó a um novo segmento e o seguimento começa então com a coordenada $x$ do nó, já rotacionado. Para fins que serão explicados adiante, na coordenada $x$ do nó serão plotados dois pontos.

### 3. Determinação da função de Macaulay
As funções de Macaulay são usadas para determinar uma única função definida por partes para analizar o momento fletor ao longo da barra. Cada parte da função pode ser descrita como um polinômio com valor definido apenas na dada seção. No presente algorítmo extedemos este método para calcular também o diagrama de esforço cortante e de esforço normal.

Pelas restrições do sistema (em que a carga mais complexa é uma carga distirbuída de intensidade constante), sabe-se que o maior polinômio obtido deve ser de 2° grau. Assim, basta achar qual seria o valor das constantes $a, b$ e $c$ num polinômio $y=ax²+bx+c$ (onde todas as constantes podem assumir o valor de $0$). Para isto, vamos analizar a expressão para os três casos de cargas possíveis, definindo $N_n(x), Q_n(x)$ e $M_n(x)$, como respectivamente, a contrinuição da n-ésima carga para o esforço normal, cortante e momento fletor.

#### a) Carga pontual
Dada uma carga pontual $\vec{F}$ aplicada em $\vec{A}$ e sabendo que estamos num sistema linear onde $A_x$ é paralela a $x$, temos que:

$$ \left \{ \begin{cases}
N_n(x) = F_x \implies c=F_x \\
Q_n(x) = F_y \implies c=F_y \\
M_n(x) = (x - A_x) F_y \implies F_y x - F_y A_x \implies b=F_y, c=-F_y A_x
\end{cases}$$

Com as demais constantes com o valor de $0$.

#### b) Carga distribuída
Dada uma carga distribuída com intensidade e direção $\vec{F}$, com início em $\vec{A}$ e mesmo sentido de aplicação que o eixo $x$, se conclui que:

$$ \left \{\begin{cases}
N_n(x) = (x - A_x)F_x \implies b=F_x, c=-F_x A_x \\
Q_n(x) = (x - A_x)F_y \implies b=F_y, c=-F_y A_x \\
M_n(x) = \frac{(x - A_x)^2 F_y}{2} \implies \frac{F_y}{2} x^2 - F_y A_x x + \frac{F_y}{2} A_x^2 \implies a=\frac{F_y}{2}, b=-F_y A_x, c=a=\frac{F_y}{2} A_x^2
\end{cases}$$

Vale destacar que, após o fim de uma carga distribuída em um ponto $\vec{B}$, basta adicionar outra carga distrbuída de intensidade e direção $- \vec{F}$ e início em $\vec{B}$.

#### c) Momento fletor concentrado
Se aplicando um momento fletor de intensidade $M$ no ponto $\vec{A}$ da barra, temos que:

$$ \left \{\begin{cases}
N_n(x) = 0 \\
Q_n(x) = 0 \\
M_n(x) = M \implies c=M
\end{cases}$$

#### Determinando a função
Para determinar a função de Macaulay, usando do princípio da superposição, basta fazer $M(x) = \Sigma M_n(x)$, para todas as cargas a esquerda do segmento. O mesmo serve para os diagramas de esforço cortante e normal.

Assim, basta aplicar todas as partes a seus devidos segmentos, observando que existem indefinições nos pontos em que os segmentos se coincidem. Decidimos que, para fins de plotagem, a função assumirá dois valores nestes casos.

### 4. Calculo dos valores
O cálculo dos valores de $y$ é simples: basta mapear cada valor em $x$ a sua função em $y$, atentando para as intersecções. Para rotacionar os pontos alinhando o eixo $x$ com a barra, são tomados todos os pontos $(x,y)$ e rotacionados, como j pelo negativo da inclinação da barra.
