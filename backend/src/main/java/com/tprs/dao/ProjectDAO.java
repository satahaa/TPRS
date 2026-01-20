package com.tprs.dao;

import com.tprs.config.DatabaseConfig;
import com.tprs.model.Project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Project Data Access Object (DAO)
 * Handles all database operations for Project entity
 */
public class ProjectDAO {
    
    private Connection connection;
    
    public ProjectDAO() {
        this.connection = DatabaseConfig.getConnection();
    }
    
    /**
     * Create a new project
     * @param project Project object to create
     * @return true if successful, false otherwise
     */
    public boolean create(Project project) {
        String sql = "INSERT INTO project (title, description, type, student_id, supervisor_id, status, file_path, keywords, year, semester, department) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, project.getTitle());
            stmt.setString(2, project.getDescription());
            stmt.setString(3, project.getType());
            stmt.setInt(4, project.getStudentId());
            stmt.setInt(5, project.getSupervisorId());
            stmt.setString(6, project.getStatus());
            stmt.setString(7, project.getFilePath());
            stmt.setString(8, project.getKeywords());
            stmt.setInt(9, project.getYear());
            stmt.setString(10, project.getSemester());
            stmt.setString(11, project.getDepartment());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    project.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating project: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get project by ID
     * @param id Project ID
     * @return Project object or null if not found
     */
    public Project getById(int id) {
        String sql = "SELECT * FROM project WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToProject(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting project by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get all projects
     * @return List of all projects
     */
    public List<Project> getAll() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM project ORDER BY created_at DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all projects: " + e.getMessage());
            e.printStackTrace();
        }
        return projects;
    }
    
    /**
     * Get projects by student ID
     * @param studentId Student ID
     * @return List of projects by the student
     */
    public List<Project> getByStudentId(int studentId) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM project WHERE student_id = ? ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting projects by student: " + e.getMessage());
            e.printStackTrace();
        }
        return projects;
    }
    
    /**
     * Get projects by supervisor ID
     * @param supervisorId Supervisor/Teacher ID
     * @return List of projects supervised by the teacher
     */
    public List<Project> getBySupervisorId(int supervisorId) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM project WHERE supervisor_id = ? ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, supervisorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting projects by supervisor: " + e.getMessage());
            e.printStackTrace();
        }
        return projects;
    }
    
    /**
     * Get projects by department
     * @param department Department name
     * @return List of projects in the department
     */
    public List<Project> getByDepartment(String department) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM project WHERE department = ? ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, department);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting projects by department: " + e.getMessage());
            e.printStackTrace();
        }
        return projects;
    }
    
    /**
     * Get projects by status
     * @param status Project status
     * @return List of projects with the given status
     */
    public List<Project> getByStatus(String status) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM project WHERE status = ? ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting projects by status: " + e.getMessage());
            e.printStackTrace();
        }
        return projects;
    }
    
    /**
     * Search projects by keyword
     * @param keyword Search keyword
     * @return List of matching projects
     */
    public List<Project> searchByKeyword(String keyword) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM project WHERE title LIKE ? OR description LIKE ? OR keywords LIKE ? ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching projects: " + e.getMessage());
            e.printStackTrace();
        }
        return projects;
    }
    
    /**
     * Update project
     * @param project Project object with updated data
     * @return true if successful, false otherwise
     */
    public boolean update(Project project) {
        String sql = "UPDATE project SET title = ?, description = ?, type = ?, status = ?, file_path = ?, keywords = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, project.getTitle());
            stmt.setString(2, project.getDescription());
            stmt.setString(3, project.getType());
            stmt.setString(4, project.getStatus());
            stmt.setString(5, project.getFilePath());
            stmt.setString(6, project.getKeywords());
            stmt.setInt(7, project.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating project: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Update project status
     * @param id Project ID
     * @param status New status
     * @return true if successful, false otherwise
     */
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE project SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating project status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Approve project
     * @param id Project ID
     * @return true if successful, false otherwise
     */
    public boolean approve(int id) {
        String sql = "UPDATE project SET status = 'approved', approval_date = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error approving project: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Delete project by ID
     * @param id Project ID
     * @return true if successful, false otherwise
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM project WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting project: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Map ResultSet to Project object
     */
    private Project mapResultSetToProject(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setId(rs.getInt("id"));
        project.setTitle(rs.getString("title"));
        project.setDescription(rs.getString("description"));
        project.setType(rs.getString("type"));
        project.setStudentId(rs.getInt("student_id"));
        project.setSupervisorId(rs.getInt("supervisor_id"));
        project.setStatus(rs.getString("status"));
        project.setFilePath(rs.getString("file_path"));
        project.setKeywords(rs.getString("keywords"));
        project.setYear(rs.getInt("year"));
        project.setSemester(rs.getString("semester"));
        project.setDepartment(rs.getString("department"));
        project.setSubmissionDate(rs.getTimestamp("submission_date"));
        project.setApprovalDate(rs.getTimestamp("approval_date"));
        project.setCreatedAt(rs.getTimestamp("created_at"));
        project.setUpdatedAt(rs.getTimestamp("updated_at"));
        return project;
    }
}
