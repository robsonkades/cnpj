package io.github.robsonkades;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes para a classe CnpjNumeric")
class CnpjNumericTest {

    @Test
    @DisplayName("Deve criar CNPJ numérico válido")
    void deveCriarCnpjValido() {
        String cnpjValido = "12.345.678/0001-95";
        Cnpj cnpj = CnpjNumeric.create(cnpjValido);
        assertThat(cnpj).isNotNull();
        assertThat(cnpj.getValue()).isEqualTo(cnpjValido);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para CNPJ inválido")
    void deveLancarExcecaoParaCnpjInvalido() {
        String cnpjInvalido = "12.345.678/0001-00";
        assertThatThrownBy(() -> CnpjNumeric.create(cnpjInvalido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(cnpjInvalido);
    }

    @Test
    @DisplayName("Deve formatar CNPJ corretamente")
    void deveFormatarCnpjCorretamente() {
        Cnpj cnpj = CnpjNumeric.create("12345678000195");
        assertThat(cnpj.format()).isEqualTo("12.345.678/0001-95");
    }

    @Test
    @DisplayName("Deve limpar caracteres não alfanuméricos")
    void deveLimparCaracteresNaoAlfanumericos() {
        assertThat(Cnpj.strip("12.345.678/0001-95!@#")).isEqualTo("12345678000195");
        assertThat(Cnpj.strip("AB12.34XY/0001-95")).isEqualTo("AB1234XY000195");
        assertThat(Cnpj.strip(null)).isEqualTo("");
        assertThat(Cnpj.strip("!@#$%")).isEqualTo("");
    }
}