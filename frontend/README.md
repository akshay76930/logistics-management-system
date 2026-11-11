# Fleetflow Dashboard

An Angular 21, responsive dashboard for the Spring Boot logistics API in this
repository. It includes a JWT sign-in screen, live summary cards, searchable
today-records table, and mobile layouts.

## Run locally

Start the backend from the repository root with its required `APP_AUTH_USERNAME`,
`APP_AUTH_PASSWORD_BCRYPT`, and `JWT_SECRET_BASE64` environment variables set.
Then open another terminal:

```powershell
cd frontend
npm install
npm start
```

Open [http://localhost:4200](http://localhost:4200). In development Angular
proxies `/api` requests to `http://localhost:8080`, where the Spring Boot
backend must be running. Use the backend-configured username and password to
sign in. The JWT is kept in browser local storage and cleared after signing out
or when the API reports an expired token.

Production builds can be created with `npm run build`.

## Dashboard API integration

The app loads these existing JWT-protected endpoints:

- `GET /api/dashboard/summary`
- `GET /api/dashboard/details`
- `POST /api/auth/login` for the access token

The API base path is `/api`. Set up the matching reverse proxy for this path
when deploying the Angular app outside the local Angular development server.
