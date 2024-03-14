<p align="center">
    <img src="https://raw.githubusercontent.com/clagomess/pirilampo/master/gui/src/main/resources/favicon.svg" width="64" alt="Logo">
</p>

<p align="center">
    <a href="https://github.com/clagomess/pirilampo/releases/latest">
        <img src="https://img.shields.io/github/release/clagomess/pirilampo.svg?maxAge=2592000" alt="GitHub Release">
    </a>
</p>

# Pirilampo

The open source living documentation generator of features files writen in 
[Gherkin](https://cucumber.io/docs/gherkin/reference/) and Markdown support.

## Installation
- Java JRE >= 8
- Check: [All releases](https://github.com/clagomess/pirilampo/releases/latest)

| Platform             | Download                                                                                                           |
|----------------------|--------------------------------------------------------------------------------------------------------------------|
| Windows x64          | [pirilampo-gui-2.0.0.exe](https://github.com/clagomess/pirilampo/releases/download/v2.0.0/pirilampo-gui-2.0.0.exe) |
| Any (Executable JAR) | [pirilampo-gui-2.0.0.jar](https://github.com/clagomess/pirilampo/releases/download/v2.0.0/pirilampo-gui-2.0.0.jar) |
| CLI                  | [pirilampo-cli-2.0.0.jar](https://github.com/clagomess/pirilampo/releases/download/v2.0.0/pirilampo-cli-2.0.0.jar) |

## GUI running

[//]: # (@TODO: change)
<img src="https://raw.githubusercontent.com/clagomess/pirilampo/master/readme_assets/img_01.png" width="400">
 

## CLI

```
java -jar pirilampo-cli-*.jar -projectSource /foo/bar/features
```

Available options:

| Option              | Description                                                                        |
|---------------------|------------------------------------------------------------------------------------|
| compilationType     | Compilation Type. Expected values: [FOLDER, FOLDER_DIFF, FEATURE]. Default: FOLDER |
| compilationArtifact | Compilation Artifact. Expected values: [HTML, PDF]. Default: HTML                  |
| projectSource       | Folder or *.feature                                                                |
| projectMasterSource | Folder to compare                                                                  |
| projectTarget       | Target Folder                                                                      |
| projectName         | Project Name. Default: Pirilampo                                                   |
| projectVersion      | Project Version. Default: 1.0                                                      |
| projectLogo         | Image file for logo                                                                |
| menuColor           | Menu Color. Default: #14171A                                                       |
| menuTextColor       | Menu Text Color. Default: #DDDDDD                                                  |
| htmlPanelToggle     | Panel Toggle. Expected values: [CLOSED, OPENED]. Default: OPENED                   |
| disableEmbedImages  | Disable Emded Images?                                                              |
| layoutPdf           | Layout PDF. Expected values: [PORTRAIT,LANDSCAPE]. Default: PORTRAIT               |

## Sample
### Folder structure
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

#### Compilation type/artifact

| Feature/HTML                                                                                | Folder/HTML                                                                                 | Feature/PDF                                                                                 |
|---------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------|
| ![a](https://raw.githubusercontent.com/clagomess/pirilampo/master/readme_assets/img_02.png) | ![b](https://raw.githubusercontent.com/clagomess/pirilampo/master/readme_assets/img_03.png) | ![c](https://raw.githubusercontent.com/clagomess/pirilampo/master/readme_assets/img_04.png) |
