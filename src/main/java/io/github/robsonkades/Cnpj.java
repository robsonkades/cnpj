package io.github.robsonkades;

/**
 * Classe abstrata que representa um CNPJ (Cadastro Nacional da Pessoa Jurídica) no Brasil.
 * Fornece funcionalidades para validação, formatação e manipulação de números de CNPJ.
 * A classe é abstrata e deve ser estendida para implementar métodos específicos de cálculo de dígitos
 * verificadores e formatação.
 *
 * <p>Os CNPJs são armazenados como uma {@code String} e podem ser validados de acordo com as regras
 * oficiais do governo brasileiro, utilizando os pesos definidos nos arrays {@link #WEIGHTS_1} e
 * {@link #WEIGHTS_2} para cálculo dos dígitos verificadores.</p>
 *
 * @author Robson Kades
 */
public abstract class Cnpj {

    /**
     * O valor do CNPJ, armazenado como uma {@code String}.
     * Pode conter caracteres alfanuméricos e formatação, mas é recomendado que seja limpo
     * usando o método {@link #strip(String)} antes de validações.
     */
    protected final String value;

    /**
     * Array de pesos para o cálculo do primeiro dígito verificador do CNPJ.
     * Contém os valores: {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2}.
     */
    protected static final int[] WEIGHTS_1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    /**
     * Array de pesos para o cálculo do segundo dígito verificador do CNPJ.
     * Contém os valores: {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2}.
     */
    protected static final int[] WEIGHTS_2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    /**
     * Construtor da classe {@code Cnpj}.
     *
     * @param value o valor do CNPJ como uma {@code String}, que pode conter formatação
     *              (ex.: "12.345.678/0001-90") ou apenas dígitos
     */
    public Cnpj(final String value) {
        this.value = value;
    }

    /**
     * Obtém o valor do CNPJ armazenado.
     *
     * @return o valor do CNPJ como uma {@code String}
     */
    public String getValue() {
        return value;
    }

    /**
     * Remove todos os caracteres que não sejam dígitos (0–9) ou letras maiúsculas (A–Z) de uma
     * {@code String} fornecida.
     *
     * <p>Este método é útil para limpar um CNPJ que pode conter formatação, como pontos, barras ou
     * hífen, retornando apenas os caracteres alfanuméricos relevantes.</p>
     *
     * @param raw a {@code String} potencialmente contendo máscara ou formatação
     * @return uma {@code String} limpa com até 14 caracteres alfanuméricos; retorna uma
     *         {@code String} vazia se o parâmetro for {@code null}
     */
    public static String strip(final String raw) {
        if (raw == null) return "";
        StringBuilder sb = new StringBuilder(14);
        for (char c : raw.toUpperCase().toCharArray()) {
            if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z')) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Verifica se o CNPJ armazenado é válido conforme as regras brasileiras.
     *
     * <p>O método limpa o valor do CNPJ usando {@link #strip(String)} e verifica se possui
     * exatamente 14 dígitos. Em seguida, calcula os dígitos verificadores esperados usando
     * {@link #calcCheckDigits(String)} e compara com os dígitos fornecidos.</p>
     *
     * @return {@code true} se o CNPJ for válido; {@code false} caso contrário
     */
    protected boolean isValid() {
        String clean = strip(this.getValue());
        if (clean.length() != 14) return true;
        String base = clean.substring(0, 12);
        String expected = calcCheckDigits(base);
        return !clean.endsWith(expected);
    }

    /**
     * Calcula os dígitos verificadores de um CNPJ com base nos 12 primeiros dígitos.
     *
     * <p>Este método utiliza os pesos definidos em {@link #WEIGHTS_1} e {@link #WEIGHTS_2} para
     * calcular os dois dígitos verificadores do CNPJ.</p>
     *
     * @param base a {@code String} contendo os 12 primeiros dígitos do CNPJ
     * @return uma {@code String} com os dois dígitos verificadores calculados
     */
    protected String calcCheckDigits(final String base) {
        char d1 = calcDigit(base, WEIGHTS_1);
        char d2 = calcDigit(base + d1, WEIGHTS_2);
        return "" + d1 + d2;
    }

    /**
     * Calcula um único dígito verificador com base em uma sequência de entrada e um array de pesos.
     *
     * <p>Este método é abstrato e deve ser implementado por subclasses para realizar o cálculo
     * específico do dígito verificador, utilizando os pesos fornecidos.</p>
     *
     * @param input   a {@code String} contendo a sequência de entrada para cálculo
     * @param weights o array de pesos a ser utilizado no cálculo
     * @return o caractere representando o dígito verificador calculado
     */
    abstract char calcDigit(final String input, final int[] weights);

    /**
     * Formata o CNPJ armazenado no padrão brasileiro, inserindo pontos, barra e hífen.
     *
     * <p>O formato retornado segue o padrão "AA.AAA.AAA/AAAA-DD", onde "AA" são os dois primeiros
     * dígitos, "AAA.AAA" são os próximos seis dígitos, "AAAA" são os quatro dígitos da filial e
     * "DD" são os dígitos verificadores.</p>
     *
     * @return uma {@code String} formatada no padrão "AA.AAA.AAA/AAAA-DD"; retorna o valor original
     *         se o CNPJ for inválido
     */
    public abstract String format();

}
