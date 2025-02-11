# FAIN_J - Service Facade pour Instanciateurs DDST

## Table des matières
1. [Aperçu](#aperçu)
2. [Architecture](#architecture)
3. [Points d'accès REST](#points-daccès-rest)
4. [Configuration](#configuration)
5. [Guide d'utilisation](#guide-dutilisation)
    - [Cas de succès](#cas-de-succès)
    - [Cas d'erreur](#cas-derreur)
6. [Types de composants disponibles](#types-de-composants-disponibles)
7. [Features disponibles](#features-disponibles)

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

### GET /v1/instanciate
Génère un nouveau projet à partir des paramètres fournis.

**Paramètres:**
- `typeDeComposant` (requis): Type du composant à générer (ex: TONIC, HUMAN, STUMP)
- `nomDuComposant` (requis): Nom du projet
- `groupId` (optionnel): GroupId Maven
- `artifactId` (optionnel): ArtifactId Maven
- `features` (optionnel): Liste des dépendances séparées par des virgules

### GET /v1/features
Retourne la liste des features disponibles pour un type de composant.

**Paramètres:**
- `typeDeComposant` (requis): Type du composant (ex: TONIC, HUMAN, STUMP)

## Configuration

Le service utilise un fichier `application.yml` pour sa configuration. Les principaux éléments configurables sont :
- Port du service (par défaut: 9000)
- URLs des services d'initialisation
- Liste des features disponibles par type de composant

## Guide d'utilisation

### Cas de succès

1. **Dépendance unique**
```
http://localhost:9000/v1/instanciate?typeDeComposant=TONIC&nomDuComposant=test-project&groupId=fr.cnam.myGroupId&artifactId=myArtefactId&features=toni-starter-security-authapp-v2-client
```

2. **Dépendances multiples**
```
http://localhost:9000/v1/instanciate?typeDeComposant=TONIC&nomDuComposant=test-project&groupId=fr.cnam.myGroupId&artifactId=myArtefactId&features=toni-starter-security-authapp-v2-client,toni-starter-client-espoir,toni-contract-openapi
```

3. **Sans dépendances**
```
http://localhost:9000/v1/instanciate?typeDeComposant=TONIC&nomDuComposant=test-project&groupId=fr.cnam.myGroupId&artifactId=myArtefactId
```

### Cas d'erreur

1. **Type de composant invalide**
```
http://localhost:9000/v1/instanciate?typeDeComposant=INVALID&nomDuComposant=test-project&groupId=fr.cnam.myGroupId&artifactId=myArtefactId
```

2. **Feature invalide ou mal orthographiée**
```
http://localhost:9000/v1/instanciate?typeDeComposant=TONIC&nomDuComposant=test-project&features=toni-starter-security-authap-v2-client
```

3. **Casse incorrecte dans les features**
```
http://localhost:9000/v1/instanciate?typeDeComposant=TONIC&nomDuComposant=test-project&features=TONI-STARTER-SECURITY-AUTHAPP-V2-CLIENT
```

## Types de composants disponibles

- `HUMAN`
- `STUMP`
- `TONIC`
- `CONTRAT-OPENAPI`
- `CONTRAT-AVRO`
- `CONTRAT-SOAP`
- `LIB-JAVA`
- `LIB-NODE`

## Features disponibles

Liste des features supportées pour les composants TONIC :

```
toni-starter-security-authapp-v2-client
toni-starter-security-authapp-v3-client
toni-starter-security-authapp-v3-server
toni-starter-security-oauth2-agent-server
toni-starter-jdbc
toni-starter-amazon-s3
toni-starter-client-espoir
toni-starter-client-pat
toni-contract-openapi
toni-contract-avro
```