# common
common package with submodules used in many projects

## Structure

```
common
-> common-bom
-> common-dto
-> common-error
```

## Description
1. common is the main module and contains the BOM and DTO modules as submodules.
2. common-bom is the submodule imported as "dependency management" in the other modules. Its pom points to the other submodules' poms.
3. common-dto is the submodule containing the DTOs that are commonly used by other projects.
4. common-error is the submodule containing the classes that are commonly used to handle errors.

## Github packages
In order to make other projects use this common package, it is needed to publish this module on the web. 
It could have been done in several ways (maven central repo, nexus, etc.), but for now the choice fell on Github packages.

### Github packages with maven documentation
This is the guide: https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry

## How to use common package in other projects

### Settings.xml
In order to make maven able to publish and download the packages from Github, it is needed to add the following code to the settings.xml file.
This is the example of my settings.xml file:
```
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
        </repository>
        <repository>
          <id>my-github-common</id>
          <url>https://maven.pkg.github.com/alessandrogranato/common</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <servers>
    <server>
      <id>my-github-common</id>
      <username>alessandrogranato</username>
      <password><MY_TOKEN></password>
    </server>
  </servers>
</settings>

```

### Import the common package as dependency
In order to use this package in other projects, it is needed to add the following code to the parent pom.xml file of the destination project:
```
	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>com.pyrosandro</groupId>
				<artifactId>common-bom</artifactId>
				<version>${common-bom-version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
```
Where **${common-bom-version}** is the version of the common-bom module defined in the property tag of the parent pom.xml file.
For example:
```
    <properties>
        <common-bom-version>1.0.0</common-bom-version>
    </properties>
```

Then, inside the submodule pom.xml file where you need common features, it is needed to add the following code:
```
    <dependencies>
        <dependency>
            <groupId>com.pyrosandro</groupId>
            <artifactId>common-dto</artifactId>
        </dependency>
        <dependency>
            <groupId>com.pyrosandro</groupId>
            <artifactId>common-error</artifactId>
        </dependency>
    </dependencies>
```
Finally, it is needed to add the following code to the settings.xml file:

### Implement the destination module's custom error handling that is based on common-error module

If you need only common error messages in your destination module, you are already good to go.
If you need custom error messages, you need to do the following things in your destination module:

1. Create a class that handles your custom exceptions. An example:
```
@Data
public class AuthException extends Exception {

    private final AuthErrorConstants errorCode;
    private final Object[] errorArgs;

    public AuthException(AuthErrorConstants errorCode, Object[] errorArgs) {
        this.errorCode = errorCode;
        this.errorArgs = errorArgs;
    }
}
```

2. Create a class that handles your custom error constants. An example:
``` 
@RequiredArgsConstructor
@Getter
public enum AuthErrorConstants {

    RESOURCE_NOT_FOUND(1001),
    INVALID_JWT_SIGNATURE(1002),
    MALFORMED_JWT(1003),
    ;

    public final int code;

}
```

3. Create a class that extends GlobalExceptionHandler and handles your custom exceptions. An example:
``` 
@Slf4j
@ControllerAdvice
public class AuthExceptionHandler extends GlobalExceptionHandler {

    public AuthExceptionHandler(@Value("${common.printstacktrace:false}") boolean printStackTrace, MessageSource messageSource) {
        super(printStackTrace, messageSource);
    }


    @ExceptionHandler({AuthException.class})
    public ResponseEntity<Object> handleAuthException(AuthException ex, WebRequest request) {

        switch (ex.getErrorCode()) {
            case RESOURCE_NOT_FOUND:
                log.error("Resource not found", ex);
                return buildErrorDTO(ex, messageSource.getMessage(String.valueOf(ex.getErrorCode().getCode()), ex.getErrorArgs(), Locale.getDefault()), HttpStatus.NOT_FOUND, request);
            case INVALID_JWT_SIGNATURE:
            case MALFORMED_JWT:
                log.error("unauthorized access", ex);
                return buildErrorDTO(ex, messageSource.getMessage(String.valueOf(ex.getErrorCode().getCode()), ex.getErrorArgs(), Locale.getDefault()), HttpStatus.UNAUTHORIZED, request);
            default:
                log.error("generic error", ex);
                return buildErrorDTO(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }
}
```

4. Create a property file used by the messageSource bean. An example would be to have a file called **src/main/resources/auth-messages.properties** with the following content:
```
1001=Resource not found with path {0}
1002=Unauthorized - Invalid JWT signature
1003=Unauthorized - Invalid JWT token

``` 

5. Add the messageSource properties to use both the common-messages.properties and the auth-messages.properties inside the application.yml file of the target module. An example:
``` 
spring:
    messages:
        basename: auth-messages,common-messages
```




