# Plano de melhoria da qualidade (Parte 2)

Registrado em 12/11/2026, antes da primeira alteração no código. Issue de origem: #3.

## Ponto de partida

| Item | Valor na baseline |
|---|---|
| Versão | tag `v1.0-baseline`, merge commit `1d92482` (código Java igual ao commit `b36940e`) |
| Ferramenta | SonarCloud, análise automática, perfil Sonar way |
| Reprodução | o projeto `matfsouza_Clinica-Vet` analisou `1d92482` em 12/11/2026 e repetiu os números do projeto `vihtyn_Clinica-Vet` (25/09/2026) |

| Métrica (SonarCloud) | Baseline |
|---|---|
| Linhas de código | 3.507 |
| Confiabilidade | 64 issues, nota E (2 no Java, 1 Blocker) |
| Segurança | 7 issues, nota E (1 Blocker: senha do banco) |
| Manutenibilidade | 6 issues, nota A |
| Duplicação | 8,9% |
| Complexidade cognitiva | 492 (345 em `SistemaClinicaVet`) |
| Cobertura | 0,0%, sem testes |

| Métrica (lizard 1.24) | Baseline |
|---|---|
| Métodos | 137 |
| CCN máximo | 36 (`exibirMenuAnimais`) |
| Métodos com CCN > 15 | 6 |

## Problemas escolhidos

A Parte 1 deixou três prioridades. Escolhemos as duas primeiras.

1. **Funcionalidades quebradas no acesso ao banco.** É o defeito que o usuário sente: a clínica não registra consultas nem tratamentos.
2. **Nenhum teste automatizado.** Foi a falta de teste que deixou o defeito acima chegar à `main`. Sem testes, também não dá para refatorar com segurança.

A terceira prioridade, quebrar o `SistemaClinicaVet` em classes, fica para depois. São 938 linhas de menu com `Scanner`, e mexer nelas sem testes antes repete o erro que a Parte 1 apontou. Ela continua registrada como meta não atingida.

## Plano

| Problema | Evidência da Parte 1 | Ação planejada | Responsável | Indicador | Meta |
|---|---|---|---|---|---|
| Funcionalidades quebradas no acesso ao banco | 6 queries com `consulta.html` (commit `6eadb4f`); `atualizar()` de exótico preenche 3 parâmetros numa query de 2 (java:S2695, Blocker) | Escrever um teste por operação, ver o teste falhar e corrigir as queries (#4) | Matheus (código), Victor (revisão) | Operações quebradas | de 7 para 0, cada uma com teste |
| Nenhum teste automatizado | Cobertura 0,0%; não existe `src/test` | JUnit 5 + JaCoCo + H2 em memória; testes de modelo e de cada DAO (#5); pipeline no GitHub Actions (#6) | Matheus (código), Victor (revisão) | Nº de testes; cobertura de linhas (JaCoCo) | ao menos 25 testes; cobertura ≥ 30% |

Mudança de apoio: os DAOs abrem a conexão com `new ConexaoMySQL()` em 31 pontos. Vamos injetar a conexão pela interface `ConexaoBD` e tirar a senha do código (#7). É essa refatoração que permite testar os DAOs contra o H2.

| Indicador da refatoração | Baseline | Meta |
|---|---|---|
| Pontos com `new ConexaoMySQL()` nos DAOs | 31 | 0 |
| Linhas duplicadas na camada DAO (SonarCloud) | 238 | ≤ 60 |

## Como vamos saber se funcionou

- Operações quebradas: cada teste de consulta e de exótico falha na baseline e passa depois da correção. O pipeline mostra as duas execuções.
- Testes: o relatório do Surefire conta os testes e o JaCoCo mede a cobertura em cada execução do pipeline.
- Refatoração: os testes de caracterização dos DAOs rodam antes e depois, e o SonarCloud mede a duplicação na `main` depois do merge.

## Riscos

- Mexer nos DAOs pode quebrar um fluxo que funciona hoje. Por isso os testes de caracterização entram antes da refatoração.
- A cobertura conta o `SistemaClinicaVet` (938 linhas sem teste). Mesmo testando todos os DAOs, a cobertura total fica perto de 50%. A meta de 30% já considera isso.
- O H2 no modo MySQL aceita quase todo o SQL do projeto, mas não é um MySQL. Um teste que passa no H2 ainda pode falhar num MySQL de verdade, então a revisão confere as queries alteradas.

## Fluxo de cada mudança

issue → branch `tipo/descricao` → commits com `Refs #N` → pull request com checklist → pipeline verde + análise do SonarCloud → revisão de outro integrante → merge → tag `v2.0-parte2` ao final.
