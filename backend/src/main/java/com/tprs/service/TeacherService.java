package com.tprs.service;

import com.tprs.dao.TeacherDAO;
import com.tprs.model.Teacher;
import com.tprs.util.PasswordUtil;

import java.util.List;

/**
 * Teacher Service Layer
 * Handles business logic for Teacher operations
 */
public class TeacherService {
    
    private TeacherDAO teacherDAO;
    
    public TeacherService() {
        this.teacherDAO = new TeacherDAO();
    }
    
    /**
     * Register a new teacher
     * @param teacher Teacher object
     * @return true if successful, false otherwise
     */
    public boolean register(Teacher teacher) {
        // Check if email already exists
        if (teacherDAO.getByEmail(teacher.getEmail()) != null) {
            System.out.println("Email already registered!");
            return false;
        }
        
        teacher.setPassword(PasswordUtil.hashPassword(teacher.getPassword()));
        
        return teacherDAO.create(teacher);
    }
    
    /**
     * Login teacher
     * @param email Teacher email
     * @param password Teacher password
     * @return Teacher object if authenticated, null otherwise
     */
    public Teacher login(String email, String password) {
        Teacher teacher = teacherDAO.getByEmail(email);
        if (teacher != null && PasswordUtil.checkPassword(password, teacher.getPassword())) {
            return teacher;
        }
        return null;
    }
    
    /**
     * Get teacher by ID
     * @param id Teacher ID
     * @return Teacher object
     */
    public Teacher getById(int id) {
        return teacherDAO.getById(id);
    }
    
    /**
     * Get teacher by email
     * @param email Teacher email
     * @return Teacher object
     */
    public Teacher getByEmail(String email) {
        return teacherDAO.getByEmail(email);
    }
    
    /**
     * Get all teachers
     * @return List of all teachers
     */
    public List<Teacher> getAllTeachers() {
        return teacherDAO.getAll();
    }
    
    /**
     * Get teachers by department
     * @param department Department name
     * @return List of teachers in the department
     */
    public List<Teacher> getTeachersByDepartment(String department) {
        return teacherDAO.getByDepartment(department);
    }
    
    /**
     * Update teacher profile
     * @param teacher Teacher object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateProfile(Teacher teacher) {
        return teacherDAO.update(teacher);
    }
    
    /**
     * Delete teacher
     * @param id Teacher ID
     * @return true if successful, false otherwise
     */
    public boolean deleteTeacher(int id) {
        return teacherDAO.delete(id);
    }
    
    /**
     * Change password
     * @param teacherId Teacher ID
     * @param oldPassword Old password
     * @param newPassword New password
     * @return true if successful, false otherwise
     */
    public boolean changePassword(int teacherId, String oldPassword, String newPassword) {
        Teacher teacher = teacherDAO.getById(teacherId);
        if (teacher != null && PasswordUtil.checkPassword(oldPassword, teacher.getPassword())) {
            String hashedPassword = PasswordUtil.hashPassword(newPassword);
            return teacherDAO.updatePassword(teacherId, hashedPassword);
        }
        return false;
    }
}
