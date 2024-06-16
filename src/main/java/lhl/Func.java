package lhl;

import java.sql.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Func {

    public Connection conn;
    public Statement exec;
    public Scanner sc;

    public Func(String url, String username, String password) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(url, username, password);
        exec = conn.createStatement();
        sc = new Scanner(System.in);

    }

    public void close() throws Exception {
        conn.close();
        exec.close();
        sc.close();
    }

    // CAPCmd provides clearing
    public void CAPCmd(int seconds) throws Exception {
        TimeUnit.SECONDS.sleep(seconds);
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void QueryCheckInDetails() throws Exception {
        CAPCmd(0);

        String sql = "SELECT ci.id, c.name, r.location, r.`type`, r.price, " +
                "ci.`checkInTime`, ci.`checkOutTime`, " +
                "DATEDIFF(ci.`checkOutTime`, ci.`checkInTime`) as stayDay " +
                "FROM checkIns ci " +
                "LEFT JOIN rooms r ON ci.roomId = r.id " +
                "LEFT JOIN customers c ON ci.customerId = c.id";

        try (ResultSet items = exec.executeQuery(sql)) {
            System.out.println("---------------------------------------");
            while (items.next()) {
                int checkInId = items.getInt("id");
                String name = items.getString("name");
                String location = items.getString("location");
                String type = items.getString("type");
                double price = items.getDouble("price");
                Date inDate = items.getDate("checkInTime");
                Date outDate = items.getDate("checkOutTime");
                int stayDay = items.getInt("stayDay");

                System.out.println("Check-in ID: " + checkInId);
                System.out.println("Name: " + name);
                System.out.println("Location: " + location);
                System.out.println("Type: " + type);
                System.out.println("Price: " + price);
                System.out.println("Check-in Date: " + inDate);
                System.out.println("Check-out Date: " + outDate);
                System.out.println("Stay Days: " + stayDay + "day");
                System.out.println();
            }
            System.out.println("---------------------------------------");
        }

        System.out.println("Press any dight key to exit");
        sc.nextInt();
        CAPCmd(0);
    }

    void QueryPayDetails() throws Exception {
        CAPCmd(0);
        String sql = "SELECT t.id, t.`paymentType`, t.`paymentDescription`, t.`paymentTime`, " +
                "t.`customerId`, c.name, t.`amountDue`, t.`amountPaid` " +
                "FROM transactions t " +
                "LEFT JOIN customers c ON t.`customerId` = c.id";

        try (ResultSet items = exec.executeQuery(sql)) {
            System.out.println("---------------------------------------");
            while (items.next()) {
                int paymentId = items.getInt("id");
                String paymentType = items.getString("paymentType");
                String paymentDescription = items.getString("paymentDescription");
                Timestamp paymentTime = items.getTimestamp("paymentTime");
                int customerId = items.getInt("customerId");
                String customerName = items.getString("name");
                double amountDue = items.getDouble("amountDue");
                double amountPaid = items.getDouble("amountPaid");

                System.out.println("Payment ID: " + paymentId);
                System.out.println("Payment Type: " + paymentType);
                System.out.println("Payment Description: " + paymentDescription);
                System.out.println("Payment Time: " + paymentTime);
                System.out.println("Customer ID: " + customerId);
                System.out.println("Customer Name: " + customerName);
                System.out.println("Amount Due: " + amountDue);
                System.out.println("Amount Paid: " + amountPaid);
                System.out.println();
            }
            System.out.println("---------------------------------------");
        }

        System.out.println("Press any dight key to exit");
        sc.nextInt();
        CAPCmd(0);
    }

    void AddCustomer() throws Exception {
        CAPCmd(0);
        System.out.println("Enter customer information:\n");
        System.out.print("Name: ");
        String customerName = sc.nextLine().strip();
        System.out.print("Gender (only Male or Female): ");
        String gender = sc.nextLine().strip();
        System.out.print("ID Number (must be 18 digits): ");
        String idNumber = sc.nextLine().strip();
        System.out.print("Phone Number (must be 11 digits): ");
        String phoneNumber = sc.nextLine().strip();

        try {
            String sql = "INSERT INTO customers (name, gender, idNumber, phoneNumber) VALUES (?, ?, ?, ?)";
            PreparedStatement exec = conn.prepareStatement(sql);
            exec.setString(1, customerName);
            exec.setString(2, gender);
            exec.setString(3, idNumber);
            exec.setString(4, phoneNumber);
            exec.executeUpdate();
            System.out.println("Customer added successfully!\n");

        } catch (Exception e) {
            System.out.println("Failed to add customer, reason: " + e.getMessage());
        } finally {
            System.out.println("Press any dight key to exit");
            sc.nextInt();
            CAPCmd(0);
        }
    }

    void DropCustomer() throws Exception {
        CAPCmd(0);

        System.out.print("Enter customer ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        String sql = "DELETE FROM customers WHERE id = ?";
        try (PreparedStatement exec = conn.prepareStatement(sql)) {
            exec.setInt(1, id);
            int rowsAffected = exec.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("Customer deleted successfully!\n");
            } else {
                System.out.println("Failed to delete customer. Customer with ID " + id + " not found.\n");
            }
        }

        System.out.println("Press any key to exit");
        sc.nextInt();
        CAPCmd(0);
    }

    void UpdateCustomerInfo() throws Exception {
        CAPCmd(0);

        System.out.print("Enter customer ID to update: ");
        int idToUpdate = sc.nextInt();
        sc.nextLine();

        System.out.println("Enter new customer information:");
        System.out.print("New ID: ");
        int newId = sc.nextInt();
        sc.nextLine();

        System.out.print("Name: ");
        String name = sc.nextLine();

        System.out.print("Gender (only Male or Female): ");
        String gender = sc.nextLine();

        System.out.print("ID Number (must be 18 digits): ");
        String idNumber = sc.nextLine();

        System.out.print("Phone Number (must be 11 digits): ");
        String phoneNumber = sc.nextLine();

        try (PreparedStatement exec = conn.prepareStatement(
                "UPDATE customers SET id = ?, gender = ?, `idNumber` = ?, name = ?, `phoneNumber` = ? WHERE id = ?")) {
            exec.setInt(1, newId);
            exec.setString(2, gender);
            exec.setString(3, idNumber);
            exec.setString(4, name);
            exec.setString(5, phoneNumber);
            exec.setInt(6, idToUpdate);
            int rowsAffected = exec.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("Customer updated successfully!\n");
            } else {
                System.out.println("Failed to update customer. Customer with ID " + idToUpdate + " not found.\n");
            }
        } catch (Exception e) {
            System.out.println("Failed to update customer, reason: " + e.getMessage());
        } finally {
            System.out.println("Press any key to exit");
            sc.nextInt();
            CAPCmd(0);
        }
    }

    void ShowAllCustomers() throws Exception {
        CAPCmd(0);

        String sql = "SELECT * FROM customers";

        try (ResultSet items = exec.executeQuery(sql)) {
            System.out.println("---------------------------------------");
            while (items.next()) {
                int id = items.getInt("id");
                String name = items.getString("name");
                String idNumber = items.getString("idNumber");
                String phoneNumber = items.getString("phoneNumber");

                System.out.println("Customer ID: " + id);
                System.out.println("Customer Name: " + name);
                System.out.println("ID Number: " + idNumber);
                System.out.println("Phone Number: " + phoneNumber);
                System.out.println();
            }
            System.out.println("---------------------------------------");
        }

        System.out.println("Press any key to exit");
        sc.nextInt();
        CAPCmd(0);
    }

    void AddRoom() throws Exception {
        CAPCmd(0);

        System.out.println("Enter new room information:");

        System.out.print("Location: ");
        String location = sc.nextLine();

        System.out.print("Type: ");
        String type = sc.nextLine();

        System.out.print("Area: ");
        float area = sc.nextFloat();
        sc.nextLine();

        System.out.print("Air Conditioner (true/false): ");
        boolean airConditioner = sc.nextBoolean();
        sc.nextLine();

        System.out.print("Water Heater (true/false): ");
        boolean waterHeater = sc.nextBoolean();
        sc.nextLine();

        System.out.print("Status: ");
        String status = sc.nextLine();

        System.out.print("Price: ");
        double price = sc.nextDouble();
        sc.nextLine();

        try (PreparedStatement exec = conn.prepareStatement(
                "INSERT INTO rooms (location, type, area, airConditioner, waterHeater, status, price) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            exec.setString(1, location);
            exec.setString(2, type);
            exec.setFloat(3, area);
            exec.setBoolean(4, airConditioner);
            exec.setBoolean(5, waterHeater);
            exec.setString(6, status);
            exec.setDouble(7, price);
            int rowsAffected = exec.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("Room added successfully!\n");
            } else {
                System.out.println("Failed to add room.\n");
            }
        } catch (Exception e) {
            System.out.println("Failed to add room, reason: " + e.getMessage());
        } finally {
            System.out.println("Press any key to exit");
            sc.nextInt();
            CAPCmd(0);
        }
    }

    void DropRoom() throws Exception {
        CAPCmd(0);

        System.out.print("Enter room ID to delete: ");
        int idToDelete = sc.nextInt();
        sc.nextLine();

        String sql = "DELETE FROM rooms WHERE id = ?";
        try (PreparedStatement exec = conn.prepareStatement(sql)) {
            exec.setInt(1, idToDelete);
            int rowsAffected = exec.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("Room deleted successfully!\n");
            } else {
                System.out.println("Failed to delete room. Room with ID " + idToDelete + " not found.\n");
            }
        }

        System.out.println("Press any dight key to exit");
        sc.nextInt();
        CAPCmd(0);
    }

    void UpdateRoomInfo() throws Exception {
        CAPCmd(0);

        System.out.print("Enter room ID to update: ");
        int idToUpdate = sc.nextInt();
        sc.nextLine();

        System.out.println("Enter new room information:");
        System.out.print("New ID: ");
        int newId = sc.nextInt();
        sc.nextLine();

        System.out.print("Location: ");
        String location = sc.nextLine();

        System.out.print("Type: ");
        String type = sc.nextLine();

        System.out.print("Area: ");
        float area = sc.nextFloat();
        sc.nextLine();

        System.out.print("Air Conditioner (true/false): ");
        boolean airConditioner = sc.nextBoolean();
        sc.nextLine();

        System.out.print("Water Heater (true/false): ");
        boolean waterHeater = sc.nextBoolean();
        sc.nextLine();

        System.out.print("Status: ");
        String status = sc.nextLine();

        System.out.print("Price: ");
        double price = sc.nextDouble();
        sc.nextLine();

        try (PreparedStatement exec = conn.prepareStatement(
                "UPDATE rooms SET location = ?, type = ?, area = ?, airConditioner = ?, waterHeater = ?, status = ?, price = ?, id = ? WHERE id = ?")) {
            exec.setString(1, location);
            exec.setString(2, type);
            exec.setFloat(3, area);
            exec.setBoolean(4, airConditioner);
            exec.setBoolean(5, waterHeater);
            exec.setString(6, status);
            exec.setDouble(7, price);
            exec.setInt(8, newId);
            exec.setInt(9, idToUpdate);
            int rowsAffected = exec.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("Room updated successfully!\n");
            } else {
                System.out.println("Failed to update room. Room with ID " + idToUpdate + " not found.\n");
            }
        } catch (Exception e) {
            System.out.println("Failed to update room, reason: " + e.getMessage());
        } finally {
            System.out.println("Press any dight key to exit");
            sc.nextInt();
            CAPCmd(0);
        }
    }

    void ShowAllRooms() throws Exception {
        CAPCmd(0);

        String sql = "SELECT * FROM rooms";

        try (ResultSet items = exec.executeQuery(sql)) {
            System.out.println("---------------------------------------");
            while (items.next()) {
                int id = items.getInt("id");
                String location = items.getString("location");
                String type = items.getString("type");
                float area = items.getFloat("area");
                boolean airConditioner = items.getBoolean("airConditioner");
                boolean waterHeater = items.getBoolean("waterHeater");
                String status = items.getString("status");
                double price = items.getDouble("price");

                System.out.println("Room ID: " + id);
                System.out.println("Location: " + location);
                System.out.println("Type: " + type);
                System.out.println("Area: " + area + " square meters");
                System.out.println("Air Conditioner: " + (airConditioner ? "Yes" : "No"));
                System.out.println("Water Heater: " + (waterHeater ? "Yes" : "No"));
                System.out.println("Status: " + status);
                System.out.println("Price: $" + price);
                System.out.println();
            }
            System.out.println("---------------------------------------");
        }

        System.out.println("Press any dight key to exit");
        sc.nextInt();
        CAPCmd(0);
    }

    void CheckIn() throws Exception {
        CAPCmd(0);

        System.out.print("Enter customer ID: ");
        int customerId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter room ID: ");
        int roomId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter expected stay days: ");
        int stayDays = sc.nextInt();
        sc.nextLine();

        // Calculate the current system time and ‘stayDays’ days later
        long checkInTimeInMillis = System.currentTimeMillis();
        long checkOutTimeInMillis = checkInTimeInMillis + (stayDays * 24 * 60 * 60 * 1000);

        // Turn off transaction auto-commit because you want to modify multiple tables,
        // and one fails and all fail
        conn.setAutoCommit(false);
        try {
            String sql = "INSERT INTO checkIns (customerId, roomId, checkInTime, checkOutTime) VALUES (?, ?, ?, ?)";
            try (PreparedStatement exec = conn.prepareStatement(sql)) {
                exec.setInt(1, customerId);
                exec.setInt(2, roomId);
                exec.setTimestamp(3, new Timestamp(checkInTimeInMillis));
                exec.setTimestamp(4, new Timestamp(checkOutTimeInMillis));
                exec.executeUpdate();
            }

            sql = "UPDATE rooms SET status = 'Occupied' WHERE id = ?";
            try (PreparedStatement exec = conn.prepareStatement(sql)) {
                exec.setInt(1, roomId);
                exec.executeUpdate();
            }

            System.out.println("Room checked in successfully!\n");
            // The execution is successful, and the transaction is committed
            conn.commit();

        } catch (Exception e) {

            System.out.println("Failed to check in room, reason: " + e.getMessage());
            // Execution fails, and the transaction is rolled back
            conn.rollback();
        } finally {
            conn.setAutoCommit(true);
            System.out.println("Press any dight key to exit");
            sc.nextInt();
            sc.nextLine();
            CAPCmd(0);
        }
    }

    void CheckOut() throws Exception {
        CAPCmd(0);

        System.out.print("Enter check-in ID: ");
        int checkInId = sc.nextInt();
        sc.nextLine();

        String sql = "SELECT * FROM checkIns WHERE id = ?";
        try (PreparedStatement exec = conn.prepareStatement(sql)) {
            exec.setInt(1, checkInId);
            ResultSet roomInfo = exec.executeQuery();

            if (!roomInfo.next()) {
                System.out.println("Failed to check out. Check-in with ID " + checkInId + " not found.\n");
                System.out.println("Press any dight key to exit");
                sc.nextInt();
                CAPCmd(0);
                return;
            }

            int roomId = roomInfo.getInt("roomId");
            sql = "UPDATE rooms SET status = 'Vacant' WHERE id = ?";
            try (PreparedStatement updateRoom = conn.prepareStatement(sql)) {
                updateRoom.setInt(1, roomId);
                updateRoom.executeUpdate();
            }

            sql = "DELETE FROM checkIns WHERE id = ?";
            try (PreparedStatement deleteCheckIn = conn.prepareStatement(sql)) {
                deleteCheckIn.setInt(1, checkInId);
                deleteCheckIn.executeUpdate();
            }

            System.out.println("Room checked out successfully!\n");

        }

        System.out.println("Press any dight key to exit");
        sc.nextInt();
        CAPCmd(0);
    }

    void AddCost() throws Exception {
        CAPCmd(0);
        class Service {
            private final Integer id;
            private final String name;
            private final String description;
            private final Double price;

            public Service(Integer id, String name, String description, Double price) {
                this.id = id;
                this.name = name;
                this.description = description;
                this.price = price;
            }

            @Override
            public String toString() {
                return "id:" + id + ", name:" + name + ", description:" + description + ", price:" + price;
            }
        }

        Map<Integer, Service> servicesById = new HashMap<>();

        String sql = "SELECT * FROM services";
        ResultSet query = exec.executeQuery(sql);

        while (query.next()) {
            servicesById.put(query.getInt("id"), new Service(
                    query.getInt("id"),
                    query.getString("name"),
                    query.getString("description"),
                    query.getDouble("price")));
        }

        System.out.println("opt some services:");
        servicesById.values().forEach(System.out::println);

        // Calculate the amount due
        double amountDue = Arrays.stream(sc.nextLine().trim().split(" "))
                .map(Integer::parseInt)
                .map(servicesById::get)
                .filter(Objects::nonNull)
                .mapToDouble(service -> service.price)
                .reduce(0, Double::sum);

        System.out.print("Payment Type: ");
        String paymentType = sc.nextLine();

        System.out.print("Payment Description: ");
        String paymentDescription = sc.nextLine();

        Timestamp paymentTime = Timestamp.valueOf(LocalDateTime.now());

        System.out.print("Customer ID: ");
        int customerId = sc.nextInt();
        sc.nextLine();

        System.out.print("Amount Paid: ");
        double amountPaid = sc.nextDouble();
        sc.nextLine();

        try (PreparedStatement exec = conn.prepareStatement(
                "INSERT INTO transactions (paymentType, paymentDescription, paymentTime, customerId, amountDue, amountPaid) VALUES (?, ?, ?, ?, ?, ?)")) {
            exec.setString(1, paymentType);
            exec.setString(2, paymentDescription);
            exec.setTimestamp(3, paymentTime);
            exec.setInt(4, customerId);
            exec.setDouble(5, amountDue);
            exec.setDouble(6, amountPaid);
            int rowsAffected = exec.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("Transaction added successfully!\n");
            } else {
                System.out.println("Failed to add transaction.\n");
            }
        } catch (Exception e) {
            System.out.println("Failed to add transaction, reason: " + e.getMessage());
        } finally {
            System.out.println("Press any dight key to exit");
            sc.nextInt();
            sc.nextLine();
            CAPCmd(0);
        }

    }

    void DropTransaction() throws Exception {
        CAPCmd(0);

        System.out.print("Enter transaction ID to delete: ");
        int transactionId = sc.nextInt();
        sc.nextLine();

        String sql = "DELETE FROM transactions WHERE id = ?";
        try (PreparedStatement exec = conn.prepareStatement(sql)) {
            exec.setInt(1, transactionId);
            int rowsAffected = exec.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("Transaction deleted successfully!\n");
            } else {
                System.out.println(
                        "Failed to delete transaction. Transaction with ID " + transactionId + " not found.\n");
            }
        }

        System.out.println("Press any dight key to exit");
        sc.nextInt();
        CAPCmd(0);
    }

    void ShowAllTransactions() throws Exception {
        CAPCmd(0);

        String sql = "SELECT * FROM transactions";

        try (ResultSet items = exec.executeQuery(sql)) {
            System.out.println("---------------------------------------");
            while (items.next()) {
                int id = items.getInt("id");
                String paymentType = items.getString("paymentType");
                String paymentDescription = items.getString("paymentDescription");
                Timestamp paymentTime = items.getTimestamp("paymentTime");
                int customerId = items.getInt("customerId");
                double amountDue = items.getDouble("amountDue");
                double amountPaid = items.getDouble("amountPaid");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedPaymentTime = paymentTime.toLocalDateTime().format(formatter);

                System.out.println("Payment ID: " + id);
                System.out.println("Payment Type: " + paymentType);
                System.out.println("Payment Description: " + paymentDescription);
                System.out.println("Payment Time: " + formattedPaymentTime);
                System.out.println("Customer ID: " + customerId);
                System.out.println("Amount Due: $" + amountDue);
                System.out.println("Amount Paid: $" + amountPaid);
                System.out.println();
            }
            System.out.println("---------------------------------------");
        }

        System.out.println("Press any dight key to exit");
        sc.nextInt();
        CAPCmd(0);
    }

}
