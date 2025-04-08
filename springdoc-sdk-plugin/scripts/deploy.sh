#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored messages
print_message() {
    echo -e "${2}${1}${NC}"
}

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check prerequisites
check_prerequisites() {
    print_message "Checking prerequisites..." "$YELLOW"
    
    # Check Docker
    if ! command_exists docker; then
        print_message "Docker is not installed. Please install Docker first." "$RED"
        exit 1
    fi
    
    # Check kubectl
    if ! command_exists kubectl; then
        print_message "kubectl is not installed. Please install kubectl first." "$RED"
        exit 1
    fi
    
    # Check if Docker daemon is running
    if ! docker info >/dev/null 2>&1; then
        print_message "Docker daemon is not running. Please start Docker first." "$RED"
        exit 1
    fi
    
    print_message "All prerequisites are met." "$GREEN"
}

# Build Docker image
build_image() {
    print_message "Building Docker image..." "$YELLOW"
    if docker build -t springdoc-demo:latest .; then
        print_message "Docker image built successfully." "$GREEN"
    else
        print_message "Failed to build Docker image." "$RED"
        exit 1
    fi
}

# Deploy to Kubernetes
deploy_to_kubernetes() {
    print_message "Deploying to Kubernetes..." "$YELLOW"
    
    # Create namespace if it doesn't exist
    kubectl create namespace springdoc-demo --dry-run=client -o yaml | kubectl apply -f -
    
    # Apply Kubernetes configurations
    for file in k8s/*.yaml; do
        print_message "Applying $file..." "$YELLOW"
        kubectl apply -f "$file" -n springdoc-demo
    done
    
    print_message "Deployment completed." "$GREEN"
}

# Wait for deployment to be ready
wait_for_deployment() {
    print_message "Waiting for deployment to be ready..." "$YELLOW"
    kubectl rollout status deployment/springdoc-demo -n springdoc-demo
    print_message "Deployment is ready." "$GREEN"
}

# Get service URLs
get_service_urls() {
    print_message "Service URLs:" "$YELLOW"
    
    # Get the ingress host
    INGRESS_HOST=$(kubectl get ingress springdoc-demo -n springdoc-demo -o jsonpath='{.spec.rules[0].host}')
    
    if [ -n "$INGRESS_HOST" ]; then
        print_message "API: http://$INGRESS_HOST/api" "$GREEN"
        print_message "Documentation: http://$INGRESS_HOST/docs" "$GREEN"
        print_message "Swagger UI: http://$INGRESS_HOST/api/swagger-ui.html" "$GREEN"
    else
        print_message "Could not determine ingress host. Please check your ingress configuration." "$RED"
    fi
}

# Main execution
main() {
    check_prerequisites
    build_image
    deploy_to_kubernetes
    wait_for_deployment
    get_service_urls
}

# Run main function
main 