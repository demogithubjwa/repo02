# assistant-microservice

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/assistant-microservice-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- Apache Kafka Client ([guide](https://quarkus.io/guides/kafka)): Connect to Apache Kafka with its native API
- SmallRye Health ([guide](https://quarkus.io/guides/smallrye-health)): Monitor service health

## Assistant Configuration

### List Buttons
Para personalizar los botones y agregar información adicional, se debe modificar el JSON del step en el cual se han agregado los botones con la siguiente estructura:
```json
{
  "generic": [
    {
      "values": [
        {
          "text_expression": {
            "concat": [
              {
                "scalar": "Las opciones disponibles son:"
              }
            ]
          }
        }
      ],
      "response_type": "text",
      "selection_policy": "sequential",
      "repeat_on_reprompt": false
    },
    {
      "options": [
        {
          "label": "Opción 1",
          "value": {
            "input": {
              "text": "Opción 1"
            }
          }
        },
        {
          "label": "Opción 2",
          "value": {
            "input": {
              "text": "Opción 2"
            }
          }
        },
        {
          "label": "Opción 3",
          "value": {
            "input": {
              "text": "Opción 3"
            }
          }
        },
        {
          "label": "Opción 4",
          "value": {
            "input": {
              "text": "Opción 4"
            }
          }
        }
      ],
      "user_defined": {
        "options": [
          {
            "id": "Opción 1",
            "description": "Este botón redirige a la página de inicio en WhatsApp."
          },
          {
            "id": "Opción 2",
            "description": "Este botón redirige a la página de inicio en WhatsApp."
          }
        ],
        "buttonText": "<BUTTON_TEXT>",
        "messageBody": "<MESSAGE_BODY_TEXT>",
        "sectionTitle": "<SECTION_TITLE_TEXT>",
        "messageFooter": "<MESSAGE_FOOTER_TEXT>",
        "messageHeader": "<MESSAGE_HEADER_TEXT>"
      },
      "response_type": "option",
      "repeat_on_reprompt": true
    }
  ]
}
```
La visualización de los atributos personalizados son accesibles por todos los canales, sin embargo se debe confirmar que el canal pueda soportar todos esos atributos. 

### Estructura de `user_defined` Explicada

El campo `user_defined` en el JSON permite personalizar diversos aspectos de los botones y mensajes que se envían a través de WhatsApp. A continuación se detallan los campos que puedes utilizar y su propósito:

### Campos Personalizados

#### `buttonText`
- **Descripción**: Especifica el texto que aparecerá en los botones presentados al usuario. Este texto es lo que verá el usuario para interactuar con las opciones.
- **Ejemplo**: `"buttonText": "Seleccione una opción"`

#### `messageBody`
- **Descripción**: Define el texto principal del mensaje que se enviará junto con los botones. Este mensaje generalmente incluye una instrucción o pregunta para el usuario antes de mostrar las opciones.
- **Ejemplo**: `"messageBody": "¿Qué deseas hacer ahora?"`

#### `sectionTitle`
- **Descripción**: Título opcional para una sección del mensaje. Este título puede usarse para categorizar o dividir la información en secciones dentro del mensaje.
- **Ejemplo**: `"sectionTitle": "Opciones de acción"`

#### `messageFooter`
- **Descripción**: Pie de mensaje opcional. Este campo puede contener un mensaje adicional que se mostrará al final de la interacción, como un agradecimiento o un recordatorio.
- **Ejemplo**: `"messageFooter": "Gracias por usar nuestro servicio."`

#### `messageHeader`
- **Descripción**: Encabezado opcional del mensaje. Este texto aparece en la parte superior del mensaje y puede ser utilizado para saludar o dar una bienvenida al usuario.
- **Ejemplo**: `"messageHeader": "Bienvenido"`

#### `options`
- **Descripción**: Especifica las opciones o botones disponibles para el usuario. Cada opción tiene un `id`, un `label` (texto visible) y una `description` que describe la acción que realizará esa opción.
- **Ejemplo**:
  ```json
  "options": [
    {
      "id": "Opción 1",
      "description": "Este botón redirige a la página de inicio en WhatsApp."
    },
    {
      "id": "Opción 2",
      "description": "Este botón redirige a la página de contacto en WhatsApp."
    }
  ]
  ```
