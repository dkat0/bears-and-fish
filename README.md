# Fun with Fish, Bears, & Friend

This lab is from my Post-AP Data Structures course, through which I created a 2D ecosystem simulation where bears and fish compete for survival. The simulation allows multidirectional movement and is made possible through a 2D array and a variety of complex methods which create randomized animal movement and define the logic for handling collisions. This project enabled me to demonstrate my programming knowledge in Java as well as achieve new insights into Java conventions like class structure and creating an elegant and attractive user interface.

# Game Mechanics:
- The river is 20 by 20 spaces (400 total spaces).
- Spikes cannot move, and they kill both bears and fish.
- Bears and Fish can either move left, right, up, or down, or stay.
- When a bear moves to a bear, or fish moves to a fish, they will breed and generate new animal of same type in random null location.
- The game is finished when there are 0 bears left or 0 fish left (or 0 of both).