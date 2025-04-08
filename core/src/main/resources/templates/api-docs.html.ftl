<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${openAPI.info.title} - API Reference</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
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
        .endpoint {
            margin-bottom: 2rem;
            padding: 1rem;
            border: 1px solid #dee2e6;
            border-radius: .25rem;
        }
        .method {
            display: inline-block;
            padding: .25rem .5rem;
            border-radius: .25rem;
            color: white;
            font-weight: bold;
        }
        .method.get { background-color: #28a745; }
        .method.post { background-color: #007bff; }
        .method.put { background-color: #ffc107; color: #212529; }
        .method.delete { background-color: #dc3545; }
        .method.patch { background-color: #6f42c1; }
        .parameters {
            margin-top: 1rem;
        }
        .parameter {
            margin-bottom: .5rem;
        }
        .parameter-name {
            font-weight: bold;
        }
        .parameter-type {
            color: #6c757d;
        }
        .parameter-required {
            color: #dc3545;
            font-weight: bold;
        }
        .swagger-link {
            margin-bottom: 2rem;
        }
        .api-info {
            margin-bottom: 2rem;
        }
        .response {
            margin-top: 1rem;
            padding: 1rem;
            background-color: #f8f9fa;
            border-radius: .25rem;
        }
        .response-code {
            font-weight: bold;
            color: #28a745;
        }
        .schema {
            margin-top: 1rem;
            padding: 1rem;
            background-color: #f8f9fa;
            border-radius: .25rem;
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
                    <a class="nav-link active" href="api-docs.html">API Reference</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="sdk.html">SDK Guide</a>
                </li>
            </ul>
        </div>
    </nav>

    <main class="main-content">
        <div class="api-info">
            <h1>${openAPI.info.title}</h1>
            <p class="lead">${openAPI.info.description!""}</p>
            <p>Version: ${openAPI.info.version}</p>
            <#if openAPI.info.contact??>
                <p>Contact: 
                    <#if openAPI.info.contact.name??>${openAPI.info.contact.name}</#if>
                    <#if openAPI.info.contact.email??> - <a href="mailto:${openAPI.info.contact.email}">${openAPI.info.contact.email}</a></#if>
                    <#if openAPI.info.contact.url??> - <a href="${openAPI.info.contact.url}">${openAPI.info.contact.url}</a></#if>
                </p>
            </#if>
            <#if openAPI.info.license??>
                <p>License: 
                    <#if openAPI.info.license.name??>${openAPI.info.license.name}</#if>
                    <#if openAPI.info.license.url??> - <a href="${openAPI.info.license.url}">${openAPI.info.license.url}</a></#if>
                </p>
            </#if>
        </div>

        <div class="swagger-link">
            <a href="/swagger-ui.html" class="btn btn-primary" target="_blank">
                <i class="bi bi-code-square"></i> Open Swagger UI
            </a>
        </div>

        <#if openAPI.servers?? && openAPI.servers?size gt 0>
            <div class="servers mb-4">
                <h2>Servers</h2>
                <ul>
                    <#list openAPI.servers as server>
                        <li>${server.url} <#if server.description??>- ${server.description}</#if></li>
                    </#list>
                </ul>
            </div>
        </#if>

        <#if openAPI.paths??>
            <h2>Endpoints</h2>
            <#list openAPI.paths.entrySet() as path>
                <#assign pathItem = path.value>
                <div class="endpoint">
                    <h3>${path.key}</h3>
                    
                    <#if pathItem.get??>
                        <div class="method get">GET</div>
                        <#assign operation = pathItem.get>
                        <div class="operation-details">
                            <p>${operation.summary!""}</p>
                            <#if operation.description??>
                                <p>${operation.description}</p>
                            </#if>
                            <#if operation.parameters?? && operation.parameters?size gt 0>
                                <div class="parameters">
                                    <h4>Parameters</h4>
                                    <#list operation.parameters as parameter>
                                        <div class="parameter">
                                            <span class="parameter-name">${parameter.name}</span>
                                            <span class="parameter-type">(${parameter.in})</span>
                                            <#if parameter.required>
                                                <span class="parameter-required">required</span>
                                            </#if>
                                            <#if parameter.description??>
                                                <p>${parameter.description}</p>
                                            </#if>
                                            <#if parameter.schema??>
                                                <div class="schema">
                                                    <p>Schema: ${parameter.schema.type!""}</p>
                                                    <#if parameter.schema.format??>
                                                        <p>Format: ${parameter.schema.format}</p>
                                                    </#if>
                                                </div>
                                            </#if>
                                        </div>
                                    </#list>
                                </div>
                            </#if>
                            <#if operation.responses??>
                                <div class="responses">
                                    <h4>Responses</h4>
                                    <#list operation.responses.entrySet() as response>
                                        <div class="response">
                                            <span class="response-code">${response.key}</span>
                                            <#if response.value.description??>
                                                <p>${response.value.description}</p>
                                            </#if>
                                            <#if response.value.content??>
                                                <#list response.value.content.entrySet() as content>
                                                    <p>Content-Type: ${content.key}</p>
                                                    <#if content.value.schema??>
                                                        <div class="schema">
                                                            <p>Schema: ${content.value.schema.type!""}</p>
                                                            <#if content.value.schema.format??>
                                                                <p>Format: ${content.value.schema.format}</p>
                                                            </#if>
                                                        </div>
                                                    </#if>
                                                </#list>
                                            </#if>
                                        </div>
                                    </#list>
                                </div>
                            </#if>
                        </div>
                    </#if>

                    <#if pathItem.post??>
                        <div class="method post">POST</div>
                        <#assign operation = pathItem.post>
                        <div class="operation-details">
                            <p>${operation.summary!""}</p>
                            <#if operation.description??>
                                <p>${operation.description}</p>
                            </#if>
                            <#if operation.parameters?? && operation.parameters?size gt 0>
                                <div class="parameters">
                                    <h4>Parameters</h4>
                                    <#list operation.parameters as parameter>
                                        <div class="parameter">
                                            <span class="parameter-name">${parameter.name}</span>
                                            <span class="parameter-type">(${parameter.in})</span>
                                            <#if parameter.required>
                                                <span class="parameter-required">required</span>
                                            </#if>
                                            <#if parameter.description??>
                                                <p>${parameter.description}</p>
                                            </#if>
                                            <#if parameter.schema??>
                                                <div class="schema">
                                                    <p>Schema: ${parameter.schema.type!""}</p>
                                                    <#if parameter.schema.format??>
                                                        <p>Format: ${parameter.schema.format}</p>
                                                    </#if>
                                                </div>
                                            </#if>
                                        </div>
                                    </#list>
                                </div>
                            </#if>
                            <#if operation.responses??>
                                <div class="responses">
                                    <h4>Responses</h4>
                                    <#list operation.responses.entrySet() as response>
                                        <div class="response">
                                            <span class="response-code">${response.key}</span>
                                            <#if response.value.description??>
                                                <p>${response.value.description}</p>
                                            </#if>
                                            <#if response.value.content??>
                                                <#list response.value.content.entrySet() as content>
                                                    <p>Content-Type: ${content.key}</p>
                                                    <#if content.value.schema??>
                                                        <div class="schema">
                                                            <p>Schema: ${content.value.schema.type!""}</p>
                                                            <#if content.value.schema.format??>
                                                                <p>Format: ${content.value.schema.format}</p>
                                                            </#if>
                                                        </div>
                                                    </#if>
                                                </#list>
                                            </#if>
                                        </div>
                                    </#list>
                                </div>
                            </#if>
                        </div>
                    </#if>

                    <#if pathItem.put??>
                        <div class="method put">PUT</div>
                        <#assign operation = pathItem.put>
                        <div class="operation-details">
                            <p>${operation.summary!""}</p>
                            <#if operation.description??>
                                <p>${operation.description}</p>
                            </#if>
                            <#if operation.parameters?? && operation.parameters?size gt 0>
                                <div class="parameters">
                                    <h4>Parameters</h4>
                                    <#list operation.parameters as parameter>
                                        <div class="parameter">
                                            <span class="parameter-name">${parameter.name}</span>
                                            <span class="parameter-type">(${parameter.in})</span>
                                            <#if parameter.required>
                                                <span class="parameter-required">required</span>
                                            </#if>
                                            <#if parameter.description??>
                                                <p>${parameter.description}</p>
                                            </#if>
                                            <#if parameter.schema??>
                                                <div class="schema">
                                                    <p>Schema: ${parameter.schema.type!""}</p>
                                                    <#if parameter.schema.format??>
                                                        <p>Format: ${parameter.schema.format}</p>
                                                    </#if>
                                                </div>
                                            </#if>
                                        </div>
                                    </#list>
                                </div>
                            </#if>
                            <#if operation.responses??>
                                <div class="responses">
                                    <h4>Responses</h4>
                                    <#list operation.responses.entrySet() as response>
                                        <div class="response">
                                            <span class="response-code">${response.key}</span>
                                            <#if response.value.description??>
                                                <p>${response.value.description}</p>
                                            </#if>
                                            <#if response.value.content??>
                                                <#list response.value.content.entrySet() as content>
                                                    <p>Content-Type: ${content.key}</p>
                                                    <#if content.value.schema??>
                                                        <div class="schema">
                                                            <p>Schema: ${content.value.schema.type!""}</p>
                                                            <#if content.value.schema.format??>
                                                                <p>Format: ${content.value.schema.format}</p>
                                                            </#if>
                                                        </div>
                                                    </#if>
                                                </#list>
                                            </#if>
                                        </div>
                                    </#list>
                                </div>
                            </#if>
                        </div>
                    </#if>

                    <#if pathItem.delete??>
                        <div class="method delete">DELETE</div>
                        <#assign operation = pathItem.delete>
                        <div class="operation-details">
                            <p>${operation.summary!""}</p>
                            <#if operation.description??>
                                <p>${operation.description}</p>
                            </#if>
                            <#if operation.parameters?? && operation.parameters?size gt 0>
                                <div class="parameters">
                                    <h4>Parameters</h4>
                                    <#list operation.parameters as parameter>
                                        <div class="parameter">
                                            <span class="parameter-name">${parameter.name}</span>
                                            <span class="parameter-type">(${parameter.in})</span>
                                            <#if parameter.required>
                                                <span class="parameter-required">required</span>
                                            </#if>
                                            <#if parameter.description??>
                                                <p>${parameter.description}</p>
                                            </#if>
                                            <#if parameter.schema??>
                                                <div class="schema">
                                                    <p>Schema: ${parameter.schema.type!""}</p>
                                                    <#if parameter.schema.format??>
                                                        <p>Format: ${parameter.schema.format}</p>
                                                    </#if>
                                                </div>
                                            </#if>
                                        </div>
                                    </#list>
                                </div>
                            </#if>
                            <#if operation.responses??>
                                <div class="responses">
                                    <h4>Responses</h4>
                                    <#list operation.responses.entrySet() as response>
                                        <div class="response">
                                            <span class="response-code">${response.key}</span>
                                            <#if response.value.description??>
                                                <p>${response.value.description}</p>
                                            </#if>
                                            <#if response.value.content??>
                                                <#list response.value.content.entrySet() as content>
                                                    <p>Content-Type: ${content.key}</p>
                                                    <#if content.value.schema??>
                                                        <div class="schema">
                                                            <p>Schema: ${content.value.schema.type!""}</p>
                                                            <#if content.value.schema.format??>
                                                                <p>Format: ${content.value.schema.format}</p>
                                                            </#if>
                                                        </div>
                                                    </#if>
                                                </#list>
                                            </#if>
                                        </div>
                                    </#list>
                                </div>
                            </#if>
                        </div>
                    </#if>

                    <#if pathItem.patch??>
                        <div class="method patch">PATCH</div>
                        <#assign operation = pathItem.patch>
                        <div class="operation-details">
                            <p>${operation.summary!""}</p>
                            <#if operation.description??>
                                <p>${operation.description}</p>
                            </#if>
                            <#if operation.parameters?? && operation.parameters?size gt 0>
                                <div class="parameters">
                                    <h4>Parameters</h4>
                                    <#list operation.parameters as parameter>
                                        <div class="parameter">
                                            <span class="parameter-name">${parameter.name}</span>
                                            <span class="parameter-type">(${parameter.in})</span>
                                            <#if parameter.required>
                                                <span class="parameter-required">required</span>
                                            </#if>
                                            <#if parameter.description??>
                                                <p>${parameter.description}</p>
                                            </#if>
                                            <#if parameter.schema??>
                                                <div class="schema">
                                                    <p>Schema: ${parameter.schema.type!""}</p>
                                                    <#if parameter.schema.format??>
                                                        <p>Format: ${parameter.schema.format}</p>
                                                    </#if>
                                                </div>
                                            </#if>
                                        </div>
                                    </#list>
                                </div>
                            </#if>
                            <#if operation.responses??>
                                <div class="responses">
                                    <h4>Responses</h4>
                                    <#list operation.responses.entrySet() as response>
                                        <div class="response">
                                            <span class="response-code">${response.key}</span>
                                            <#if response.value.description??>
                                                <p>${response.value.description}</p>
                                            </#if>
                                            <#if response.value.content??>
                                                <#list response.value.content.entrySet() as content>
                                                    <p>Content-Type: ${content.key}</p>
                                                    <#if content.value.schema??>
                                                        <div class="schema">
                                                            <p>Schema: ${content.value.schema.type!""}</p>
                                                            <#if content.value.schema.format??>
                                                                <p>Format: ${content.value.schema.format}</p>
                                                            </#if>
                                                        </div>
                                                    </#if>
                                                </#list>
                                            </#if>
                                        </div>
                                    </#list>
                                </div>
                            </#if>
                        </div>
                    </#if>
                </div>
            </#list>
        <#else>
            <div class="alert alert-warning">
                No API endpoints found in the OpenAPI specification.
            </div>
        </#if>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 