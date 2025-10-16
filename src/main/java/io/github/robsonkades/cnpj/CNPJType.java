package io.github.robsonkades.cnpj;

/**
 * Define o tipo de um {@link CNPJ}, indicando se o valor contém apenas dígitos numéricos
 * ou se inclui também caracteres alfabéticos.
 * <p>
 * Este tipo é determinado automaticamente pelo método {@link CNPJ#getType()},
 * com base no conteúdo do valor interno do CNPJ após a normalização (remoção de
 * máscaras e conversão para maiúsculas).
 * <p>
 * O tipo é utilizado, principalmente, para diferenciar CNPJs tradicionais (numéricos)
 * de identificadores expandidos ou integrados a sistemas externos que utilizam
 * códigos alfanuméricos compatíveis com o algoritmo de cálculo de dígitos verificadores.
 *
 * <h3>Exemplos</h3>
 * <pre>{@code
 * CNPJ cnpjNumerico = CNPJ.of("11222333000181");
 * System.out.println(cnpjNumerico.getType()); // Saída: NUMERIC
 *
 * CNPJ cnpjAlfa = CNPJ.of("AB2223330001C1");
 * System.out.println(cnpjAlfa.getType()); // Saída: ALPHANUMERIC
 * }</pre>
 *
 * @see CNPJ
 * @see CNPJ#getType()
 */
public enum CNPJType {

    /**
     * Indica que o CNPJ contém exclusivamente caracteres numéricos (0–9).
     * Este é o formato tradicional utilizado pela Receita Federal.
     */
    NUMERIC,

    /**
     * Indica que o CNPJ contém pelo menos um caractere alfabético (A–Z),
     * utilizado em cenários de integração, sistemas externos ou identificadores
     * híbridos que preservam compatibilidade com o cálculo de dígitos verificadores.
     */
    ALPHANUMERIC
}
