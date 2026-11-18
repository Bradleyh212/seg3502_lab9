# SEG3502 — Laboratoire 9 — Code de départ

Ce projet contient l'API REST de conversion de température à sécuriser pour le laboratoire 9.

L’API utilise **Spring Security**, avec une authentification **In Memory** et
**Basic Authentication** obligatoire pour toutes les requêtes. Les deux utilisateurs
`user1` / `pass1` et `user2` / `pass2` ont le rôle `USER`. Les mots de passe sont
encodés avec BCrypt en mémoire. Chaque requête doit fournir ses identifiants;
aucune session de connexion n’est conservée.

## Démarrage

Le projet utilise Java 21.

Sous macOS/Linux :

```bash
./gradlew bootRun
```

Sous Windows :

```text
gradlew.bat bootRun
```

Exemples de points de terminaison déjà disponibles :

```text
GET http://localhost:8080/temperature-converter/celsius-fahrenheit/50
GET http://localhost:8080/temperature-converter/fahrenheit-celsius/122
```

## Travail demandé

Conformément aux consignes du laboratoire :

- ajouter Spring Security au projet;
- configurer une authentification en mémoire;
- créer au minimum :
  - `user1` avec le mot de passe `pass1`;
  - `user2` avec le mot de passe `pass2`;
- exiger l'authentification Basic pour toutes les requêtes à l'API;
- tester l'API avec Postman, Insomnia, Curl ou un outil similaire.

Aucune interface utilisateur n'est demandée pour ce livrable.

## Vérification

Exécuter les tests :

```bash
./gradlew test
```

Après le démarrage de l’application :

```bash
# Sans identifiants : 401 Unauthorized
curl -i http://localhost:8080/temperature-converter/celsius-fahrenheit/50

# Identifiants incorrects : 401 Unauthorized
curl -i -u user1:wrong http://localhost:8080/temperature-converter/celsius-fahrenheit/50

# user1 : 200 OK, résultat 122.0
curl -i -u user1:pass1 http://localhost:8080/temperature-converter/celsius-fahrenheit/50

# user2 : 200 OK, résultat 50.0
curl -i -u user2:pass2 http://localhost:8080/temperature-converter/fahrenheit-celsius/122
```

Dans Postman ou Insomnia, choisir **Basic Auth** et saisir les identifiants.
Les identifiants ci-dessus sont ceux imposés pour le laboratoire. Basic Auth
encode les identifiants en Base64; HTTPS est nécessaire pour protéger leur transport
hors de cet environnement local.
