import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;

public class LibrarySystem {
    public static void main(String[] args) {
        Client client = new Client();
        client.read_input_file(args[0]);
    }
}


class Client{
    // Implement main logic in this class
    Library library;
    Commander commander;

    public Client(){
        library = new Library();
        commander = new Commander();   
    }

    public void read_input_file(String file_path){
        try {
            File file = new File(file_path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            boolean addBookYet = false;
            boolean addUsersYet = false;
            while ((st = br.readLine()) != null) {
                String[] tokens = st.split(" ");
                // Process input here
                if (!addBookYet){
                    int numOfBook = Integer.parseInt(tokens[0]);
                    for (int i = 0; i < numOfBook; i++){
                        int bid = library.getBookCnt();
                        String[] subst = br.readLine().split(" ");;
                        String author = subst[0];
                        String subject = subst[1];
                        Book book = new Book(bid, author, subject);
                        library.addBook(book);
                    }
                    addBookYet = true;
                //     System.out.println("~~~~~ Added books ~~~~~");
                //     library.showLibrary();
                }
                else if (!addUsersYet){
                    int numOfUsers = Integer.parseInt(tokens[0]);
                    for (int i = 0; i < numOfUsers; i++){
                        String[] subst = br.readLine().split(" ");;
                        String userType = subst[0];
                        String userName = subst[1];
                        if (userType.equals("Staff")){
                            Staff staff = new Staff(userName);
                            commander.addUser(staff);
                        }
                        else if (userType.equals("Borrower")){
                            int bookLimit = Integer.parseInt(subst[2]);
                            RealBorrower borrower = new RealBorrower(userName, bookLimit);
                            commander.addUser(borrower);   
                        }
                    }
                    addUsersYet = true;
                    // System.out.println("~~~~~ Added Users ~~~~~");
                    // library.showLibrary();
                }
                else{
                    String commandUserName =  tokens[0];
                    String commandType = tokens[1];
                    Command cmd = new NullCommand();
                    // System.out.println("Command: " + tokens[0] + " " +tokens[1]);

                    if (commandType.equals("addBook")){
                        String[] subst = br.readLine().split(" ");
                        String author = subst[0];
                        String subject = subst[1];
                        cmd = new AddCommand(author, subject);
                    }
                    else if (commandType.equals("removeBook")){
                        int bid = Integer.parseInt(tokens[2]);
                        List<Integer> bids = new ArrayList<>();
                        bids.add(bid);
                        cmd = new RemoveCommand(bids);
                    }
                    else if (commandType.equals("checkout")){
                        String targetUser = tokens[2];
                        String[] subst = br.readLine().split(" ");
                        List<Integer> bids = new ArrayList<>();
                        for (String s : subst){
                            bids.add(Integer.parseInt(s));
                        }
                        User user = commander.findUser(targetUser);
                        cmd = new CheckoutCommand(bids, user);
                    }
                    else if (commandType.equals("return")){
                        int bid = Integer.parseInt(tokens[2]);
                        List<Integer> bids = new ArrayList<>();
                        bids.add(bid);
                        cmd = new ReturnCommand(bids);
                    }
                    else if (commandType.equals("listAuthor")){
                        String field = "author";
                        String listByName = tokens[2];
                        cmd = new ListByCommand(field, listByName);
                    }
                    else if (commandType.equals("listSubject")){
                        String field = "subject";
                        String listByName = tokens[2];
                        cmd = new ListByCommand(field, listByName);
                    }
                    else if (commandType.equals("findChecked")){
                        String field = "borrower";
                        String listByName = tokens[2];
                        cmd = new ListByCommand(field, listByName);
                    }
                    else if (commandType.equals("Borrower")){
                        int bid = Integer.parseInt(tokens[2]);
                        List<Integer> bids = new ArrayList<>();
                        bids.add(bid);
                        cmd = new FindBorrowerCommand(bids);
                    }
                    else{
                        System.out.println("Error");
                    }
                    boolean canDoCommand = commander.addCommand(commandUserName, cmd, library);
                    if (canDoCommand){
                        commander.doCommand(library);
                    }
                    // System.out.println("~~~~~ Run command ~~~~~");
                    // library.showLibrary();
                }

            }
            br.close();
        }
        catch (Exception e){
            System.out.println(e);
            System.out.println("Error");
        }
    }
}

// ===== Book and System =====
class Book{
    int bid;
    String author;
    String subject;
    Borrower borrowedBy;

    Book(int bid, String author, String subject){
        this.bid = bid;
        this.author = author;
        this.subject = subject;
        this.borrowedBy = new NullBorrower();
    }

    void display(){
        System.out.println("ID: " + bid + " Author: " + author + " Subject: " + subject);
    }
}


class Library{
    List<Book> bookList;

    Library(){
        bookList = new ArrayList<Book>();
    }

    int getBookCnt(){
        return bookList.size();
    }

    void checkoutBook(int bid, User user){
        for (Book book : bookList){
            if (book.bid == bid){
                Borrower brrUser = (Borrower) user;
                book.borrowedBy = brrUser;
                brrUser.addBook(book);
            }
        }
    }

    void returnBook(int bid){
        for (Book book : bookList){
            if (book.bid == bid){
                Borrower brrUser = book.borrowedBy;
                book.borrowedBy = (Borrower) new NullBorrower();
                brrUser.removeBook(book);
            }
        }
    }

    void addBook(Book book){
        this.bookList.add(book);
    }

    void removeBook(int bid){
        for(Book book : bookList){
            if(book.bid == bid){
                bookList.remove(book);
                break;
            }
        }
    }

    void listBookByField(String field, String name){
        for(Book book : bookList){
            if(field.equals("author") && book.author.equals(name)){
                book.display();
            }
            else if(field.equals("subject") && book.subject.equals(name)){
                book.display();
            }
            else if(field.equals("borrower") && book.borrowedBy.getName().equals(name)){
                book.display();
            }
        }
    }

    void findBorrower(int bid){
        for(Book book : bookList){
            if(book.bid == bid){
                String bName = book.borrowedBy.getName();
                System.out.println("User: " + bName);
            }
        }
    }

    void showLibrary(){
        System.out.println("========== Library ==========");
        for(Book book : bookList){
            book.display();
            System.out.println("Borrowed by: " + book.borrowedBy.getName());
        }
        System.out.println("========== ===== ==========");
    }

    boolean isBookCheckedOut(int bid){
        for(Book book : bookList){
            if(book.bid == bid){
                return book.borrowedBy instanceof RealBorrower;
            }
        }
        return false;
    }


}

// ===== Users ===== 
interface User{
    String name = "";

    abstract String getName();
}

class Staff implements User{
    String name;

    Staff(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}


// ===== Borrower =====
interface Borrower extends User{
    int bookLimit = 0;
    List<Book> bookList = new ArrayList<>();

    abstract void addBook(Book book);
    abstract void removeBook(Book book);
}

class RealBorrower implements Borrower{
    String name;
    int bookLimit;
    List<Book> bookList;

    RealBorrower(String name, int bookLimit){
        this.name = name;
        this.bookLimit = bookLimit;
        this.bookList = new ArrayList<>();
    }

    public String getName(){
        return name;
    }
    
    public void addBook(Book book){
        bookList.add(book);
    }

    public void removeBook(Book book){
        bookList.remove(book);
    }
}

class NullBorrower implements Borrower{
    String name;
    int bookLimit;
    List<Book> bookList;

    NullBorrower(){
        this.name = "N/A";
        this.bookLimit = Integer.MAX_VALUE;
        this.bookList = new ArrayList<>();
    }
    
    public String getName(){
        return name;
    }

    public void addBook(Book book){
        // do nothing
    }

    public void removeBook(Book book){
        // do nothing
    }
}


// ===== Commands ===== 
class Commander{
    List<Command> commandList = new ArrayList<>();
    List<User> userList = new ArrayList<>();

    void addUser(User user){
        userList.add(user);
    }

    User findUser(String name){
        for(User user : userList){
            if(user.getName().equals(name)){
                return user;
            }
        }
        return new NullBorrower();
    }

    boolean addCommand(String commandUserName, Command command, Library library){
        User commanduser = findUser(commandUserName);
        boolean sudoLevel = commanduser instanceof Staff;

        if (command instanceof AddCommand){
            if (!sudoLevel){
                System.out.println("Borrower can not add book");
                return false;
            }
        }
        else if (command instanceof RemoveCommand){
            if (!sudoLevel){
                System.out.println("Borrower can not remove book");
                return false;
            }
        }
        else if (command instanceof CheckoutCommand){
            CheckoutCommand subcmd = (CheckoutCommand) command;
            if (!sudoLevel){
                System.out.println("Borrower can not checkout book");
                return false;
            }
            else{
                // staff check status
                List<Integer> bids = subcmd.getBids();
                User targetUser = subcmd.getTargetUser();

                for (int bid : bids){
                    if (library.isBookCheckedOut(bid)){
                        System.out.println("Can not checkout since the book is already checked out");
                        return false;
                    }
                }

                // check if reach limit
                RealBorrower subBorrower = (RealBorrower) targetUser;
                if (subBorrower.bookList.size() + bids.size() > subBorrower.bookLimit){
                    System.out.println("Can not checkout since the user reach the limit");
                    return false;
                }
            }
        }
        else if (command instanceof ReturnCommand){
            ReturnCommand subcmd = (ReturnCommand) command;
            if (!sudoLevel){
                System.out.println("Borrower can not return book");
                return false;
            }
            else{
                // staff check status
                if (!library.isBookCheckedOut(subcmd.getBids().get(0))){
                    System.out.println("Can not return since the book isn't checked out");
                    return false;
                }
            }
        }
        else if (command instanceof ListByCommand){
            // if findChecked then check if commandUser equals to targetUser
            ListByCommand subcmd = (ListByCommand) command;
            if (!sudoLevel){
                if (subcmd.getField().equals("borrower") & !subcmd.getListByName().equals(commandUserName)){
                    System.out.println("Borrower can not find books checked out by other users");
                    return false;
                }
            }
        }
        else if (command instanceof FindBorrowerCommand){
            if (!sudoLevel){
                System.out.println("Borrower can not find borrower");
                return false;
            }
        }

        commandList.add(command);
        return true;
    }

    void doCommand(Library library){
        commandList.get(commandList.size() - 1).doTransaction(library);
    }

}

interface Command{
    List<Integer> bids = new ArrayList<>();
    String bookAuthor = "";
    String bookSubject = "";
    String field = "";
    String listByName = "";
    User targetUser = new NullBorrower();

    abstract void doTransaction(Library library);
}


class CheckoutCommand implements Command{
    List<Integer> bids;
    User targetUser;

    CheckoutCommand(List<Integer> bids, User targetUser){
        this.bids = bids;
        this.targetUser = targetUser;
    }

    List<Integer> getBids(){
        return bids;
    }

    User getTargetUser(){
        return targetUser;
    }

    public void doTransaction(Library library){
        for(int bid : bids){
            library.checkoutBook(bid, targetUser);
        }
    }
}


class ReturnCommand implements Command{
    List<Integer> bids;

    ReturnCommand(List<Integer> bids){
        this.bids = bids;
    }

    List<Integer> getBids(){
        return bids;
    }

    public void doTransaction(Library library){
        for(int bid : bids){
            library.returnBook(bid);
        }
    }
}


class AddCommand implements Command{
    String bookAuthor;
    String bookSubject;

    AddCommand(String bookAuthor, String bookSubject){
        this.bookAuthor = bookAuthor;
        this.bookSubject = bookSubject;
    }

    public void doTransaction(Library library){
        Book book = new Book(library.getBookCnt(), bookAuthor, bookSubject);
        library.addBook(book);
    }
}


class RemoveCommand implements Command{
    List<Integer> bids;

    RemoveCommand(List<Integer> bids){
        this.bids = bids;
    }

    public void doTransaction(Library library){
        for(int bid : bids){
            library.removeBook(bid);
        }
    }
}


class ListByCommand implements Command{
    String field;
    String listByName;

    ListByCommand(String field, String listByName){
        this.field = field;
        this.listByName = listByName;
    }

    String getField(){
        return field;
    }

    String getListByName(){
        return listByName;
    }

    public void doTransaction(Library library){
        library.listBookByField(field, listByName);
    }
}


class FindBorrowerCommand implements Command{
    List<Integer> bids;

    FindBorrowerCommand(List<Integer> bids){
        this.bids = bids;
    }

    public void doTransaction(Library library){
        for(int bid : bids){
            library.findBorrower(bid);
        }
    }
}


class NullCommand implements Command{

    NullCommand(){
        // do nothing
    }

    public void doTransaction(Library library){
        // do nothing
    }
}