#!/bin/bash

# Create directories for SDK downloads
mkdir -p docs/sdks/java/download
mkdir -p docs/sdks/typescript/download

# Build Java SDK
cd target/generated-sources/java-client
mvn clean package
cp target/openapi-java-client-1.0.0.jar ../../../docs/sdks/java/download/springdoc-demo-client-0.0.1-SNAPSHOT.jar

# Build TypeScript SDK
cd ../typescript-client
npm install
npm pack
cp springdoc-demo-client-0.0.1.tgz ../../../docs/sdks/typescript/download/ 