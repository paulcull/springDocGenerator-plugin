<!DOCTYPE html>
<html>
<head>
    <title>TypeScript SDK</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div># TypeScript SDK

Our TypeScript SDK offers type-safe API access for TypeScript and JavaScript applications.

## Installation
```bash
npm install @example/api-client
```

## Basic Usage
```typescript
import { DefaultApi, Configuration } from '@example/api-client';

// Create API client
const config = new Configuration({
    basePath: 'http://localhost:8080/api',
    accessToken: 'your-token'
});

// Create API instance
const api = new DefaultApi(config);
```

## SDK-Specific Features

### Configuration Options
```typescript
// Configure timeouts
const config = new Configuration({
    basePath: 'http://localhost:8080/api',
    accessToken: 'your-token',
    timeout: 30000,
    proxy: {
        host: 'proxy.example.com',
        port: 8080
    }
});
```

### Type Safety
```typescript
// Strongly typed responses
const user: User = await api.usersUserIdGet('user-123');

// Type-safe request bodies
const newUser: UserCreate = {
    name: 'John Doe',
    email: 'john@example.com'
};
```

### Custom Interceptors
```typescript
// Add request interceptor
api.interceptors.request.use((config) => {
    // Add custom headers
    config.headers['X-Custom-Header'] = 'value';
    return config;
});

// Add response interceptor
api.interceptors.response.use(
    (response) => response,
    (error) => {
        // Handle errors
        return Promise.reject(error);
    }
);
```

### Authentication
```typescript
// API Key Auth
const config = new Configuration({
    apiKey: 'your-api-key'
});

// OAuth2
const config = new Configuration({
    accessToken: 'your-access-token'
});

// Basic Auth
const config = new Configuration({
    username: 'username',
    password: 'password'
});
```

### Retry Configuration
```typescript
// Configure retry policy
const config = new Configuration({
    retryConfig: {
        maxRetries: 3,
        retryDelay: 1000,
        retryCondition: (error) => error.response?.status === 429
    }
});
```

For more examples and general API usage, check out our [API Integration Guide](../../guides/api-integration.md).
</div>
</body>
</html> 