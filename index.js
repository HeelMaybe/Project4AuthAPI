const express = require("express");
const app = express();
const port = 3000;
const jwt = require("jsonwebtoken");
const jwtSecret = "Sig";
const dotenv = require("dotenv");
const { MongoClient, ObjectId } = require("mongodb");
const bcrypt = require("bcrypt");
const saltRounds = 10;

//global MiddleWare
app.use(express.urlencoded()); //url encoded data key=value&key=value
app.use(express.json()); //JSON encoded data key=value&key=value
app.use(express.text()); //Text encoded data key=value&key=value

dotenv.config();
const uri = process.env.DB_CONNECT;
const client = new MongoClient(uri, {
  useNewUrlParser: true,
});
let userCollection;
client.connect((err) => {
  userCollection = client.db("Users").collection("auth");
});

const jwtValidationMiddleware = (req, res, next) => {
  console.log("jwtValidationMiddleware");

  let token = req.header("x-jwt-token");
  if (token) {
    try {
      //2. validate token
      let decoded = jwt.verify(token, jwtSecret);
      req.decodedToken = decoded;
    } catch (error) {
      res
        .status(401)
        .send({ error: "Invalid Token Provided", fullError: error });
    }
  } else {
    res.status(401).send({ error: "Token is required" });
  }
  next();
};

app.get("/", (req, res) => {
  res.send("Hello World");
});

app.post("/api/signup", (req, res) => {
  let name = req.body.name;
  let email = req.body.email;
  let password = req.body.password;
  let age = req.body.age;
  let weight = req.body.weight;
  let address = req.body.address;

  //Hash password to store in DB
  bcrypt.hash(password, saltRounds).then(function (hash) {
    if (name && email && password) {
      MongoClient.connect(uri, function (err, db) {
        if (err) throw err;

        //Create new user
        var newUser = {
          name: name,
          email: email,
          password: hash,
          age: age,
          weight: weight,
          address: address,
        };
        //unique email index to stop dublicate users in DB
        userCollection.createIndex({ email: 1 }, { unique: true });
        //insert new user into database
        userCollection.insertOne(newUser, function (error, insertedUser) {
          if (error) {
            res.status(500).send({ error: error.message });
          } else {
            let token = jwt.sign(
              {
                _id: insertedUser.insertedId,
                name: newUser.name,
                email: newUser.email,
                password: newUser.password,
                age: newUser.age,
                weight: newUser.weight,
                address: newUser.address,
                exp: Math.floor(Date.now() / 1000) + 60 * 60,
              },
              jwtSecret
            );
            db.close();
            res.send({
              status: "ok",
              token: token,
              insertedUser: insertedUser,
            });
          }
        });
      });
    } else {
      res.status(400).send({ error: "All information is required" });
    }
  });
});

app.get("/api/login", (req, res) => {
  let email = req.query.email;
  let password = req.query.password;
  if (email && password) {
    MongoClient.connect(uri, function (err, db) {
      if (err) {
        throw err;
      } else {
        userCollection.findOne({ email: email }, async function (error, user) {
          if (error) {
            res.status(500).send({ error: error.message });
          } else {
            if (user) {
              const match = await bcrypt.compare(password, user.password);
              if (match) {
                //login
                let token = jwt.sign(
                  {
                    _id: user._id,
                    name: user.name,
                    email: user.email,
                    password: user.password,
                    age: user.age,
                    weight: user.weight,
                    address: user.address,
                    exp: Math.floor(Date.now() / 1000) + 60 * 60,
                  },
                  jwtSecret
                );
                db.close();
                res.send({
                  token: token,
                });
              }
            } else {
              res.status(404).send({ error: "User not found" });
            }
            db.close();
          }
        });
      }
    });
  }
});

//protected
app.get("/api/profile", jwtValidationMiddleware, (req, res) => {
  //1. Recieve token.
  let decodedToken = req.decodedToken;

  if (decodedToken) {
    MongoClient.connect(uri, function (err, db) {
      if (err) {
        throw err;
      } else {
        userCollection.findOne(
          { _id: ObjectId(decodedToken._id) },
          function (error, user) {
            if (error) {
              res.status(500).send({ error: error.message });
            } else {
              if (user) {
                res.send({ user: user });
              } else {
                res.status(404).send({ error: "User not found" });
              }
              db.close();
            }
          }
        );
      }
    });
  } else {
    res.status(400).send({ error: "Invalid User" });
  }

  console.log(decodedToken);
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Listening at port http://localhost:${PORT}`);
});
