# CLIMate
A Java command-line application that instantly fetches live weather data via a public API and outputs a daily forecast alongside tailored clothing recommendations.

## The Problem
People often waste time checking multiple weather apps and guessing what to wear, or they end up underdressed for unexpected conditions. This application solves that problem by instantly fetching live weather data and providing clear, practical clothing recommendations so users can dress appropriately before stepping outside.

## How It Works
The program runs in the terminal, prompts the user for a city name, fetches live weather conditions via HTTP, parses the JSON response using Gson, and outputs clothing advice using conditional logic.

![System diagram](docs/diagram.png)

## Tech Stack
- **Language:** Java
- **Networking:** Java's built-in HTTP Client
- **Data Parsing:** Gson library
- **Input Handling:** Java `Scanner`
- **API Source:** Public Weather API (e.g., wttr.in or Open-Meteo)

## Setup
Step-by-step instructions to get the project running locally:

bash
git clone [https://github.com/deekshad15/CLIMate.git](https://github.com/deekshad15/CLIMate.git)
cd CLIMate

## Usage

# Compile the Java file
javac Main.java

# Run the program
java Main

Enter your target city name: Chicago
Fetching weather for Chicago...
Current Temperature: 58°F
Conditions: Partly Cloudy

--- CLOTHING RECOMMENDATION ---
It's a bit brisk out! Wear a light jacket or sweater, and comfortable walking shoes.

## Roadmap

Features planned for future releases (pulled directly from the Eisenhower Matrix *Schedule* quadrant):

- [ ] Save user location preferences to a local configuration file
- [ ] Add a multi-day weekly weather trend forecast summary
- [ ] Implement automated desktop alerts for severe weather warnings

## Author

Deeksha Dachepalli
deekshad15
