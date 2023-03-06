# Clips Stats API

REST API service for expose information about Twitch Clips.

## Usage

For run this application need configure connection to Postgres with information for expose.

| option                     | description                | example                                   |
|----------------------------|----------------------------|-------------------------------------------|
| **spring.r2dbc.url***      | url connection to database | r2dbc:postgresql://localhost:10227/twitch |
| **spring.r2dbc.username*** | database username          | twitch                                    |
| **spring.r2dbc.password*** | database password          | twitch                                    |

Example:

```shell
java -jar clips-stats-api-*.jar \
  --spring.r2dbc.url=r2dbc:postgresql://localhost:10227/twitch \
  --spring.r2dbc.username=twitch \
  --spring.r2dbc.password=twitch
```

