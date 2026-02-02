-- Create fresh database
DROP DATABASE IF EXISTS library_system;
CREATE DATABASE library_system;
USE library_system;

-- Create books table
CREATE TABLE books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    publication_year INT NOT NULL,
    shelf_number VARCHAR(10) NOT NULL
) AUTO_INCREMENT = 1;

-- Insert 50 books with short titles
INSERT INTO books (title, author, publication_year, shelf_number) VALUES
('C++ Basics', 'John Smith', 2015, 'A01'),
('Python Guide', 'Emma Wilson', 2020, 'A02'),
('Java Essentials', 'Mike Johnson', 2018, 'A03'),
('SQL Quick Start', 'Sarah Brown', 2019, 'A04'),
('Web Development', 'Alex Clark', 2022, 'B01'),
('Algorithms', 'David Lee', 2016, 'B02'),
('Data Structures', 'Sophia Kim', 2017, 'B03'),
('Git Basics', 'Ryan Taylor', 2021, 'B04'),
('Linux Commands', 'Emily White', 2019, 'C01'),
('HTML & CSS', 'Daniel Green', 2020, 'C02'),
('JavaScript Intro', 'Olivia Hall', 2021, 'C03'),
('React Basics', 'James Miller', 2022, 'C04'),
('Network Fundamentals', 'Lucas Moore', 2015, 'D01'),
('DB Management', 'Grace Young', 2018, 'D02'),
('Cyber Security', 'Ethan Harris', 2023, 'D03'),
('Mobile Apps', 'Chloe Martin', 2021, 'E01'),
('Cloud Computing', 'Noah Davis', 2022, 'E02'),
('AI Basics', 'Mia Anderson', 2020, 'E03'),
('Machine Learning', 'Liam Thomas', 2021, 'F01'),
('Game Development', 'Amelia Jackson', 2019, 'F02'),
('Python Projects', 'Benjamin Lewis', 2022, 'F03'),
('Java Coding', 'Ella Walker', 2018, 'G01'),
('C# Basics', 'William Allen', 2020, 'G02'),
('Ruby Intro', 'Charlotte Scott', 2017, 'G03'),
('PHP Basics', 'Mason Adams', 2016, 'H01'),
('Swift Guide', 'Harper Nelson', 2021, 'H02'),
('Kotlin Intro', 'Logan Carter', 2022, 'H03'),
('Rust Basics', 'Elizabeth Perez', 2023, 'I01'),
('Go Language', 'Alexander Evans', 2022, 'I02'),
('TypeScript Guide', 'Sofia Collins', 2021, 'I03'),
('Docker Basics', 'Daniela Rivera', 2020, 'J01'),
('Kubernetes Guide', 'Samuel Phillips', 2023, 'J02'),
('AWS Basics', 'Victoria Sanchez', 2022, 'K01'),
('Azure Intro', 'Joseph Rogers', 2021, 'K02'),
('GCP Basics', 'Grace Patterson', 2023, 'K03'),
('UI Design', 'Andrew Flores', 2020, 'L01'),
('UX Principles', 'Abigail Washington', 2021, 'L02'),
('Testing Basics', 'Christopher Baker', 2019, 'M01'),
('Debugging Guide', 'Madison Gonzalez', 2020, 'M02'),
('OOP Concepts', 'Joshua Reed', 2018, 'N01'),
('Functional Programming', 'Avery Sanders', 2019, 'N02'),
('API Design', 'David Morgan', 2021, 'O01'),
('Microservices', 'Scarlett Bell', 2022, 'O02'),
('DevOps Basics', 'Julian Murphy', 2023, 'P01'),
('Agile Methods', 'Luna Rivera', 2020, 'P02'),
('Scrum Guide', 'Carter Cox', 2021, 'Q01'),
('Linux Security', 'Penelope Howard', 2022, 'Q02'),
('Shell Scripting', 'Luke Ward', 2019, 'R01'),
('Data Analysis', 'Hannah Torres', 2020, 'R02'),
('Big Data Basics', 'Zoey Peterson', 2021, 'S01');
