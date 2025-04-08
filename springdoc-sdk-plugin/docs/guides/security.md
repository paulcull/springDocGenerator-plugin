# Security Guide

This guide covers security best practices and features of the SpringDoc Demo API.

## Table of Contents
- [Overview](#overview)
- [Authentication](#authentication)
- [Authorization](#authorization)
- [Data Protection](#data-protection)
- [API Security](#api-security)
- [Best Practices](#best-practices)
- [Compliance](#compliance)

## Overview

The SpringDoc Demo API implements multiple security layers:
- JWT-based authentication
- Role-based authorization
- HTTPS encryption
- Rate limiting
- Input validation
- Security headers

## Authentication

### JWT Authentication

The API uses JSON Web Tokens (JWT) for authentication:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "tokenType": "Bearer"
}
```

### Token Management

1. **Token Generation**
   ```bash
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username": "user", "password": "pass"}'
   ```

2. **Token Refresh**
   ```bash
   curl -X POST http://localhost:8080/api/auth/refresh \
     -H "Authorization: Bearer your-token"
   ```

3. **Token Revocation**
   ```bash
   curl -X POST http://localhost:8080/api/auth/logout \
     -H "Authorization: Bearer your-token"
   ```

## Authorization

### Role-Based Access Control

| Role | Permissions |
|------|-------------|
| ADMIN | Full access to all resources |
| USER | Access to own resources |
| READER | Read-only access |

### Permission Levels

1. **Resource-Level Permissions**
   ```java
   @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
   public User getUser(String userId) {
     // Implementation
   }
   ```

2. **Endpoint-Level Permissions**
   ```java
   @PreAuthorize("hasAuthority('READ_USERS')")
   @GetMapping("/users")
   public List<User> getUsers() {
     // Implementation
   }
   ```

## Data Protection

### Encryption

1. **Data in Transit**
   - TLS 1.2+ required
   - HSTS enabled
   - Perfect Forward Secrecy

2. **Data at Rest**
   - AES-256 encryption
   - Secure key management
   - Regular key rotation

### Input Validation

1. **Request Validation**
   ```java
   @Validated
   public class UserController {
     @PostMapping
     public User createUser(@Valid @RequestBody User user) {
       // Implementation
     }
   }
   ```

2. **Output Sanitization**
   ```java
   @Component
   public class XssSanitizer {
     public String sanitize(String input) {
       // Implementation
     }
   }
   ```

## API Security

### Security Headers

The API includes these security headers:
```
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Content-Security-Policy: default-src 'self'
Strict-Transport-Security: max-age=31536000; includeSubDomains
```

### Rate Limiting

See [Rate Limiting Guide](rate-limiting) for details.

## Best Practices

1. **Authentication**
   - Use strong passwords
   - Implement MFA where possible
   - Rotate tokens regularly
   - Monitor failed attempts

2. **Authorization**
   - Follow principle of least privilege
   - Regular permission reviews
   - Audit logging
   - Separation of duties

3. **Data Protection**
   - Encrypt sensitive data
   - Regular backups
   - Secure key management
   - Data classification

4. **API Security**
   - Input validation
   - Output encoding
   - Error handling
   - Security headers

## Compliance

### Security Standards

The API complies with:
- OWASP Top 10
- GDPR requirements
- PCI DSS (where applicable)
- ISO 27001

### Security Testing

1. **Static Analysis**
   - SonarQube scanning
   - Dependency checking
   - Code review

2. **Dynamic Analysis**
   - Penetration testing
   - Vulnerability scanning
   - Security headers validation

## Next Steps

- [Authentication Guide](authentication) - Detailed authentication information
- [API Integration Guide](api-integration) - Learn how to integrate with the API
- [Error Handling Guide](error-handling) - Understand error handling
- [FAQ](faq) - Frequently asked questions 