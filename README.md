# UniTech app

I made a microservice architecture only for demonstrate my knowledge. For such a small application, I think it's better to use a monolith. 

This is a multi-module maven application.
You can run a project without having a database on your machine! Just run the project in your IDE.
I connect this project to my database on my server, you can see in the application.properties 
of the projects unitech-app or unitech-auth-server.

To run the project, do everything in sequence:
0)
```bash
mvn clean package
```
1) RUN unitech-eureka-server
2) RUN unitech-api-gateway
3) RUN unitech-auth-app
4) RUN unitech-app


# --- AUTH APP ---

## USER REGISTER API
```bash
curl --location 'localhost:8000/auth/register' \
--header 'Content-Type: application/json' \
--data '{
    "name":"test",
    "phone":"123",
    "pin":"testUser123",
    "password":"Test782!*&"
}'
```


## LOGIN API
```bash
curl --location 'localhost:8000/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "pin":"testUser123",
    "password":"Test782!*&"
}'
```

## GET USER AUTHOROTIES BY TOKEN API (Paste here in Authorization header your token)
```bash
curl --location 'localhost:8000/auth/authorities' \
--header 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJ1c2VyIjp7ImlkIjoyLCJuYW1lIjoidGVzdCIsInBpbiI6InBvcDEyMyIsInNlbGVjdGVkUm9sZSI6eyJpZCI6MiwibmFtZSI6IlVTRVIifX0sImlhdCI6MTY4NjQ4NTA2NCwiZXhwIjoxNjg2NDg4NjY0fQ.M78kpk5HdEnvbjYVNtYvt83wT1ff7wZi5oSSuQqc2i249TgXWnyCF3BIEVSf6GQEQAaQPHC0SdfiW9FeBE8tkw'
```

## REFRESH TOKEN API. (Paste your token into the request body, in the field 'refreshToken' your refresh token from login api)
```bash
curl --location 'localhost:8000/auth/refresh' \
--header 'Content-Type: application/json' \
--data '{
"refreshToken":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJkYXRhIjp7InVzZXJJZCI6Mn0sImlhdCI6MTY4NjQ4NTA2NCwiZXhwIjoxNjg2NTcxNDY0fQ.f3qvqry8YVYUM-Kj6R-zdpZrXKQDwvjFwUrkG6a9VlvbrBCjjsE8Y6fg8vWLNdKIqyINSWITt7HQGdXo6DXJ6A"
}'
```

# --- UNITECH APP ---
 
## Get all active accounts (Paste here in Authorization header your token)
```bash
curl --location 'localhost:8000/app/accounts' \
--header 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJ1c2VyIjp7ImlkIjoyLCJuYW1lIjoidGVzdCIsInBpbiI6InBvcDEyMyIsInNlbGVjdGVkUm9sZSI6eyJpZCI6MiwibmFtZSI6IlVTRVIifX0sImlhdCI6MTY4NjQ4NTQyOCwiZXhwIjoxNjg2NDg5MDI4fQ.Z403GLaLb9-9s73SuGv_W-KGXPhObHMLMIHS-7VEa-P1wozLHe-CZFf9ZleDrB40p_mi85fdwhE2jWJ7B3JT-Q'
```

## Transfer money between your accounts (Paste here in Authorization header your token)
### This method will automatically convert according to the exchange rate and send money to another user account.
```bash
curl --location --request PUT 'localhost:8000/app/accounts/transfer-money' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJ1c2VyIjp7ImlkIjoyLCJuYW1lIjoidGVzdCIsInBpbiI6InBvcDEyMyIsInNlbGVjdGVkUm9sZSI6eyJpZCI6MiwibmFtZSI6IlVTRVIifX0sImlhdCI6MTY4NjQ4NTQyOCwiZXhwIjoxNjg2NDg5MDI4fQ.Z403GLaLb9-9s73SuGv_W-KGXPhObHMLMIHS-7VEa-P1wozLHe-CZFf9ZleDrB40p_mi85fdwhE2jWJ7B3JT-Q' \
--data '{
"fromAccountId":2,
"toAccountId":1,
"transferMoneyAmount": 100
}'
```


## Get current currency rate value (Paste here in Authorization header your token)
```bash
curl --location 'localhost:8000/app/currency-rate/from/1/to/2' \
--header 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJ1c2VyIjp7ImlkIjoyLCJuYW1lIjoidGVzdCIsInBpbiI6InBvcDEyMyIsInNlbGVjdGVkUm9sZSI6eyJpZCI6MiwibmFtZSI6IlVTRVIifX0sImlhdCI6MTY4NjQ4NTQyOCwiZXhwIjoxNjg2NDg5MDI4fQ.Z403GLaLb9-9s73SuGv_W-KGXPhObHMLMIHS-7VEa-P1wozLHe-CZFf9ZleDrB40p_mi85fdwhE2jWJ7B3JT-Q'
```

