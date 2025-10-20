# Add Docker Toolbox to PATH
$env:PATH += ";C:\Program Files\Docker Toolbox"

# Set Docker Machine environment
& "C:\Program Files\Docker Toolbox\docker-machine.exe" env --shell powershell default | Invoke-Expression

# Navigate to project
cd "C:\Users\josep\Development\My projects\e-commerce-backend-app"

# Start Docker Compose
docker-compose -f local-docker-compose.yml up -d

Write-Host "Docker services started. You can now run Spring Boot."
