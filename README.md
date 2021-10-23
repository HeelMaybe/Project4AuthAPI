# Project 4 Auth API
This app is an individual project for the class **ITIS 5280** at UNCC, made by Graham Helton. It consists of a Rest API for the backend and android mobile app for the frontend, with support for:
- Authentication with your email with unique indexs
- Converting of user password to a hash using bcrypt
- MongoDB for the database
- Heroku to run the backend code on the server
- JWT tokens to verify and authenticate users

## Technologies

In order to develop this app, we used:
- An Android Application as the **front-end**
	- For Android 10 (API level 29)
	- Material Design as the style guideline
- Node.js as the **back-end**
	- Jwt as the authentication provider
	- MongoDB as the database
	- bcrypt to hash users passwords

## API Routes
-The /api/signup route is a post request that posts users data and bcrypt hashs the users password, then a new user schema is made and signed in a jwt token to be added to the database and responses with the token. A unique index is made using user emails, so no same user can have the same email in the database. 

-The /api/login route is a get request that receives the users email and password, and a db search is made using the users email. If a user with that email is in the database, then a compare function is made with the users entered password with the hashed password saved in the database. If the passwords match, then the user is logged in and users data is signed in a jwt token and responses with the token.

-The api/profile is protected so no unauthorized users can see authorized users data. This is done with a middleware that takes in the token from either the signup of login call and decodes the jwt token to get the user _id. With the user -id, a database search is made and and responses with the user's data in a JSON Object. 



## Data Design
The main collection in the database is the following:
|Collection Name |Description                    		 |Properties				   |
|----------------|---------------------------------------|-------------------------------------------------------------------------------|
|`Users`     	 |Information of the users       		     | `_id`, `name`, `email`, `password`, `age`, `weight`, `address`|


## External Resources
- Video explaining the app: 
