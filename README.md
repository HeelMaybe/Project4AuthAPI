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


## Data Design
The main collection in the database is the following:
|Collection Name |Description                    		 |Properties				   |
|----------------|---------------------------------------|-------------------------------------------------------------------------------|
|`Users`     		 |Information of the users       		     | `_id`, `name`, `email`, `password`, `age`, `weight`, `address`|


## External Resources
- Video explaining the app: 
