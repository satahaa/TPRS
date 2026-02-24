package com.tprs.dao;

import com.tprs.config.DatabaseConfig;
import com.tprs.model.Student;
import com.tprs.model.Teacher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Supervisor-Student Assignment DAO
 * Handles supervisor-student relationship operations
 */
public class SupervisorStudentDAO {
    
    private Connection getConnection() {
        return DatabaseConfig.getConnection();
    }
    
    /**
     * Assign a student to a supervisor
     */
    public boolean assign(int supervisorId, int studentId) {
        String sql = "INSERT IGNORE INTO supervisor_student (supervisor_id, student_id) VALUES (?, ?)";
        Connection connection = getConnection();
        
        if (connection == null) return false;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, supervisorId);
            stmt.setInt(2, studentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error assigning student to supervisor: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Remove a student from a supervisor
     */
    public boolean unassign(int supervisorId, int studentId) {
        String sql = "DELETE FROM supervisor_student WHERE supervisor_id = ? AND student_id = ?";
        Connection connection = getConnection();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, supervisorId);
            stmt.setInt(2, studentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing student from supervisor: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get all students assigned to a supervisor
     */
    public List<Student> getStudentsBySupervisor(int supervisorId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.* FROM student s JOIN supervisor_student ss ON s.id = ss.student_id WHERE ss.supervisor_id = ? ORDER BY s.last_name, s.first_name";
        Connection connection = getConnection();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, supervisorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting assigned students: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }
    
    /**
     * Get the supervisor(s) assigned to a student
     */
    public List<Teacher> getSupervisorsForStudent(int studentId) {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT t.* FROM teacher t JOIN supervisor_student ss ON t.id = ss.supervisor_id WHERE ss.student_id = ? ORDER BY t.last_name";
        Connection connection = getConnection();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                teachers.add(mapResultSetToTeacher(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting supervisors for student: " + e.getMessage());
            e.printStackTrace();
        }
        return teachers;
    }
    
    /**
     * Check if a student is assigned to a supervisor
     */
    public boolean isAssigned(int supervisorId, int studentId) {
        String sql = "SELECT COUNT(*) AS cnt FROM supervisor_student WHERE supervisor_id = ? AND student_id = ?";
        Connection connection = getConnection();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, supervisorId);
            stmt.setInt(2, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("cnt") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking assignment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get all students NOT yet assigned to a supervisor (available for assignment)
     */
    public List<Student> getUnassignedStudents(int supervisorId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.* FROM student s WHERE s.id NOT IN (SELECT student_id FROM supervisor_student WHERE supervisor_id = ?) ORDER BY s.last_name, s.first_name";
        Connection connection = getConnection();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, supervisorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting unassigned students: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }
    
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setStudentId(rs.getString("student_id"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setEmail(rs.getString("email"));
        student.setPassword(rs.getString("password"));
        student.setDepartment(rs.getString("department"));
        student.setSemester(rs.getString("semester"));
        student.setPhone(rs.getString("phone"));
        student.setCreatedAt(rs.getTimestamp("created_at"));
        student.setUpdatedAt(rs.getTimestamp("updated_at"));
        return student;
    }
    
    private Teacher mapResultSetToTeacher(ResultSet rs) throws SQLException {
        Teacher teacher = new Teacher();
        teacher.setId(rs.getInt("id"));
        teacher.setTeacherId(rs.getString("teacher_id"));
        teacher.setFirstName(rs.getString("first_name"));
        teacher.setLastName(rs.getString("last_name"));
        teacher.setEmail(rs.getString("email"));
        teacher.setPassword(rs.getString("password"));
        teacher.setDepartment(rs.getString("department"));
        teacher.setDesignation(rs.getString("designation"));
        teacher.setSpecialization(rs.getString("specialization"));
        teacher.setPhone(rs.getString("phone"));
        teacher.setCreatedAt(rs.getTimestamp("created_at"));
        teacher.setUpdatedAt(rs.getTimestamp("updated_at"));
        return teacher;
    }
}
