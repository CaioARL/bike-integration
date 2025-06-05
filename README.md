# bike-integration

API de integração para gerenciamento do APP principal (Bicity).

## Descrição

Este projeto é uma API desenvolvida em Java utilizando o Maven para gerenciamento de dependências.
A API é responsável por integrar e gerenciar as funcionalidades do aplicativo principal (Bicity).

## Tecnologias Utilizadas

- Java
- Maven
- Spring Boot
- JPA/Hibernate
- Banco de Dados Relacional (MySQL)
- AWS S3

## Requisitos

- Java 11 ou superior
- Maven 3.6.0 ou superior

## Configuração do Ambiente

1. Clone o repositório:
   ```sh
   git clone https://github.com/seu-usuario/bike-integration.git
   ```
2. Navegue até o diretório do projeto:
   ```sh
   cd bike-integration
   ```
3. Crie e configure o arquivo `.env` com as informações necessárias:

   ```env
   # Configuração do JWT
   JWT_ACCESS_KEY=
   JWT_SECRET_KEY=
   JWT_EXPIRATION=

   # Chave extra para integração ou controle
   X_ACCESS_KEY=

   # Chave para descriptografia
   DECRYPTION_KEY=

   # Configuração da AWS e Cloud
   AWS_REGION=
   AWS_PROFILE=
   AWS_ENDPOINT=
   AWS_S3_BUCKET_NAME=

   # Configuração dos níveis de Log
   LOGGING_LEVEL_ROOT=INFO
   LOGGING_LEVEL_ORG_APACHE_CATALINA=INFO
   LOGGING_LEVEL_ORG_APACHE_HTTP=INFO
   LOGGING_LEVEL_ORG_APACHE_TOMCAT=INFO
   LOGGING_LEVEL_ORG_HIBERNATE=INFO
   LOGGING_LEVEL_ORG_SPRINGFRAMEWORK=INFO

   # Configurações do Servidor
   SERVER_PORT=8080
   SERVER_SERVLET_CONTEXT_PATH=/bike-integration

   # Configuração do Springdoc (Swagger e documentação da API)
   SPRINGDOC_API_DOCS_PATH=/api-docs
   SPRINGDOC_SWAGGER_UI_PATH=/swagger-ui-custom.html
   SPRINGDOC_PACKAGES_TO_SCAN=br.edu.ifsp.spo.bike_integration.controller,br.edu.ifsp.spo.bike_integration.rest.controller
   SPRINGDOC_SWAGGER_UI_TRY_IT_OUT_ENABLED=true
   SPRINGDOC_SWAGGER_UI_OPERATIONS_SORTER=method
   SPRINGDOC_SWAGGER_UI_TAGS_SORTER=alpha
   SPRINGDOC_SWAGGER_UI_FILTER=true
   SPRINGDOC_SWAGGER_UI_PERSIST_AUTHORIZATION=true

   # Configurações do Jackson para manipulação de datas
   SPRING_JACKSON_SERIALIZATION_WRITE_DATES_AS_TIMESTAMPS=false
   SPRING_JACKSON_DESERIALIZATION_ADJUST_DATES_TO_CONTEXT_TIME_ZONE=true

   # Configuração do DataSource (MySQL)
   DB_HOST=
   DB_PORT=
   DB_NAME=
   DB_USER=
   DB_PASSWORD=
   DB_SCHEMA=

   # Configurações do JPA e do Hibernate
   SPRING_JPA_SHOW_SQL=true
   SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL=true

   # Configuração do Spring Mail para envio de e-mails
   SPRING_MAIL_HOST=smtp.gmail.com
   SPRING_MAIL_PORT=587
   SPRING_MAIL_USERNAME=
   SPRING_MAIL_PASSWORD=
   SPRING_MAIL_PROPERTIES_MAIL_TRANSPORT_PROTOCOL=smtp
   SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
   SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
   SPRING_MAIL_PROPERTIES_MAIL_DEBUG=true

   # Configuração dos Endpoints de gerenciamento (Actuator)
   MANAGEMENT_ENDPOINTS_WEB_BASE_PATH=
   MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics,loggers,heapdump,threaddump

   # Configuração do Dotenv para carga das variáveis de ambiente
   DOTENV_ENABLED=true
   DOTENV_SYSTEM_PROPERTIES=true
   ```

## Compilação e Execução

Para compilar e executar o projeto, utilize os seguintes comandos:

1. Compile o projeto:

   ```sh
   mvn clean package -DskipTests
   ```

2. Execute a aplicação:
   ```sh
   mvn spring-boot:run
   ```

## Endpoints

A API utiliza Swagger para mapeamento dos endpoints

```url
localhost:8080/bike-integration/swagger-ui/index.html
```

## Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para mais detalhes.
