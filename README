# Notify RabbitMQ

Neste projeto foram desenvolvidos dois microserviços que se comunicam via requisições HTTP e utilizam um serviço de mensageria (RabbitMQ) para comunicação. Os microserviços foram desenvolvidos utilizando a linguagem de programação Java.

## Requisitos

1. **Microserviço 1**: Responsável por receber requisições HTTP no endpoint `POST /pagar`.
2. **Microserviço 2**: Recebe requisições HTTP do Microserviço 1 no endpoint `POST /notificar` e posteriormente envia uma mensagem para o tópico de mensageria "notify_rabbitmq" gerenciado pelo serviço RabbitMQ.
3. **Serviço de Mensageria**: Foi utilizado o RabbitMQ como serviço de mensageria.
4. **Dockerfile**: Cada microserviço tem seu próprio Dockerfile para fazer as suas respectivas pré-instalações.

## Referências

- [Rabbit MQ vs PubSub](https://engineering.3ap.ch/post/rabbitmq-vs-pubsub-part-2/)
- [GC Rabbit MQ](https://cloud.google.com/stackdriver/docs/solutions/agents/ops-agent/third-party/rabbitmq?hl=pt-br)
- [Como subir AWS localmente](https://thomasdacosta.com.br/2023/02/16/spring-boot-localstack-usando-o-aws-sqs/)

## Como Executar

1. Clone o repositório:
    ```bash
    git clone https://github.com/MontebellerZ/notify_rabbitmq_java.git
    ```

2. Certifique-se de ter o Docker e o Docker-compose instalados em sua máquina.

3. Certifique-se de ter o RabbitMQ instalado em sua máquina: [RabbitMQ Downloads](https://www.rabbitmq.com/docs/download)

4. Navegue até o diretório raiz de cada microserviço do projeto.

5. Execute o seguinte comando para construir e cada microserviço:
    ```bash
    docker-compose up
    ```

6. Após a execução bem-sucedida, os serviços estarão em execução e prontos para uso.

## Como utilizar

Para a utilização correta dos serviços desenvolvidos, é necessário que todos estejam rodando, cada um em portas diferentes (pré-definidas como 3000 para o serviço 1 e 3001 para o serviço 2). Um serviço adicional receptor foi desenvolvido para visualizar as mensagens que chegam pelo RabbitMQ, porém não é necessário para o funcionamento dos serviços.

Com os serviços online, apenas é necessário gerar uma requisição HTTP do tipo POST para o link de endpoint padrão ``http://localhost:3000/pagar``. Caso esteja rodando o serviço em um domínio diferente do seu ambiente local, basta substituir o domínio no link.

#### Executa o serviço de pagamento
```http
  POST /pagar
```

| Parâmetro   | Tipo     | Descrição                               |
| :---------- | :------- | :-------------------------------------- |
| `paymentId` | `int`    | **Obrigatório**. ID da conta a ser paga |
| `payer`     | `string` | **Obrigatório**. Nome do pagador        |
| `value`     | `float`  | **Obrigatório**. Valor a ser pago       |