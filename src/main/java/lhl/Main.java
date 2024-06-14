package lhl;

import java.util.Scanner;

public class Main {
    static Func func;
    static Scanner scan;

    public static void main(String[] args) throws Exception {
        init();
        run();
        clean();
    }
    
    public static void init() throws Exception {
        String url = "jdbc:mysql://localhost:3306/hotelRoom?serverTimezone=UTC";
        String username = "root";
        String passWord = "abc";
        func = new Func(url, username, passWord);
        scan = new Scanner(System.in);
    }

    public static void clean() throws Exception {
        scan.close();
        func.close();
    }

    static void run() throws Exception {
        int x = 0;
        boolean loop = true;

        while (loop) {
            System.out.println("1. Query Check-in Details");
            System.out.println("2. Query Payment Details");
            // Customer Management
            System.out.println("3. Add a Customer");
            System.out.println("4. Delete a Customer (by Customer ID)");
            System.out.println("5. Update a Customer (by Customer ID)");
            System.out.println("6. List All Customers");
            // Room Management
            System.out.println("7. Add a Room");
            System.out.println("8. Delete a Room (by Room ID)");
            System.out.println("9. Update a Room (by Room ID)");
            System.out.println("10. List All Rooms");
            // Check-in Management
            System.out.println("11. Check-in");
            System.out.println("12. Check-out");
            // Transaction Management
            System.out.println("13. Add a Transaction");
            System.out.println("14. Delete a Transaction");
            System.out.println("15. View Transaction History");
            System.out.println("Other number will exit system");
            System.out.println("Enter your choice: ");


            x = scan.nextInt();
            scan.nextLine();
            switch (x) {
                case 1:
                    func.QueryCheckInDetails();
                    break;
                case 2:
                    func.QueryPayDetails();
                    break;
                case 3:
                    func.AddCustomer();
                    break;
                case 4:
                    func.DropCustomer();
                    break;
                case 5:
                    func.UpdateCustomerInfo();
                    break;
                case 6:
                    func.ShowAllCustomers();
                    break;
                case 7:
                    func.AddRoom();
                    break;
                case 8:
                    func.DropRoom();
                case 9:
                    func.UpdateRoomInfo();
                    break;
                case 10:
                    func.ShowAllRooms();
                    break;
                case 11:
                    func.CheckIn();
                    break;
                case 12:
                    func.CheckOut();
                    break;
                case 13:
                    func.AddCost();
                    break;
                case 14:
                    func.DropTransaction();
                    break;
                case 15:
                    func.ShowAllTransactions();
                    break;
                default:
                    System.out.println("System Exit...");
                    func.CAPCmd(2);
                    loop = false;
                    break;
            }
        }
    }
}