# Service Management Platform

Aplicação de gestão de serviços e atendimentos desenvolvida como um laboratório prático de **DevOps, SRE, Cloud e Observabilidade**.

O projeto simula uma aplicação distribuída composta por múltiplos serviços, mensageria assíncrona, containers, Kubernetes, monitoramento, CI/CD e infraestrutura como código.

> **O objetivo principal não é construir uma aplicação de negócio complexa.**
> O objetivo é demonstrar o ciclo completo de uma aplicação: **desenvolvimento → containerização → deploy → observabilidade → incidentes → recuperação → automação de infraestrutura**.

---

## 📌 Sobre o projeto

A aplicação permite:

* cadastrar e consultar clientes;
* criar e consultar agendamentos/atendimentos;
* validar a existência de um cliente antes da criação de um atendimento;
* publicar eventos utilizando Kafka;
* processar eventos de forma assíncrona;
* registrar notificações;
* expor health checks;
* coletar métricas operacionais;
* executar os serviços em containers;
* realizar deploy em Kubernetes;
* automatizar build e deploy através de Jenkins;
* provisionar infraestrutura AWS utilizando Terraform.

A primeira versão não possui frontend, autenticação complexa, pagamentos ou integração real com e-mail/SMS.

A interação inicial pode ser feita diretamente através da API utilizando `curl`.

---

# 🏗️ Arquitetura

```text
                         ┌──────────────────────┐
                         │      Cliente         │
                         │       curl           │
                         └──────────┬───────────┘
                                    │
                                   HTTP
                                    │
                     ┌──────────────┴──────────────┐
                     │                             │
                     ▼                             ▼
          ┌───────────────────┐        ┌────────────────────┐
          │  Customer Service │        │ Appointment Service│
          │    Spring Boot    │        │    Spring Boot     │
          └─────────┬─────────┘        └──────┬───────┬─────┘
                    │                         │       │
                    ▼                         ▼       │
              ┌───────────┐             ┌───────────┐│
              │ PostgreSQL│             │ PostgreSQL││
              │ customers │             │appointments││
              └───────────┘             └───────────┘│
                                                      │
                                                      │ Kafka
                                                      ▼
                                           ┌────────────────────┐
                                           │ Notification       │
                                           │ Service            │
                                           │ Spring Boot        │
                                           └────────────────────┘


       ┌────────────────────────────────────────────────────────┐
       │                    Observabilidade                     │
       │                                                        │
       │       Prometheus ───────────────► Grafana              │
       │                                                        │
       │       Logs estruturados + métricas + traces            │
       └────────────────────────────────────────────────────────┘


       ┌────────────────────────────────────────────────────────┐
       │                       DevOps                           │
       │                                                        │
       │ Jenkins → Build → Test → Scan → Registry → Deploy     │
       │                                                        │
       │ Terraform → AWS / EKS / ECR / IAM / Network           │
       └────────────────────────────────────────────────────────┘
```

A arquitetura possui três serviços independentes:

### Customer Service

Responsável pelo domínio de clientes.

* Cadastro de clientes
* Consulta por ID
* Consulta por e-mail
* Persistência própria
* Health checks
* Métricas

### Appointment Service

Responsável pelos agendamentos/atendimentos.

Antes de criar um atendimento, consulta o **Customer Service** para verificar se o cliente existe.

Depois de persistir o atendimento, publica um evento no Kafka.

### Notification Service

Consumidor assíncrono dos eventos de atendimento.

Recebe `AppointmentCreated` através do Kafka e registra uma notificação demonstrativa.

Esse serviço não participa da resposta HTTP da criação do atendimento.

---

# 🔄 Fluxo principal

```text
1. Cliente
     │
     │ POST /api/appointments
     ▼
2. Appointment Service
     │
     │ verifica cliente
     ▼
3. Customer Service
     │
     │ cliente encontrado
     ▼
4. Appointment Service
     │
     │ salva atendimento
     ▼
5. PostgreSQL
     │
     │ publica evento
     ▼
6. Kafka
     │
     │ AppointmentCreated
     ▼
7. Notification Service
     │
     │ processa evento
     ▼
8. Notificação registrada
```

O fluxo demonstra duas formas diferentes de comunicação:

**Síncrona**

```text
Appointment Service
        │
        │ HTTP
        ▼
Customer Service
```

**Assíncrona**

```text
Appointment Service
        │
        │ Kafka
        ▼
Notification Service
```

---

# 📁 Estrutura do projeto

```text
service-management-platform/
│
├── services/
│   │
│   ├── customer-service/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   └── resources/
│   │   │   │       └── db/migration/
│   │   │   └── test/
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   ├── appointment-service/
│   │   ├── src/
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   └── notification-service/
│       ├── src/
│       ├── Dockerfile
│       └── pom.xml
│
├── infra/
│   │
│   ├── compose/
│   │   └── docker-compose.yml
│   │
│   ├── observability/
│   │   ├── prometheus.yml
│   │   └── grafana/
│   │
│   ├── kubernetes/
│   │   ├── base/
│   │   └── overlays/
│   │       ├── local/
│   │       └── staging/
│   │
│   └── terraform/
│       ├── modules/
│       │   ├── network/
│       │   ├── eks/
│       │   ├── registry/
│       │   └── iam/
│       │
│       └── environments/
│           └── dev/
│
├── Jenkinsfile
├── Makefile
└── README.md
```

Cada serviço possui seu próprio projeto Maven para manter as fronteiras entre os serviços explícitas.

---

# 🛠️ Tecnologias

| Área                    | Tecnologia             |
| ----------------------- | ---------------------- |
| Linguagem               | Java                   |
| Framework               | Spring Boot            |
| Persistência            | PostgreSQL             |
| Migrações               | Flyway                 |
| Mensageria              | Apache Kafka           |
| Containers              | Docker                 |
| Orquestração            | Kubernetes             |
| Configuração Kubernetes | Kustomize              |
| Observabilidade         | Prometheus + Grafana   |
| Métricas                | Micrometer             |
| Health checks           | Spring Boot Actuator   |
| CI/CD                   | Jenkins                |
| Cloud                   | AWS                    |
| Kubernetes Cloud        | Amazon EKS             |
| Container Registry      | Amazon ECR             |
| IaC                     | Terraform              |
| Testes                  | JUnit / Testcontainers |
| Tracing                 | OpenTelemetry          |
| Controle de versão      | Git                    |

---

# 🚀 Roadmap de implementação

O projeto será desenvolvido de forma incremental.

Cada camada será adicionada somente depois que a anterior estiver funcionando.

```text
Application
    │
    ▼
Tests + Database Migrations
    │
    ▼
Docker
    │
    ▼
Kafka
    │
    ▼
Kubernetes
    │
    ▼
Observability
    │
    ▼
CI/CD
    │
    ▼
AWS / EKS
    │
    ▼
SRE / Incident Response
```

## Fase 1 — Aplicação

Implementação dos serviços:

* Customer Service
* Appointment Service
* PostgreSQL
* REST APIs
* validações
* tratamento de erros

---

## Fase 2 — Testes e migrações

* testes unitários;
* testes de integração;
* Flyway;
* validação do schema;
* Testcontainers.

---

## Fase 3 — Docker

Cada serviço possuirá seu próprio Dockerfile multi-stage.

Características:

* imagem de build separada da imagem de execução;
* JRE enxuta;
* execução sem root;
* porta explícita;
* configuração através de variáveis de ambiente;
* nenhum segredo dentro da imagem.

O ambiente local será executado através do Docker Compose.

```bash
make up
```

Para acompanhar os logs:

```bash
make logs
```

Para parar o ambiente:

```bash
make down
```

---

# 📨 Kafka

Quando um atendimento é criado, o `Appointment Service` publica o evento:

```text
AppointmentCreated
```

No tópico:

```text
appointments.created.v1
```

Exemplo de evento:

```json
{
  "eventId": "uuid",
  "eventType": "AppointmentCreated",
  "eventVersion": 1,
  "occurredAt": "2026-09-30T12:00:00Z",
  "traceId": "...",
  "data": {
    "appointmentId": "uuid",
    "customerId": "uuid",
    "scheduledAt": "2026-10-02T14:00:00Z"
  }
}
```

O `Notification Service` consome esse evento.

O consumidor será **idempotente**, utilizando `eventId` para impedir o processamento duplicado do mesmo evento.

> A primeira implementação não utiliza Transactional Outbox. Essa melhoria será adicionada posteriormente para tratar a possibilidade de uma falha entre a persistência do atendimento e a publicação do evento.

---

# ☸️ Kubernetes

O deploy local será realizado inicialmente utilizando **Kind ou Minikube**.

Namespace:

```text
service-platform
```

Cada aplicação possuirá:

* Deployment;
* Service;
* ConfigMap;
* Secret;
* startupProbe;
* readinessProbe;
* livenessProbe;
* requests e limits de recursos;
* securityContext.

Exemplo conceitual:

```text
Deployment
    │
    ├── Pod
    ├── Pod
    └── Pod
         │
         ▼
      Service
```

Os manifests serão organizados utilizando Kustomize:

```text
kubernetes/
├── base/
└── overlays/
    ├── local/
    └── staging/
```

As imagens utilizadas nos deployments serão identificadas por versões imutáveis, preferencialmente pelo commit SHA.

---

# 📊 Observabilidade

A aplicação será instrumentada utilizando:

* Spring Boot Actuator;
* Micrometer;
* Prometheus;
* Grafana;
* logs estruturados;
* OpenTelemetry em uma etapa posterior.

## Métricas

Serão acompanhados indicadores como:

* taxa de requests;
* taxa de erros;
* latência p50/p95/p99;
* CPU;
* memória;
* quantidade de restarts;
* réplicas disponíveis;
* erros de dependências;
* Kafka consumer lag;
* mensagens processadas;
* agendamentos criados;
* notificações processadas.

---

## Logs

Os serviços produzirão logs estruturados em JSON.

Exemplo:

```json
{
  "timestamp": "2026-10-02T12:00:00Z",
  "level": "INFO",
  "service": "appointment-service",
  "traceId": "abc123",
  "requestId": "req123",
  "message": "Appointment created"
}
```

Dados sensíveis não serão registrados nos logs.

Também serão evitados labels de alta cardinalidade nas métricas, como:

```text
customerId
appointmentId
email
```

---

# 🔭 Distributed Tracing

Como evolução do projeto, será utilizado OpenTelemetry para conectar:

```text
HTTP Request
     │
     ▼
Appointment Service
     │
     ├──── HTTP ────► Customer Service
     │
     └──── Kafka ────► Notification Service
                            │
                            ▼
                           Log
```

O objetivo é conseguir acompanhar uma mesma operação através dos diferentes componentes da aplicação.

---

# 🚨 SRE e Incident Response

O projeto também será utilizado para simular incidentes controlados.

## Incidentes planejados

### 1. Pod encerrado

```bash
kubectl delete pod <pod>
```

Objetivo:

Demonstrar a capacidade do Kubernetes de recriar o pod através do Deployment.

---

### 2. Falha de readiness

Demonstrar como um pod que não está pronto deixa de receber tráfego.

---

### 3. PostgreSQL indisponível

Interromper o banco e observar:

```text
Application
     │
     ▼
Dependency failure
     │
     ▼
Readiness / Metrics
     │
     ▼
Alert
     │
     ▼
Recovery
```

---

### 4. Notification Service indisponível

Criar novos atendimentos enquanto o consumidor está parado.

Resultado esperado:

```text
Appointment Service
        │
        ▼
       Kafka
        │
        ▼
     Backlog
        │
        X
Notification Service
```

Após o serviço retornar:

```text
Kafka backlog
     │
     ▼
Consumer
     │
     ▼
Messages processed
```

---

### 5. Evento inválido

Publicar uma mensagem inválida e demonstrar:

* retry limitado;
* dead-letter;
* ausência de loop infinito.

---

### 6. Release defeituosa

Realizar deploy de uma imagem conhecida como problemática.

Demonstrar:

```text
Deploy
  │
  ▼
Error rate ↑
  │
  ▼
Detection
  │
  ▼
Rollback
  │
  ▼
Previous version
```

---

# 🎯 SLOs do laboratório

Os objetivos iniciais são:

| Indicador                     |   Objetivo |
| ----------------------------- | ---------: |
| Disponibilidade               |        99% |
| p95 de latência               |   < 500 ms |
| Processamento de notificações | 95% < 30 s |

Esses valores são **objetivos para o ambiente de demonstração**, não garantias de produção.

Os alertas serão associados a runbooks contendo:

1. sintoma;
2. evidências;
3. diagnóstico;
4. mitigação;
5. validação da recuperação.

---

# 🔄 CI/CD

O Jenkins será responsável pelo pipeline de entrega.

```text
Git
 │
 ▼
Checkout
 │
 ▼
Tests
 │
 ▼
Static Analysis
 │
 ▼
Integration Tests
 │
 ▼
Build
 │
 ▼
Docker Images
 │
 ▼
Security Scan
 │
 ▼
Registry
 │
 ▼
Deploy
 │
 ▼
Smoke Tests
 │
 ▼
Rollout
```

As imagens serão identificadas pelo commit SHA.

Exemplo:

```text
appointment-service:a83f91c
```

O uso de `latest` não será utilizado como referência de deploy.

Em caso de falha durante o rollout, o pipeline deverá permitir a recuperação para a versão anterior.

---

# ☁️ AWS

Depois que o ambiente local estiver funcionando de maneira repetível, a aplicação será implantada na AWS.

Arquitetura planejada:

```text
                    AWS
                     │
          ┌──────────┴──────────┐
          │                     │
         ECR                   EKS
          │                     │
          │             ┌───────┴───────┐
          │             │               │
          ▼             ▼               ▼
      Container     Customer       Appointment
      Images        Service          Service
                          │
                          ▼
                   Notification
                      Service
```

Dependendo do objetivo e orçamento, PostgreSQL e Kafka poderão utilizar serviços gerenciados.

---

# 🏗️ Terraform

A infraestrutura será organizada em módulos:

```text
terraform/
├── modules/
│   ├── network/
│   ├── eks/
│   ├── registry/
│   └── iam/
│
└── environments/
    └── dev/
```

O Terraform será responsável pelo provisionamento de recursos como:

* VPC;
* subnets;
* rotas;
* EKS;
* node groups;
* ECR;
* IAM;
* security groups;
* access entries;
* outputs;
* state remoto.

Antes de aplicar alterações:

```bash
terraform fmt
terraform validate
terraform plan
```

Os recursos AWS devem ser destruídos ao final dos experimentos para evitar custos desnecessários.

---

# 🔐 Segurança

Alguns princípios utilizados no projeto:

* containers executando como usuário não-root;
* filesystem read-only quando possível;
* capabilities removidas;
* secrets fora das imagens;
* credenciais armazenadas no Jenkins Credential Store;
* IAM com menor privilégio;
* ausência de chaves AWS no código;
* `.env` fora do Git;
* `.env.example` sem valores secretos;
* logs sem dados pessoais desnecessários;
* imagens identificadas por versão imutável.

---

# ▶️ Executando localmente

> Esta seção será atualizada conforme a implementação evoluir.

### Pré-requisitos

* Java LTS
* Maven
* Docker
* Docker Compose
* Git

Para as etapas posteriores:

* Kind ou Minikube
* kubectl
* Helm, caso necessário
* Jenkins
* Terraform
* AWS CLI

---

## Clone

```bash
git clone <repository-url>

cd service-management-platform
```

---

## Subir ambiente local

```bash
make up
```

Verificar os containers:

```bash
docker compose ps
```

Visualizar logs:

```bash
make logs
```

---

# 🧪 Testando a API

## Criar cliente

```bash
curl -X POST http://localhost:<port>/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "+55 11 99999-9999"
  }'
```

## Consultar cliente

```bash
curl http://localhost:<port>/api/customers/<customer-id>
```

## Criar atendimento

```bash
curl -X POST http://localhost:<port>/api/appointments \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "<customer-id>",
    "description": "Service appointment",
    "scheduledAt": "2026-10-02T14:00:00Z"
  }'
```

## Consultar atendimento

```bash
curl http://localhost:<port>/api/appointments/<appointment-id>
```

> Os endpoints e portas serão atualizados neste README conforme a implementação for concluída.

---

# ❤️ Health Checks

Os serviços Spring Boot utilizarão Actuator.

Exemplos:

```text
/actuator/health/liveness
/actuator/health/readiness
/actuator/prometheus
```

A diferença entre os probes é importante:

### Liveness

Verifica se o processo está vivo.

Não deve depender diretamente de PostgreSQL ou Kafka para evitar reinicializações em cascata durante uma falha de dependência.

### Readiness

Indica se a instância está pronta para receber tráfego.

### Startup

Protege aplicações que precisam de mais tempo para inicializar.

---

# 📚 Decisões arquiteturais

Algumas decisões importantes do projeto:

### Por que três serviços?

Para demonstrar diferentes padrões de comunicação e responsabilidades:

```text
Customer Service
       │
       │ HTTP
       ▼
Appointment Service
       │
       │ Kafka
       ▼
Notification Service
```

### Por que cada serviço possui seu próprio banco/schema?

Para manter ownership dos dados e evitar acoplamento através de tabelas compartilhadas.

### Por que não começar diretamente no EKS?

Porque o ambiente local permite validar cada camada isoladamente antes de adicionar complexidade e custo de cloud.

### Por que Kafka?

Para demonstrar comunicação assíncrona, consumer groups, retries, backlog, lag e recuperação de consumidores.

### Por que Kubernetes?

Para demonstrar conceitos de deployment, scheduling, probes, scaling, service discovery, configuração e recuperação automática.

### Por que Terraform?

Para transformar a infraestrutura em código versionado, revisável e reproduzível.

---

# 🔮 Evoluções planejadas

Após a implementação principal, algumas evoluções poderão ser adicionadas:

* Transactional Outbox;
* OpenTelemetry;
* distributed tracing;
* dead-letter topic;
* HPA;
* NetworkPolicy;
* RDS PostgreSQL;
* Kafka gerenciado;
* melhoria dos dashboards;
* testes de carga;
* chaos experiments controlados;
* melhoria dos runbooks.

Essas funcionalidades não fazem parte do primeiro incremento para evitar adicionar complexidade antes de dominar o fluxo principal.

---

# 🚫 Fora do escopo inicial

Não fazem parte da primeira versão:

* frontend completo;
* gateway dedicado;
* autenticação complexa;
* múltiplos bancos por serviço;
* service mesh;
* multi-região;
* Kubernetes Operators;
* Kafka altamente disponível operado manualmente;
* automação de produção.

Novos componentes serão adicionados somente quando houver um objetivo técnico ou de aprendizagem claro.

---

# ✅ Definition of Done

O projeto será considerado concluído quando:

* [ ] aplicação funciona localmente;
* [ ] Customer Service implementado;
* [ ] Appointment Service implementado;
* [ ] Notification Service implementado;
* [ ] PostgreSQL configurado;
* [ ] Flyway configurado;
* [ ] testes unitários implementados;
* [ ] testes de integração implementados;
* [ ] Docker Compose funcional;
* [ ] Kafka funcionando;
* [ ] consumidor idempotente;
* [ ] Kubernetes local funcional;
* [ ] probes configuradas;
* [ ] recursos e segurança configurados;
* [ ] Prometheus coletando métricas;
* [ ] Grafana com dashboards;
* [ ] logs estruturados;
* [ ] incidentes reproduzíveis documentados;
* [ ] runbooks criados;
* [ ] Jenkins executando CI/CD;
* [ ] imagens versionadas por SHA;
* [ ] deploy automatizado;
* [ ] rollback documentado;
* [ ] Terraform funcional;
* [ ] infraestrutura AWS provisionável;
* [ ] aplicação executando no EKS;
* [ ] procedimento de teardown documentado;
* [ ] README permite reproduzir o projeto do zero.

---

# 🎬 Demonstração

A apresentação do projeto seguirá aproximadamente este fluxo:

```text
1. Arquitetura
      ↓
2. Criar cliente
      ↓
3. Criar atendimento
      ↓
4. Evento Kafka
      ↓
5. Notification Service
      ↓
6. Dashboard Grafana
      ↓
7. Parar consumidor
      ↓
8. Demonstrar backlog
      ↓
9. Recuperar consumidor
      ↓
10. Deploy via Jenkins
      ↓
11. Demonstrar Kubernetes
      ↓
12. Mostrar Terraform / AWS
      ↓
13. Simular incidente
      ↓
14. Recovery / Rollback
```

A demonstração deve conseguir evidenciar pelo menos:

* uma chamada HTTP;
* uma comunicação entre serviços;
* um evento Kafka;
* uma métrica;
* um deploy;
* uma falha controlada;
* uma recuperação.

---

# 📖 Objetivo de aprendizagem

Este projeto foi construído para praticar e demonstrar conhecimentos em:

```text
Java / Spring Boot
        │
        ▼
REST APIs
        │
        ▼
Microservices
        │
        ▼
Docker
        │
        ▼
Kafka
        │
        ▼
Kubernetes
        │
        ▼
Observability
        │
        ▼
CI/CD
        │
        ▼
Terraform
        │
        ▼
AWS / EKS
        │
        ▼
SRE / Incident Response
```

O foco é compreender não apenas **como desenvolver a aplicação**, mas principalmente **como operar, observar, automatizar, diagnosticar e recuperar um sistema distribuído**.

---

## Status

🚧 **Em desenvolvimento**

O projeto está sendo implementado incrementalmente, seguindo as fases descritas neste documento.
