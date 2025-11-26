# ProCreds Backend - Build Instructions

## Build System Migration: Maven → Gradle

This project has been migrated from Maven to Gradle for improved build performance and flexibility.

## Prerequisites

- **Java 17+** (OpenJDK or Oracle JDK)
- **Gradle 8.5+** (included via Gradle Wrapper)

## Quick Start

### 1. Verify Java Installation
```bash
java -version
# Should show Java 17 or higher
```

### 2. Build the Project
```bash
# Clean and compile
./gradlew clean compileJava

# Build the complete project (includes tests)
./gradlew clean build

# Build without running tests
./gradlew clean build -x test
```

### 3. Run the Application
```bash
# Run with default profile
./gradlew bootRun

# Run with development profile
./gradlew bootRunDev

# Or run the JAR directly
./gradlew bootJar
java -jar build/libs/procreds-backend-1.0.0.jar
```

## Gradle Commands Reference

### Build Commands
| Command | Description |
|---------|-------------|
| `./gradlew clean` | Clean build directory |
| `./gradlew compileJava` | Compile Java source code |
| `./gradlew build` | Full build with tests |
| `./gradlew bootJar` | Create executable JAR |
| `./gradlew buildNoTests` | Build without running tests |

### Development Commands
| Command | Description |
|---------|-------------|
| `./gradlew bootRun` | Run Spring Boot application |
| `./gradlew bootRunDev` | Run with development profile |
| `./gradlew test` | Run all tests |
| `./gradlew check` | Run tests and code quality checks |

### Dependency Commands
| Command | Description |
|---------|-------------|
| `./gradlew dependencies` | Show dependency tree |
| `./gradlew dependencyInsight --dependency <name>` | Analyze specific dependency |

## Project Structure

```
backend/
├── build.gradle              # Main build configuration
├── gradle.properties         # Build properties and versions
├── settings.gradle           # Project settings
├── gradlew                   # Gradle wrapper (Unix)
├── gradlew.bat              # Gradle wrapper (Windows)
├── gradle/                  # Gradle wrapper files
│   └── wrapper/
├── src/                     # Source code
│   ├── main/java/           # Application code
│   └── test/java/           # Test code
└── build/                   # Build output (generated)
    ├── classes/
    ├── libs/
    └── reports/
```

## Configuration

### Application Properties
- **Development**: `src/main/resources/application-dev.yml`
- **Production**: `src/main/resources/application-prod.yml`
- **Default**: `src/main/resources/application.yml`

### Build Properties
Edit `gradle.properties` to customize:
- Java version
- Dependency versions
- Build optimization settings

## Migration from Maven

### Key Changes
1. **Build File**: `pom.xml` → `build.gradle`
2. **Dependencies**: XML format → Gradle DSL
3. **Commands**: `mvn` → `./gradlew`
4. **Build Output**: `target/` → `build/`

### Command Mapping
| Maven | Gradle |
|-------|--------|
| `mvn clean compile` | `./gradlew clean compileJava` |
| `mvn clean package` | `./gradlew clean build` |
| `mvn spring-boot:run` | `./gradlew bootRun` |
| `mvn test` | `./gradlew test` |
| `mvn dependency:tree` | `./gradlew dependencies` |

## Troubleshooting

### Common Issues

1. **Java Version Mismatch**
   ```bash
   # Check Java version
   java -version
   
   # Set JAVA_HOME if needed
   export JAVA_HOME=/path/to/java-17
   ```

2. **Permission Denied (Unix/Linux)**
   ```bash
   chmod +x gradlew
   ```

3. **Build Cache Issues**
   ```bash
   ./gradlew clean --refresh-dependencies
   ```

4. **Gradle Daemon Issues**
   ```bash
   ./gradlew --stop
   ./gradlew clean build
   ```

## Performance Tips

1. **Use Gradle Daemon** (enabled by default)
2. **Enable Build Cache** (configured in `gradle.properties`)
3. **Parallel Builds** (enabled in `gradle.properties`)
4. **Configuration Cache** (enabled in `settings.gradle`)

## IDE Integration

### IntelliJ IDEA
1. Open the `backend` directory
2. IntelliJ will auto-detect Gradle
3. Import Gradle project when prompted

### VS Code
1. Install "Extension Pack for Java"
2. Open the `backend` directory
3. VS Code will detect Gradle automatically

### Eclipse
1. Install "Buildship Gradle Integration"
2. Import → Existing Gradle Project
3. Select the `backend` directory

## Environment Variables

Set these environment variables for production:

```bash
export JAVA_HOME=/path/to/java-17
export SPRING_PROFILES_ACTIVE=prod
export JWT_SECRET=your-jwt-secret-key
export DB_URL=your-database-url
export DB_USERNAME=your-db-username
export DB_PASSWORD=your-db-password
```

## Docker Support

Build Docker image:
```bash
# Build JAR first
./gradlew bootJar

# Build Docker image (if Dockerfile exists)
docker build -t procreds-backend .
```

## Support

For build issues or questions:
1. Check this documentation
2. Review Gradle logs: `./gradlew build --info`
3. Clean and rebuild: `./gradlew clean build --refresh-dependencies`

