
const express = require("express");
const path = require("path");
const bodyParser = require("body-parser");

const { connectDB } = require('./Dbconnect/DB');
const { createScan, findlocation, getAllScan } = require("./Controller/scanController")

const app = require('express')();
const server = require('http').createServer(app);

connectDB()

app.use(express.static(path.join(__dirname, "public")));

app.use(function(req, res, next) {
   res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
  res.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
  next();
});

const port = 3001;

app.use(bodyParser.json());

app.get("/", (req, res) => {
    res.json(store)
})
app.get("/back", (req, res) => {
    res.sendFile(path.join(__dirname, "public", "index.html"));
});

app.post("/scan", createScan);
app.post("/findlocation", findlocation);
app.get("/getAllScan", getAllScan);

app.listen(port, () => {
    console.log(`Express server is running on port ${port}`);
});


