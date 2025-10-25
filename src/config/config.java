package config;

import java.sql.*;
import java.util.*;

public class config {

    private static final String DB_URL = "jdbc:sqlite:rikko.db"; // ✅ Use existing DB

    public Connection connectDB() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            return conn;
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed: " + e.getMessage());
            return null;
        }
    }

    // ✅ Reusable helper for setting parameters dynamically
    private void setParameters(PreparedStatement pstmt, Object... values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            Object val = values[i];
            if (val == null) pstmt.setNull(i + 1, Types.NULL);
            else if (val instanceof Integer) pstmt.setInt(i + 1, (Integer) val);
            else if (val instanceof Double) pstmt.setDouble(i + 1, (Double) val);
            else pstmt.setString(i + 1, val.toString());
        }
    }

    // ✅ Insert record
    public void addRecord(String sql, Object... values) {
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setParameters(pstmt, values);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Insert error: " + e.getMessage());
        }
    }

    // ✅ Update record
    public void updateRecord(String sql, Object... values) {
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setParameters(pstmt, values);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Update error: " + e.getMessage());
        }
    }

    // ✅ Delete record
    public void deleteRecord(String sql, Object... values) {
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setParameters(pstmt, values);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Delete error: " + e.getMessage());
        }
    }

    // ✅ Fetch records (returns list of maps)
    public List<Map<String, Object>> fetchRecords(String sql, Object... values) {
        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setParameters(pstmt, values);
            ResultSet rs = pstmt.executeQuery();

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
        } catch (SQLException e) {
            System.out.println("❌ Fetch error: " + e.getMessage());
        }

        return results;
    }

    // ✅ Check if user exists
    public boolean userExists(String sql, Object... values) {
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setParameters(pstmt, values);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("❌ userExists error: " + e.getMessage());
            return false;
        }
    }

    // ✅ Login validation
    public boolean loginUser(String sql, Object... values) {
        return userExists(sql, values);
    }

    // ✅ View records neatly
    public void viewRecords(String sql, String[] headers, String[] columns) {
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            for (String header : headers) {
                System.out.print(header + "\t");
            }
            System.out.println("\n-------------------------------------------------------");

            while (rs.next()) {
                for (String col : columns) {
                    System.out.print(rs.getString(col) + "\t");
                }
                System.out.println();
            }

        } catch (SQLException e) {
            System.out.println("❌ View error: " + e.getMessage());
        }
    }
}
