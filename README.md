# Employee state service
An application for adding employee and changing their state.

Solution for optional Parts [Solution](optional_parts_answers.md)

## Requirements:
[Docker](https://docs.docker.com/install/)
[Docker-compose](https://docs.docker.com/compose/install/)

## running the application:
from the project application directory run
`docker-compose up`
or
`docker-compose up -f {path/to/docker-compose.yml}`

you can find the application swagger page at

- http://localhost:8080/swagger-ui.html
- http://localhost:8080/actuator

Another solution with the same api but using camunda bpmn is provided in the following url

- http://localhost:9090/swagger-ui.html
- http://localhost:9090/actuator

Camunda can be found on with username: admin and password: admin

- http://localhost:9090

## Links
HTTP POST  http://localhost:8080/employee

HTTP PATCH http://localhost:8080/employee/{empCode}

## Employee add request Sample:
HTTP POST http://localhost:8080/employee

```javascript
{
  "firstName": "farouk",
  "lastName": "elabady",
  "email": "farouk@example.com",
  "mobilePhone": "00201001033180",
  "birthDate": "1989-01-14",
  "hireDate": "2021-03-27",
  "gender": "MALE"
}
```
## Employee change state request Sample:

HTTP PATCH http://localhost:8080/employee/{empCode} 

- eg: aac024ca-a0eb-4db5-9b05-94bf8e8f746a
- available Events: ADDED,APPROVED,ACTIVATED

```javascript
{
  "event": "ADDED"
}
```
