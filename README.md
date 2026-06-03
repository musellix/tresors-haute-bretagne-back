# Les Trésors de Haute Bretagne - Backend

Backend Spring Boot pour l'application mobile et web "Les Trésors de Haute Bretagne".

## Stack

- **Framework**: Spring Boot 3.2
- **Database**: PostgreSQL
- **Java**: 17
- **Build**: Maven
- **Container**: Docker

## Structure

```
├── src/
│   ├── main/
│   │   ├── java/com/tresorshautebretagne/
│   │   │   └── TresorsApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── pom.xml
├── Dockerfile
└── README.md
```

## Développement Local

### Avec Docker Compose (recommandé)

```bash
# À la racine du projet
docker-compose up -d
```

### En local

```bash
mvn spring-boot:run
```

L'API sera disponible sur `http://localhost:8080/api`

## Configuration

Variables d'environnement (Docker):
- `DATABASE_HOST`: localhost (default)
- `DATABASE_PORT`: 5432 (default)
- `DATABASE_USER`: postgres (default)
- `DATABASE_PASSWORD`: postgres (default)

## Build

```bash
mvn clean package
```

## Docker

```bash
docker build -t tresors-backend .
docker run -p 8080:8080 tresors-backend
```
