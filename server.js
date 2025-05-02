const express = require('express');
const http = require('http');
const socketIo = require('socket.io');

const app = express();
const server = http.createServer(app);
const io = require('socket.io')(server, {
    cors: {
      origin: "*",
      methods: ["GET", "POST"]
    }
  });
  
  io.on('connection', (socket) => {
    console.log('a user connected');
  
    // Listen for incoming messages from the frontend
    socket.on('chat message', (msg) => {
      console.log('Message received: ', msg);
  
      // Emit back a response
      io.emit('chat message', `Server received: ${msg}`);
    });
  
    socket.on('disconnect', () => {
      console.log('user disconnected');
    });
  });
  

// Serve static files from the "public" folder
app.use(express.static('public'));

io.on('connection', (socket) => {
  console.log('New client connected');

  // Handle incoming messages from clients
  socket.on('send_message', (message) => {
    io.emit('receive_message', message);
  });

  socket.on('disconnect', () => {
    console.log('Client disconnected');
  });
});

const PORT = process.env.PORT || 5000;
server.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
