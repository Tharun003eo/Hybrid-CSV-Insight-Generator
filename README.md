# 🧠 InsightIQ – Hybrid CSV Insight Generator

**InsightIQ** is a hybrid intelligent insight platform built with **Spring Boot (Java)** and **Flask (Python)**. It enables users to upload two CSV files and view a smart analysis on unit sales, revenue trends, and product performance, all visualized in real time.

---

## 🚀 Features

- 📁 Upload and compare any two CSV datasets through the browser
- 📈 Detect and highlight increased or decreased product performance (units/revenue)
- 📊 Visual summary via Chart.js bar graphs
- 🧠 Flask handles all backend insight calculations
- 🧩 Spring Boot hosts the frontend and manages file uploads
- 💾 JSON output saved with timestamp (`jsonFilesTesting` folder)

---

## 📦 Tech Stack

| Layer          | Technology        |
|----------------|-------------------|
| Frontend       | HTML, CSS, JavaScript, Chart.js |
| Main Server    | Spring Boot (Java) |
| Microservice   | Flask (Python)     |
| Data Format    | CSV                |
| Output Format  | JSON               |

---

## 🗂 Directory Structure

```plaintext
InsightIQ/
├── backend/                         # Spring Boot backend
│   ├── src/                         # Java source files
│   ├── uploads/                     # Uploaded CSV files from frontend
│   └── static/                      # Frontend UI (HTML/CSS/JS)
├── microservice/
│   └── analyze_service.py           # Flask microservice for insights
├── uploads/                         # Local file drop for dev/testing
├── jsonFilesTesting/               # Saved JSON insight output files
├── requirements.txt                # Python dependencies
└── README.md
```

---

## ⚙️ Requirements

### ✅ Python (Flask Microservice)
Install Python dependencies from `requirements.txt`:

```bash
pip install -r requirements.txt
```

**`requirements.txt`**
```txt
flask
pandas
openpyxl
flask-cors
```

> ℹ️ The microservice reads the **latest two CSV files** from the `uploads/` folder and saves insights to `jsonFilesTesting/`.

---

### ✅ Java (Spring Boot)
Make sure you have:
- Java 17+
- Maven installed

Then you can build and run the backend:

```bash
cd backend/backend
./mvnw spring-boot:run
```

Spring Boot will:
- Serve `index.html` and `drive.html`
- Accept file uploads
- Connect to Flask for data processing via `http://localhost:5000/analyze`

---

## 🧪 Sample Use

1. Run the **Flask service**:
    ```bash
    cd microservice
    python analyze_service.py
    ```

2. Start the **Spring Boot server**:
    ```bash
    cd backend/backend
    ./mvnw spring-boot:run
    ```

3. Open your browser to:
    ```
    http://localhost:8080
    ```

4. Upload two CSV files.
5. Click "Upload" → Analysis and Chart will appear instantly.
6. Output is saved to:
   ```
   /jsonFilesTesting/analysis_<YYYY-MM-DD>.json
   ```

---

## 📌 Notes

- The Flask microservice is tightly integrated. It auto-processes files from `uploads/`. You may change this path inside `analyze_service.py`.
- The output JSON includes:
  - Total value comparison
  - Unit increase summary
  - Revenue increase/decrease summary

---

