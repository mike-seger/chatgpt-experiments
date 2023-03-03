# Running
```
set -e
docker-compose up -d
./gradlew clean build
java -cp build/libs/dbquery.jar DbQuery 'SELECT * FROM MY_TABLE'
```
