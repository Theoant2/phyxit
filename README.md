# PHYsically eXtended Interactive Technologies
Manage sensors of different kinds from the same place 

## Qu'est ce que Phyxit est capable de faire ?

Phyxit a été créé pour faciliter la centralisation des données pouvant provenir de différents
capteurs, communiquant chaqu'un à l'aide d'un protocol particulier. Par exemple, il devient
plus simple de recevoir des données de capteurs issues d'un réseau de type Broker
(avec une découverte de ces derniers automatique) et des capteurs communiquant via des API REST.
L'outil offre aussi la possibilité de déployer sa propre API Rest afin d'effectuer des operations sur
les capteurs enregistrés ou découverts.

Phyxit supporte de la généricité à tous les étages:

- Définition d'un capteur
    
    Les capteurs par défaut définis dans Phyxit peuvent être étendus et modifiés,
    pour par exemple pouvoir ajouter des attributs à ces derniers.

- Position du capteur

    Par défaut, Phyxit n'implémente qu'un seul type de position (décrit par une chaîne de caractère),
    mais comme montré dans les exemples ci-dessous, il est possible de pousser la complexité plus loin,
    par exemple en implémentant des positions décritent par une latitude et une longitude.

- Fréquence d'actualisation / publication
    
    Un capteur dans Phyxit peut vivre côté serveur et côté client. La version client va pouvoir
    publier et la version serveur va pouvoir recevoir. La définition des fréquences d'actualisation et
    de publication sont très génériques. Par défaut, une implémentation avec un timer est fournie.

- Types de données

    Un capteur maintient un type de donnée, qu'il peut publier (client) ou recevoir (serveur).
    Par défaut, les types de données pour les types primitifs sont fournis pour les capteurs
    communiquant par MQTT et par API REST.

- Providers

    Un provider (uniquement côté serveur) est un objet qui va permettre au système de collecter
    les informations du capteur (côté client) publiées. Ainsi, les providers par défaut implémentés
    vous permettrons de créer des capteurs communiquant par MQTT ou par API REST.
    **Attention** Vous pourrez utiliser par exemple [shiftr.io](https://www.shiftr.io/) pour simuler le serveur MQTT sur votre machine.

- Publishers

    Un publisher (uniquement côté client) est un objet qui va permettre à un capteur de publier ses informations.
    Par défaut, les implémentations fournissent des publishers pour les capteurs communiquant par MQTT.

- Sérialisation
  
    Un capteur peut communiquer sa description sur les canaux de communication.
    Les différents procédés de sérialisation pour tous les attrbiuts d'un capteur sont aussi génériques.

## Création d'un capteur connu

La librairie Phyxit fonctionne sur les deux fronts, client et serveur. La seule différence
résidera dans l'utilisation de cette dernière.
Si l'on connait la position de notre capteur, et sa façon dont il va nous faire parvenir les données,
on peut directement fournir au système sa description au lieu d'attendre que ce dernier ne se manifeste (= enregistrement automatique).

### Serveur

```java
public class PhyxitServer {

    // Topic sur lequel les capteurs communiquent sur le réseau MQTT
    private static String MASTER_TOPIC = "tcp://localhost:1883";
    
    private static SensorManager sensorManager;

    public static void main(String[] args) {

        // Création du gestionnaire de capteurs côté serveur
        sensorManager = new SensorManager();
        
        // Création de la description de notre capteur
        SensorBuilder.Server<Double> temperatureSensor = ServerSensor.builder()
                // Le nom du capteur
                .name("Bedroom Temperature Sensor")
                // Sa position
                .location(SensorLocation.simple("Bedroom"))
                // Sa fréquence de publication
                // (comme nous sommes côté serveur, ce sera sa fréquence d'actualisation)
                .frequency(ASensorFrequency.simple(TimeUnit.SECONDS, 2))
                // Le type de donnée qu'il publi
                .dataType(ISensorDataType.MQTT_DOUBLE)
                // La façon dont le serveur peut comprendre ses messages
                .provider(new APrimitiveMQTTSensorProvider.Double(MASTER_TOPIC));

        // Finalement, on fourni au gestionnaire la description de notre capteur
        sensorManager.createSensor(temperatureSensor);

    }

    public static SensorManager getSensorManager() { return sensorManager; }

}
```

### Client

```java
public class PhyxitClient1 {

    // Topic sur lequel notre capteur communique sur le réseau MQTT
    private static String MASTER_TOPIC = "tcp://localhost:1883";


    public static void main(String[] args) throws MqttException, IOException {
        // Création du gestionnaire de capteur côté client
        SensorPublisherManager sensorPublisherManager = new SensorPublisherManager(MASTER_TOPIC);

        // Création de la description de notre capteur
        SensorBuilder.Client<Float> sensorBuilder = ClientSensor.builder()
                // Le nom du capteur
                .name("Bedroom Temperature Sensor")
                // Sa position
                .location(SensorLocation.simple("Bedroom"))
                // Sa fréquence de publication
                .frequency(ASensorFrequency.simple(TimeUnit.SECONDS, 2))
                // Le type de donnée qu'il publi
                .dataType(ISensorDataType.MQTT_DOUBLE)
                // La façon dont il publi les informations
                // (ici, on veut publier une temperature [double], on utilise donc le TemperatureMQQTPublishSensorProvider)
                .publisher(new TemperatureMQQTPublishSensorProvider(MASTER_TOPIC));

        // Finalement, on fourni au gestionnaire la description de notre capteur
        sensorPublisherManager.registerSensorAndAdvertise(sensorBuilder);

        System.out.println("Publishing ...");

        // Et on évite que le thread principal ne meurt
        sensorPublisherManager.spin();
    }


}
```

## Création d'une nouvelle position

### GeographicSensorLocation

```java
public class GeographicSensorLocation implements SensorLocation {

    private double longitude;
    private double latitude;
    private String location;

    public GeographicSensorLocation(double longitude, double latitude, String location) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.location = location;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    @Override
    public String getLocationName() {
        return location;
    }

}
```

Cependant, étendre une nouvelle structure de donnée de ``SensorLocation`` ne suffit pas au
système pour pouvoir fonctionner correctement. En effet, lorsqu'un capteur est sérialisé,
l'information sur sa position l'est aussi. Il faut donc décrire au système comment procéder.

Comme notre capteur communique via un réseau MQTT, tout est représenté par des octets.
Chaque type de données se voit donc attribué un identifiant de sérialisation:

```java
package serialization.mqtt;

public interface SerializationID {

    // 100 - 199 SensorLocation
    public static int SIMPLE_SENSOR_LOCATION = 100;

    // 200 - 299 ISensorDataType
    public static int SENSOR_DATA_TYPE_BOOLEAN = 200;
    public static int SENSOR_DATA_TYPE_DOUBLE = 201;
    public static int SENSOR_DATA_TYPE_FLOAT = 202;
    public static int SENSOR_DATA_TYPE_INTEGER = 203;
    public static int SENSOR_DATA_TYPE_LONG = 204;
    public static int SENSOR_DATA_TYPE_STRING = 205;

    // 300 - 399 ASensorFrequency
    public static int SIMPLE_SENSOR_FREQUENCY = 300;

}
```

Nous allons donc créer notre identifiant, qui sera le ``101``, et sera attribué à un ``AMQTTSerializableSensorData<GeographicSensorLocation>``
qui va décrire la façon de sérialiser et désérialiser une ``GeographicSensorLocation`` pour une communication sur un réseau MQTT:

```java
package models;

import serialization.ISerializableSensorData;
import serialization.SensorSerializationManager;
import serialization.mqtt.AMQTTSerializableSensorData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GeographicSensorLocationSerializer extends AMQTTSerializableSensorData<GeographicSensorLocation> {

    public static final int SERIALIZATION_ID = 101;

    @Override
    public int getSerializationID() {
        return SERIALIZATION_ID;
    }

    @Override
    public void serializeObject(GeographicSensorLocation data, DataOutputStream outputResource, SensorSerializationManager sensorSerializationManager) throws IOException {
        outputResource.writeDouble(data.getLongitude());
        outputResource.writeDouble(data.getLatitude());
        outputResource.writeUTF(data.getLocationName());
    }

    @Override
    public GeographicSensorLocation deserializeObject(DataInputStream inputResource, SensorSerializationManager sensorSerializationManager) throws IOException {
        double longitude = inputResource.readDouble();
        double latitude = inputResource.readDouble();
        String locationName = inputResource.readUTF();
        return new GeographicSensorLocation(longitude, latitude, locationName);
    }

}
```

Si vous êtes curieux, un ``AMQTTSerializableSensorData<T>`` étends d'un ``ISerializableSensorData<T, E1, E2>``
qui décrit généralement comment une donnée doit être sérialisé / désérialisé pour n'importe quel canal de communication.

Maintenant que nous avons décrit le procédé de sérialisation, il nous font indiquer au système
que cette méthode éxiste (exemple côté serveur):

```java
// Récupération du gestionnaire de sérialisation
sensorManager.getSensorSerializationManager()
            // Récupération du protocol de communication via les réseau MQTT
             .getProtocol(MQTTSensorSerializationProtocol.class)
            // Si ce protocol est bien enregistré:
            .ifPresent(protocol -> {
                // On enregistre notre nouveau procédé de sérialisation
                protocol.registerSerializer(GeographicSensorLocation.class, new GeographicSensorLocationSerializer());
            });
```

## Découverte automatique de capteurs sur un réseau MQTT

Pour ce faire, côté serveur, il vous suffit d'appeler la méthode suivante:

```java
public class PhyxitServer {

    private static String MASTER_TOPIC = "tcp://localhost:1883";
    private static SensorManager sensorManager;

    public static void main(String[] args) {

        sensorManager = new SensorManager();

        sensorManager.enableMQTTSensorDiscovery(MASTER_TOPIC);
    }

    public static SensorManager getSensorManager() { return sensorManager; }

}
```

Le système se chargera du reste: comprendre la nature du capteur, ses attributs et de l'enregistrer dans le système.

## Storage Spaces

Phyxit propose un système de stockage vous permettant de récupérer à tout moment vos capteurs selon leurs attributs.
Par exemple, je veux récupérer le capteur qui s'appelle ``Bedroom Temperature Sensor``:

```java
ServerSensor<?> serverSensor = sensorManager.getByName("Bedroom Temperature Sensor");
```

Ou tous les capteurs qui se situent dans la chambre:

```java
Set<ServerSensor<?>> sensors = sensorManager.getByLoc(SensorLocation.simple("Bedroom"));
```

## Exemple avec plusieurs types de capteurs:

### Serveur

```java
public class PhyxitServer {

    private static String MASTER_TOPIC = "tcp://localhost:1883";
    private static SensorManager sensorManager;

    public static void main(String[] args) {

        sensorManager = new SensorManager();

        // Création d'un capteur qui récolte des informations sur les chats via une API REST
        SensorBuilder.Server<String> catFactSensor = ServerSensor.builder()
                .name("Cat Fact Sensor")
                .location(SensorLocation.simple("Cloud"))
                .frequency(ASensorFrequency.simple(TimeUnit.SECONDS, 10))
                .dataType(ISensorDataType.REST_API_STRING)
                .provider(new CatFactJsonClientSensorProvider());

        sensorManager.createSensor(catFactSensor);

        // Et on active la découverte de capteurs sur le réseau MQTT
        sensorManager.enableMQTTSensorDiscovery(MASTER_TOPIC);

    }

    public static SensorManager getSensorManager() { return sensorManager; }

}
```

### Client

```java
public class PhyxitTemperatureSensorClient {

    private static String MASTER_TOPIC = "tcp://localhost:1883";


    public static void main(String[] args) throws MqttException, IOException {
        SensorPublisherManager sensorPublisherManager = new SensorPublisherManager(MASTER_TOPIC);

        SensorBuilder.Client<Float> sensorBuilder = ClientSensor.builder()
                .name("Bedroom Temperature Sensor")
                .location(SensorLocation.simple("Bedroom"))
                .frequency(ASensorFrequency.simple(TimeUnit.SECONDS, 2))
                .dataType(ISensorDataType.MQTT_DOUBLE)
                .publisher(new TemperatureMQQTPublishSensorProvider(MASTER_TOPIC));

        sensorPublisherManager.registerSensorAndAdvertise(sensorBuilder);

        System.out.println("Publishing ...");

        sensorPublisherManager.spin();
    }


}
```

## Utilisation de l'API Rest interne

Phyxit offre une API Rest interne à l'aide d'un serveur HTTP. A travers cette API, un utilisateur
(aucune authentification n'a été implémentée pour le moment) va pouvoir effectuer des opérations
sur les capteurs enregistrés ou découverts.

Pour mieux comprendre les opérations, merci de vous référer à la section associée [Ensemble de capteurs et operations](#ensemble-de-capteurs-et-operations).

Côté implémentation, vous pourrez créer un serveur HTTP à travers le ``SensorManager``
(donc une implémentation faite uniquement côté ``serveur``).

```java
public class PhyxitServer {

    private static SensorManager sensorManager;

    public static void main(String[] args) {

      // ...

      // Accessible sur http://localhost:27078/
      HTTPRestServer httpRestServer = sensorManager.createRestAPIServer("localhost", 27078);
      httpRestServer.start();
      
      // ...

    }
    
}
```

Pour le moment, seulement un accès aux ``SensorSets`` a été développé.
Rendez-vous donc sur ``http://localhost:27078/sets``.

Vous trouverez dans le tableau ci-dessous les valeurs acceptables des arguments ``GET`` (seule méthode implémentée pour l'instant).

| Arguments     | Valeurs possibles                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
|---------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ``action``    | Action à effectuer sur un ensemble de capteurs.<br /><br /> ``operation``: seule cette valeur a été implémentée. Elle vous permettra de spécifier les arguments suivants dans ce tableau pour effectuer des opérations arithmétiques sur un ensemble de capteurs.                                                                                                                                                                                                                                              |
| ``operation`` | Opération à effectuer sur un ensemble de capteurs.<br /><br /> ``sum``: fait la somme de toutes les dernières valeurs reçus par l'ensemble de capteurs.<br /><br /> ``average``: fait la moyenne de toutes les dernières valeurs reçus par l'ensemble de capteurs.<br /><br /> ``max``: retourne la valeur maximale parmi toutes les dernières valeurs reçues par l'ensemble de capteurs.<br /><br /> ``min``: retourne la valeur minimale parmi toutes les dernières valeurs reçues par l'ensemble de capteurs. |
| ``type``      | Permet de savoir sur quel type de capteur faire certaines actions.<br /><br /> ``double``: référence tous les capteurs renvoyant des données de type ``Double``<br /><br /> ``float``: référence tous les capteurs renvoyant des données de type ``Float``<br /><br /> ``integer``: référence tous les capteurs renvoyant des données de type ``Integer``<br /><br /> ``long``: référence tous les capteurs renvoyant des données de type ``Long``                                                          |

Exemple:

Effctuer la somme de toutes les dernières valeurs reçues de capteurs renvoyant des données de type ``Double``: 
```
http://localhost:27078/sets?action=operation&operation=sum&type=double
```

## Ensemble de capteurs et operations

Comme décrit dans la partie [Storage Spaces](#storage-spaces), le ``SensorManager`` permet de récupérer
des ensembles de capteurs. Ces ensembles seront retournés sous forme de collection ``Set`` si
tous les capteurs présent dans cette collection ne renvoyant pas le même type de donnée.

Si cette dernière condition est vraie (notamment à travers les méthodes ``SensorManager#getByDataType``),
il sera retourné un ``SensorSet<T>`` sur lequels des opérations pourront être faites.

La nature de ces opérations peut être étendue (par l'extension de ``ASensorSetOperation``),
mais plusieurs opérations arithmétiques sont déjà implémentées et accessibles via des méthodes utiles statiques dans ``ASensorSetOperation``:

```java
// Calcule la somme
public static ReduceOperation<Double> sumDouble();
public static ReduceOperation<Float> sumFloat();
public static ReduceOperation<Integer> sumInteger();
public static ReduceOperation<Long> sumLong();

// Calcule la moyenne
public static AverageOperation<Double> averageDouble();
public static AverageOperation<Float> averageFloat();
public static AverageOperation<Integer> averageInteger();
public static AverageOperation<Long> averageLong();

// Calcule la valeur maximale
public static CollectionOperation<Double> maxDouble();
public static CollectionOperation<Float> maxFloat();
public static CollectionOperation<Integer> maxInteger();
public static CollectionOperation<Long> maxLong();

// Calcule la valeur minimale
public static CollectionOperation<Double> minDouble();
public static CollectionOperation<Float> minFloat();
public static CollectionOperation<Integer> minInteger();
public static CollectionOperation<Long> minLong();
```

Exemple:

Calcule la somme de toutes les valeurs dernièrement reçues sur l'ensemble de capteurs renvoyant des données de type ``Double``.

```java
SensorSet<Double> sensors = sensorManager.getByDataType(Double.class);
double sum = sensors.performOperation(ASensorSetOperation.sumDouble());
```
