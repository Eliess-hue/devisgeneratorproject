# Générateur de Devis — Backend

[← Retour au README principal](../README.md)

API REST développée avec Spring Boot pour la génération et la gestion de devis.

## Prérequis
* Java 21
* Maven (le wrapper `./mvnw` est fourni, pas besoin de l'installer globalement)
* Une instance PostgreSQL accessible (locale ou distante)

## Configuration

L'application utilise le profil **`dev`** par défaut (`spring.profiles.active=dev`).

Variables d'environnement requises : voir [.env.example](../.env.example) à la racine du projet.

En développement, l'envoi d'e-mails passe par SMTP (`spring-boot-starter-mail`, ex : Mailtrap). En production, il passe par l'API [Brevo](https://www.brevo.com) — voir le profil `prod`.

Les migrations de base de données sont gérées par **Flyway** et s'exécutent automatiquement au démarrage.

## Lancer en local

```bash
export DB_URL=jdbc:postgresql://localhost:5432/devis_db
# ... (voir .env.example à la racine pour toutes les variables)
./mvnw spring-boot:run
```

L'API est alors disponible sur `http://localhost:8080`.

## Tests

```bash
./mvnw test
```

Les tests unitaires utilisent **Mockito**. Les tests d'intégration (`AuthController`, `ClientController`, `QuoteController`, `UserController`) s'exécutent via **MockMvc** avec le profil `test`, sans dépendance externe supplémentaire (pas de Testcontainers ici).

## Documentation API

Une fois l'application lancée, la documentation Swagger UI est disponible sur :

http://localhost:8080/swagger-ui/index.html


Le contrat OpenAPI brut (JSON) est accessible sur `http://localhost:8080/v3/api-docs`.

## Supervision

Spring Boot Actuator expose des informations sur l'état de l'application :

http://localhost:8080/actuator/health