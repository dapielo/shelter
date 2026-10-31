FROM node:trixie AS builder
WORKDIR /build

# estos archivos contienen las dependencias y las versiones de las mismas
COPY package.json package-lock.json ./

# instala las dependencias (ci es de continuous integration)
RUN npm ci

COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /build/dist/shelter/browser/ /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

