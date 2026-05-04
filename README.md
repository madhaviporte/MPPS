# Motor Part Shop Software (MPSS)

MPSS ek simple desktop application hai jo motor parts shop ko manage karne ke liye banaya gaya hai. Iska main purpose inventory track karna, sales record karna aur stock ko properly manage karna hai.

## What this project does

- New motor parts add kar sakte hain  
- Sales record kar sakte hain  
- Stock status dekh sakte hain (IN_STOCK, LOW_STOCK, OUT_OF_STOCK)  
- Low stock hone par alert milta hai  
- Vendor details manage kar sakte hain  
- Daily revenue calculate hota hai  
- Monthly sales graph show hota hai  

## Concept used

Is project me Just-In-Time (JIT) concept use kiya gaya hai. Matlab unnecessary stock store nahi kiya jata aur jab stock kam ho jata hai to system reorder suggest karta hai.

## Technologies used

- Java  
- Java Swing  
- OOP concepts  

## Project Structure

MPSS/
 ├── src/
 │    ├── model/
 │    ├── ui/
 ├── bin/
 ├── .gitignore
 ├── run.bat

## How to run

Step 1: Compile
javac -d bin src\model\*.java src\ui\*.java

Step 2: Run
java -cp bin ui.MainFrame

## Purpose

Ye project learning purpose ke liye banaya gaya hai jisme Java aur OOP concepts ko practical way me use kiya gaya hai.

## Future improvements

- Database integration  
- Login system  
- Multi-user support  
- Web version  

## Author

Madhavi Porte  
Registration No: 2023UG1102  

## Note

Ye project simple hai lekin real-world concept pe based hai jo inventory management ka basic idea deta hai.
