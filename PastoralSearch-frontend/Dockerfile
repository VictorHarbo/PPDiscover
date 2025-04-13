# Build stage
FROM node:18.19-alpine3.19 as build-stage
WORKDIR /app

# Verify node and npm installation
RUN node --version && npm --version

COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Production stage
FROM nginx:stable-alpine as production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 9000
CMD ["nginx", "-g", "daemon off;"] 