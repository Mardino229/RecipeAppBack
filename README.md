# CookHub Backend (RecipeApp)

### Description

Cette partie du projet correspond à l'API backend de 
l'application RecipeApp, 
développée avec Spring Boot. 
Elle gère les utilisateurs, les recettes, l'authentification et 
les opérations liées aux favoris et likes.

### Prérequis 

Avant de commencer, assurez-vous d'avoir installé: 

 - Java 17
 - Maven
 - MySQL (ou autre base de données compatible avec Spring Boot)

### Installation

1. Clonez le dépot backend:
    
    ```bash 
    git clone https://github.com/Mardino229/RecipeAppBack.git
    cd recipeappback
    ```

2. Assurez-vous de configurer votre base de données dans application.properties. Exemple pour **MySQL** :

    ```properties 
    spring.datasource.url=jdbc:mysql://localhost:3306/recipeapp?createDatabaseIfNotExist=true
    spring.datasource.username=root
    spring.datasource.password=yourpassword
   
    spring.jpa.hibernate.ddl-auto=update

3. Installez les dépendances et démarrez l'application avec Maven : 
    
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

4. Accéder au fichier script.sql en suivant src/main/ressources et exécuter le script

L'api sera accessible à http://localhost:9084

### Endpoints principaux

Les endpoints seront disponibles sur l'interface de swagger à l'addresse http://localhost:9084/swagger-ui/index.html

### Mise en place du front 

Suivez ce [lien](https://github.com/Mardino229/RecipeApp) pour pour installer et configurer le front de l'application

### Configuration en fonction du Front 

1. Accéder au fichier webConfig en suivant src/main/java/com.nidas.recipeapp/config
2. Remplacer au niveau de la ligne 18 au niveau de la propriété allowedOrigins le lien par défaut par le lien menant au front 

### Fonctionnalités 

 - Inscription et connexion des utilisateurs basé sur **JWT**
 - CRUD (Create, Read, Update, Delete) des recettes
 - Ajout aux favoris et gestion des likes pour les recettes
 - Protection des routes avec Spring Security
 - Gestion de fichiers

Consultez mon portfolio [ici](https://mardino229.github.io/myportfolio/) 😊😜😏