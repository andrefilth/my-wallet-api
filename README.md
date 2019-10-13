# AME Wallet API
<TODO>

## Documentação da API
A documentação da API segue está disponível no formato [OAS3 (OpenAPI Specification 3)](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md). Logo é possível utilizar diversar ferramentas para se ler e ter acesso as informações segue alguns exemplos:

### Swagger-UI
```
docker run --rm -p 9580:8080 -e API_URL=https://s3.amazonaws.com/bpay-docs/ame-wallet-api/openapi.yaml swaggerapi/swagger-ui
```

### Redoc
<TODO>

### Swagger Editor
Para se editar a documentação é redocumentado usar o [Swagger Editor](https://editor.swagger.io/) você pode execula-lo localmente em um container docker:
```
docker run --rm -d -p 9581:8080 swaggerapi/swagger-editor
```

## Integração com SonarQube
Para realizar o scan do projeto e enviar as metricas de qualidade para o sonar basta executar o comando abaixo:

```
./gradlew sonarqube
```

```
./gradlew sonarqube -Dsonar.host.url=http://localhost:9000 -Dsonar.login=8b6936e6993ca3f21b84d9bfc98fb4ebd4614ad5

```

### Startar Sonar Server (Lcocalhost)
```
docker run -d --name sonarqube -p 9000:9000 -p 9092:9092 sonarqube
```

Onde **sonar.host.url** representa o endereço do SonarQube e **sonar.login** a chave de acesso. Para se obter a chave de acesso deve solicitar ao responsável pelo SonarQube.


Testes com cartão de crédito
                amount
Autorizado   < 1k
Unauthorized < 10k
ERROR_TO_AUTHORIZE < 100k
Unexpected Error > 100k

CAPTURE < 900
ERROR_TO_CAPTURE < 100k
Unexpected Error > 100k

CANCELED < 900
RESUFED_CANCELED < 100k
ERROR_TO_CANCEL > 100k
TODO fazer o Unexpected Error
teste