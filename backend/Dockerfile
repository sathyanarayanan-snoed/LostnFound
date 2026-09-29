FROM node:20-slim
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
EXPOSE 8080
VOLUME ["/app/data", "/app/uploads"]
CMD ["node", "server.js"]
