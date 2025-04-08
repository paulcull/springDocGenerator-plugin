# Contributing Guide

Thank you for your interest in contributing to our project! This guide will help you get started with contributing to our Java and TypeScript SDKs.

## Prerequisites

Before you begin, ensure you have the following installed:
1. Java 11 or later
2. Maven 3.6 or later
3. Node.js 14 or later
4. npm 6 or later
5. Git

## Development Setup

### Java SDK
1. Clone the repository
2. Navigate to the Java SDK directory
3. Run `mvn clean install`

### TypeScript SDK
1. Clone the repository
2. Navigate to the TypeScript SDK directory
3. Run `npm install`

## Code Style

### Java
- Follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use 4 spaces for indentation
- Maximum line length of 100 characters
- Use Javadoc for public methods and classes

### TypeScript
- Follow the [TypeScript Style Guide](https://google.github.io/styleguide/tsguide.html)
- Use 2 spaces for indentation
- Maximum line length of 100 characters
- Use JSDoc for public methods and classes

## Testing

### Java
- Write unit tests using JUnit 5
- Run tests with `mvn test`
- Maintain at least 80% test coverage

### TypeScript
- Write unit tests using Jest
- Run tests with `npm test`
- Maintain at least 80% test coverage

## Pull Request Process

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Write or update tests
5. Run all tests
6. Submit a pull request

## Code Review

- All pull requests must be reviewed
- Address review comments promptly
- Keep pull requests focused and small
- Update documentation as needed

## Documentation

- Update relevant documentation
- Add examples for new features
- Keep the API reference up to date
- Document breaking changes

## Release Process

1. Update version numbers
2. Update changelog
3. Create release notes
4. Tag the release
5. Deploy to package repositories

## Additional Resources

- [API Reference](../api-reference/index.md)
- [Examples](examples.md)
- [Community Guide](community.md)
- [FAQ](faq.md)
- [SDKs](../sdks/index.md) - SDK documentation and examples

# Contributing to the Documentation

## Documentation Version Management

The documentation uses a custom version management system that replaces the previous mkdocs-mike plugin. Here's how to work with documentation versions:

### Building Documentation for a Specific Version

To build documentation for a specific version:

```bash
./version-docs.sh <version>
```

For example:
```bash
./version-docs.sh 1.0.0
```

This will:
1. Build the documentation for the specified version
2. Update the versions.json file
3. Create a version selector dropdown

### Version Structure

Documentation versions are stored in the following structure:
```
target/site/
  ├── versions/
  │   ├── latest/
  │   ├── 1.0.0/
  │   └── 0.9.0/
  └── versions.json
```

### Adding a New Version

1. Update the version in `mkdocs.yml`:
```yaml
extra:
  version_dropdown: true
  current_version: <new-version>
  versions:
    - latest
    - <new-version>
    - <previous-versions>
```

2. Build the documentation for the new version:
```bash
./version-docs.sh <new-version>
```

3. Commit the changes:
```bash
git add target/site/versions/<new-version>
git add target/site/versions.json
git commit -m "Add documentation for version <new-version>"
```

### Version Selector

The version selector is automatically added to the navigation bar. It allows users to switch between different versions of the documentation.

### Best Practices

1. Always keep the `latest` version up to date with the current state of the project
2. Create a new version when making significant changes to the API
3. Update the version selector in both `mkdocs.yml` and `version-docs.sh`
4. Test the version selector after adding a new version
5. Keep the versions.json file in sync with the actual documentation versions 