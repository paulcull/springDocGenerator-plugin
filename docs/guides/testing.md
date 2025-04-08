# Testing Guide

This guide covers testing strategies and best practices for the SpringDoc Demo API.

## Table of Contents
- [Overview](#overview)
- [Test Types](#test-types)
- [Test Environment](#test-environment)
- [Testing Tools](#testing-tools)
- [Best Practices](#best-practices)
- [Examples](#examples)
- [Troubleshooting](#troubleshooting)

## Overview

The SpringDoc Demo API implements a comprehensive testing strategy:
- Unit tests
- Integration tests
- API tests
- Security tests
- Performance tests
- Documentation tests

## Test Types

### Unit Tests

1. **Controller Tests**
   ```java
   @WebMvcTest(UserController.class)
   public class UserControllerTest {
     @Autowired
     private MockMvc mockMvc;
     
     @Test
     public void testGetUser() throws Exception {
       mockMvc.perform(get("/api/users/1"))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.name").value("John Doe"));
     }
   }
   ```

2. **Service Tests**
   ```java
   @ExtendWith(MockitoExtension.class)
   public class UserServiceTest {
     @Mock
     private UserRepository repository;
     
     @Test
     public void testCreateUser() {
       User user = new User("John Doe");
       when(repository.save(user)).thenReturn(user);
       
       User result = service.createUser(user);
       assertEquals("John Doe", result.getName());
     }
   }
   ```

### Integration Tests

1. **API Integration Tests**
   ```java
   @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
   public class ApiIntegrationTest {
     @LocalServerPort
     private int port;
     
     @Test
     public void testUserFlow() {
       // Create user
       // Get user
       // Update user
       // Delete user
     }
   }
   ```

2. **Database Tests**
   ```java
   @DataJpaTest
   public class UserRepositoryTest {
     @Autowired
     private UserRepository repository;
     
     @Test
     public void testFindByEmail() {
       User user = repository.findByEmail("john@example.com");
       assertNotNull(user);
     }
   }
   ```

### API Tests

1. **OpenAPI/Swagger Tests**
   ```java
   @Test
   public void testApiDocumentation() {
     // Verify OpenAPI documentation
     // Check endpoint coverage
     // Validate schemas
   }
   ```

2. **SDK Tests**
   ```java
   @Test
   public void testGeneratedSdk() {
     // Test Java SDK
     // Test TypeScript SDK
     // Verify client generation
   }
   ```

## Test Environment

### Configuration

1. **Test Properties**
   ```yaml
   spring:
     datasource:
       url: jdbc:h2:mem:testdb
     jpa:
       hibernate:
         ddl-auto: create-drop
   ```

2. **Test Dependencies**
   ```xml
   <dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-test</artifactId>
     <scope>test</scope>
   </dependency>
   ```

### Test Data

1. **Test Fixtures**
   ```java
   @Component
   public class TestData {
     public static User createTestUser() {
       return new User()
         .name("Test User")
         .email("test@example.com");
     }
   }
   ```

2. **Test Databases**
   - H2 for in-memory testing
   - Test containers for Docker-based testing
   - Mock repositories for service testing

## Testing Tools

### Code Coverage

1. **JaCoCo Configuration**
   ```xml
   <plugin>
     <groupId>org.jacoco</groupId>
     <artifactId>jacoco-maven-plugin</artifactId>
     <version>0.8.7</version>
   </plugin>
   ```

2. **Coverage Reports**
   - Line coverage
   - Branch coverage
   - Method coverage
   - Class coverage

### API Testing Tools

1. **Postman Collections**
   - API testing
   - Environment variables
   - Automated testing
   - Documentation

2. **OpenAPI Testing**
   - Schema validation
   - Request/response validation
   - Documentation testing

## Best Practices

1. **Test Organization**
   - Clear test names
   - Logical grouping
   - Consistent structure
   - Documentation

2. **Test Quality**
   - Single responsibility
   - Independent tests
   - Repeatable results
   - Meaningful assertions

3. **Performance**
   - Fast execution
   - Minimal dependencies
   - Efficient setup
   - Proper cleanup

4. **Maintenance**
   - Regular updates
   - Code review
   - Documentation
   - Version control

## Examples

### Controller Test Example

```java
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    public void testGetUser() throws Exception {
        User user = TestData.createTestUser();
        when(userService.getUser(1L)).thenReturn(user);
        
        mockMvc.perform(get("/api/users/1")
            .header("Authorization", "Bearer test-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Test User"));
    }
}
```

### Integration Test Example

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    public void testUserCrud() {
        // Create
        User user = TestData.createTestUser();
        ResponseEntity<User> createResponse = restTemplate.postForEntity(
            "/api/users", user, User.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        // Read
        ResponseEntity<User> getResponse = restTemplate.getForEntity(
            "/api/users/1", User.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // Update
        user.setName("Updated Name");
        restTemplate.put("/api/users/1", user);
        
        // Delete
        restTemplate.delete("/api/users/1");
    }
}
```

## Troubleshooting

### Common Issues

1. **Test Failures**
   - Check test data
   - Verify mocks
   - Review assertions
   - Check environment

2. **Performance Issues**
   - Optimize setup
   - Reduce dependencies
   - Use appropriate tools
   - Monitor resources

3. **Environment Issues**
   - Check configuration
   - Verify dependencies
   - Review logs
   - Test isolation

### Solutions

1. **Debugging Tests**
   - Use debugger
   - Add logging
   - Check test data
   - Review mocks

2. **Improving Tests**
   - Add assertions
   - Improve coverage
   - Optimize performance
   - Update documentation

## Next Steps

- [API Integration Guide](api-integration) - Learn how to integrate with the API
- [Security Guide](security) - Understand security testing
- [Error Handling Guide](error-handling) - Learn about error testing
- [FAQ](faq) - Frequently asked questions 