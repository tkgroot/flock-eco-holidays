# Flock Workday

[![Build Status](https://travis-ci.org/flock-community/flock-eco-holidays.svg?branch=master)](https://travis-ci.org/flock-community/flock-eco-holidays)
[![code style: prettier](https://img.shields.io/badge/code_style-prettier-ff69b4.svg?style=flat-square)](https://github.com/prettier/prettier)

## Run

```bash
mvn clean spring-boot:run -Pdevelop -Dspring.profiles.active=local
```

## User

| User                     | Password    |
| ------------------------ | ----------- |
| tommy@sesam.straat       | tommy       |
| pino@sesam.straat        | pino        |
| ieniemienie@sesam.straat | ieniemienie |
| bert@sesam.straat        | bert        |
| ernie@sesam.straat       | ernie       |

## Linting

Use `ktlint` to lint kotlin files or `eslint` for javascript files

```bash
# check code style (it's also bound to "mvn verify")
$> mvn antrun:run@ktlint
  src/main/kotlin/Main.kt:10:10: Unused import

# fix code style deviations (runs built-in formatter)
$> mvn antrun:run@ktlint-format

# fix code styles for js files with eslint
$> npm run lint
```

## Generate secrets

Generate secrets to deploy via travis-ci

```bash
tar cvf secrets.tar ./service-account.json src/main/resources/application-cloud.properties
travis encrypt-file secrets.tar --add
```
