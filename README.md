# satispay

L'esercizio è stato svolto usando Spring Boot e utilizzando il comando
```
mvn spring-boot:run
```
è possibile lanciale l'applicazione, alla quale si può accedere tramite browser su
```
localhost:8080
```
Nella homepage ho realizzato una semplice interfaccia per poter scegliere il verbo desiderato.

Ogni metodo è stato mappato nel MainController, il quale fa riferimento al SatispayCallsService per effettuare la chiamata http tramite il RestTemplate. In questo processo vengono creati gli header desiderati nel metodo getHeaders(), il quale a sua volta fa riferimento al SatispayService per recuperare le informazioni relative alla Signature e alla Digest.
All'interno di questo service ci sono i metodi per leggere in modo appropriato la PrimaryKey dal file pem e utilizzarla durante la creazione della Signature.
