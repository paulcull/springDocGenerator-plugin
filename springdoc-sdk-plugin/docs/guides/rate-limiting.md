# Rate Limiting Guide

This guide explains how rate limiting works in the SpringDoc Demo API and how to handle it effectively.

## Table of Contents
- [Overview](#overview)
- [Rate Limits](#rate-limits)
- [Rate Limit Headers](#rate-limit-headers)
- [Handling Rate Limits](#handling-rate-limits)
- [Best Practices](#best-practices)
- [Monitoring](#monitoring)
- [Troubleshooting](#troubleshooting)

## Overview

The API implements rate limiting to:
- Prevent abuse
- Ensure fair usage
- Maintain service stability
- Protect system resources

## Rate Limits

### Global Limits

| Resource | Limit | Window |
|----------|-------|--------|
| All endpoints | 100 requests | 1 minute |
| Authentication | 10 attempts | 1 minute |

### Endpoint-Specific Limits

| Endpoint | Limit | Window |
|----------|-------|--------|
| GET /users | 200 requests | 1 minute |
| POST /users | 50 requests | 1 minute |
| PUT /users/{id} | 100 requests | 1 minute |
| DELETE /users/{id} | 50 requests | 1 minute |

## Rate Limit Headers

All responses include rate limit headers:

```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 2024-04-06T22:00:00Z
```

### Header Descriptions

- `X-RateLimit-Limit`: Maximum requests allowed in the window
- `X-RateLimit-Remaining`: Remaining requests in the current window
- `X-RateLimit-Reset`: Time when the rate limit window resets (ISO 8601)

## Handling Rate Limits

### Java SDK

```java
try {
    // API call
    User user = api.usersGet();
} catch (ApiException e) {
    if (e.getCode() == 429) { // Rate limit exceeded
        // Get reset time from headers
        String resetTime = e.getResponseHeaders()
            .get("X-RateLimit-Reset")
            .get(0);
        
        // Calculate wait time
        long waitTime = calculateWaitTime(resetTime);
        
        // Wait and retry
        Thread.sleep(waitTime);
        user = api.usersGet();
    }
}

private long calculateWaitTime(String resetTime) {
    Instant reset = Instant.parse(resetTime);
    Instant now = Instant.now();
    return Duration.between(now, reset).toMillis();
}
```

### TypeScript SDK

```typescript
try {
    // API call
    const user = await api.usersGet();
} catch (error) {
    if (error.response?.status === 429) {
        // Get reset time from headers
        const resetTime = error.response.headers['x-ratelimit-reset'];
        
        // Calculate wait time
        const waitTime = calculateWaitTime(resetTime);
        
        // Wait and retry
        await new Promise(resolve => setTimeout(resolve, waitTime));
        const user = await api.usersGet();
    }
}

function calculateWaitTime(resetTime: string): number {
    const reset = new Date(resetTime).getTime();
    const now = Date.now();
    return Math.max(0, reset - now);
}
```

## Best Practices

1. **Request Optimization**
   - Batch requests when possible
   - Use pagination for large datasets
   - Implement caching
   - Reduce unnecessary requests

2. **Error Handling**
   - Implement exponential backoff
   - Set maximum retry attempts
   - Log rate limit events
   - Monitor rate limit usage

3. **Client Implementation**
   - Track rate limit headers
   - Implement request queuing
   - Use connection pooling
   - Handle concurrent requests

4. **Monitoring**
   - Track rate limit usage
   - Monitor error rates
   - Set up alerts
   - Analyze usage patterns

## Monitoring

### Rate Limit Metrics

| Metric | Description |
|--------|-------------|
| requests.total | Total requests made |
| requests.rate_limited | Requests that hit rate limits |
| rate_limit.remaining | Average remaining requests |
| rate_limit.reset_time | Average time until reset |

### Monitoring Tools

1. **Application Logs**
   ```json
   {
     "timestamp": "2024-04-06T21:00:00Z",
     "level": "WARN",
     "message": "Rate limit exceeded",
     "endpoint": "/users",
     "remaining": 0,
     "reset": "2024-04-06T22:00:00Z"
   }
   ```

2. **Monitoring Dashboard**
   - Request rate over time
   - Rate limit usage
   - Error distribution
   - Response times

## Troubleshooting

### Common Issues

1. **Rate Limit Exceeded**
   - Check request frequency
   - Verify client implementation
   - Review usage patterns
   - Consider request optimization

2. **Header Issues**
   - Missing rate limit headers
   - Invalid header values
   - Time synchronization
   - Header parsing errors

3. **Performance Impact**
   - High latency during rate limiting
   - Connection timeouts
   - Resource exhaustion
   - Client-side bottlenecks

### Solutions

1. **Optimize Requests**
   - Implement caching
   - Use batch operations
   - Reduce request frequency
   - Implement request queuing

2. **Handle Errors**
   - Implement retry logic
   - Use exponential backoff
   - Set appropriate timeouts
   - Monitor error rates

3. **Monitor Usage**
   - Track rate limit headers
   - Analyze usage patterns
   - Set up alerts
   - Review logs regularly

## Next Steps

- [API Integration Guide](api-integration) - Learn how to integrate with the API
- [Error Handling Guide](error-handling) - Understand error handling
- [Authentication Guide](authentication) - Learn about authentication
- [FAQ](faq) - Frequently asked questions 