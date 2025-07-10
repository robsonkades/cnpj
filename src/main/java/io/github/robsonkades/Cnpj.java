package io.github.robsonkades;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utilitário para geração, formatação e validação de números de
 * Cadastro Nacional da Pessoa Jurídica (CNPJ), suportando os formatos
 * numérico tradicional e o novo alfanumérico (base-36) conforme a
 * Nota Técnica Conjunta 2025.001 – publicada em 08/05/2025.
 *
 * <p>Esta classe é final e todos os métodos são estáticos, servindo
 * como biblioteca open‑source para uso em projetos Java.</p>
 *
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * // Geração de CNPJ numérico
 * String cnpjNum = Cnpj.generate(Cnpj.Type.NUMERIC);
 *
 * // Geração de CNPJ alfanumérico
 * String cnpjAlpha = Cnpj.generate(Cnpj.Type.ALPHANUMERIC);
 *
 * // Validação automática
 * boolean validNum = Cnpj.isValid(cnpjNum);
 * boolean validAlpha = Cnpj.isValid(cnpjAlpha);
 *
 * // Formatação
 * String formatted = Cnpj.format(cnpjNum);
 *
 * // Remoção de máscara
 * String raw = Cnpj.strip("12.345.678/0001-95");
 * }</pre>
 *
 * @since 2025-07-10
 */
public final class Cnpj {

    /**
     * Tipos de CNPJ suportados:
     * <ul>
     *   <li>{@link Type#NUMERIC NUMERIC}: formato tradicional (apenas dígitos)</li>
     *   <li>{@link Type#ALPHANUMERIC ALPHANUMERIC}: novo formato base‑36 (0–9, A–Z)</li>
     * </ul>
     */
    public enum Type {
        /** CNPJ contendo apenas dígitos (0–9). */
        NUMERIC,

        /** CNPJ contendo dígitos e letras (0–9, A–Z). */
        ALPHANUMERIC
    }

    private static final char[] BASE36 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final int[] WEIGHTS_1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] WEIGHTS_2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private Cnpj() {
        // Construtor privado para evitar instanciação
    }

    /**
     * Gera um CNPJ válido no formato especificado.
     *
     * @param type o tipo de CNPJ a ser gerado ({@link Type#NUMERIC} ou {@link Type#ALPHANUMERIC})
     * @return uma {@code String} de 14 caracteres representando o CNPJ sem pontuação ou máscara
     */
    public static String generate(final Type type) {
        StringBuilder base = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            if (type == Type.NUMERIC) {
                base.append(ThreadLocalRandom.current().nextInt(10));
            } else {
                int index = ThreadLocalRandom.current().nextInt(36);
                base.append(BASE36[index]);
            }
        }

        String baseStr = base.toString();
        String checkDigits = calcCheckDigits(baseStr, type);
        return baseStr + checkDigits;
    }

    /**
     * Verifica se o CNPJ informado é válido, detectando automaticamente
     * o tipo (numérico ou alfanumérico).
     *
     * @param rawCnpj com ou sem formatação
     * @return um {@code true} se o CNPJ for válido; {@code false} caso contrário
     */
    public static boolean isValid(final String rawCnpj) {
        String clean = strip(rawCnpj);
        if (clean.length() != 14) return false;

        Optional<Type> maybeType = detectType(clean);
        if (maybeType.isEmpty()) return false;

        Type type = maybeType.get();
        String base = clean.substring(0, 12);
        String expected = calcCheckDigits(base, type);
        return clean.endsWith(expected);
    }

    /**
     * Detecta o tipo de CNPJ com base nos caracteres fornecidos.
     *
     * @param cnpj já sem formatação (14 caracteres alfanuméricos)
     * @return um {@link Optional} contendo o {@link Type} detectado, ou vazio se inválido
     */
    public static Optional<Type> detectType(final String cnpj) {
        String clean = strip(cnpj);
        if (clean.matches("\\d{14}")) return Optional.of(Type.NUMERIC);
        if (clean.matches("[0-9A-Z]{14}")) return Optional.of(Type.ALPHANUMERIC);
        return Optional.empty();
    }

    /**
     * Formata um CNPJ de 14 caracteres, inserindo pontos, barra e hífen
     * conforme padrão brasileiro.
     *
     * @param cnpj de 14 caracteres sem formatação
     * @return uma {@code String} formatada ("AA.AAA.AAA/AAAA-DD") ou a própria string se inválida
     */
    public static String format(final String cnpj) {
        if (cnpj == null || cnpj.length() != 14) return cnpj;
        Optional<Type> cnpjType = detectType(cnpj);
        return cnpjType.map(type -> (type == Type.NUMERIC)
                ? cnpj.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5")
                : cnpj.replaceFirst("(.{2})(.{3})(.{3})(.{4})(.{2})", "$1.$2.$3/$4-$5")).orElse(cnpj);

    }

    /**
     * Remove todos os caracteres que não sejam dígitos (0–9) ou letras maiúsculas (A–Z).
     *
     * @param raw potencialmente contendo máscara ou formatação
     * @return uma {@code String} limpa com até 14 caracteres alfanuméricos
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

    // Calcula os dois dígitos verificadores usando módulo 11 e pesos predefinidos.
    private static String calcCheckDigits(final String base, final Type type) {
        char d1 = calcDigit(base, type, WEIGHTS_1);
        char d2 = calcDigit(base + d1, type, WEIGHTS_2);
        return "" + d1 + d2;
    }

    // Calcula um dígito verificador a partir da string de entrada e dos pesos.
    private static char calcDigit(final String input, final Type type, final int[] weights) {
        int sum = 0;
        for (int i = 0; i < weights.length; i++) {
            char c = input.charAt(i);
            int value;

            if (type == Type.NUMERIC) {
                value = Character.getNumericValue(c);
            } else {
                value = ((int) c) - 48;
                if (value < 0 || value > 42) return '0'; // valor inválido
            }

            sum += value * weights[i];
        }

        int mod = sum % 11;
        int dv = 11 - mod;
        return (dv >= 10) ? '0' : Character.forDigit(dv, 10);
    }
}