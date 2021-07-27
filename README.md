# DJet Spring Auto-Configurations and Libraries

Includes auto-configuration for OpenTelemetry instrumentation. Leverages Spring Aspect Oriented Programming, dependency injection, and bean post-processing to trace spring applications.

## Quickstart
### Add these dependencies to your project.

Replace `DJET_VERSION` with the latest stable [release](https://search.maven.org/search?q=g:cloud.djet).
For Gradle add to your dependencies:
```kotlin
//djet for opentelemetry spring auto-configuration
implementation("cloud.djet:opentelemetry-springboot-starter:DJET_VERSION")
```