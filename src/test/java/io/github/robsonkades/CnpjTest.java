package io.github.robsonkades;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testes da classe Cnpj")
class CnpjTest {

    @Test
    @DisplayName("Geração de CNPJ NUMERIC produz 14 dígitos e é válido")
    void shouldGenerateValidNumericCnpj() {
        String cnpj = Cnpj.generate(Cnpj.Type.NUMERIC);
        assertNotNull(cnpj);
        assertEquals(14, cnpj.length(), "Deve ter 14 caracteres");
        assertTrue(cnpj.chars().allMatch(Character::isDigit), "Deve conter apenas dígitos");
        assertTrue(Cnpj.isValid(cnpj), "Gerado deve ser válido");
    }

    @Test
    @DisplayName("Geração de CNPJ ALPHANUMERIC produz 14 caracteres e é válido")
    void shouldGenerateValidAlphanumericCnpj() {
        String cnpj = Cnpj.generate(Cnpj.Type.ALPHANUMERIC);
        assertNotNull(cnpj);
        assertEquals(14, cnpj.length(), "Deve ter 14 caracteres");
        assertTrue(cnpj.chars().allMatch(c -> Character.isDigit(c) || (c >= 'A' && c <= 'Z')),
                "Deve conter apenas dígitos ou letras A–Z");
        assertTrue(Cnpj.isValid(cnpj), "Gerado deve ser válido");
    }

    @Test
    @DisplayName("isValid retorna true para CNPJ numérico válido com máscara")
    void shouldValidateNumericWithMask() {
        String masked = "12.345.678/0001-95";
        assertTrue(Cnpj.isValid(masked));
    }

    @Test
    @DisplayName("isValid retorna true para CNPJ alfanumérico válido com máscara")
    void shouldValidateAlphanumericWithMask() {
        String base = Cnpj.generate(Cnpj.Type.ALPHANUMERIC);
        String formatted = Cnpj.format(base);
        assertTrue(Cnpj.isValid(formatted));
    }

    @Test
    @DisplayName("isValid retorna false para CNPJ com tamanho incorreto")
    void shouldInvalidateWrongLength() {
        assertFalse(Cnpj.isValid("123"), "Muito curto");
        assertFalse(Cnpj.isValid("123456789012345"), "Muito longo");
    }

    @Test
    @DisplayName("isValid retorna false para CNPJ com caracteres inválidos")
    void shouldInvalidateInvalidChars() {
        String invalid = "12!345?78/0001-95";
        assertFalse(Cnpj.isValid(invalid));
    }

    @Test
    @DisplayName("isValid retorna false para CNPJ numérico com DV incorreto")
    void shouldInvalidateNumericBadChecksum() {
        String bad = "12345678000100"; // base válido, mas dígitos finais inventados
        assertFalse(Cnpj.isValid(bad));
    }

    @Test
    @DisplayName("isValid retorna false para CNPJ alfanumérico com DV incorreto")
    void shouldInvalidateAlphanumericBadChecksum() {
        // pega um válido e altera o último caractere
        String good = Cnpj.generate(Cnpj.Type.ALPHANUMERIC);
        String bad = good.substring(0, 13) + (good.charAt(13) == '0' ? '1' : '0');
        assertFalse(Cnpj.isValid(bad));
    }

    @Test
    @DisplayName("detectType identifica NUMERIC corretamente")
    void shouldDetectNumericType() {
        String num = Cnpj.generate(Cnpj.Type.NUMERIC);
        Optional<Cnpj.Type> type = Cnpj.detectType(num);
        assertTrue(type.isPresent());
        assertEquals(Cnpj.Type.NUMERIC, type.get());
    }

    @Test
    @DisplayName("detectType identifica ALPHANUMERIC corretamente")
    void shouldDetectAlphanumericType() {
        String alpha = Cnpj.generate(Cnpj.Type.ALPHANUMERIC);
        Optional<Cnpj.Type> type = Cnpj.detectType(alpha);
        assertTrue(type.isPresent());
        assertEquals(Cnpj.Type.ALPHANUMERIC, type.get());
    }

    @Test
    @DisplayName("detectType retorna vazio para string com caracteres inválidos")
    void shouldDetectTypeEmptyOnInvalid() {
        assertTrue(Cnpj.detectType("1234*67890123").isEmpty());
    }

    @Test
    @DisplayName("format aplica máscara correta para NUMERIC")
    void shouldFormatNumeric() {
        String raw = "12345678000195";
        String expected = "12.345.678/0001-95";
        assertEquals(expected, Cnpj.format(raw));
    }

    @Test
    @DisplayName("format aplica máscara correta para ALPHANUMERIC")
    void shouldFormatAlphanumeric() {
        String raw = "1A3BC5F700XZ43";
        String expected = "1A.3BC.5F7/00XZ-43";
        assertEquals(expected, Cnpj.format(raw));
    }

    @Test
    @DisplayName("format retorna entrada quando inválido")
    void shouldFormatReturnRawOnInvalid() {
        String bad = "12345";
        assertEquals(bad, Cnpj.format(bad));
    }

    //----- strip -----

    @Test
    @DisplayName("strip remove máscara e pontuação")
    void shouldStripFormatting() {
        String masked = "12.345.678/0001-95";
        assertEquals("12345678000195", Cnpj.strip(masked));
    }

    @Test
    @DisplayName("strip retorna vazio para null")
    void shouldStripReturnEmptyOnNull() {
        assertEquals("", Cnpj.strip(null));
    }

    @Test
    @DisplayName("strip mantém apenas dígitos e letras maiúsculas")
    void shouldStripKeepOnlyAllowedChars() {
        String raw = " ab.12-c!34 /Zz ";
        assertEquals("AB12C34ZZ", Cnpj.strip(raw));
    }
}
