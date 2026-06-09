# Stage 1 : Build React/Vite

FROM node:22-alpine AS build

WORKDIR /app

# Copie des fichiers de dépendances

COPY package*.json .

# Installation des dépendances

RUN npm ci

# Copie du reste du projet

COPY . .

# Build de production

ARG VITE_API_URL
ENV VITE_API_URL=$VITE_API_URL

RUN npm run build

# Stage 2 : Nginx

FROM nginx:alpine

# Copie du build généré par Vite

COPY --from=build /app/dist /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
