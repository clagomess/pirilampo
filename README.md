# Pirilampo

Pirilampo é um sistema gerador de documentação com base features escritos na linguagem Gherkin ([Cucumber](https://cucumber.io/docs/reference)) e com suporte a markedown ([CommonMark](http://commonmark.org/)).

[![GitHub release](https://img.shields.io/github/release/clagomess/pirilampo.svg?maxAge=2592000)](https://github.com/clagomess/pirilampo/releases/latest)
[![Travis Build](https://travis-ci.org/clagomess/pirilampo.svg?branch=master)](https://travis-ci.org/clagomess/pirilampo)

## Pré-requisito:
 - Java JRE 8
 
## Imagem do Sistema
<img src="https://cloud.githubusercontent.com/assets/9750668/17646018/1c4e2160-618e-11e6-8625-6d0e7298b6ed.jpg" width="400">
 
## Exemplos
### Estrutura de pasta
```
feature/
  Independência do Brasil/
	  001 - Introdução.feature
html/
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
![screenshot- domain date time](https://cloud.githubusercontent.com/assets/9750668/17646023/8c0855de-618e-11e6-9eff-894473aa4b1a.png) | ![screenshot- domain date time _2](https://cloud.githubusercontent.com/assets/9750668/17646025/93b7b43c-618e-11e6-94b9-24e5b12e1aec.png) | ![screenshot- domain date time _3](https://cloud.githubusercontent.com/assets/9750668/17646026/97c24920-618e-11e6-95d8-b65bb965c7b1.png)