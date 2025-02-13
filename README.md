# FAIN_J - Service Facade pour Instanciateurs DDST

## Table des matières
1. [Aperçu](#aperçu)
2. [Architecture](#architecture)
3. [Points d'accès REST](#points-daccès-rest)
4. [Guide d'utilisation](#guide-dutilisation)
   - [Cas de succès](#cas-de-succès)
   - [Cas d'erreur](#cas-derreur)
5. [Types de composants disponibles](#types-de-composants-disponibles)
6. [Features disponibles](#features-disponibles)
7. [Configuration technique](#configuration-technique)

## Aperçu

FAIN_J est une API facade qui uniformise l'accès aux différents instanciateurs de projets DDST. Le service expose une API REST unifiée permettant de générer des projets à partir de différents starter kits tout en conservant les spécificités de chaque type de composant.

## Architecture

L'architecture se compose de trois diagrammes principaux :

1. **Diagramme de séquence** - Illustre le flux d'interaction entre les composants :
   ![Diagramme de séquence](/src/main/resources/diagrams/sequence-diagram.png)

2. **Diagramme de classes** - Montre la structure des classes du système :
   ![Diagramme de classes](/src/main/resources/diagrams/class-diagram.png)

3. **Diagramme de composants** - Présente l'architecture globale du système :
   ![Diagramme de composants](/src/main/resources/diagrams/component-diagram.png)

## Points d'accès REST

Le service expose les endpoints suivants sur le port 9000:

### GET /type-composant
Récupère la liste des types de composants disponibles.

**Réponse:** Liste des types de composants supportés (TONIC, HUMAN)

### GET /instanciate
Génère un nouveau projet à partir des paramètres fournis.

**Paramètres:**
- `typeDeComposant` (requis): Type du composant à générer (ex: TONIC, HUMAN)
- `nomDuComposant` (requis): Nom du projet
- `groupId` (optionnel): GroupId Maven (défaut: fr.cnam.default)
- `artifactId` (optionnel): ArtifactId Maven (défaut: nomDuComposant)
- `features` (optionnel): Liste des dépendances séparées par des virgules

### GET /features
Retourne la liste des features disponibles pour un type de composant.

**Paramètres:**
- `typeDeComposant` (requis): Type du composant (ex: TONIC, HUMAN)

## Guide d'utilisation

### Cas de succès

1. **Dépendance unique**
```
http://localhost:9000/instanciate?typeDeComposant=TONIC&nomDuComposant=test-project&groupId=fr.cnam.myGroupId&artifactId=myArtefactId&features=toni-starter-security-authapp-v2-client
```

2. **Dépendances multiples**
```
http://localhost:9000/instanciate?typeDeComposant=TONIC&nomDuComposant=test-project&groupId=fr.cnam.myGroupId&artifactId=myArtefactId&features=toni-starter-security-authapp-v2-client,toni-starter-client-espoir,toni-contract-openapi
```

3. **Sans dépendances**
```
http://localhost:9000/instanciate?typeDeComposant=TONIC&nomDuComposant=test-project&groupId=fr.cnam.myGroupId&artifactId=myArtefactId
```

### Cas d'erreur

1. **Type de composant invalide**
```
http://localhost:9000/instanciate?typeDeComposant=INVALID&nomDuComposant=test-project
```
Réponse: 400 Bad Request - Type de composant non supporté

2. **Feature invalide ou mal orthographiée**
```
http://localhost:9000/instanciate?typeDeComposant=TONIC&nomDuComposant=test-project&features=toni-starter-security-authap-v2-client
```
Réponse: 400 Bad Request - Feature non disponible

3. **Casse incorrecte dans les features**
```
http://localhost:9000/instanciate?typeDeComposant=TONIC&nomDuComposant=test-project&features=TONI-STARTER-SECURITY-AUTHAPP-V2-CLIENT
```
Réponse: 400 Bad Request - Feature non reconnue

4. **Paramètres requis manquants**
```
http://localhost:9000/instanciate?typeDeComposant=TONIC
```
Réponse: 400 Bad Request - Paramètre nomDuComposant manquant

5. **Type de composant non implémenté**
```
http://localhost:9000/instanciate?typeDeComposant=HUMAN&nomDuComposant=test-project
```
Réponse: 500 Internal Server Error - Type de composant non encore implémenté

## Types de composants disponibles

Actuellement supportés :
- `TONIC`
- `HUMAN`

## Features disponibles

Les features sont uniquement disponibles pour les composants TONIC. La liste des features disponibles est dynamiquement récupérée depuis le service TONIC et peut inclure :

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