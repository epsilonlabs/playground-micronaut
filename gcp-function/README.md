## Micronaut 4.1.6 Documentation

- [User Guide](https://docs.micronaut.io/4.1.6/guide/index.html)
- [API Reference](https://docs.micronaut.io/4.1.6/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/4.1.6/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

# Micronaut and Google Cloud Function

## Deploying the function

First build the function with:

```bash
$ ./gradlew clean shadowJar
```

Then `cd` into the `build/libs` directory (deployment has to be done from the location where the JAR lives):

```bash
$ cd build/libs
```

Now run:

```bash
$ gcloud functions deploy playground-fn-flexmi2plantuml --entry-point org.eclipse.epsilon.labs.playground.fn.flexmi2plantuml.Function --runtime java17 --trigger-http
```

Choose unauthenticated access if you don't need auth.

To obtain the trigger URL do the following:

```bash
$ YOUR_HTTP_TRIGGER_URL=$(gcloud functions describe playground-fn-flexmi2plantuml --format='value(httpsTrigger.url)')
```

You can then use this variable to test the function invocation:

```bash
$ curl -i $YOUR_HTTP_TRIGGER_URL/playground-fn-flexmi2plantuml
```

- [Shadow Gradle Plugin](https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow)
- [Micronaut Gradle Plugin documentation](https://micronaut-projects.github.io/micronaut-gradle-plugin/latest/)
- [GraalVM Gradle Plugin documentation](https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html)
## Feature google-cloud-function documentation

- [Micronaut Google Cloud Function documentation](https://micronaut-projects.github.io/micronaut-gcp/latest/guide/index.html#simpleFunctions)

