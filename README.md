# FAIN_J - Service Facade pour Instanciateurs DDST

## Table des matières
1. [Aperçu](#aperçu)
2. [Architecture](#architecture)
3. [Points d'accès REST](#points-daccès-rest)
4. [Guide d'utilisation](#guide-dutilisation)
   - [Cas de succès](#cas-de-succès)
   - [Cas d'erreur](#cas-derreur)
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

Le service expose les endpoints suivants :

### Starter Kits

#### GET /starter-kits/types
Récupère la liste des types de starter kits disponibles.

**Réponse:** Liste des types supportés (TONIC, HUMAN, STUMP)

#### GET /starter-kits/{type}/features
Retourne la liste des features disponibles pour un type de starter kit.

**Paramètres:**
- `type` (chemin, requis): Type du starter kit (TONIC, HUMAN, STUMP)

#### GET /starter-kits/{type}/instanciate
Génère un nouveau projet starter kit.

**Paramètres:**
- `type` (chemin, requis): Type du starter kit
- `componentName` (requis): Nom du projet
- `groupId` (optionnel): GroupId Maven (défaut: fr.cnam.default)
- `artifactId` (optionnel): ArtifactId Maven (défaut: componentName)
- `features` (optionnel): Liste des features à inclure

### Contracts

#### GET /contracts/types
Récupère la liste des types de contrats disponibles.

**Réponse:** Liste des types supportés (OPENAPI, AVRO, SOAP)

#### GET /contracts/{type}/instanciate
Génère un nouveau projet de contrat.

**Paramètres:**
- `type` (chemin, requis): Type du contrat
- `componentName` (requis): Nom du projet
- `groupId` (optionnel): GroupId Maven
- `artifactId` (optionnel): ArtifactId Maven

### Libraries

#### GET /libraries/types
Récupère la liste des types de bibliothèques disponibles.

**Réponse:** Liste des types supportés (JAVA, NODE)

#### GET /libraries/{type}/instanciate
Génère un nouveau projet de bibliothèque.

**Paramètres:**
- `type` (chemin, requis): Type de la bibliothèque
- `componentName` (requis): Nom du projet
- `groupId` (optionnel): GroupId Maven
- `artifactId` (optionnel): ArtifactId Maven

## Guide d'utilisation

### Cas de succès

1. **Starter Kit TONIC avec une feature**
```
http://localhost:9000/starter-kits/TONIC/instanciate?componentName=test-project&groupId=fr.cnam.myGroupId&artifactId=myArtefactId&features=toni-starter-security-authapp-v2-client
```

2. **Starter Kit TONIC avec plusieurs features**
```
http://localhost:9000/starter-kits/TONIC/instanciate?componentName=test-project&features=toni-starter-security-authapp-v2-client,toni-starter-client-espoir
```

3. **Contrat OpenAPI**
```
http://localhost:9000/contracts/OPENAPI/instanciate?componentName=my-api&groupId=fr.cnam.api
```

4. **Bibliothèque Java**
```
http://localhost:9000/libraries/JAVA/instanciate?componentName=my-lib&groupId=fr.cnam.lib
```

### Cas d'erreur

1. **Type de starter kit invalide**
```
http://localhost:9000/starter-kits/INVALID/instanciate?componentName=test-project
```
Réponse: 400 Bad Request - Type de starter kit non supporté

2. **Feature invalide pour TONIC**
```
http://localhost:9000/starter-kits/TONIC/instanciate?componentName=test-project&features=invalid-feature
```
Réponse: 400 Bad Request - Feature non disponible

3. **Paramètres requis manquants**
```
http://localhost:9000/starter-kits/TONIC/instanciate
```
Réponse: 400 Bad Request - Paramètre componentName manquant

4. **Type non implémenté**
```
http://localhost:9000/starter-kits/HUMAN/instanciate?componentName=test-project
```
Réponse: 500 Internal Server Error - Type non encore implémenté

## Types disponibles

### Starter Kits
- `TONIC` (implémenté)
- `HUMAN` (non implémenté)
- `STUMP` (non implémenté)

### Contracts
- `OPENAPI` (non implémenté)
- `AVRO` (non implémenté)
- `SOAP` (non implémenté)

### Libraries
- `JAVA` (non implémenté)
- `NODE` (non implémenté)

Note: Actuellement, seul le starter kit TONIC est pleinement implémenté. Les autres types sont listés mais pas encore disponibles.

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