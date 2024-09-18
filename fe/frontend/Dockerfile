# Stage 1: Build the Angular application
FROM node:22.6.0 AS build

# Set the working directory
WORKDIR /app

# Copy only package.json to install dependencies
COPY package.json ./

# Install dependencies without using the package-lock.json file
RUN npm install

# Copy the rest of the application code
COPY . .

# Build the Angular application with the correct project name and configuration
RUN npm run build -- --configuration=production --project=frontend

# Stage 2: Serve the application with Nginx
FROM nginx:alpine

# Copy custom Nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built files from the previous stage
COPY --from=build /app/dist/ai-tools-fe/browser /usr/share/nginx/html

# Expose the port Nginx is running on
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
