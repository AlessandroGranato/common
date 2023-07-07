# common
common package with submodules used in many projects

## Structure

```
common
-> common-bom
-> common-dto
```

## Description
1. common is the main module and contains the BOM and DTO modules as submodules.
2. common-bom is the submodule imported as "dependency management" in the other modules. Its pom points to the other submodules' poms.
3. common-dto is the submodule containing the DTOs that are commonly used by other projects.


## Github packages
In order to make other projects use this common package, it is needed to publish this module on the web. 
It could have been done in several ways (maven central repo, nexus, etc.), but for now the choice fell on Github packages.

### Github packages with maven documentation
This is the guide: https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry

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

