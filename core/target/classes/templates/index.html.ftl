<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title} - Documentation</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
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
            margin-left: 250px;
            padding: 2rem;
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
    </style>
</head>
<body>
    <nav class="sidebar bg-light" style="width: 250px;">
        <div class="sidebar-sticky">
            <ul class="nav flex-column">
                <li class="nav-item">
                    <a class="nav-link active" href="index.html">Overview</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="api-docs.html">API Reference</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="sdk.html">SDK Guide</a>
                </li>
                <#if swaggerUIConfig.enabled>
                <li class="nav-item">
                    <a class="nav-link" href="${swaggerUIConfig.path}" target="_blank">Swagger UI</a>
                </li>
                </#if>
            </ul>
        </div>
    </nav>

    <main class="main-content">
        <h1>${title}</h1>
        <p class="lead">${description}</p>
        
        <h2>Overview</h2>
        <p>Welcome to the ${title} documentation. This API provides endpoints for managing ${title} resources.</p>
        
        <h2>Getting Started</h2>
        <p>To get started with the API, you can:</p>
        <ul>
            <li>View the <a href="api-docs.html">API Reference</a> for detailed endpoint documentation</li>
            <li>Check out the <a href="sdk.html">SDK Guide</a> for client library usage</li>
            <#if swaggerUIConfig.enabled>
            <li>Explore the API using <a href="${swaggerUIConfig.path}" target="_blank">Swagger UI</a></li>
            </#if>
        </ul>

        <h2>Authentication</h2>
        <p>All API requests require authentication. Please refer to the <a href="api-docs.html">API Reference</a> for details.</p>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 