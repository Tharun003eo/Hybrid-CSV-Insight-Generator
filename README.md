# 🧠 InsightIQ – Hybrid CSV Insight Generator

InsightIQ is a powerful hybrid data analysis platform built using **Spring Boot (Java)** and **Flask (Python)**. It lets users upload two CSV files and generate a visual comparison of unit sales, revenue changes, and insights in real-time.

---

## 🚀 Features

- 📊 Visual insight charts (Chart.js)
- 📈 Detect increased/decreased product performance
- 📁 Upload and compare CSV files via the web UI
- 🔍 Flask microservice for backend data processing
- 🧩 Spring Boot server for serving frontend + routing
- 💾 Insights saved as `.json` files with timestamps

---

## 🗂 Directory Structure

```plaintext
InsightIQ/
├── backend/               # Spring Boot backend
│   ├── src/
│   ├── uploads/           # CSVs uploaded from the browser
│   └── static/            # Frontend files: HTML, JS, CSS
├── microservice/
│   └── analyze_service.py # Flask analysis microservice
├── uploads/               # Local uploads folder
├── jsonFilesTesting/      # Output JSON insights (auto-named with date)
└── README.md




