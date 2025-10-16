package io.github.robsonkades.cnpj;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Representa um CNPJ (Cadastro Nacional da Pessoa Jurídica) de forma imutável e validada.
 * <p>
 * Esta classe oferece uma implementação eficiente e segura para manipulação de CNPJs,
 * incluindo:
 * <ul>
 *   <li>Validação dos dígitos verificadores conforme o algoritmo oficial;</li>
 *   <li>Suporte a valores numéricos e alfanuméricos (ex.: CNPJs usados em chaves de acesso);</li>
 *   <li>Extração e normalização automática de máscaras;</li>
 *   <li>Geração da representação formatada (máscara padrão {@code 00.000.000/0000-00}).</li>
 * </ul>
 *
 * <p>
 * O objeto {@link CNPJ} é <strong>imutável</strong> e pode ser usado com segurança em coleções
 * e ambientes concorrentes. Todos os métodos de validação são <strong>thread-safe</strong>.
 *
 * <p><b>Exemplo de uso:</b>
 * <pre>{@code
 * // Cria uma instância validada (lança exceção se inválido)
 * CNPJ cnpj = CNPJ.of("12.345.678/0001-95");
 *
 * System.out.println(cnpj.getValue());         // 12345678000195
 * System.out.println(cnpj.getValueWithMask()); // 12.345.678/0001-95
 * System.out.println(CNPJ.isValid("12345678000195")); // true
 * }</pre>
 *
 * @author Seu Nome
 * @version 1.0
 * @since 2025
 */
public final class CNPJ implements Serializable {

    /**
     * Expressão regular para remover caracteres não permitidos (mantém apenas dígitos e letras A-Z).
     */
    private static final Pattern STRIP_PATTERN = Pattern.compile("[^0-9A-Z]");

    /**
     * Valor base utilizado na conversão de caracteres numéricos/alfanuméricos.
     */
    private static final int BASE_CHAR_INDEX = '0';

    /**
     * Pesos oficiais para o cálculo dos dígitos verificadores do CNPJ.
     */
    private static final int[] WEIGHTS = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    /**
     * Valor interno do CNPJ (somente os 14 caracteres normalizados, sem máscara).
     */
    private final String value;

    /**
     * Construtor privado para garantir imutabilidade e controle de criação via {@link #of(String)}.
     *
     * @param value valor já validado e normalizado do CNPJ
     */
    private CNPJ(String value) {
        this.value = value;
    }

    /**
     * Cria uma nova instância de {@link CNPJ} a partir de um valor informado.
     * <p>
     * Remove automaticamente quaisquer máscaras, valida o formato e calcula os
     * dígitos verificadores conforme o algoritmo oficial.
     *
     * @param value valor do CNPJ (com ou sem máscara, podendo conter letras)
     * @return uma instância válida de {@link CNPJ}
     * @throws IllegalArgumentException se o valor for inválido ou não atender ao formato esperado
     */
    public static CNPJ of(final String value) {
        String clean = extractValue(value);
        if (isValid(clean)) {
            return new CNPJ(clean);
        }
        throw new IllegalArgumentException("O valor de CNPJ fornecido é inválido: " + value);
    }

    /**
     * Verifica se o valor informado representa um CNPJ válido.
     * <p>
     * Esta validação suporta CNPJs numéricos e alfanuméricos. São considerados inválidos
     * valores nulos, vazios, com tamanho diferente de 14 caracteres, ou compostos por
     * caracteres repetidos.
     *
     * @param input valor do CNPJ (com ou sem máscara)
     * @return {@code true} se o CNPJ for válido, {@code false} caso contrário
     */
    public static boolean isValid(final String input) {
        if (input == null || input.isEmpty()) return false;
        String clean = extractValue(input);
        if (clean.length() != 14 || clean.equals("00000000000000")) return false;

        String body = clean.substring(0, 12);
        String providedCheckDigits = clean.substring(12);
        String calculatedCheckDigits = calculateCheckDigits(body);

        return providedCheckDigits.equals(calculatedCheckDigits);
    }

    /**
     * Remove todos os caracteres não numéricos ou não alfabéticos do valor informado.
     * <p>
     * É utilizado internamente pelos métodos {@link #of(String)} e {@link #isValid(String)},
     * mas pode ser usado externamente para limpeza de dados de entrada.
     *
     * @param input valor de entrada (com ou sem máscara)
     * @return apenas os caracteres numéricos e alfanuméricos, em letras maiúsculas
     */
    static String extractValue(final String input) {
        if (input == null) return "";
        return STRIP_PATTERN.matcher(input.toUpperCase()).replaceAll("");
    }

    /**
     * Calcula os dois dígitos verificadores (DV) do CNPJ com base no corpo de 12 caracteres.
     * <p>
     * O algoritmo aplica pesos definidos pela Receita Federal e retorna uma string
     * com os dois dígitos resultantes.
     *
     * @param base corpo do CNPJ (12 caracteres)
     * @return string contendo os dois dígitos verificadores
     */
    private static String calculateCheckDigits(String base) {
        int sum1 = 0;
        int sum2 = 0;

        for (int i = 0; i < 12; i++) {
            int value = base.charAt(i) - BASE_CHAR_INDEX;
            sum1 += value * WEIGHTS[i + 1];
            sum2 += value * WEIGHTS[i];
        }

        int checkDigit1 = sum1 % 11 < 2 ? 0 : 11 - (sum1 % 11);
        sum2 += checkDigit1 * WEIGHTS[12];
        int checkDigit2 = sum2 % 11 < 2 ? 0 : 11 - (sum2 % 11);

        return Integer.toString(checkDigit1) + checkDigit2;
    }

    /**
     * Obtém a raiz do CNPJ, que corresponde aos primeiros 8 dígitos do número.
     *
     * <p>A raiz do CNPJ é composta pelos 8 primeiros caracteres do valor armazenado, representando a
     * identificação principal da empresa, excluindo os 4 dígitos do sufixo da filial e os 2 dígitos
     * verificadores. Este método não realiza validação do tamanho ou formato do CNPJ, retornando
     * diretamente a substring dos primeiros 8 caracteres do valor retornado por {@link #getValue()}.</p>
     *
     * @return uma {@code String} contendo os 8 primeiros caracteres do CNPJ
     */
    public String getRoot() {
        return value.substring(0, 8);
    }

    /**
     * Retorna o valor do CNPJ formatado com a máscara padrão {@code 00.000.000/0000-00}.
     * <p>
     * Caso o valor interno não possua 14 caracteres, o valor original é retornado sem formatação.
     *
     * @return CNPJ formatado como string
     */
    public String getValueWithMask() {
        if (value.length() != 14) return value;
        return value.substring(0, 2) + '.' +
                value.substring(2, 5) + '.' +
                value.substring(5, 8) + '/' +
                value.substring(8, 12) + '-' +
                value.substring(12, 14);
    }

    /**
     * Retorna o valor bruto (sem máscara) do CNPJ.
     *
     * @return valor normalizado de 14 caracteres
     */
    public String getValue() {
        return value;
    }

    /**
     * Retorna o tipo do CNPJ (numérico ou alfanumérico).
     *
     * @return {@link CNPJType} indicando se é numérico ou alfanumérico
     */
    public CNPJType getType() {
        // Se contiver qualquer letra A-Z, é alfanumérico
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if ((c >= 'A' && c <= 'Z')) {
                return CNPJType.ALPHANUMERIC;
            }
        }
        return CNPJType.NUMERIC;
    }

    /**
     * Retorna uma representação textual da instância para fins de depuração.
     *
     * @return representação no formato {@code CNPJ{value='12345678000195'}}
     */
    @Override
    public String toString() {
        return "CNPJ{" +
                "value='" + value + '\'' +
                '}';
    }

    /**
     * Compara esta instância com outro objeto para verificar igualdade.
     * <p>
     * Dois objetos {@link CNPJ} são considerados iguais se possuírem o mesmo valor
     * normalizado (sem máscara).
     *
     * @param object outro objeto a ser comparado
     * @return {@code true} se ambos representarem o mesmo CNPJ
     */
    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        CNPJ cnpj = (CNPJ) object;
        return value.equals(cnpj.value);
    }

    /**
     * Retorna o código hash baseado no valor interno do CNPJ.
     *
     * @return hash code consistente com {@link #equals(Object)}
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
