# *Gerenciamento de Alunos:*
- Uma aplicação em spring, em que consiste ser um gerenciamento acadêmico de Alunos e professores:
-  Gerenciamento de Professores
-  Gerenciamento de Disciplinas
-  Diário de Turma:
-  Sistema de Presenças:
-  Validação de Dados

**para rodar o projeto, necessário rodar um comando no docker, para subir o banco, dps é só executar a aplicação java em http://localhost:8080/**

```sh docker run --name siga-postgres -e POSTGRES_DB=siga_db -e POSTGRES_USER=siga_user -e POSTGRES_PASSWORD=siga_password -p 5432:5432 -d postgres```

Jdk 17
