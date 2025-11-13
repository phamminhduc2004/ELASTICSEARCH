📘 Program Overview

The “Find in Files” program is a Java desktop application designed to quickly search for a specific keyword across multiple text files within a selected directory. It combines the efficiency of multithreading with the simplicity of a Graphical User Interface (GUI), making it both powerful and user-friendly.

The main purpose of this program is to help users locate information in large collections of log or text files. Instead of manually opening and reading each file, the application automatically scans them in parallel and returns all lines that contain the target keyword.

This project demonstrates important Java programming concepts, including file I/O, concurrency control, synchronization, and GUI development using Swing. It is an excellent example of how multithreading can be used to speed up real-world data processing tasks.

⚙️ Main Features

Keyword Search:
Users can enter any keyword to search for in all .txt files inside the chosen directory.

Parallel Processing:
The program uses multiple threads to read and search several files simultaneously, significantly reducing processing time.

Real-Time Result Display:
Search results appear instantly in the text area of the GUI, showing the file name, line number, and matching line content.

Automatic Result Saving:
All matches are saved automatically to a file named ketqua.txt in the same directory.

Performance Summary:
After the search completes, the program displays the total number of matches, number of files containing the keyword, and total search time.

Error Handling:
The system gracefully handles file reading or permission errors without interrupting the search.

🧩 Program Components

Graphical Interface (Swing): Includes text fields, buttons, and a text area for input and output.

ExecutorService: Manages a pool of worker threads for concurrent file searching.

Semaphore: Controls how many files are processed simultaneously to avoid I/O overload.

BufferedReader / BufferedWriter: Used for efficient file reading and writing.

AtomicInteger: Keeps track of total matches across all threads safely.

🧠 How It Works

The user inputs a keyword and selects a folder containing log or text files.

The program lists all .txt files in that directory.

Using multithreading, each file is assigned to a separate worker thread that reads its content line by line.

Whenever a line contains the keyword, the program records:

File name

Line number

The line content

All matching lines are displayed in the GUI’s result area and written to ketqua.txt in real time.

When the process is finished, the program shows a summary, including:

Total matches found

Number of files containing the keyword

Total time taken (in seconds)

💻 User Guide
Step 1 – Launch the Program

Run the Java file using your IDE or command line:

javac TimKiemLogSongSong_GUI.java
java TimKiemLogSongSong_GUI

Step 2 – Enter Search Information

In the “Find what” field, type the keyword you want to search for.

In the “Directory” field, type or browse to the folder containing .txt files.

Step 3 – Start the Search

Click the “Find All” button.
The program will begin scanning all files in the selected folder.
The GUI remains responsive during the search.

Step 4 – View Results

Matching lines will appear in the main text area, each showing:

log_01.txt – Line 345: [INFO] User login by 99 successful


At the bottom of the window, you’ll see a summary like:

Found 128 lines in 15 files. Time: 2.93 seconds.
Results saved at: C:\Users\Admin\Desktop\cuong\ketqua.txt

Step 5 – Access Saved Results

Open the automatically generated file ketqua.txt in the same directory.
It contains all matching lines, formatted exactly as shown in the results panel.

🧾 Example Scenario

Suppose you have 3,000 log files generated daily by a system, and you want to find all events containing the phrase “login by 99”.
Instead of manually searching each file, you simply:

Select the folder C:\Users\Admin\Desktop\cuong

Enter the keyword: login by 99

Click “Find All”

Within a few seconds, the program will display all occurrences across thousands of files, showing where and when each match appears, and save the complete report to ketqua.txt.

🏁 Conclusion

The “Find in Files” application is a practical and efficient tool for keyword searching within large text datasets.
By combining Java’s multithreading features with a simple and intuitive GUI, it achieves both speed and ease of use.
It can be applied to analyze logs, documents, reports, or any text-based data sources.

This project not only demonstrates strong Java technical skills but also shows how concurrent programming can solve real-world problems in a clear, user-focused way.

