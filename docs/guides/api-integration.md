# API Integration Guide

This guide provides comprehensive information on integrating with the SpringDoc Demo API.

## Table of Contents
- [Authentication](#authentication)
- [Making Requests](#making-requests)
- [Error Handling](#error-handling)
- [Rate Limiting](#rate-limiting)
- [Best Practices](#best-practices)
- [SDK Usage](#sdk-usage)
- [Examples](#examples)

## Authentication

All API requests require authentication. The API supports JWT (JSON Web Token) authentication.

### Getting a Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "your-username", "password": "your-password"}'
```

### Using the Token

Include the token in the Authorization header:

```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer your-token"
```

## Making Requests

### Base URL

All API endpoints are relative to the base URL:
```
http://localhost:8080/api
```

### Request Headers

Required headers for all requests:
- `Authorization: Bearer <token>`
- `Content-Type: application/json` (for POST/PUT requests)

### Response Format

All responses follow this format:
```json
{
  "data": {}, // Response data
  "meta": {}, // Metadata
  "errors": [] // Error messages if any
}
```

## Error Handling

The API uses standard HTTP status codes and provides detailed error messages.

### Common Error Codes

- `400 Bad Request`: Invalid request parameters
- `401 Unauthorized`: Missing or invalid authentication
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `429 Too Many Requests`: Rate limit exceeded
- `500 Internal Server Error`: Server error

### Error Response Format

```json
{
  "errors": [
    {
      "code": "ERROR_CODE",
      "message": "Human-readable error message",
      "details": {} // Additional error details
    }
  ]
}
```

## Rate Limiting

The API implements rate limiting to ensure fair usage:
- 100 requests per minute per IP address
- Rate limit headers are included in responses:
  - `X-RateLimit-Limit`: Maximum requests per minute
  - `X-RateLimit-Remaining`: Remaining requests
  - `X-RateLimit-Reset`: Time until limit reset

## Best Practices

1. **Authentication**
   - Store tokens securely
   - Implement token refresh logic
   - Handle token expiration gracefully

2. **Error Handling**
   - Implement comprehensive error handling
   - Log errors appropriately
   - Provide user-friendly error messages

3. **Performance**
   - Implement request caching where appropriate
   - Use pagination for large datasets
   - Optimize request frequency

4. **Security**
   - Use HTTPS for all requests
   - Validate all input data
   - Implement proper error handling

## SDK Usage

### Java SDK

```java
import org.openapitools.client.ApiClient;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.User;

// Create API client
ApiClient client = new ApiClient();
client.setBasePath("http://localhost:8080/api");
client.setBearerToken("your-token");

// Create API instance
DefaultApi api = new DefaultApi(client);

// Make API calls
List<User> users = api.usersGet();
```

### TypeScript SDK

```typescript
import { DefaultApi, Configuration } from '@springdoc-demo/sdk';

// Create API client
const config = new Configuration({
  basePath: 'http://localhost:8080/api',
  accessToken: 'your-token'
});

// Create API instance
const api = new DefaultApi(config);

// Make API calls
const users = await api.usersGet();
```

## Examples

### User Management

#### Get All Users
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer your-token"
```

#### Get Specific User
```bash
curl -X GET http://localhost:8080/api/users/user-123 \
  -H "Authorization: Bearer your-token"
```

#### Create User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer your-token" \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "email": "john@example.com"}'
```

#### Update User
```bash
curl -X PUT http://localhost:8080/api/users/user-123 \
  -H "Authorization: Bearer your-token" \
  -H "Content-Type: application/json" \
  -d '{"name": "John Smith", "email": "john@example.com"}'
```

### Pagination
```bash
curl -X GET "http://localhost:8080/api/users?page=1&size=10" \
  -H "Authorization: Bearer your-token"
```

### File Upload
```bash
curl -X POST http://localhost:8080/api/files \
  -H "Authorization: Bearer your-token" \
  -F "file=@example.txt"
```

### Error Handling Example
```bash
# Example of a 404 error
curl -X GET http://localhost:8080/api/users/non-existent-id \
  -H "Authorization: Bearer your-token"
```

## Next Steps

- [Authentication Guide](authentication) - Detailed authentication information
- [Error Handling Guide](error-handling) - Comprehensive error handling guide
- [SDKs](../sdks/index.md) - SDK documentation and examples
- [API Reference](../api-reference/index.md) - Complete API documentation
- [Getting Started](../getting-started/index.md) - Quick start guide
- [Examples](../guides/examples.md) - Code examples 