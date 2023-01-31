# Memória de cálculo do kstability

Modificado por último: 05/12/2022

Neste documento serão transcritos os procedimentos de cálculo e fórmulas
utlizadas pelo projeto kstability.

## Algebra linear

As operações de algebra linear foram definidas na classe [`Vector`](https://github.com/icarob-eng/Estabilidade-IO/blob/main/app/src/main/java/com/montaigne/estabilidade_io/Models.kt), transcrita abaixo:

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

    fun getNormal() = Vector(-y, x).normalize()
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

Para obter um vetor $\hat{B}$ tal que $\hat{B} \perp \vec{A}$ a função `getNormal()` faz:

$getNormal(\vec{A}) = \langle - A_y; A_x\rangle = \hat{B}$

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

$\vec{F_e} = (getNormal(\vec{P_2} - \vec{P_1}) \cdot |\vec{P_2} - \vec{P_1}|) \times N$

A implementação das equações foi esta:

```kotlin
fun getEqvLoad() = Load(
        Knot(knot1.name + knot2.name,
            (knot1.pos + knot2.pos) / 2F,  // midpoint
            null),
        (knot2.pos - knot1.pos).getNormal() * (knot2.pos - knot1.pos).modulus() * norm
        )
```

### Somatório de cargas e momentos fletores

O somatório das cargas foi definido como o somatório vetorial $\Sigma \vec{F}$, 
para todas as cargas pontuais e equivalentes.

O somatório dos momentos fletores foi definido como $\Sigma |\vec{F} \times \vec{P}|$, onde 
$\vec{P}$ é definido como a diferença vetorial entre o ponto de aplicação da força e
o nó que serve como eixo de rotação.

## Cáculo dos esforços nas barras

### Determinação das reações (não implementado)

Até o momento não foi feita a determinação das reações, tendo se cogitado o seguinte
procedimento: a partir da quantidade e tipos de apoios, usar algoritmos diferentes para
adicionar cargas de ração, sejam forças ou momentos fletores nos nós dos apoios.

### Etc

Os demais procedimentos de cálculos, como gráfico de esforço cortante ainda não foram implementados
e sequer planejados.
