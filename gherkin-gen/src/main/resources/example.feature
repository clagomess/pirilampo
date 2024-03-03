# language: pt
# encoding: utf-8
Funcionalidade: HU01.007 - Empresa
  Com o usuário que possui o perfil <assinante>

  Contexto:
    Dado que o usuário acessa o "Menu > Empresa", o sistema apresenta o cenário CN01.007.001

  Cenário: CN01.007.008 - Aba - Departamento > Manter
    Dado que o usuário acionou o botão [Novo] ou [Editar] na coluna de "Ações" no cenário CN01.007.004
    Então é apresentado uma modal
    E é apresentado os seguintes campos
      | Campo              | Exemplo       | Obrigatório? | Definição                          |
      | Sigla              | FNDE          | S            | Tam. 20                            |
      | Nome               | Alocação FNDE | S            | Tam. 200                           |
      | Gestor Responsável | João da Silva | N            | RN01.007.001                       |
      | Ativo?             | SIM           | S            | boolean; default SIM; RN01.007.002 |
    Mas sendo que o campo "Ativo?" só será apresentado quando for uma alteração do registro
    Quando o usuário acionar o botão [Cadastrar] ou [Alterar]
    Então o sistema aplica a regra RN01.007.002
    E aplica a regra [DEF003](#/scenario/01-APLICACAO_HU01.001-definicoes/3) quando for novo registro
    E aplica a regra [DEF004](#/scenario/01-APLICACAO_HU01.001-definicoes/4) e a RN01.007.003 quando for alteração do registro
    E persiste o registro

  Cenário: CN01.007.009 - Aba - Departamento > Realocar Funcionários
    Dado que o usuário acione o botão [Realocar Funcionários] na coluna de ações no cenário CN01.007.004
    Então é apresentado uma modal
    E apresenta a combo <Novo Departamento> conforme RN01.007.004
    Quando o usuário acionar o botão <Confirmar>
    Então é apresentado uma mensagem de confirmação, conforme regra RN01.007.005:
    """
    Deseja realmente alocar os 30 funcionários do departamento "FNDE" para o(a) "FUNASA"?
    """
    Quando o usuário confirmar a mensagem de confirmação através do botão <Confirmar>
    Então o sistema irá persistir a realocação

  Dado XXX
    E Teste
      | Ibagem |
      | ![Image](xxx.png) |
      | <img src="xxx.png"> |
      | <img src="xxx.png" width="50"> |
      | ![Image](https://pt.wikipedia.org/static/images/project-logos/ptwiki.png) |
      | Link Html Embeded: [Link Embeded](html_embed.html) |
      | Link Google: [Google](https://www.google.com.br) |
      | <strike>strike</strike> |
      | <strike>strike<br>strike</strike> |


  Esquema do Cenário: JJJ
    Quando xxx
    E YYY
    Exemplos:
      | a | b |
      | c | d |
