This repository contains the code of my FSPA password manager and analyser app which I made for my final project at NCI. 
BEFORE DOWNLOADING ENSURE YOU HAVE JAVA JDK23 OR HIGHER INSTALLED.
It is recommended that you open this project in an IDE like NetBeans as I was having issues attempting to open it with just the JAR file.
If using NetBeans, please use the most up to date version to ensure that everything works as intended. I developed this app using NetBeans IDE 23.
This application was made using Java and makes use of APIs in order to for the facial authentication system and the "HaveIBeenPwned" feature to work.
I'm aware that it is poor practice to have the secret key of the encryption be in a visible config file. However, I left the config file in the upload so that it would work right away.
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
FEATURES
Password Management: Add, delete, and view saved passwords.
Encryption: AES encryption used to securely store passwords.
Facial Authentication:
Users enroll their face on first use.
Facial recognition is required for access on subsequent runs.
Webcam Integration: Utilizes the system's camera to detect and verify user identity.
SQLite Database: Lightweight local database to store credentials.
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Prerequisites **(IMPORTANT)**
Java JDK 23
Webcam
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Technologies Used
Java (JDK 23)- Primary language and runtime
Java Swing- GUI framework for user interaction
SQLite (via JDBC)- Embedded database
OpenCV with JavaCV- Face detection and recognition
AES Encryption- For password security
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
