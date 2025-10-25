package main;

import config.config;
import java.util.*;

public class Main {

    // ✅ Login + Create Account System
    public static Map<String, Object> loginSystem(config con) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n======== ACCOUNT SYSTEM ========");
            System.out.println("1. Login");
            System.out.println("2. Create Account");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine(); // clear buffer

            if (choice == 3) {
                System.out.println("Exiting program...");
                return null;
            }

            if (choice == 2) {
                System.out.print("Enter Username: ");
                String username = sc.nextLine();

                System.out.print("Enter Password: ");
                String pass = sc.nextLine();

                System.out.print("Enter Full Name: ");
                String fullname = sc.nextLine();

                System.out.print("Enter Email: ");
                String email = sc.nextLine();

                if (username.isEmpty() || pass.isEmpty()) {
                    System.out.println("❌ Username and password cannot be empty!");
                    continue;
                }

                String insert = "INSERT INTO tble_user (u_name, u_pass, u_fullname, u_email) VALUES (?, ?, ?, ?)";
                con.addRecord(insert, username, pass, fullname, email);
                System.out.println("✅ Account created successfully! You can now log in.");
                continue;
            }

            // ✅ LOGIN SECTION
            System.out.print("Enter Username: ");
            String username = sc.nextLine();

            String checkUser = "SELECT * FROM tble_user WHERE u_name = ?";
            List<Map<String, Object>> userResult = con.fetchRecords(checkUser, username);

            if (userResult.isEmpty()) {
                System.out.println("❌ Username not found. Returning to main menu...");
                continue;
            }

            Map<String, Object> user = userResult.get(0);

            // Loop only password check
            while (true) {
                System.out.print("Enter Password: ");
                String pass = sc.nextLine();

                String correctPass = (String) user.get("u_pass");

                if (pass.equals(correctPass)) {
                    System.out.println("✅ Login successful!");
                    System.out.println("Welcome, " + user.get("u_fullname") + "!");
                    return user; // Return the whole user data
                } else {
                    System.out.println("❌ Incorrect password. Try again.");
                }
            }
        }
    }

    // ✅ Exercise Management (User must be logged in)
    public static void addExercise(config con, int userId) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n==== EXERCISE MENU ====");
            System.out.println("1. Add Exercise");
            System.out.println("2. View Exercises");
            System.out.println("3. Logout");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine(); // clear buffer

            if (choice == 3) {
                System.out.println("Logging out...");
                break;
            }

            if (choice == 1) {
                System.out.print("Enter Exercise Name: ");
                String name = sc.nextLine();

                System.out.print("Enter Type (e.g., Strength, Cardio): ");
                String type = sc.nextLine();

                System.out.print("Enter Reps: ");
                int reps = sc.nextInt();

                System.out.print("Enter Sets: ");
                int sets = sc.nextInt();
                sc.nextLine();

                String insert = "INSERT INTO tble_exercise (u_id, e_name, e_type, e_reps, e_sets) VALUES (?, ?, ?, ?, ?)";
                con.addRecord(insert, userId, name, type, reps, sets);
                System.out.println("✅ Exercise added successfully!");
            } 
            else if (choice == 2) {
                String fetch = "SELECT * FROM tble_exercise WHERE u_id = ?";
                List<Map<String, Object>> records = con.fetchRecords(fetch, userId);

                if (records.isEmpty()) {
                    System.out.println("No exercises found.");
                } else {
                    System.out.println("\n=== Your Exercises ===");
                    for (Map<String, Object> row : records) {
                        System.out.println("• " + row.get("e_name") + " (" + row.get("e_type") + ") — " +
                                           row.get("e_sets") + " sets of " + row.get("e_reps") + " reps");
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        config db = new config();
        db.connectDB();

        // ✅ Login System
        Map<String, Object> user = loginSystem(db);
        if (user == null) {
            System.out.println("Goodbye!");
            return;
        }

        int userId = ((Number) user.get("u_id")).intValue();

        // ✅ After login — Manage Exercises
        addExercise(db, userId);
    }
}
