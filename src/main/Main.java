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

                String checkEmail = "SELECT * FROM tble_user WHERE u_email = ?";
                List<Map<String, Object>> emailCheck = con.fetchRecords(checkEmail, email);
                if (!emailCheck.isEmpty()) {
                    System.out.println("❌ This email is already registered! Please use another one.");
                    continue;
                }

                String insert = "INSERT INTO tble_user (u_name, u_pass, u_fullname, u_email) VALUES (?, ?, ?, ?)";
                con.addRecord(insert, username, pass, fullname, email);
                System.out.println("✅ Account created successfully! You can now log in.");
                continue;
            }

            // LOGIN SECTION
            System.out.print("Enter Username: ");
            String username = sc.nextLine();

            String checkUser = "SELECT * FROM tble_user WHERE u_name = ?";
            List<Map<String, Object>> userResult = con.fetchRecords(checkUser, username);

            if (userResult.isEmpty()) {
                System.out.println("❌ Username not found. Returning to main menu...");
                continue;
            }

            Map<String, Object> user = userResult.get(0);

            int attempts = 0;

            while (true) {
                if (attempts >= 5) {
                    System.out.println("❌ Maximum attempts reached. Returning to main menu...");
                    break;
                }

                System.out.print("Enter Password: ");
                String pass = sc.nextLine();

                String correctPass = (String) user.get("u_pass");

                if (pass.equals(correctPass)) {
                    System.out.println("✅ Login successful!");
                    System.out.println("Welcome, " + user.get("u_fullname") + "!");
                    return user;
                } else {
                    attempts++;
                    System.out.println("❌ Incorrect password. Attempts: " + attempts + "/5");

                    if (attempts == 2) {
                        System.out.print("You've entered the wrong password twice. Go back to start? (y/n): ");
                        String choiceBack = sc.nextLine();
                        if (choiceBack.equalsIgnoreCase("y")) {
                            System.out.println("Returning to main menu...");
                            break;
                        }
                    }
                }
            }

            if (attempts >= 2) {
                continue;
            }
        }
    }

    public static void manageExercises(config con, int userId) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n==== EXERCISE MENU ====");
            System.out.println("1. Add Exercise");
            System.out.println("2. View Exercises");
            System.out.println("3. Update Exercise");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 4) break;

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
                        System.out.println("ID: " + row.get("e_id") + " | " + row.get("e_name") + 
                                           " (" + row.get("e_type") + ") — " +
                                           row.get("e_sets") + " sets of " + row.get("e_reps") + " reps");
                    }
                }
            }
            else if (choice == 3) {
                System.out.println("\n=== Update Exercise ===");
                System.out.print("Enter Exercise ID to update: ");
                int eId = sc.nextInt();
                sc.nextLine();

                String check = "SELECT * FROM tble_exercise WHERE e_id = ? AND u_id = ?";
                List<Map<String, Object>> result = con.fetchRecords(check, eId, userId);

                if (result.isEmpty()) {
                    System.out.println("❌ Exercise not found or does not belong to you.");
                    continue;
                }

                System.out.print("Enter new Exercise Name: ");
                String newName = sc.nextLine();

                System.out.print("Enter new Type: ");
                String newType = sc.nextLine();

                System.out.print("Enter new Reps: ");
                int newReps = sc.nextInt();

                System.out.print("Enter new Sets: ");
                int newSets = sc.nextInt();
                sc.nextLine();

                String update = "UPDATE tble_exercise SET e_name = ?, e_type = ?, e_reps = ?, e_sets = ? WHERE e_id = ? AND u_id = ?";
                con.updateRecord(update, newName, newType, newReps, newSets, eId, userId);
                System.out.println("✅ Exercise updated successfully!");
            }
        }
    }

    public static void manageSessions(config con, int userId) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n==== WORKOUT SESSION MENU ====");
            System.out.println("1. Add Workout Session");
            System.out.println("2. View Workout Sessions");
            System.out.println("3. Edit Workout Session");
            System.out.println("4. Delete Workout Session");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 5) break;

            if (choice == 1) {
                System.out.print("Enter Session Date (YYYY-MM-DD): ");
                String date = sc.nextLine();

                System.out.print("Enter Duration (minutes): ");
                int duration = sc.nextInt();
                sc.nextLine();

                System.out.print("Enter Notes (optional): ");
                String notes = sc.nextLine();

                String insert = "INSERT INTO tble_workout_session (u_id, ws_date, ws_duration, ws_notes) VALUES (?, ?, ?, ?)";
                con.addRecord(insert, userId, date, duration, notes);
                System.out.println("✅ Workout session added!");
            }
            else if (choice == 2) {
                String fetch = "SELECT * FROM tble_workout_session WHERE u_id = ?";
                List<Map<String, Object>> records = con.fetchRecords(fetch, userId);

                if (records.isEmpty()) {
                    System.out.println("No workout sessions found.");
                } else {
                    System.out.println("\n=== Your Workout Sessions ===");
                    for (Map<String, Object> row : records) {
                        System.out.println(
                                "ID: " + row.get("ws_id") +
                                " | Date: " + row.get("ws_date") +
                                " | Duration: " + (row.get("ws_duration") == null ? "N/A" : row.get("ws_duration") + " mins") +
                                " | Notes: " + (row.get("ws_notes") == null ? "" : row.get("ws_notes"))
                        );
                    }
                }
            }
            else if (choice == 3) {
                System.out.print("Enter Session ID to edit: ");
                int wsId = sc.nextInt();
                sc.nextLine();

                String check = "SELECT * FROM tble_workout_session WHERE ws_id = ? AND u_id = ?";
                List<Map<String, Object>> result = con.fetchRecords(check, wsId, userId);

                if (result.isEmpty()) {
                    System.out.println("❌ Session not found or does not belong to you.");
                    continue;
                }

                Map<String, Object> session = result.get(0);

                System.out.println("Current Date: " + session.get("ws_date"));
                System.out.print("Enter new Date (YYYY-MM-DD) (leave blank to keep): ");
                String newDate = sc.nextLine();
                if (newDate.isEmpty()) newDate = (String) session.get("ws_date");

                System.out.println("Current Duration: " + session.get("ws_duration"));
                System.out.print("Enter new Duration (minutes) (0 to keep): ");
                String durationInput = sc.nextLine();
                int newDuration;

                if (durationInput.isEmpty()) {
                    newDuration = session.get("ws_duration") == null ? 0 : ((Number)session.get("ws_duration")).intValue();
                } else {
                    newDuration = Integer.parseInt(durationInput);
                }

                System.out.println("Current Notes: " + session.get("ws_notes"));
                System.out.print("Enter new Notes (leave blank to keep): ");
                String newNotes = sc.nextLine();
                if (newNotes.isEmpty()) newNotes = session.get("ws_notes") == null ? "" : (String) session.get("ws_notes");

                String update = "UPDATE tble_workout_session SET ws_date = ?, ws_duration = ?, ws_notes = ? WHERE ws_id = ? AND u_id = ?";
                con.updateRecord(update, newDate, newDuration, newNotes, wsId, userId);
                System.out.println("✅ Workout session updated!");
            }
            else if (choice == 4) {
                System.out.print("Enter Session ID to delete: ");
                int wsId = sc.nextInt();
                sc.nextLine();

                String check = "SELECT * FROM tble_workout_session WHERE ws_id = ? AND u_id = ?";
                List<Map<String, Object>> result = con.fetchRecords(check, wsId, userId);

                if (result.isEmpty()) {
                    System.out.println("❌ Session not found or does not belong to you.");
                    continue;
                }

                System.out.print("Are you sure you want to delete this session? (y/n): ");
                String confirm = sc.nextLine();

                if (confirm.equalsIgnoreCase("y")) {
                    String delete = "DELETE FROM tble_workout_session WHERE ws_id = ? AND u_id = ?";
                    con.deleteRecord(delete, wsId, userId);
                    System.out.println("✅ Workout session deleted.");
                } else {
                    System.out.println("Deletion cancelled.");
                }
            }
        }
    }

    public static void updateUserInfo(config con, int userId) {
        Scanner sc = new Scanner(System.in);

        System.out.println("\n=== Update User Info ===");
        System.out.print("Enter new Full Name: ");
        String newFull = sc.nextLine();

        System.out.print("Enter new Email: ");
        String newEmail = sc.nextLine();

        String checkEmail = "SELECT * FROM tble_user WHERE u_email = ? AND u_id != ?";
        List<Map<String, Object>> emailCheck = con.fetchRecords(checkEmail, newEmail, userId);
        if (!emailCheck.isEmpty()) {
            System.out.println("❌ This email is already used by another account!");
            return;
        }

        System.out.print("Enter new Password: ");
        String newPass = sc.nextLine();

        String updateUser = "UPDATE tble_user SET u_fullname = ?, u_email = ?, u_pass = ? WHERE u_id = ?";
        con.updateRecord(updateUser, newFull, newEmail, newPass, userId);
        System.out.println("✅ User information updated successfully!");
    }

    public static void main(String[] args) {
        config db = new config();
        db.connectDB();

        Map<String, Object> user = loginSystem(db);
        if (user == null) {
            System.out.println("Goodbye!");
            return;
        }

        int userId = ((Number) user.get("u_id")).intValue();

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n==== MAIN MENU ====");
            System.out.println("1. Manage Exercises");
            System.out.println("2. Manage Workout Sessions");
            System.out.println("3. Update User Info");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 4) {
                System.out.println("Logging out...");
                break;
            }

            if (choice == 1) manageExercises(db, userId);
            else if (choice == 2) manageSessions(db, userId);
            else if (choice == 3) updateUserInfo(db, userId);
            else System.out.println("Invalid option.");
        }

        System.out.println("Goodbye!");
    }
}
