# TrixCore
TrixCore est un plugin permettant de relier vos serveurs à votre cms ! Cette version de TrixCore supporte seulement les serveurs sous velocity

## Téléchargement

[TrixCore Velocity v1.0.1](https://github.com/TrixCMS-V-2/trixcore-minecraft-velocity/releases/download/v1.0.1/trixcore-velocity-1.0.1.jar)

## Installation

Pour installer TrixCore vous n'avez qu'a glisser le plugins dans le dossier plugins (ou mods suivant la plateforme). 

Puis vous n'aurez qu'a lancer votre serveur et taper la commande :

> trixcore setup <port>

ou <port> correspond au port qui sera utilisé pour la communication. Le port ne peut pas être inférieur à 1000.

Si vous souhaitez réinitialiser TrixCore tapez :

> trixcore reset 

## Développement

Un exemple de plugin ajoutant des fonctionnalités est disponible ici : 
[trixcore-minecraft-example](https://github.com/TrixCMS-V-2/trixcore-minecraft-example)

Le plugin est disponible sur la plateforme [jitpack.io](https://jitpack.io/#eu.trixcms/trixcore-minecraft-velocity)

### Gradle

Ajoutez jipack.io dans les repositories de votre projet :

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Puis incluez **eu.trixcms:trixcore-minecraft-velocity** en dépendance :

```
dependencies {
     implementation 'eu.trixcms:trixcore-minecraft-velocity:v1.0.1'
}
```

### Maven

Ajoutez jipack.io dans les repositories de votre projet :

```
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
```

Puis incluez **eu.trixcms:trixcore-minecraft-velocity** en dépendance :

```
<dependency>
	<groupId>eu.trixcms</groupId>
	<artifactId>trixcore-minecraft-velocity</artifactId>
	<version>v1.0.1</version>
</dependency>
```

## Builds

Pour buid les plugins :

> mvn clean install

et vous trouverez l'artifact dans le dossier target
