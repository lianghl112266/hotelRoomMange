package lhl;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Func {

    public Connection coon;
    public Statement exec;
    public Scanner scan;

    public Func(String url, String username, String password) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        coon = DriverManager.getConnection(url, username, password);
        exec = coon.createStatement();
        scan = new Scanner(System.in);

    }
    
    public void close() throws Exception {
        coon.close();
        exec.close();
        scan.close();
    }
    

    public void CAPCmd(int seconds) throws Exception {
        TimeUnit.SECONDS.sleep(seconds);
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }


    public Date convertToSqlDate(LocalDateTime localDateTime) {
        LocalDate localDate = localDateTime.toLocalDate();
        Date sqlDate = new Date(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        return sqlDate;
    }


    public void QueryCheckInDetails() throws Exception {
        CAPCmd(0);
        String sql = "SELECT ci.`checkInId`, c.name, r.location, r.`type`, r.price, ci.`checkInTime`, ci.`checkOutTime`, DATEDIFF(ci.`checkOutTime`, ci.`checkInTime`) as stayDay FROM checkIns ci LEFT JOIN rooms r ON ci.roomId = r.id LEFT JOIN customers c ON ci.customerId = c.id";
        ResultSet items = exec.executeQuery(sql);

        while (items.next()) {
            Integer checkInId = items.getInt("checkInId");
            String name = items.getString("name");
            String location = items.getString("location");
            String type = items.getString("type");
            Double price = items.getDouble("price");
            Date inDate = items.getDate("checkInTime");
            Date outDate = items.getDate("checkOutTime");
            Integer stayDay = items.getInt("stayDay");

            System.out.println("---------------------------------------");
            System.out.println("Check-in ID: " + checkInId);
            System.out.println("Name: " + name);
            System.out.println("Location: " + location);
            System.out.println("Type: " + type);
            System.out.println("Price: " + price);
            System.out.println("Check-in Date: " + inDate);
            System.out.println("Check-out Date: " + outDate);
            System.out.println("stayDay: " + stayDay + "天");
        }

        System.out.println("展示完毕， 按任意数字键推出");
        scan.nextInt();
        CAPCmd(0);
    }


    void QueryPayDetails() throws Exception {
        CAPCmd(0);
        String sql = "SELECT t.id, t.`paymentType`, t.`paymentDescription`, t.`paymentTime`, t.`customerId`, c.name, t.`amountDue`, t.`amountPaid` FROM transactions t LEFT JOIN customers c ON t.`customerId` = c.id";

        ResultSet items = exec.executeQuery(sql);

        while (items.next()) {
            int id = items.getInt("id");
            String paymentType = items.getString("paymentType");
            String paymentDescription = items.getString("paymentDescription");
            Timestamp paymentTime = items.getTimestamp("paymentTime");
            int customerId = items.getInt("customerId");
            String customerName = items.getString("name");
            double amountDue = items.getDouble("amountDue");
            double amountPaid = items.getDouble("amountPaid");

            System.out.println("---------------------------------------");
            System.out.println("pay id: " + id);
            System.out.println("paymentType: " + paymentType);
            System.out.println("paymentDescription: " + paymentDescription);
            System.out.println("paymentTime: " + paymentTime);
            System.out.println("customerId: " + customerId);
            System.out.println("customerName: " + customerName);
            System.out.println("amountDue: " + amountDue);
            System.out.println("amountPaid: " + amountPaid);
        }

        System.out.println("展示完毕， 按任意数字键推出");
        scan.nextInt();
        CAPCmd(0);
    }


    void AddCustomer() throws Exception {
        CAPCmd(0);
        System.out.println("input customer information:\n");
        System.out.println("name:");
        String name = scan.nextLine().strip();
        System.out.println("gender(only Male or Female):");
        String gender = scan.nextLine().strip();
        System.out.println("idNumber(Length must is 18):");
        String idNumber = scan.nextLine().strip();
        System.out.println("phoneNumber(Length must is 11):");
        String phoneNumber = scan.nextLine().strip();

        if (!gender.equals("Male") && !gender.equals("FeMale")) {

            System.out.println(
                    "your input has error near gender, input any number key and system will go back dashboard\n");
            scan.nextInt();
            CAPCmd(0);
            return;
        }
        if (idNumber.length() != 18) {
            System.out.println(
                    "your input has error near idNumber, input any number key and system will go back dashboard\n");
            scan.nextInt();
            CAPCmd(0);
            return;
        }
        if (phoneNumber.length() != 11) {
            System.out.println(
                    "your input has error near phoneNumber, input any number key and system will go back dashboard\n");
            scan.nextInt();
            CAPCmd(0);
            return;
        }

        try {
            String sql = "INSERT INTO customers (name, gender, idNumber, phoneNumber) VALUES (?, ?, ?, ?)";
            PreparedStatement exec = coon.prepareStatement(sql);
            exec.setString(1, name);
            exec.setString(2, gender);
            exec.setString(3, idNumber);
            exec.setString(4, phoneNumber);
            exec.executeUpdate();
            System.out.print("successful add customer\n");

        } catch (Exception e) {
            System.out.println("fail add customer due to" + e.getMessage());
        } finally {
            System.out.println("按任意数字键推出");
            scan.nextInt();
            CAPCmd(0);
        }
    }


    void DropCustomer() throws Exception {
        CAPCmd(0);
        System.out.println("input customer id:");
        Integer id = scan.nextInt();

        String sql = "DELETE FROM customers WHERE id = (?);";
        PreparedStatement exec = coon.prepareStatement(sql);
        exec.setInt(1, id);
        int row = exec.executeUpdate();
        if (row == 1) {
            System.out.print("successful drop customer\n");
        } else {
            System.out.print("fail to drop customer due to id absence\n");
        }

        System.out.println("按任意数字键推出");
        scan.nextInt();
        CAPCmd(0);
    }


    void UpdateCustomerInfo() throws Exception {
        CAPCmd(0);
        System.out.println("expect customer id:");
        Integer id = scan.nextInt();

        System.out.println("input customer new information");
        System.out.println("id:");
        Integer newId = scan.nextInt();
        scan.nextLine();

        System.out.println("name:");
        String name = scan.nextLine();

        System.out.println("gender(only Male or Female):");
        String gender = scan.nextLine();

        System.out.println("idNumber(Length must is 18):");
        String idNumber = scan.nextLine();

        System.out.println("phoneNumber(Length must is 11):");
        String phoneNumber = scan.nextLine();

        try {
            String sql = "UPDATE customers SET id = (?), gender = (?), `idNumber` = (?), name = (?), `phoneNumber` = (?) WHERE id = (?)";
            PreparedStatement exec = coon.prepareStatement(sql);
            exec.setInt(1, newId);
            exec.setString(2, gender);
            exec.setString(3, idNumber);
            exec.setString(4, name);
            exec.setString(5, phoneNumber);
            exec.setInt(6, id);
            exec.executeUpdate();
            System.out.print("successful update customer\n");

        } catch (Exception e) {
            System.out.println("fail update customer due to" + e.getMessage());
        } finally {
            System.out.println("按任意数字键推出");
            scan.nextInt();
            CAPCmd(0);
        }
    }


    void ShowAllCustomers() throws Exception {
        CAPCmd(0);
        String sql = "SELECT * FROM customers";
        ResultSet items = exec.executeQuery(sql);
        while (items.next()) {
            Integer id = items.getInt("id");
            String name = items.getString("name");
            String idNumber = items.getString("idNumber");
            String phoneNumber = items.getString("phoneNumber");

            System.out.println("---------------------------------------");
            System.out.println("customer id: " + id);
            System.out.println("customerName: " + name);
            System.out.println("idNumber: " + idNumber);
            System.out.println("phoneNumber: " + phoneNumber);
        }
        System.out.println("按任意数字键推出");
        scan.nextInt();
        CAPCmd(0);
    }


    void AddRoom() throws Exception {
        CAPCmd(0);

        System.out.println("input room new information");

        System.out.println("location:");
        String location = scan.nextLine();

        System.out.println("type:");
        String type = scan.nextLine();

        System.out.println("area:");
        Float area = scan.nextFloat();
        scan.nextLine();

        System.out.println("airConditioner:");
        Boolean airConditioner = scan.nextBoolean();
        scan.nextLine();

        System.out.println("waterHeater:");
        Boolean waterHeater = scan.nextBoolean();
        scan.nextLine();

        System.out.println("status:");
        String status = scan.nextLine();

        System.out.println("price:");
        Double price = scan.nextDouble();
        scan.nextLine();

        try {
            String sql = "INSERT INTO rooms (location,type,area,airConditioner,waterHeater,status,price) VALUES ((?), (?), (?), (?), (?), (?), (?))";

            PreparedStatement exec = coon.prepareStatement(sql);
            exec.setString(1, location);
            exec.setString(2, type);
            exec.setFloat(3, area);
            exec.setBoolean(4, airConditioner);
            exec.setBoolean(5, waterHeater);
            exec.setString(6, status);
            exec.setDouble(7, price);
            exec.executeUpdate();
            System.out.print("successful add room\n");

        } catch (Exception e) {
            System.out.println("fail add room due to" + e.getMessage());
        } finally {
            System.out.println("按任意数字键推出");
            scan.nextInt();
            CAPCmd(0);
        }
    }


    void DropRoom() throws Exception {
        CAPCmd(0);
        System.out.println("input room id:");
        Integer id = scan.nextInt();
        scan.nextLine();
        String sql = "DELETE FROM rooms WHERE id = (?);";
        PreparedStatement exec = coon.prepareStatement(sql);
        exec.setInt(1, id);
        int row = exec.executeUpdate();
        if (row == 1) {
            System.out.println("successful drop room");
        } else {
            System.out.println("fail to drop room due to id absence");
        }

        System.out.println("按任意数字键推出");
        scan.nextInt();
        CAPCmd(0);
    }


    void UpdateRoomInfo() throws Exception {
        CAPCmd(0);

        System.out.println("expect room id:");
        Integer id = scan.nextInt();
        scan.nextLine();

        System.out.println("input room new information");
        System.out.println("id:");
        Integer newId = scan.nextInt();
        scan.nextLine();

        System.out.println("location:");
        String location = scan.nextLine();

        System.out.println("type:");
        String type = scan.nextLine();

        System.out.println("area:");
        Float area = scan.nextFloat();
        scan.nextLine();

        System.out.println("airConditioner:");
        Boolean airConditioner = scan.nextBoolean();
        scan.nextLine();

        System.out.println("waterHeater:");
        Boolean waterHeater = scan.nextBoolean();
        scan.nextLine();

        System.out.println("status:");
        String status = scan.nextLine();

        System.out.println("price:");
        Double price = scan.nextDouble();
        scan.nextLine();

        try {
            String sql = "UPDATE rooms SET location = (?), type = (?), area = (?), `airConditioner` = (?), `waterHeater` = (?), status = (?), price = (?), id = (?) WHERE id = (?)";
            PreparedStatement exec = coon.prepareStatement(sql);
            exec.setString(1, location);
            exec.setString(2, type);
            exec.setFloat(3, area);
            exec.setBoolean(4, airConditioner);
            exec.setBoolean(5, waterHeater);
            exec.setString(6, status);
            exec.setDouble(7, price);
            exec.setInt(8, id);
            exec.setInt(9, newId);
            exec.executeUpdate();

            System.out.println("successful update room");

        } catch (Exception e) {
            System.out.println("fail update customer due to" + e.getMessage());
        } finally {
            System.out.println("按任意数字键推出");
            scan.nextInt();
            CAPCmd(0);
        }
    }


    void ShowAllRooms() throws Exception {
        CAPCmd(0);
        String sql = "SELECT * FROM rooms";
        ResultSet items = exec.executeQuery(sql);
        while (items.next()) {
            int id = items.getInt("id");
            String location = items.getString("location");
            String type = items.getString("type");
            float area = items.getFloat("area");
            boolean airConditioner = items.getBoolean("airConditioner");
            boolean waterHeater = items.getBoolean("waterHeater");
            String status = items.getString("status");
            double price = items.getDouble("price");

            System.out.println("-------------------------");
            System.out.println("Room ID: " + id);
            System.out.println("Location: " + location);
            System.out.println("Type: " + type);
            System.out.println("Area: " + area + " square meters");
            System.out.println("Air Conditioner: " + (airConditioner ? "Yes" : "No"));
            System.out.println("Water Heater: " + (waterHeater ? "Yes" : "No"));
            System.out.println("Status: " + status);
            System.out.println("Price: $" + price);

        }
        System.out.println("按任意数字键推出");
        scan.nextInt();
        CAPCmd(0);
    }


    void CheckIn() throws Exception {
        CAPCmd(0);
        System.out.println("customer id:");
        int user = scan.nextInt();
        scan.nextLine();

        System.out.println("room id:");
        int room = scan.nextInt();
        scan.nextLine();

        System.out.println("expect stay day:");
        int day = scan.nextInt();
        scan.nextLine();

        long timeInMillis = System.currentTimeMillis();
        long DaysInMillis = day * 24 * 60 * 60 * 1000;
        coon.setAutoCommit(false);
        try {
            String sql = "INSERT INTO checkIns (customerId, roomId, checkInTime, checkOutTime) VALUES (?, ?, ?, ?)";
            PreparedStatement exec = coon.prepareStatement(sql);
            exec.setInt(1, user);
            exec.setInt(2, room);

            exec.setTimestamp(3, new Timestamp(timeInMillis));
            exec.setTimestamp(4, new Timestamp(DaysInMillis));
            exec.executeUpdate();

            sql = "UPDATE rooms SET status = 'Occupied' WHERE id = " + room;
            coon.createStatement().executeUpdate(sql);

            System.out.println("successful check in room");
            coon.commit();

        } catch (Exception e) {
            System.out.println("fail to check in room due to " + e.getMessage());
            coon.rollback();
        } finally {
            coon.setAutoCommit(true);
            System.out.println("按任意数字键推出");
            scan.nextInt();
            scan.nextLine();
            CAPCmd(0);
        }
    }


    void CheckOut() throws Exception {
        CAPCmd(0);
        System.out.println("input checkIn id:");
        Integer id = scan.nextInt();
        scan.nextLine();

        String sql = "SELECT * FROM checkIns WHERE id = " + id;

        ResultSet roomInfo = exec.executeQuery(sql);
        if (roomInfo == null) {
            System.out.println("fail to check out due to id absence");
            System.out.println("按任意数字键推出");
            scan.nextInt();
            CAPCmd(0);
            return;
        }
        roomInfo.next();
        int roomId = roomInfo.getInt("roomId");
        System.out.println(roomId);
        sql = "UPDATE rooms SET status = 'Vacant' WHERE id = " + roomId;
        coon.createStatement().executeUpdate(sql);

        sql = "DELETE FROM checkIns WHERE id = (?)";
        PreparedStatement exec = coon.prepareStatement(sql);
        exec.setInt(1, id);
        exec.executeUpdate();

        System.out.println("successful check out");

        System.out.println("按任意数字键推出");
        scan.nextInt();
        CAPCmd(0);
    }


    void AddCost() throws Exception {
        CAPCmd(0);

        System.out.println(" payment type: ");
        String paymentType = scan.nextLine();

        System.out.println(" payment description: ");
        String paymentDescription = scan.nextLine();

        // Assuming paymentTime should be current time
        LocalDateTime paymentTime = LocalDateTime.now();

        System.out.println(" customer ID: ");
        int customerId = scan.nextInt();
        scan.nextLine();

        System.out.println(" amount due: ");
        double amountDue = scan.nextDouble();

        System.out.println(" amount paid: ");
        double amountPaid = scan.nextDouble();

        try {
            String sql = "INSERT INTO transactions " +
                    "(paymentType, paymentDescription, paymentTime, customerId, amountDue, amountPaid) " +
                    "VALUES (?, ?, ?, ?, ?, ?)" ;

            PreparedStatement exec = coon.prepareStatement(sql);
            exec.setString(1, paymentType);
            exec.setString(2, paymentDescription);
            exec.setObject(3, paymentTime);
            exec.setInt(4, customerId);
            exec.setDouble(5, amountDue);
            exec.setDouble(6, amountPaid);
            exec.executeUpdate();
            System.out.print("successful add transaction\n");

        } catch (Exception e) {
            System.out.println("fail add transcation due to" + e.getMessage());
        } finally {
            System.out.println("按任意数字键推出");
            scan.nextInt();
            CAPCmd(0);
        }
    }


    void DropTransaction() throws Exception {
        CAPCmd(0);
        System.out.println("input transaction id:");
        Integer id = scan.nextInt();
        scan.nextLine();
        String sql = "DELETE FROM transactions WHERE id = (?);";
        PreparedStatement exec = coon.prepareStatement(sql);
        exec.setInt(1, id);
        int row = exec.executeUpdate();
        if (row == 1) {
            System.out.println("successful drop transactions");
        } else {
            System.out.println("fail to drop transactions due to id absence");
        }
        System.out.println("按任意数字键推出");
        scan.nextInt();
        CAPCmd(0);
    }


    void ShowAllTransactions() throws Exception {
        CAPCmd(0);
        String sql = "SELECT * FROM transactions";
        ResultSet items = exec.executeQuery(sql);
        while (items.next()) {
            int id = items.getInt("id");
            String paymentType = items.getString("paymentType");
            String paymentDescription = items.getString("paymentDescription");
            String paymentTime = items.getString("paymentTime");
            int customerId = items.getInt("customerId");
            double amountDue = items.getDouble("amountDue");
            double amountPaid = items.getDouble("amountPaid");

            System.out.println("-------------------------");
            System.out.println("Payment ID: " + id);
            System.out.println("Payment Type: " + paymentType);
            System.out.println("Payment Description: " + paymentDescription);
            System.out.println("Payment Time: " + paymentTime);
            System.out.println("Customer ID: " + customerId);
            System.out.println("Amount Due: $" + amountDue);
            System.out.println("Amount Paid: $" + amountPaid);
        }

        System.out.println("按任意数字键推出");
        scan.nextInt();
        CAPCmd(0);
    }

}
