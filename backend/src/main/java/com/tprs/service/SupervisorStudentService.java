package com.tprs.service;

import com.tprs.dao.SupervisorStudentDAO;
import com.tprs.model.Student;
import com.tprs.model.Teacher;

import java.util.List;

/**
 * Supervisor-Student Assignment Service Layer
 */
public class SupervisorStudentService {
    
    private SupervisorStudentDAO supervisorStudentDAO;
    
    public SupervisorStudentService() {
        this.supervisorStudentDAO = new SupervisorStudentDAO();
    }
    
    /**
     * Assign a student to a supervisor
     */
    public boolean assignStudent(int supervisorId, int studentId) {
        return supervisorStudentDAO.assign(supervisorId, studentId);
    }
    
    /**
     * Remove a student from a supervisor
     */
    public boolean unassignStudent(int supervisorId, int studentId) {
        return supervisorStudentDAO.unassign(supervisorId, studentId);
    }
    
    /**
     * Get all students assigned to a supervisor
     */
    public List<Student> getAssignedStudents(int supervisorId) {
        return supervisorStudentDAO.getStudentsBySupervisor(supervisorId);
    }
    
    /**
     * Get supervisors for a student
     */
    public List<Teacher> getSupervisorsForStudent(int studentId) {
        return supervisorStudentDAO.getSupervisorsForStudent(studentId);
    }
    
    /**
     * Check if student is assigned to supervisor
     */
    public boolean isAssigned(int supervisorId, int studentId) {
        return supervisorStudentDAO.isAssigned(supervisorId, studentId);
    }
    
    /**
     * Get students not yet assigned to this supervisor
     */
    public List<Student> getUnassignedStudents(int supervisorId) {
        return supervisorStudentDAO.getUnassignedStudents(supervisorId);
    }
}
