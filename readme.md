# 🧠 Cognify: Cognitive Health Training App  
![Cognify](https://github.com/user-attachments/assets/968cf944-a7c5-47dc-8495-679cc6017517)

Cognify is a **smart cognitive training and monitoring app** designed to support individuals in **rehabilitation and mental performance improvement**.  
Built entirely with **Kotlin** and **Jetpack Compose**, Cognify blends engaging brain games with professional-grade analytics and a two-role architecture for patients and caregivers.

---

## ✨ Core Features

### 👥 Dual-User Architecture — *Patient & Doctor/Caregiver*
| Role | Primary Access | Key Features |
|------|----------------|---------------|
| **Patient** | Home Screen & Games | Play brain games, track progress, earn achievements |
| **Doctor / Caregiver** | Caregiver Portal | Monitor data, assign tasks, track trends |

---

### 🎮 Cognitive Training Games
Target key cognitive domains through fun, data-driven games:

- 🧩 **Memory Match** – Enhances recall & pattern recognition  
- ⚡ **Reaction Test** – Improves focus & processing speed  
- 🔢 **Sudoku Challenge** – Boosts logic & executive function  

Each game adapts its difficulty level based on progress and accuracy.

---

### 🏆 Achievement & Task System
Motivates patients and enables caregivers to set personalized therapy goals.

- **Patient Rewards:** Unlock badges like *Speed Demon* or *Weekly Warrior*  
- **Doctor Tasks:** Assign goals such as *“Complete 3 games before 10 AM”* or *“Reach Level 5 in Sudoku”*

---

### 📊 Progress Dashboard (`ProgressDashboardScreen`)
A visual summary of the patient’s long-term cognitive status.

- Cognitive Score • Game Streak • Accuracy % • Total Sessions  
- Simple bar charts visualize weekly performance trends.

---

### 👩‍⚕️ Caregiver Portal (`CaregiverPortalScreen`)
Professional-grade monitoring interface.

- ⚠️ **Real-Time Alerts:** Missed sessions or sudden score drops  
- 🩺 **Patient Overview:** Daily activity, session time, and mood tracking  
- ✅ **Task Management:** Track completion of assigned therapy goals

---

## 🧠 Tech Stack
| Layer | Technology |
|-------|-------------|
| **Language** | Kotlin |
| **UI Toolkit** | Jetpack Compose |
| **Architecture** | MVVM (ViewModel + Repository) |
| **State Management** | `mutableStateOf` + ViewModel |
| **Database & Auth** | Firebase (Realtime DB / Firestore + Authentication) |
| **Styling** | Custom theming + animated gradient backgrounds |

---

## 🎨 UI / UX Highlights
- 🌈 Animated gradient home screen  
- 📈 Clean dashboard cards for data readability  
- 🎮 Interactive buttons with press/release animations  
- 🧩 Accessible design — high contrast, large touch targets, intuitive layout  

---

## 🛠️ Setup & Installation

1. **Clone the Repository**
   ```bash
   git clone [your-repo-link]
   cd cognify-app
