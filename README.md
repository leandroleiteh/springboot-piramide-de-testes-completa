<h1 align="center">
  StarWars Planet API (sw-planet-api)
</h1>

<p align="center">
  <a href="#-technologies">Tecnologias</a>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
  <a href="#-project">Projeto</a>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
  <a href="#-configuration">Configuração</a>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
  <a href="#-developing">Construir e Executar</a>
</p>



<br>

## ✨ Technologies

- [Mysql](https://dev.mysql.com/downloads/mysql/)
- [Java](https://www.oracle.com/java/technologies/downloads/)
- [Maven](https://maven.apache.org/download.cgi)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Testing](https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html#testing-introduction)
- [JUnit 5](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito](https://site.mockito.org)
- [AssertJ](https://github.com/assertj/assertj)
- [Hamcrest](http://hamcrest.org/JavaHamcrest/)
- [Jacoco](https://github.com/jacoco/jacoco)
- [Surfire](https://maven.apache.org/surefire/index.html)
- [Failsafe](https://failsafe.dev/)
- [TestcontainersTestcontainers](https://docs.spring.io/spring-boot/reference/testing/testcontainers.html)
- [commons-lang3](https://mvnrepository.com/artifact/org.apache.commons/commons-lang3)




## 💻 Projeto

sw-planet-api é um serviço web que provê dados sobre a franquia de Star Wars, mais especificamente sobre os planetas que aparecem nos filmes.



## 🛠️ Configuração

O projeto requer um banco de dados Mysql, então é necessário criar uma base de dados com os seguintes comandos:

```
$ sudo mysql

CREATE USER 'user'@'%' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON *.* TO 'user'@'%' WITH GRANT OPTION;

exit

$ mysql -u user -p

CREATE DATABASE starwars;

exit
```

Durante os testes, as tabelas de banco já serão criadas automaticamente no banco de dados.

## 🚀 Construir e Executar

Para construir e testar, execute o comando:

```sh
$ ./mvnw clean verify
```

- [by Leandro Leite](https://www.linkedin.com/in/leandroleite-ti/)


![Pit Test Coverage Report](image-1.png)

![sw-planet-api - Jacoco Coverage](image-2.png)