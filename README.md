Using Postgress Database...

Database name : springboot


step 1 : insert role

INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');

step 2 : run application 

step 3 : open Postman for call api

step 4 : post call

		http://localhost:8080/api/auth/signup

		Format Follow: 
		{
			"username": "Test",
			"email": "test@test.com",
			"password": "Test@123",
			"role": ["admin"],

		}

step 4 : public access link  http://localhost:8080/api/test/all

step 5 : Login using: POST /api/auth/signin

			{
				"username": "Test",
				"password": "Test@123",
			}	
	
step 6 : Logout using: POST /api/auth/signout
