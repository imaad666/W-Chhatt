# W-Chhatt Deployment Guide

This guide will help you deploy your chat application to make it fully functional.

## Architecture Overview

- **Frontend**: Deployed on Vercel (static files)
- **Backend**: Spring Boot application deployed on Railway/Heroku/Render
- **Database**: Supabase PostgreSQL (already configured)

## Option 1: Deploy Backend on Railway (Recommended)

### Step 1: Create Railway Account
1. Go to [railway.app](https://railway.app)
2. Sign up with GitHub
3. Connect your GitHub repository

### Step 2: Deploy Backend
1. Click "New Project" → "Deploy from GitHub repo"
2. Select your W-Chhatt repository
3. Railway will automatically detect it's a Java/Maven project
4. Set these environment variables in Railway dashboard:

```
SPRING_PROFILES_ACTIVE=production
DATABASE_URL=your-database-url-here
DATABASE_USERNAME=your-database-username
DATABASE_PASSWORD=your-database-password
JWT_SECRET=your-super-secret-jwt-key-here
CORS_ALLOWED_ORIGINS=https://your-vercel-app.vercel.app
```

### Step 3: Get Backend URL
- Railway will provide you with a URL like: `https://your-app-name.up.railway.app`

## Option 2: Deploy Backend on Render

### Step 1: Create Render Account
1. Go to [render.com](https://render.com)
2. Sign up with GitHub
3. Connect your repository

### Step 2: Create Web Service
1. Click "New" → "Web Service"
2. Connect your GitHub repository
3. Configure:
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/realtime-chat-1.0.0.jar`
   - **Environment**: Java

### Step 3: Set Environment Variables
Add the same environment variables as Railway.

## Option 3: Deploy Backend on Heroku

### Step 1: Install Heroku CLI
```bash
# macOS
brew install heroku/brew/heroku

# Or download from heroku.com
```

### Step 2: Deploy
```bash
# Login to Heroku
heroku login

# Create app
heroku create your-app-name

# Set environment variables
heroku config:set SPRING_PROFILES_ACTIVE=production
heroku config:set DATABASE_URL=your-database-url-here
heroku config:set DATABASE_USERNAME=your-database-username
heroku config:set DATABASE_PASSWORD=your-database-password
heroku config:set JWT_SECRET=your-super-secret-jwt-key-here

# Deploy
git push heroku main
```

## Step 4: Update Frontend API URLs

Once your backend is deployed, update the frontend to use the correct API URL:

### Update JavaScript API URLs
Edit `public/js/app.js` and replace the placeholder URLs:

```javascript
// Replace this line:
const API_BASE = window.location.origin.includes('vercel.app') 
    ? 'https://your-backend-url.com/api'  // Replace with your actual backend URL
    : 'http://localhost:8080/api';

// With your actual backend URL:
const API_BASE = window.location.origin.includes('vercel.app') 
    ? 'https://your-app-name.up.railway.app/api'  // Your actual Railway URL
    : 'http://localhost:8080/api';
```

### Update WebSocket URL
```javascript
// Replace this line:
const wsUrl = window.location.origin.includes('vercel.app') 
    ? 'https://your-backend-url.com/ws'  // Replace with your actual backend URL
    : '/ws';

// With your actual backend URL:
const wsUrl = window.location.origin.includes('vercel.app') 
    ? 'https://your-app-name.up.railway.app/ws'  // Your actual Railway URL
    : '/ws';
```

## Step 5: Update Vercel CORS Settings

In your backend environment variables, make sure to set:
```
CORS_ALLOWED_ORIGINS=https://your-vercel-app.vercel.app
```

Replace `your-vercel-app` with your actual Vercel app name.

## Step 6: Test the Application

1. **Frontend**: Visit your Vercel URL
2. **Register**: Create a new account
3. **Login**: Login with your credentials
4. **Chat**: Create a room and send messages
5. **WebSocket**: Verify real-time messaging works

## Troubleshooting

### Common Issues:

1. **CORS Errors**: Make sure `CORS_ALLOWED_ORIGINS` includes your Vercel domain
2. **Database Connection**: Verify Supabase credentials are correct
3. **WebSocket Issues**: Ensure WebSocket URL points to your deployed backend
4. **JWT Errors**: Check that JWT_SECRET is set correctly

### Health Check
Visit `https://your-backend-url.com/actuator/health` to check if your backend is running.

## Security Notes

1. **JWT Secret**: Use a strong, random JWT secret in production
2. **Database Password**: Consider using Supabase connection pooling
3. **CORS**: Restrict CORS to only your frontend domain in production
4. **Environment Variables**: Never commit sensitive data to Git

## Cost Considerations

- **Vercel**: Free tier for static sites
- **Railway**: $5/month for hobby plan
- **Render**: Free tier available
- **Heroku**: No free tier, starts at $7/month
- **Supabase**: Free tier with generous limits

## Next Steps

After deployment:
1. Set up a custom domain for both frontend and backend
2. Configure SSL certificates
3. Set up monitoring and logging
4. Implement CI/CD pipeline
5. Add rate limiting and security headers
