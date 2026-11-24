FROM node:26-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx/nginx-unprivileged:alpine3.24
COPY --chown=nginx:nginx --from=build /app/dist/frontend/browser/ /usr/share/nginx/html/
EXPOSE 8080