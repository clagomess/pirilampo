<p align="center">
    <img src="https://raw.githubusercontent.com/clagomess/pirilampo/master/src/main/resources/img_01.png" width="64">
</p>

<p align="center">
    <a href="https://github.com/clagomess/pirilampo/releases/latest"><img src="https://img.shields.io/github/release/clagomess/pirilampo.svg?maxAge=2592000" alt="GitHub Release"></a>
    <a href="https://github.com/clagomess/pirilampo/actions"><img src="https://img.shields.io/github/workflow/status/clagomess/pirilampo/Java CI" alt="GitHub Workflow"></a>
</p>

## Sobre
Pirilampo é um sistema gerador de documentação com base features escritos na linguagem Gherkin ([Cucumber](https://cucumber.io/docs/reference)) e com suporte a markedown ([CommonMark](http://commonmark.org/)).

## Instalação
 - Java JRE 8
 - Download: <a href="https://github.com/clagomess/pirilampo/releases/latest">https://github.com/clagomess/pirilampo/releases/latest</a>
 
## Imagem do Sistema
<img src="https://raw.githubusercontent.com/clagomess/pirilampo/master/readme_assets/img_01.png" width="400">
 
## Exemplos
### Estrutura de pasta
```
doc_exemplo/
├── feature/
│   └── Independência do Brasil/
│       └── 001 - Introdução.feature
└── html/
```

#### Feature: 001 - Introdução.feature
```feature
# language: pt
# encoding: utf-8
Funcionalidade: Introdução
  **Independência do Brasil** é um processo que se estende de 1821 a 1825 e coloca em violenta oposição o [Reino do Brasil](https://pt.wikipedia.org/wiki/Reino_do_Brasil) e o Reino de Portugal, dentro do Reino Unido de Portugal, Brasil e Algarves. As Cortes Gerais e Extraordinárias da Nação Portuguesa, instaladas em 1820, como uma consequência da Revolução Liberal do Porto, tomam decisões, a partir de 1821, que tinham como objetivo reduzir novamente o Brasil ao seu antigo estatuto colonial.

  Contexto:
  Dado Antecedendo o processo de independência do Brasil, mas com fortes influências sobre o mesmo, ocorre a transferência da corte portuguesa para o Brasil.

  | Proclamação da Independência                                                                                                              |
  | ![Image](https://upload.wikimedia.org/wikipedia/commons/thumb/4/40/Independence_of_Brazil_1888.jpg/320px-Independence_of_Brazil_1888.jpg) |
```

Html Individual | Html Múltiplas Feature | PDF Feature
--------------- | ---------------------- | -----------
![a](https://raw.githubusercontent.com/clagomess/pirilampo/master/readme_assets/img_02.png) | ![b](https://raw.githubusercontent.com/clagomess/pirilampo/master/readme_assets/img_03.png) | ![c](https://raw.githubusercontent.com/clagomess/pirilampo/master/readme_assets/img_04.png)