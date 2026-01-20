package com.tprs.service;

import com.tprs.dao.ProjectDAO;
import com.tprs.model.Project;

import java.util.List;

/**
 * Project Service Layer
 * Handles business logic for Project operations
 */
public class ProjectService {
    
    private ProjectDAO projectDAO;
    
    public ProjectService() {
        this.projectDAO = new ProjectDAO();
    }
    
    /**
     * Submit a new project
     * @param project Project object
     * @return true if successful, false otherwise
     */
    public boolean submitProject(Project project) {
        project.setStatus("pending");
        return projectDAO.create(project);
    }
    
    /**
     * Get project by ID
     * @param id Project ID
     * @return Project object
     */
    public Project getById(int id) {
        return projectDAO.getById(id);
    }
    
    /**
     * Get all projects
     * @return List of all projects
     */
    public List<Project> getAllProjects() {
        return projectDAO.getAll();
    }
    
    /**
     * Get projects by student
     * @param studentId Student ID
     * @return List of projects
     */
    public List<Project> getProjectsByStudent(int studentId) {
        return projectDAO.getByStudentId(studentId);
    }
    
    /**
     * Get projects by supervisor
     * @param supervisorId Supervisor/Teacher ID
     * @return List of projects
     */
    public List<Project> getProjectsBySupervisor(int supervisorId) {
        return projectDAO.getBySupervisorId(supervisorId);
    }
    
    /**
     * Get projects by department
     * @param department Department name
     * @return List of projects
     */
    public List<Project> getProjectsByDepartment(String department) {
        return projectDAO.getByDepartment(department);
    }
    
    /**
     * Get projects by status
     * @param status Project status
     * @return List of projects
     */
    public List<Project> getProjectsByStatus(String status) {
        return projectDAO.getByStatus(status);
    }
    
    /**
     * Search projects by keyword
     * @param keyword Search keyword
     * @return List of matching projects
     */
    public List<Project> searchProjects(String keyword) {
        return projectDAO.searchByKeyword(keyword);
    }
    
    /**
     * Update project
     * @param project Project object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateProject(Project project) {
        return projectDAO.update(project);
    }
    
    /**
     * Update project status
     * @param projectId Project ID
     * @param status New status
     * @return true if successful, false otherwise
     */
    public boolean updateStatus(int projectId, String status) {
        return projectDAO.updateStatus(projectId, status);
    }
    
    /**
     * Approve project (by supervisor)
     * @param projectId Project ID
     * @return true if successful, false otherwise
     */
    public boolean approveProject(int projectId) {
        return projectDAO.approve(projectId);
    }
    
    /**
     * Reject project
     * @param projectId Project ID
     * @return true if successful, false otherwise
     */
    public boolean rejectProject(int projectId) {
        return projectDAO.updateStatus(projectId, "rejected");
    }
    
    /**
     * Delete project
     * @param id Project ID
     * @return true if successful, false otherwise
     */
    public boolean deleteProject(int id) {
        return projectDAO.delete(id);
    }
    
    /**
     * Get pending projects for approval
     * @return List of pending projects
     */
    public List<Project> getPendingProjects() {
        return projectDAO.getByStatus("pending");
    }
    
    /**
     * Get approved projects
     * @return List of approved projects
     */
    public List<Project> getApprovedProjects() {
        return projectDAO.getByStatus("approved");
    }
}
