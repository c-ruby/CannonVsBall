# CannonVsBall 🎯  
*A Java AWT-based physics simulation game.*

## Overview  
**CannonVsBall** is a Java AWT physics simulation game where the player controls a cannon using mouse movement and clicks to fire projectiles at a bouncing ball. The game demonstrates basic game loop design, event handling, and manual physics simulation.

---

## 🧩 Features & Learning Objectives  
- **Java AWT Animation** — Implemented frame-based animation and simple rendering loops.  
- **Interactive UI** — Built menus, radio buttons, and event listeners for user input.  
- **Game Logic**  
  - Detecting collisions between projectiles and the target ball.  
  - Tracking player score dynamically.  
  - Allowing variable firing angles and projectile speeds.  
- **Physics Simulation**  
  - Manually calculated projectile motion using trigonometric functions.  
  - Simulated gravity on different planets for variable difficulty.  

---

## 💭 Reflection & Areas for Improvement  

### Overall Quality  
This was one of my earlier projects, completed before I fully understood what makes an application *production ready*. Looking back, I realize the value of treating assignment requirements as **acceptance criteria**. It encourages maintainability and clearer design thinking.

### Code Organization  
This project was the last in a sequence of assignments that built upon each other, though that wasn’t clear at the start.  
- Because of that, I didn’t begin with a strong file structure or modular design.  
- As features were added week after week, the code became a large, unwieldy file.  
- Debugging and modifying subclasses within a single file taught me a lasting lesson on **the importance of separation of concerns** and clean architecture.  

---

## 🛠️ Tech Stack  
- **Language:** Java  
- **Libraries:** AWT (Abstract Window Toolkit)  
- **IDE:** Eclipse  

---

## 📸 Screenshot (!!TODO!!)  


---

## 🚀 Future Improvements  
If revisited, I’d like to:  
- Refactor the code into modular classes (e.g., `Cannon`, `Projectile`, `Ball`, `GameEngine`).  
- Introduce smoother graphics using JavaFX or a modern rendering library. 
  - From my understanding, AWT is quite dated, but our curiculum was based on it. I'd like to gain proficiency in contemporary libraries
