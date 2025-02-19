# FAIN_J - Service Facade pour Instanciateurs DDST

## Table des matières
1. [Aperçu](#aperçu)
2. [Architecture](#architecture)
3. [Points d'accès REST](#points-daccès-rest)
4. [Guide d'utilisation](#guide-dutilisation)
5. [Types disponibles](#types-disponibles)
6. [Configuration technique](#configuration-technique)

## Aperçu

FAIN_J est une API facade qui uniformise l'accès aux différents instanciateurs de projets DDST. Le service expose une API REST unifiée permettant de générer des projets à partir de différents starter kits, contrats et bibliothèques.

## Architecture

L'architecture se compose de trois diagrammes principaux :

1. **Diagramme de séquence** - Illustre le flux d'interaction entre les composants :
   ![Diagramme de séquence](/doc/diagrams/sequence-diagram.png)

2. **Diagramme de classes** - Montre la structure des classes du système :
   ![Diagramme de classes](/doc/diagrams/class-diagram.png)

3. **Diagramme de composants** - Présente l'architecture globale du système :
   ![Diagramme de composants](/doc/diagrams/component-diagram.png)

## Points d'accès REST

### Composants (Starter Kits)

#### GET /components/starter-kits
Récupère la liste des types de starter kits disponibles.

**Réponse:** Liste des types supportés (TONIC, HUMAN, STUMP)

#### GET /components/{starter-kit}/features
Retourne la liste des features disponibles pour un type de starter kit.

**Paramètres:**
- `starter-kit` (chemin, requis): Type du starter kit (TONIC, HUMAN, STUMP)

#### GET /components/{starter-kit}
Génère un nouveau projet starter kit.

**Paramètres:**
- `starter-kit` (chemin, requis): Type du starter kit
- `code-applicatif` (query, requis): Code applicatif du composant à générer
- `features` (query, optionnel): Liste des features à inclure

### Contrats

#### GET /contracts/{starter-kit}
Récupère la liste des types de contrats disponibles pour un starter kit.

**Paramètres:**
- `starter-kit` (chemin, requis): Type du starter kit

**Réponse:** Liste des types supportés (OPENAPI, AVRO, SOAP)

#### GET /contracts/{starter-kit}/{contract-type}
Génère un nouveau projet de contrat.

**Paramètres:**
- `starter-kit` (chemin, requis): Type du starter kit
- `contract-type` (chemin, requis): Type du contrat
- `code-applicatif` (query, requis): Code applicatif du composant à générer

### Bibliothèques

#### GET /libraries/starter-kits
Récupère la liste des types de starter kits disponibles pour les bibliothèques.

**Réponse:** Liste des types supportés

#### GET /libraries/{starter-kit}
Génère un nouveau projet de bibliothèque.

**Paramètres:**
- `starter-kit` (chemin, requis): Type du starter kit
- `code-applicatif` (query, requis): Code applicatif du composant à générer

## Guide d'utilisation

### Cas de succès

#### 1. Starter Kit TONIC avec une feature
```http
http://localhost:9000/components/TONIC?code-applicatif=test-project&features=toni-starter-security-authapp-v2-client
```

#### 2. Starter Kit TONIC avec plusieurs features
```http
http://localhost:9000/components/TONIC?code-applicatif=test-project&features=toni-starter-security-authapp-v2-client&features=toni-starter-client-espoir
```

#### 3. Lister les features disponibles pour TONIC
```http
http://localhost:9000/components/TONIC/features
```

#### 4. Créer un contrat OpenAPI pour TONIC
```http
http://localhost:9000/contracts/TONIC/OPENAPI?code-applicatif=my-api
```

### Cas d'erreur

#### 1. Type de starter kit invalide
```http
http://localhost:9000/components/INVALID?code-applicatif=test-project
```
**Réponse:** 400 Bad Request - Type de starter kit non supporté

#### 2. Feature invalide pour TONIC
```http
http://localhost:9000/components/TONIC?code-applicatif=test-project&features=invalid-feature
```
**Réponse:** 400 Bad Request - Feature non disponible

#### 3. Paramètres requis manquants
```http
http://localhost:9000/components/TONIC
```
**Réponse:** 400 Bad Request - Paramètre code-applicatif manquant

#### 4. Type non implémenté
```http
http://localhost:9000/components/HUMAN?code-applicatif=test-project
```
**Réponse:** 500 Internal Server Error - Type non encore implémenté

## Types disponibles

### Starter Kits
- `TONIC` (implémenté)
- `HUMAN` (non implémenté)
- `STUMP` (non implémenté)

### Contracts
- `OPENAPI` (implémenté)
- `AVRO` (implémenté)
- `SOAP` (non implémenté)

### Libraries
- `JAVA` (non implémenté)
- `NODE` (non implémenté)

Note: Actuellement, seuls le starter kit TONIC et les contrats OPENAPI et AVRO sont pleinement implémentés. Les autres types sont listés mais pas encore disponibles.

### Features TONIC disponibles

Les features sont uniquement disponibles pour le starter kit TONIC. La liste des features disponibles est dynamiquement récupérée depuis le service TONIC et peut inclure :

- toni-starter-security-authapp-v2-client
- toni-starter-security-authapp-v3-client
- toni-starter-security-authapp-v3-server
- toni-starter-security-oauth2-agent-server
- toni-starter-jdbc
- toni-starter-amazon-s3
- toni-starter-client-espoir
- toni-starter-client-pat
- toni-contract-openapi
- toni-contract-avro

## Configuration technique

Le projet utilise :
- Spring Boot 3.x
- Java 17+
- Maven
- OpenAPI Generator pour la génération des clients et des contrôleurs
- WebClient pour les appels HTTP
- Lombok pour la réduction du boilerplate
- SpringDoc pour la documentation OpenAPI

### Notes importantes

- Le paramètre `code-applicatif` est obligatoire pour toutes les opérations de génération
- Les features doivent être spécifiées en tant que paramètres de requête répétés
- Les features de contrat (`toni-contract-openapi`, `toni-contract-avro`) ne sont disponibles que via l'endpoint `/contracts`
- Seul le starter kit TONIC est actuellement implémenté et fonctionnel