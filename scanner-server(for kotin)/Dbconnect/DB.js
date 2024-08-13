const mongoose = require('mongoose');

function connectDB() {
  // mongoose.connect('mongodb://192.168.1.195:27017/Inventory', {
  mongoose.connect('mongodb://localhost:27017/Inventory', {
      connectTimeoutMS: 30000,
  }).then(() => {
      console.log('Connected to MongoDB');
  }).catch((error) => {
      console.error('Error connecting to MongoDB:', error);
  });
  
}

module.exports = { connectDB };