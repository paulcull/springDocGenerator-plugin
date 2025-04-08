<!DOCTYPE html>
<html>
<head>
    <title>Java SDK</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div># Java SDK

Our Java SDK provides a clean and idiomatic way to interact with our API.

## Installation
Add the following dependency to your `pom.xml`:
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>api-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Basic Usage
```java
// Initialize the client
ApiClient client = new ApiClient();
client.setBasePath("http://localhost:8080/api");
client.setBearerToken("your-token");

// Create API instance
DefaultApi api = new DefaultApi(client);
```

## SDK-Specific Features

### Configuration Options
```java
// Configure timeouts
client.setConnectTimeout(30000);
client.setReadTimeout(30000);

// Configure proxy
client.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.example.com", 8080)));

// Configure SSL
client.setSslCaCert(new File("path/to/cert.pem"));
```

### Custom Serialization
```java
// Configure custom JSON serializer
client.setJSON(new JSON() {
    @Override
    public <T> T deserialize(String body, Type returnType) {
        // Custom deserialization logic
    }

    @Override
    public String serialize(Object obj) {
        // Custom serialization logic
    }
});
```

### Authentication
```java
// Basic Auth
client.setUsername("username");
client.setPassword("password");

// API Key Auth
client.setApiKey("your-api-key");

// OAuth2
client.setAccessToken("your-access-token");
```

### Retry Configuration
```java
// Configure retry policy
client.setRetryPolicy(new RetryPolicy()
    .maxRetries(3)
    .retryOnException(true)
    .retryOnResponseCode(429));
```

For more examples and general API usage, check out our [API Integration Guide](../../guides/api-integration.md).
</div>
</body>
</html> 