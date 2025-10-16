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
import io.github.robsonkades.cnpj.CNPJ;

public class Main {
    public static void main(String[] args) {
        try {
            // Criar um CNPJ numérico válido
            CNPJ numericCnpj = CNPJ.of("12345678000195");
            System.out.println("CNPJ Numérico: " + numericCnpj.getValue());
            System.out.println("CNPJ Formatado: " + numericCnpj.getValueWithMask());
            System.out.println("Raiz do CNPJ: " + numericCnpj.getRoot());
            System.out.println("Representação: " + numericCnpj);

            // Criar um CNPJ alfanumérico (apenas para demonstração, não padrão no Brasil)
            CNPJ alphaCnpj = CNPJ.of("12ABC34501DE35");
            System.out.println("CNPJ Alfanumérico: " + alphaCnpj.getValue());
            System.out.println("CNPJ Alfanumérico Formatado: " + alphaCnpj.getValueWithMask());
            System.out.println("Raiz do CNPJ: " + alphaCnpj.getRoot());
            System.out.println("Representação: " + alphaCnpj);

            // Tentar criar um CNPJ inválido (deve lançar exceção)
            CNPJ invalidCnpj = CNPJ.of("12345678000100");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: CNPJ inválido - " + e.getMessage());
        }
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

This project is licensed under the [Apache 2.0 license.](https://www.apache.org/licenses/LICENSE-2.0.html)

## Contributing

Contributions, bug reports, and feature requests are always welcome! Please see [CONTRIBUTING.md](./CONTRIBUTING.md) for more details.