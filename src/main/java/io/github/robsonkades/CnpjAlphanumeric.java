package io.github.robsonkades;

/**
 * Classe final que representa um CNPJ (Cadastro Nacional da Pessoa Jurídica) com suporte a caracteres
 * alfanuméricos. Estende a classe abstrata {@link Cnpj} e implementa métodos para validação e formatação
 * de CNPJs, considerando apenas dígitos numéricos (0-9) no cálculo dos dígitos verificadores.
 *
 * <p>Esta classe é imutável e fornece um método estático {@link #create(String)} para criação de instâncias
 * validadas. Caso o CNPJ informado seja inválido, uma exceção {@link IllegalArgumentException} será lançada.</p>
 *
 * @author Robson Kades
 * @see Cnpj
 */
public final class CnpjAlphanumeric extends Cnpj {

    /**
     * Construtor privado da classe {@code CnpjAlphanumeric}.
     *
     * <p>Este construtor é privado para garantir que instâncias sejam criadas apenas pelo método
     * estático {@link #create(String)}, que realiza a validação do CNPJ.</p>
     *
     * @param cnpj o valor do CNPJ como uma {@code String}, que pode conter formatação
     *             (ex.: "12.345.678/0001-90") ou apenas dígitos
     */
    private CnpjAlphanumeric(final String cnpj) {
        super(cnpj);
    }

    /**
     * Cria uma instância de {@code CnpjAlphanumeric} a partir de um valor de CNPJ.
     *
     * <p>O método valida o CNPJ fornecido utilizando o método {@link Cnpj#isValid()}. Se o CNPJ for
     * inválido, uma exceção {@link IllegalArgumentException} é lançada com o valor fornecido como
     * mensagem. Caso contrário, retorna uma instância válida de {@code CnpjAlphanumeric}.</p>
     *
     * @param value o valor do CNPJ como uma {@code String}, que pode conter formatação ou apenas dígitos
     * @return uma instância de {@code CnpjAlphanumeric} representando o CNPJ válido
     * @throws IllegalArgumentException se o CNPJ fornecido for inválido
     */
    public static Cnpj create(final String value) {
        Cnpj cnpj = new CnpjAlphanumeric(value);
        if (cnpj.isValid()) {
            throw new IllegalArgumentException(value);
        }
        return cnpj;
    }

    /**
     * Formata o CNPJ no padrão brasileiro, inserindo pontos, barra e hífen.
     *
     * <p>O método utiliza uma expressão regular para formatar o valor do CNPJ, retornando uma
     * {@code String} no formato "AA.AAA.AAA/AAAA-DD", onde "AA" são os dois primeiros dígitos,
     * "AAA.AAA" são os próximos seis dígitos, "AAAA" são os quatro dígitos da filial e "DD" são
     * os dígitos verificadores.</p>
     *
     * <p>Se o valor do CNPJ não possuir exatamente 14 caracteres após a limpeza com
     * {@link Cnpj#strip(String)}, o método retorna o valor original sem formatação.</p>
     *
     * @return uma {@code String} formatada no padrão "AA.AAA.AAA/AAAA-DD"; retorna o valor original
     * se o CNPJ não puder ser formatado
     */
    @Override
    public String format() {
        return this.getValue().replaceFirst("(.{2})(.{3})(.{3})(.{4})(.{2})", "$1.$2.$3/$4-$5");
    }

    /**
     * Calcula um dígito verificador para o CNPJ com base em uma sequência de entrada e um array de pesos.
     *
     * <p>O método realiza o cálculo do dígito verificador somando o produto de cada caractere da entrada
     * (convertido para valor numérico) pelos pesos fornecidos. O cálculo segue as regras oficiais para
     * CNPJ, onde o módulo 11 é usado para determinar o dígito verificador. Se o resultado do cálculo
     * for 10 ou maior, o dígito retornado é '0'. Apenas caracteres numéricos (0-9) são considerados
     * válidos; caso contrário, o método retorna '0'.</p>
     *
     * @param input   a {@code String} contendo a sequência de entrada para cálculo
     * @param weights o array de pesos a ser utilizado no cálculo (ex.: {@link Cnpj#WEIGHTS_1} ou
     *                {@link Cnpj#WEIGHTS_2})
     * @return o caractere representando o dígito verificador calculado; retorna '0' se a entrada
     * contiver caracteres inválidos
     */
    @Override
    char calcDigit(String input, int[] weights) {
        int sum = 0;
        for (int i = 0; i < weights.length; i++) {
            char c = input.charAt(i);
            int value;

            value = ((int) c) - 48;
            if (value < 0 || value > 42) return '0'; // valor inválido

            sum += value * weights[i];
        }

        int mod = sum % 11;
        int dv = 11 - mod;
        return (dv >= 10) ? '0' : Character.forDigit(dv, 10);
    }
}
