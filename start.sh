#!/usr/bin/env bash

if ! type "docker" > /dev/null; then
    echo "ERROR: docker is required please install it first: https://github.com/localstack/localstack"
    exit 1
fi

keycloak=$(docker run -d --name=keycloak \
    -e KEYCLOAK_USER=admin \
    -e KEYCLOAK_PASSWORD=admin \
    -e KEYCLOAK_IMPORT=/import/realm-export.json \
    -v $(pwd)/realm-export.json:/import/realm-export.json \
    -p 8080:8080 \
    jboss/keycloak)
sleep 1
docker exec ${keycloak} keycloak/bin/add-user-keycloak.sh -r keycloak-demo -u user -p test1234 --roles yes_we_can
docker restart ${keycloak}

function cleanup {
    echo "Stopping Containers..."
    docker rm -f ${keycloak}
    kill ${gateway}
    kill ${resource}
    kill ${resource-dotnet}
}

trap cleanup EXIT

# start gateway
./gradlew gateway:bootRun &
gateway=$!
# start resource
./gradlew resource:bootRun &
resource=$!
# start frontend

cd resource-dotnet
dotnet clean
dotnet build
dotnet run &
resource-dotnet=$!

cd ../front && npm install && ng serve --aot

