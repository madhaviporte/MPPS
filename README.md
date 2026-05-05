# Motor Part Shop Software (MPSS)

MPSS is a simple desktop application designed to manage a motor parts shop. Its main purpose is to track inventory, record sales, and manage stock efficiently.

## What this project does

* Add new motor parts
* Record sales
* View stock status (IN_STOCK, LOW_STOCK, OUT_OF_STOCK)
* Get alerts when stock is low
* Manage vendor details
* Calculate daily revenue
* Display monthly sales graph

## Concept Used

This project uses the Just-In-Time (JIT) concept. It means unnecessary stock is not stored, and when stock levels are low, the system suggests reordering.

## Technologies Used

* Java
* Java Swing
* OOP concepts

## Project Structure

MPSS/
├── src/
│    ├── model/
│    ├── ui/
├── bin/
├── .gitignore
├── run.bat

## How to Run

Step 1: Compile
javac -d bin src\model*.java src\ui*.java

Step 2: Run
java -cp bin ui.MainFrame

## Purpose

This project is built for learning purposes, demonstrating how Java and OOP concepts can be applied in a practical way.

## Future Improvements

* Database integration
* Login system
* Multi-user support
* Web version

## Note

This is a simple project, but it is based on real-world concepts and provides a basic understanding of inventory management.
