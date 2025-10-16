package io.github.robsonkades.cnpj;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários para a classe {@link CNPJ}.
 */
public class CNPJTest {

    // Exemplos válidos
    private static final String NUMERIC_VALID = "12.345.678/0001-95";
    private static final String ALPHA_VALID = "4Z.PCS.5K7/0001-36";

    // Exemplos inválidos
    private static final String INVALID_SHORT = "123";
    private static final String INVALID_LONG = "123456789012345678";
    private static final String INVALID_DV = "12.345.678/0001-00";
    private static final String INVALID_NULL = null;
    private static final String INVALID_EMPTY = "";

    @Test
    void testOfWithValidNumericCNPJ() {
        CNPJ cnpj = CNPJ.of(NUMERIC_VALID);
        assertThat(cnpj).isNotNull();
        assertThat(cnpj.getValue()).isEqualTo("12345678000195");
        assertThat(cnpj.getValueWithMask()).isEqualTo(NUMERIC_VALID);
        assertThat(cnpj.getType()).isEqualTo(CNPJType.NUMERIC);
        assertThat(cnpj.getRoot()).isEqualTo("12345678");
    }

    @Test
    void testOfWithValidAlphanumericCNPJ() {
        CNPJ cnpj = CNPJ.of(ALPHA_VALID);
        assertThat(cnpj).isNotNull();
        assertThat(cnpj.getValue()).isEqualTo("4ZPCS5K7000136");
        assertThat(cnpj.getValueWithMask()).isEqualTo(ALPHA_VALID);
        assertThat(cnpj.getType()).isEqualTo(CNPJType.ALPHANUMERIC);
        assertThat(cnpj.getRoot()).isEqualTo("4ZPCS5K7");
    }

    @Test
    void testOfThrowsExceptionForInvalidCNPJ() {
        assertThatThrownBy(() -> CNPJ.of(INVALID_DV))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");

        assertThatThrownBy(() -> CNPJ.of(INVALID_SHORT))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> CNPJ.of(INVALID_LONG))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> CNPJ.of("00000000000000"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> CNPJ.of(INVALID_NULL))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> CNPJ.of(INVALID_EMPTY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testIsValidStaticMethod() {
        assertThat(CNPJ.isValid(NUMERIC_VALID)).isTrue();
        assertThat(CNPJ.isValid(ALPHA_VALID)).isTrue();
        assertThat(CNPJ.isValid(INVALID_DV)).isFalse();
        assertThat(CNPJ.isValid(INVALID_SHORT)).isFalse();
        assertThat(CNPJ.isValid(INVALID_NULL)).isFalse();
        assertThat(CNPJ.isValid(INVALID_EMPTY)).isFalse();
        assertThat(CNPJ.isValid("00000000000000")).isFalse();
    }

    @Test
    void testGetValueWithMask() {
        CNPJ cnpj = CNPJ.of(NUMERIC_VALID);
        assertThat(cnpj.getValueWithMask()).isEqualTo("12.345.678/0001-95");
        CNPJ alpha = CNPJ.of(ALPHA_VALID);
        assertThat(alpha.getValueWithMask()).isEqualTo("4Z.PCS.5K7/0001-36"); // máscara aplicada, ainda mantém letras
    }

    @Test
    void testGetValue() {
        CNPJ cnpj = CNPJ.of(NUMERIC_VALID);
        assertThat(cnpj.getValue()).isEqualTo("12345678000195");
    }

    @Test
    void testEqualsAndHashCode() {
        CNPJ cnpj1 = CNPJ.of(NUMERIC_VALID);
        CNPJ cnpj2 = CNPJ.of("12.345.678/0001-95"); // mesmo valor, máscara diferente
        CNPJ cnpj3 = CNPJ.of(ALPHA_VALID);

        assertThat(cnpj1).isEqualTo(cnpj2);
        assertThat(cnpj1.hashCode()).isEqualTo(cnpj2.hashCode());
        assertThat(cnpj1).isNotEqualTo(cnpj3);
    }

    @Test
    void testToString() {
        CNPJ cnpj = CNPJ.of(NUMERIC_VALID);
        assertThat(cnpj.toString()).contains("12345678000195");
    }

    @Test
    void testExtractValueStaticMethod() {
        assertThat(CNPJ.extractValue("12.345.678/0001-95")).isEqualTo("12345678000195");
        assertThat(CNPJ.extractValue("12A345678B00195")).isEqualTo("12A345678B00195");
        assertThat(CNPJ.extractValue(null)).isEmpty();
        assertThat(CNPJ.extractValue("")).isEmpty();
    }

    @Test
    void testCalculateCheckDigitsPrivateMethodViaReflection() throws Exception {
        var method = CNPJ.class.getDeclaredMethod("calculateCheckDigits", String.class);
        method.setAccessible(true);
        String dv = (String) method.invoke(null, "123456780001");
        assertThat(dv).isEqualTo("95");
    }
}