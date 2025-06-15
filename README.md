# AuthREST

A broken authentication security testing tool for web APIs.

Test your API for broken authentication with AuthREST. Check if your API is vulnerable to:

- Password brute forcing
- Credential stuffing
- Use of invalid tokens

---

## <a id="getting-started"></a> Getting started

AuthREST is implemented using [RestTestGen Framework](https://github.com/SeUniVr/RestTestGen). The setup procedure of AuthREST is similar to the one of RestTestGen.

### 1️⃣ Prepare your API
AuthREST requires interacting with a deployed instance of a REST API through HTTP. Make sure your API is reachable from the machine on which AuthREST will be executed.

#### Configure the API in AuthREST
AuthREST requires some knowledge about the API under test. This includes details such as the location of its OpenAPI specification, and the authentication process (for unchecked token authenticity testing). All of this information must be provided within the designated *API directory*.

To create an API directory for your specific API, navigate to the `apis/` directory and create a subdirectory with a name of your choice, typically the API name. For instance, if you're working with the BookStore API, the API directory would be located at `apis/bookstore/`. Take a look at the BookStore API directory we provide out-of-the-box as an example of how to structure an API directory correctly.

The API directory can contain an API configuration file (`api-config.yml`) in which you can specify:
- `name`: the API name, solely for display purposes (takes the API directory name by default, e.g., `bookstore`).
- `specificationFileName`: the name of the specification file located in the `specifications/` subdirectory. (Default: `openapi.json`).
- `host`: the address or name of the host at which the API is reachable. Overrides the server address specified in the OpenAPI specification of the API.
- `authenticationCommands`: a map `<label, command>` listing authentication commands that when called will return authentication information to AuthREST. See [Authentication](#auth) section for further details. (None by default).
- ~~`resetCommand`: a command that when called will reset the state of the API. (None by default).~~ (Planned for next release).
- ~~`resetBeforeTesting`: if set to `true`, AuthREST will call the reset script before launching the testing strategy. (Default: `false`).~~ (Planned for next release).

You only need to specify the settings that you wish to override from their default values.

Example of API configuration:
```
name: Spotify
specificationFileName: spotify.json
host: "http://localhost:8080/"
authenticationCommands:
  default: "echo {\"name\": \"apikey\", \"value\": \"davide\", \"in\": \"query\", \"duration\": 6000}"
  admin: "python /path/to/script"
resetCommand: "python /path/to/script"
resetBeforeTesting: false
```

The API directory must contain a `specifications/` subdirectory in which is contained the OpenAPI specification of the API under test named as `openapi.json`, or with the custom name provided in the API configuration.

It is recommended to place authentication and reset scripts in the `scripts/` subdirectory.

Upon executing AuthREST, a `results/` subdirectory will be automatically generated. This subdirectory will store the test results and output data of each testing session.

### 2️⃣ Configuration
You can define preferences for AuthREST itself, unrelated to the API under test, by specifying them in the `rtg-config.yaml` file located in the root directory (This is actually the RestTestGen's configuration). This configuration file is especially valuable for specifying the API to be tested and selecting the desired testing strategy.

The available preferences are:
- `logVerbosity`: the verbosity of the log in the standard output. Can be `DEBUG`, `INFO`, `WARN`, or `ERROR` (default: `INFO`).
- `testingSessionName`: the name of the testing session printed in the report files (default: `test-session-CURRENT_TIME`).
- `resultsLocation`: `local` or `global`, specifies whether the test results are going to be written in the API directory or in the root directory (default: `local`).
- `strategyClassName`: the name of the testing strategy class to be run (default: `NominalAndErrorStrategy`).
- `qualifiableNames`: list of parameter names to be qualified. E.g., `id` -> `bookId`, `name` -> `userName` (default: `['id', 'name']`).
- `odgFileName`: output file name for the operation dependency graph (default: `odg.dot`).

You only need to specify the settings that you wish to override from their default values.

Example of basic AuthREST configuration to run the credential stuffing security testing strategy on the VAmPI API:
```
apiUnderTest: vampi
strategyClassName: CredentialStuffingSecurityTestingStrategy
```

### 3️⃣ Building and running AuthREST
You can build and run AuthREST in three ways: either within a Docker container, on your machine with Gradle, or on your machine within the IntelliJ IDEA IDE. 

#### Docker
Requirements: Docker. For further information, see the [REQUIREMENTS.md](REQUIREMENTS.md) file.

To build and run AuthREST with Docker, follow these steps:
1. Build the image with: `docker build -t rtg .`
2. Run the container from the image with: `docker run --rm -v ./:/app --network host rtg`

> If Docker raises an error about the relative path `./`, please replace the relative path with an absolute path to the AuthREST's source code, for example `docker run --rm -v /my/absolute/path/to/authrest:/app --network host rtg`.

> The Docker container will only include Java 11 and Gradle 8.14, without Python. If your authentication or reset scripts rely on Python, we recommend running AuthREST directly on your machine using Gradle. However, in upcoming versions of AuthREST, we aim to provide a more comprehensive container that includes additional dependencies, such as Python, to better support authentication and reset scripts.

#### Gradle
Requirements: Java 11. For further information, see the [REQUIREMENTS.md](REQUIREMENTS.md) file.

To build and run AuthREST with Gradle, follow these steps:
1. Make sure the Gradle Wrapper file is executable. You can make it executable with `sudo chmod +x gradlew`
2. Build the project using Gradle wrapper: `./gradlew build`
3. Run the application: `./gradlew run`

#### IntelliJ IDEA
Requirements: Java 11 and IntelliJ IDEA. For further information, see the [REQUIREMENTS.md](REQUIREMENTS.md) file.

If you prefer using IntelliJ IDEA, you can follow these steps to run AuthREST:
1. Open IntelliJ IDEA and import the AuthREST Gradle project.
2. Locate the `io.resttestgen.boot.cli.App` class.
3. Look for the `main` method in the `App` class.
4. Click the play icon next to the main method to run AuthREST (the play icon will appear only after IntelliJ successfully indexed the project files).

### 4️⃣ <a id="auth"></a>Authentication
> Required only for the unchecked token authenticity testing strategy.

AuthREST offers support for authenticated REST APIs. To collect authentication information, AuthREST executes authentication commands (if provided in the API configuration) and expects the standard output of these commands to be in the form of JSON objects. These objects represent authentication parameters and include four attributes: `name`, `value`, `in` (header, query, etc.), and `duration` (which determines when the authentication commands should be re-executed to obtain a new token after expiration).

An example of an authentication command could be `python3 /path/to/auth.py`, which executes the Python script located in the `auth.py` file. This user-provided script is responsible for performing the authentication process, such as obtaining the auth token, by interacting with the API via HTTP. It then prints the authentication information in the supported format by AuthREST to the standard output, for example:
```
{
  "name": "Authorization",
  "value": "Bearer [TOKEN]",
  "in": "header",
  "duration": 600
}
```

The API configuration file allows you to define multiple authentication commands for various purposes, users, and roles. Each authentication command is labeled. AuthREST currently supports only one command at a time, and it will use either the command labeled as `default` or, if no command is labeled as `default`, the first command in the list.

## Test results
Test results are located in the `results/` subdirectory of the API directory. This directory contains a list of the executed testing sessions for the API. The specific data included may vary depending on the chosen testing strategy. Typically, you will find the following:
- the Operation Dependency Graph (ODG) in dot format, stored in the `odg.dot` file.
- test reports located in the `report/` folder. These reports are JSON files that provide information about the executed HTTP test sequences and their corresponding results (pass/fail).
- JUnit + REST-assured test cases that allow for replaying the generated test cases.
- strategy-specific data, such as the enhanced specification generated by the NLP-based strategy.

---

## Replicating experiment results

The `api/` folder already contains the OpenAPI specifications of the API involved in the experiment.

Please configure AuthREST to target these APIs with the proper security testing strategies.