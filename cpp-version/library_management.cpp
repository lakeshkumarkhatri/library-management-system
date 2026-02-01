// Library Management System
// Language: C++
// Type: Console-based application
// Academic Project – Semester 1
// Features: Add, Edit, Search, Delete, File Handling
#include<iostream>
#include<sstream>
#include<fstream>
using namespace std ;
struct Book
	{
		int id;
		string title;
		string author;
		string published;
		string shelf_number;
		
	};
	void display(Book books[],int count );
	void add(Book books[],int &count );
	void edit(Book books[],int count );
	void search(Book books[],int& count );
	void Delete(Book books[],int& count );
	void saveToFile(Book books[], int count);
	void loadFromFile(Book books[], int &count);
	int maximum_books=1000;
	
	int stringToInt(const string &str) 
	{
    int num=0;
    for (int  i=0;i<str.length();++i) 
	{
        if (str[i]>='0'&&str[i]<='9') 
		{
            num=num*10+(str[i]-'0');
        }
    }
    return num;
}
	int main ()
	{
		Book books[maximum_books];
		int count=0;
		loadFromFile(books,count);
		int choice;
		
		cout<<"                 =============================================================="<<endl;
		cout<<"                 =                 Library Books Management system            ="<<endl;
		cout<<"                 =============================================================="<<endl;
		do
		{
			cout<<endl<<"1. Display all added books "<<endl;	
			cout<<"2. Add book to the library "<<endl;
			cout<<"3. Edit book information "<<endl;
			cout<<"4. Search for any library book"<<endl;
			cout<<"5. Delete any library books "<<endl;
			cout<<"6. Exit from Library Management system "<<endl;
			cout<<"\nPlease enter your choice from above : "; cin>>choice; cout<<endl;
			
			switch (choice)
			{
				
				case 1:display(books,count);
				break;
				case 2:add(books,count);
				break;
				case 3:edit(books,count);
				break;
				case 4:search(books,count);
				break;
				case 5:Delete(books,count);
				break;
				case 6:
				saveToFile(books, count);
				cout<<"Program has been exited _ Good bye!"<<endl;
				break;
				default:cout<<"Invalid choice.  Re_enter again.  ";
			}
		}
		while(choice!=6);
		
	return 0;	
	}
	
	void display(Book books[], int count) 
	{
    if(count==0)
	{ 
    	cout<<"Currently No books in the library."<<endl;
        return; 
    }
    cout<<"             ================================================================"<<endl;
	cout<<"             =                      Library Books List                      ="<<endl;
	cout<<"             ================================================================"<<endl;
    for(int i=0;i<count;++i)
	{
        cout<<"ID: "<<books[i].id 
             <<", Title: "<<books[i].title
             <<", Author: "<<books[i].author
             <<", Published: "<<books[i].published
             <<", Shelf Number: "<<books[i].shelf_number
             <<endl;
    }
}

	void add(Book books[], int& count) {
    if(count>=maximum_books) 
	{
        cout<<"Library is full. Cannot add more books." << endl;
        return; 
    }

    Book newbook; 
    cin.ignore(); 
    int max_id = 0;
    for (int i=0;i<count;++i) {
        if(books[i].id>max_id) 
		{
            max_id = books[i].id;
        }
    }
    newbook.id = max_id + 1; 
    cout<<"Book ID: "<<newbook.id<<endl;
    cout<<"Enter Book Title: ";
    getline(cin,newbook.title); 
    cout<<"Enter Book Author: ";
    getline(cin,newbook.author); 
    cout<<"Enter Year Published In: ";
    getline(cin, newbook.published);
    cout<<"Enter Shelf Number: ";
    cin>>newbook.shelf_number;
    books[count++] = newbook; 
    cout << "Book added successfully!" << endl;
}

	void edit(Book books[], int count)
	{
    int id;
    cout<<"\nEnter the ID of the library book to edit: ";
    cin>>id;

    for (int i = 0; i < count; ++i) 
	{ 
        if (books[i].id == id) 
		{ 
            cin.ignore(); 
            cout<<"Enter new Title (current: " << books[i].title << "): ";
            getline(cin,books[i].title);
            cout<<"Enter new Author (current: "<<books[i].author<< "): ";
            getline(cin,books[i].author);
			cout << "Enter new Year Published In (current: "<<books[i].published << "): ";
            getline(cin,books[i].published); 
            cout << "Enter new Shelf Number (current: "<<books[i].shelf_number<<"): ";
            cin>>books[i].shelf_number; 
            
            cout<<"Library book details updated successfully!"<<endl;
            return;
        }
    }

    cout<<"Library book with ID "<<id<<" not found."<<endl;
}

	void search(Book books[],int& count) 
	{
    int id;
    cout<<"\nEnter the ID of the library book to search: ";
    cin>>id; 
    for(int i=0;i<count;++i)
	{
        if(books[i].id==id) 
		{
        cout<<"Library book found: " << endl;
        cout<<"ID: "<<books[i].id 
            <<", Title: "<<books[i].title
            <<", Author: "<<books[i].author
            <<", Published In: "<<books[i].published
    		<<", Shelf Number: "<<books[i].shelf_number	
        	<<endl;
            return; 
        }
    }

    cout<<"Library book with ID "<<id<<" not found."<<endl;
}

	void Delete(Book books[], int& count) {
    int id;
    cout<<"\nEnter the ID of the library book to delete: ";
    cin>>id; 
	for(int i=0;i<count;++i)
	{
        if(books[i].id==id) 
		{
            for (int j=i;j<count-1;++j)
			{
                books[j]=books[j + 1];
            }
            --count;
            
    cout<<"Library book deleted successfully!"<<endl;
        return;
        }
    }

    
    cout<<"Library book with ID "<<id<<" not found."<<endl;
}
void saveToFile(Book books[],int count) 
{
    ofstream outFile("library_data.txt");
    
    for (int i=0;i<count;++i) 
	{
        outFile<<books[i].id<<","
                <<books[i].title<<","
                <<books[i].author<<","
                <<books[i].published<<","
                <<books[i].shelf_number<<endl;
    }

    outFile.close();
    
}

void loadFromFile(Book books[],int &count) 
{
    ifstream inFile("library_data.txt");
   
    count=0;
    while(!inFile.eof()&&count<maximum_books) 
	{
        string line;
        getline(inFile,line);
        if(line.empty()) 
		continue;

        stringstream ss(line);
        string id,title,author,published,shelf_number;

        getline(ss,id,',');
        getline(ss,title,',');
        getline(ss,author,',');
        getline(ss,published,',');
        getline(ss,shelf_number,',');

        books[count].id=stringToInt(id);
        books[count].title=title;
        books[count].author=author;
        books[count].published=published;
        books[count].shelf_number=shelf_number;

        ++count;
    }

    inFile.close();
    
}
