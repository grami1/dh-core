# DiHome core service
The service is a core BE part of the [DiHome](https://github.com/grami1/dihome) project. It implements the main functionality for handling the data from sensors
and manages the endpoints required for the [front-end](https://github.com/grami1/dh-web).

## Authentication
All endpoints are secured and require Bearer tokens in authorization header. The authentication process is built
on top of the [AWS Cognito user pool](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-identity-pools.html).
For now, all users are managed manually by an administrator. The service has internal user API that maps a cognito user to a dihome user.

## Available endpoints
1. Get user
2. Create user
3. Get areas
4. Get area
5. Create area
6. Get weather
7. Get status

The details are provided in the swagger doc: /swagger-ui.html

## How to run locally
There are a few requirements to run the service locally:
- Configured cognito user pool. The jwt issuer url and application id (jwt audience) are mandatory for token validation.
- Account in [weatherapi.com](https://www.weatherapi.com/) with valid key

### Steps:
1. Create `.env` file from the template (`.env.temp`) and fill required information.
2. Build jar file:  
`./gradlew bootJar`
3. Run docker containers for the service and postgres from docker-compose file:  
`docker compose up -d`  
The dh-core service will be available on the 8080 port.

## Token generation in cognito
In order to send request to dh-core service, the valid token from cognito is required.  
The flow for getting a token from cognito:
1. Login in cognito with existing user credentials by url  
https://app_name.auth.region.amazoncognito.com/login?response_type=code&client_id=your_client_id&redirect_uri=your_redirect_uri.  
The response will contain an authorization code as a part of redirect uri: `code=AUTHORIZATION_CODE`
2. Get id token from cognito
```
curl --location --request POST 'https://app_name.auth.region.amazoncognito.com/oauth2/token' \
    --header 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode 'grant_type=authorization_code' \
    --data-urlencode 'client_id=your_clien_id' \
    --data-urlencode 'code=your_authorization_code' \
    --data-urlencode 'redirect_uri=your_redirect_uri'
```