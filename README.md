# 🛡️ Secure Journal – A Privacy-Focused Offline Journal

Secure Journal is a **Java desktop application** that lets users write and manage personal journal entries securely on their local machine.  
It features **user authentication, password hashing, local SQLite storage, search & tags, and export/backup options** — all in a simple and modern **Swing UI** styled with **FlatLaf**.  

---

## ✨ Features
- 🔑 **User Authentication** – Register/Login with password hashing (SHA-256)  
- 📂 **Local Storage** – Uses SQLite to securely store entries on your machine  
- 📝 **Journal Management** – Add, edit, delete, and search journal entries  
- 🏷️ **Tags** – Organize entries with tags for quick searching  
- 🌙 **Modern UI** – Swing with FlatLaf dark/light themes  
- 📤 **Export/Backup** – Save all entries as `.txt` or `.csv` files  
- 🔒 **Privacy First** – Works fully offline, no cloud, no trackers  

---

## 🛠️ Tech Stack
- **Language**: Java 17+ (tested on JDK 24)  
- **UI Framework**: Swing + FlatLaf (modern theming)  
- **Database**: SQLite (via JDBC)  
- **Build Tool**: Maven (dependency management & build)  
- **IDE Recommended**: IntelliJ IDEA (or Eclipse)  

---

## ⚙️ Setup & Installation

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/SecureJournal.git
cd SecureJournal
```

### 2. Open in IntelliJ IDEA (or any Java IDE)
- File → Open → Select the `SecureJournal` folder  

### 3. Build with Maven
```bash
mvn clean install
```

### 4. Run the App
In IntelliJ:  
- Open `src/main/java/org/example/Main.java`  
- Click **Run ▶️**  

Or via terminal:
```bash
mvn exec:java -Dexec.mainClass="org.example.Main"
```

---

## 💻 Usage
1. **Register** – Create a new account with a username & password  
2. **Login** – Access your journal entries securely  
3. **Write Entries** – Add title, content, and optional tags  
4. **Search** – Find entries by keyword or tag  
5. **Export** – Backup your entries to `.txt` or `.csv`  

---

## 📂 Project Structure
```
SecureJournal/
│── pom.xml               # Maven dependencies
│── src/
│   └── main/
│       └── java/org/example/
│           ├── Main.java         # App entry point
│           ├── Database.java     # SQLite connection helper
│           ├── UserDAO.java      # Handles user authentication
│           ├── JournalDAO.java   # Handles journal CRUD
│           ├── models/           # Data models (User, JournalEntry)
│           └── ui/               # Swing UI components
└── secure_journal.db    # SQLite database (auto-created)
```

---

## 📦 Export/Backup
- Go to **Menu → Export**
- Choose `.txt` or `.csv`  
- Select save location  

---

## 🚀 Future Improvements
- 🔐 Password Generator for signup  
- 🌓 Toggle between Dark/Light mode  
- 📌 Rich text formatting for entries  
- 🔑 Multi-user profiles  

---

## 🤝 Contributing
Pull requests are welcome!  
1. Fork the repo  
2. Create your feature branch: `git checkout -b feature/new-feature`  
3. Commit changes: `git commit -m "Add new feature"`  
4. Push branch: `git push origin feature/new-feature`  
5. Submit a PR  

---

## 📜 License
This project is licensed under the MIT License – feel free to use, modify, and share.  
