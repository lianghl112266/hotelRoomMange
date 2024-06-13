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
        String url = "jdbc:mysql://localhost:3306/hotelroom?serverTimezone=UTC";
        String username = "root";
        String passWord = "20021028";
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
            //查询
            System.out.println("1.查询入住信息");
            System.out.println("2.查询付款信息");
            //顾客管理
            System.out.println("3.添加一个顾客");
            System.out.println("4.删除一个顾客(通过顾客id)");
            System.out.println("5.更新一个顾客信息(通过顾客Id)");
            System.out.println("6.展示所有顾客");
            //房间管理
            System.out.println("7.添加一个房间(通过房间Id)");
            System.out.println("8.删除一个房间(通过房间Id)");
            System.out.println("9.更新一个房间信息(通过房间Id)");
            System.out.println("10.展示所有房间");
            //入住管理
            System.out.println("11.入住管理");
            System.out.println("12.退房管理");
            //消费管理
            System.out.println("13.增加消费");
            System.out.println("14.删除消费");
            System.out.println("15.查看消费记录");
            System.out.println("其他.退出系统");            
            System.out.println("输入选择的功能:");


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
                    System.out.println("错误输入， 系统提出");
                    func.CAPCmd(2);
                    loop = false;
                    break;
            }
        }
    }
}