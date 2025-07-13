# CNPJ

[![Maven Central](https://img.shields.io/maven-central/v/io.github.robsonkades/cnpj)](https://search.maven.org/artifact/io.github.robsonkades/cnpj)  
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)  
[![Build Status](https://github.com/robsonkades/cnpj/actions/workflows/maven.yml/badge.svg)](https://github.com/robsonkades/cnpj/actions)

**CNPJ** é uma biblioteca Java de alto desempenho para **Geração**, **formatação** e **validação** de CNPJs no formato tradicional (numérico) e no novo formato **alfanumérico** definido pela [Nota Técnica Conjunta 2025.001](https://www.gov.br/receitafederal/).

Ideal para sistemas fiscais, contábeis, ERPs e integrações com SEFAZ.

---

## 📚 Índice

1. [Funcionalidades](#funcionalidades)
2. [Exemplo Rápido](#exemplo-rápido)
3. [Compatibilidade com a NT 2025.001](#compatibilidade-com-a-nt-2025001)
4. [Instalação (Maven/Gradle)](#instalação-mavengradle)
5. [Licença](#licença)
6. [Contribuindo](#contribuindo)

---

## ✅ Funcionalidades

- Geração de CNPJ **numérico** e **alfanumérico (base 36)**
- Validação com cálculo de dígitos verificadores via **Módulo 11**
- Compatível com o novo formato da NT 2025.001
- Formatação e desformatação (strip)
- Sem dependências externas
- Compatível com **Java 8+**

---

## 🚀 Exemplo Rápido

```java
import io.github.robsonkades.Cnpj;
import io.github.robsonkades.CnpjAlphanumeric;
import io.github.robsonkades.CnpjNumeric;
import io.github.robsonkades.cnpj.Cnpj;
import io.github.robsonkades.cnpj.Cnpj.Type;

public class Main {
    public static void main(String[] args) {
        Cnpj cnpj = CnpjNumeric.create("12345678000195");
        System.out.println("Gerado: " + cnpj.getValue());

        Cnpj alpha = CnpjAlphanumeric.create("12.ABC.345/01DE-35");

        String formatado = alpha.format();
        System.out.println("Formatado: " + formatado);
    }
}
```

---

## 🏛️ Compatibilidade com a NT 2025.001

A partir de julho de 2026, a Receita Federal iniciará a emissão de CNPJs com letras e números.
Esta biblioteca já suporta:
- Cálculo de dígitos verificadores usando ASCII - 48
- Identificação de tipo: numérico ou alfanumérico
- Validação automática de qualquer versão
- Formatação no padrão AA.AAA.AAA/AAAA-DV

---

## 📦 Instalação (Maven/Gradle)

### Maven

```xml
<dependency>
  <groupId>io.github.robsonkades</groupId>
  <artifactId>cnpj</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Gradle (Kotlin DSL)

```gradle
implementation("io.github.robsonkades:cnpj:1.0.0")
```

## License

This project is licensed under the MIT License. See [LICENSE](./LICENSE) for details.

## Contributing

Contributions, bug reports, and feature requests are always welcome! Please see [CONTRIBUTING.md](./CONTRIBUTING.md) for more details.