# Order Service

Este projeto é um microserviço Spring Boot responsável por receber mensagens de pedidos via RabbitMQ, acumular as
mensagens em um buffer e processá-las em lote. O processamento do lote ocorre de duas formas:

- **Por quantidade:** Quando o número de mensagens atinge um limite (ex.: 10 mensagens).
- **Por tempo:** Se a mensagem mais antiga do lote permanecer sem ser processada por mais de x tempo, um agendador
  processa o lote a cada x minutos.

## Funcionalidades

- **Recepção de mensagens:** Utiliza o RabbitMQ para receber mensagens no formato JSON contendo dados de pedidos.
- **Acúmulo e processamento em lote:** Armazena as mensagens em um buffer sincronizado e processa o lote quando atingir
  um tamanho definido ou ultrapassar o tempo máximo de espera.
- **Persistência dos pedidos:** Converte as mensagens recebidas para o formato de `Order` e persiste os dados utilizando
  um repositório (exemplo: MongoDB).
- **Consulta de pedidos via API:** Disponibiliza endpoints REST para consulta de pedidos, que podem ser testados via
  Swagger.

## Tecnologias Utilizadas

- Java 17
- Spring Boot
- Spring AMQP (RabbitMQ)
- Spring Scheduling
- Spring WebFlux (Reativo)
- Maven
- Lombok
- Swagger (para documentação e testes da API)

## Pré-requisitos

- **Java 17** instalado.
- **Maven** instalado.
- **Docker** instalado.

## Configuração e Execução

### 1. Iniciar os Serviços com Docker Compose

O projeto inclui um arquivo `docker-compose.yml` que inicia os containers necessários, como o MongoDB e o RabbitMQ.
Certifique-se de ter o Docker instalado e execute:

```bash
docker-compose up -d
```

Isso irá iniciar os containers do MongoDB e do RabbitMQ. O RabbitMQ estará disponível na porta `5672` para conexões e a
interface de gerenciamento na porta `15672`.  
Acesse a interface do RabbitMQ em: [http://localhost:15672](http://localhost:15672)  
Credenciais padrão: `guest` / `guest`

### 2. Clonar e Construir o Projeto

Clone o repositório:

```bash
git clone https://github.com/seu-usuario/nome-do-repositorio.git
cd nome-do-repositorio
```

Construa o projeto com Maven:

```bash
mvn clean install
```

### 3. Executar a Aplicação

Inicie a aplicação Spring Boot:

```bash
mvn spring-boot:run
```

A aplicação estará configurada para:

- Receber mensagens na fila definida em `RabbitConfig.QUEUE_ORDERS`.
- Expor os endpoints REST para consulta de pedidos.
- Gerar a documentação da API via Swagger.

## Enviando Mensagens para a Fila

Envie mensagens no formato JSON para a fila do RabbitMQ. Exemplo de payload:

```json
{
  "idExternal": "ORD123",
  "items": [
    {
      "productId": "PROD001",
      "quantity": 2,
      "price": 15.99,
      "productName": "Produto Exemplo 1"
    },
    {
      "productId": "PROD002",
      "quantity": 1,
      "price": 25.50,
      "productName": "Produto Exemplo 2"
    }
  ]
}
```

Você pode utilizar o **RabbitMQ Management UI** ou a ferramenta **rabbitmqadmin** para publicar as mensagens.

## Consulta de Pedidos e Teste com Swagger

A API disponibiliza endpoints para consulta dos pedidos:

### Endpoints

1. **GET /orders?status=STATUS**

    - **Descrição:** Busca pedidos filtrados pelo status (ex.: `OPENED`, `CALCULATED`, etc.) e retorna uma lista
      contendo os campos básicos: `idExternal`, `status` e `total`.
    - **Exemplo de uso:**  
      Acesse o endpoint via Swagger e informe o parâmetro `status`.  
      Por exemplo: `/orders?status=OPENED`

2. **GET /orders/{idExternal}**

    - **Descrição:** Busca um pedido específico pelo seu `idExternal`, retornando todos os detalhes e itens do pedido.
    - **Exemplo de uso:**  
      No Swagger, informe o `idExternal` desejado na URL (por exemplo: `/orders/ORD123`).

### Testando no Swagger

Após iniciar a aplicação, acesse a interface do Swagger para visualizar e testar os endpoints:

- **URL do Swagger:**  
  [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

Na interface do Swagger, você poderá:

- Visualizar a documentação gerada automaticamente pelas anotações (`@ApiOperation`, `@ApiResponses`, etc.).
- Testar os endpoints diretamente, informando parâmetros e visualizando as respostas retornadas pela API.

## Lógica de Processamento

- **Batch por quantidade:** Quando o buffer acumula 10 mensagens, o lote é processado imediatamente.
- **Batch por tempo:** Um agendador verifica a cada 2 minutos se a mensagem mais antiga do lote já está aguardando por
  mais de 1 hora. Se sim, o lote é processado, mesmo que o número de mensagens seja inferior a 10.
