# FindingFalcone
This is an mainly API Integration task in which I'm fetching API result via GET and POST request using Retrofit and showing/manipulating the UI and then showing the final result on a different screen.

Our main problem is to find the Queen Falcone which is hidden on some planet. King Shan has received intelligence that Queen Falcone is in hiding in one of these 6 planets - DonLon, Enchai, Jebing, Sapir, Lerbin & Pingasor. However he has limited resources at his disposal & can send his army to only 4 of these planets.

Given inputs:

- a planets API (GET request) that list out the planets, and how far they are from main planet from where we will start the search. (https://findfalcone.geektrust.com/planets)
- a vehicles API(GET request) that lists the types of space vehicle at our disposal, how many of each type you have, the maximum distance a vehicle can go (range), and their speed (https://findfalcone.geektrust.com/vehicles).
- a FindFalcone API (post request) that returns whether we were successful in out search or not (we get random response as a planet name using post request) (https://findfalcone.geektrust.com/find).


Rules for the game are as follows:
- User can select 4 planets to search (out of the total 6).
- user can select which space vehicles to send to these planets
- User can see how much time it will take for the vehicles to reach their targets.
- The final result is a game of luck. We get random response as a planet name or error response or Queen not found response (from the 6 available planets) and if the planet is in the list of 4 selected by the user, you get a success message. (https://findfalcone.geektrust.com/find)
- App shows final result of success or failure at end.


1. Planet API: https://findfalcone.geektrust.com/planets

Request type: GET
- There are 6 planets. But King Shan can send vehicles to search in only 4 at a time.
- All are at varying distances from Lengaburu.
- Each planets contains the planet name and distance from king shan planet.

Example: planetsList = listOf(
            Planets("Donlon", 100),
            Planets("Enchai", 200),
            Planets("Jebing", 300),
            Planets("Sapir", 400),
            Planets("Lerbin", 500),
            Planets("Pingasor", 600)
        )

2. Vehicles API:

https://findfalcone.geektrust.com/vehicles

Example: vehiclesList = listOf(
            Vehicles("Space pod", 2, 200, 2),
            Vehicles("Space rocket", 1, 300, 4),
            Vehicles("Space shuttle", 1, 400, 5),
            Vehicles("Space ship", 2, 600, 10)
        )

Request type: GET
- There are 4 types of vehicles
- The units of each vehicle type vary (eg:- there are 2 space pods but only 1 space rocket)
- All have different ranges (maximum distance it can travel). If the range for a vehicle is lesser than the distance to the planet, it cannot be chosen for going to the planet
- All have different speed. Based on the distance to the planet and the speed of the vehicle, time taken for the complete search should be shown
 - Each vehicle contains the vehicle name, total no of vehicles, maximum distance the vehicle can travel and the speed of the vehicle.

3. Finding falcon API token

We need to first get a token
https://findfalcone.geektrust.com/token
Request type: POST
Headers
Accept : application/json
Request body: empty

Sample response 
{
	"token": "afdfdsdgdfbfgnhsgrgs"
}

4. Finding falcon API request

The final result is a game of luck. We will randomly assign a planet to Queen Falcone (from the 6 available planets) and if the planet
is in the list of 4 selected by the user, you get a success message (https://findfalcone.geektrust.com/find)

Sample request body:
find falcone API
(request)
Request type: POST
Headers
Accept : application/json
Content-Type :application/json
Request body :
The request body is a json object which consists of a token, planet_names and vehicle_names. Value of the token is obtained from the previous API call (/token). planet_names is a JSON Array which consists of the planet names you selected from the UI. vehicle _names is also a JSON Array which consists of the vehicle names you have selected from the UI.

5. Finding falcon API response

Sample success response
{
"planet_name" = "Jebing"
"status" = "true"
}

Sample failure respons
{
"status" = false	
}

Sample error response  
{
"error" = "Token not initialized. Please get a new token with the /token API"
}
