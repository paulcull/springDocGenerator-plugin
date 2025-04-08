<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title} - SDK Guide</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .sidebar {
            position: fixed;
            top: 0;
            bottom: 0;
            left: 0;
            z-index: 100;
            padding: 48px 0 0;
            box-shadow: inset -1px 0 0 rgba(0, 0, 0, .1);
        }
        .sidebar-sticky {
            position: relative;
            top: 0;
            height: calc(100vh - 48px);
            padding-top: .5rem;
            overflow-x: hidden;
            overflow-y: auto;
        }
        .main-content {
            margin-left: 240px;
            padding: 48px 24px;
        }
        .nav-link {
            color: #333;
            padding: .5rem 1rem;
        }
        .nav-link:hover {
            color: #007bff;
        }
        .nav-link.active {
            color: #007bff;
            font-weight: bold;
        }
        .code-block {
            background-color: #f8f9fa;
            padding: 1rem;
            border-radius: .25rem;
            margin: 1rem 0;
            font-family: monospace;
        }
        .note {
            background-color: #fff3cd;
            border-left: 4px solid #ffc107;
            padding: 1rem;
            margin: 1rem 0;
        }
    </style>
</head>
<body>
    <nav class="sidebar bg-light">
        <div class="sidebar-sticky">
            <ul class="nav flex-column">
                <li class="nav-item">
                    <a class="nav-link" href="index.html">Overview</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="api-docs.html">API Reference</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link active" href="sdk.html">SDK Guide</a>
                </li>
            </ul>
        </div>
    </nav>

    <main class="main-content">
        <h1>SDK Integration Guide</h1>
        <p class="lead">Learn how to integrate the ${title} API into your application</p>

        <div class="card mb-4">
            <div class="card-body">
                <h2>Installation</h2>
                <p>Add the following dependency to your project's build file:</p>
                
                <h3>Maven</h3>
                <div class="code-block">
                    &lt;dependency&gt;<br>
                    &nbsp;&nbsp;&lt;groupId&gt;com.example&lt;/groupId&gt;<br>
                    &nbsp;&nbsp;&lt;artifactId&gt;${title?lower_case}-client&lt;/artifactId&gt;<br>
                    &nbsp;&nbsp;&lt;version&gt;${version}&lt;/version&gt;<br>
                    &lt;/dependency&gt;
                </div>

                <h3>Gradle</h3>
                <div class="code-block">
                    implementation 'com.example:${title?lower_case}-client:${version}'
                </div>
            </div>
        </div>

        <div class="card mb-4">
            <div class="card-body">
                <h2>Configuration</h2>
                <p>Configure the client with your API credentials:</p>
                
                <div class="code-block">
                    // Java example<br>
                    ApiClient client = new ApiClient();<br>
                    client.setBasePath("https://api.example.com");<br>
                    client.setApiKey("your-api-key");<br>
                    <br>
                    // Create API instance<br>
                    PetApi api = new PetApi(client);
                </div>

                <div class="note">
                    <strong>Note:</strong> Replace "your-api-key" with your actual API key. Keep your API key secure and never commit it to version control.
                </div>
            </div>
        </div>

        <div class="card mb-4">
            <div class="card-body">
                <h2>Basic Usage</h2>
                <p>Here's a simple example of how to use the SDK:</p>
                
                <div class="code-block">
                    // Create a new pet<br>
                    Pet newPet = new Pet();<br>
                    newPet.setName("Fluffy");<br>
                    newPet.setStatus(Pet.StatusEnum.AVAILABLE);<br>
                    <br>
                    try {<br>
                    &nbsp;&nbsp;Pet createdPet = api.addPet(newPet);<br>
                    &nbsp;&nbsp;System.out.println("Created pet with ID: " + createdPet.getId());<br>
                    } catch (ApiException e) {<br>
                    &nbsp;&nbsp;System.err.println("Error creating pet: " + e.getMessage());<br>
                    }
                </div>
            </div>
        </div>

        <div class="card mb-4">
            <div class="card-body">
                <h2>Error Handling</h2>
                <p>The SDK throws <code>ApiException</code> for API errors. Handle them appropriately:</p>
                
                <div class="code-block">
                    try {<br>
                    &nbsp;&nbsp;Pet pet = api.getPetById(123L);<br>
                    } catch (ApiException e) {<br>
                    &nbsp;&nbsp;switch (e.getCode()) {<br>
                    &nbsp;&nbsp;&nbsp;&nbsp;case 404:<br>
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("Pet not found");<br>
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;break;<br>
                    &nbsp;&nbsp;&nbsp;&nbsp;case 401:<br>
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("Unauthorized - check your API key");<br>
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;break;<br>
                    &nbsp;&nbsp;&nbsp;&nbsp;default:<br>
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("Error: " + e.getMessage());<br>
                    &nbsp;&nbsp;}<br>
                    }
                </div>
            </div>
        </div>

        <div class="card mb-4">
            <div class="card-body">
                <h2>Best Practices</h2>
                <ul>
                    <li>Always handle API exceptions appropriately</li>
                    <li>Use environment variables or secure configuration management for API keys</li>
                    <li>Implement retry logic for transient failures</li>
                    <li>Cache responses when appropriate to reduce API calls</li>
                    <li>Monitor API usage and implement rate limiting if needed</li>
                </ul>
            </div>
        </div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 