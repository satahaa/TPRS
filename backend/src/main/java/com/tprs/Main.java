package com.tprs;

import com.tprs.config.DatabaseConfig;
import com.tprs.service.StudentService;
import com.tprs.service.TeacherService;
import com.tprs.service.ProjectService;
import com.tprs.model.Student;
import com.tprs.model.Teacher;
import com.tprs.model.Project;

import java.util.List;

/**
 * Main Application Class
 * Entry point for TPRS - Thesis and Project Repository System
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   TPRS - Thesis and Project Repository");
        System.out.println("========================================\n");
        
        // Test database connection
        System.out.println("Testing database connection...");
        if (DatabaseConfig.testConnection()) {
            System.out.println("\n--- Running Demo Operations ---\n");
            
            // Initialize services
            StudentService studentService = new StudentService();
            TeacherService teacherService = new TeacherService();
            ProjectService projectService = new ProjectService();
            
            // Demo: List all students
            System.out.println("📚 All Students:");
            List<Student> students = studentService.getAllStudents();
            for (Student student : students) {
                System.out.println("   - " + student);
            }
            
            // Demo: List all teachers
            System.out.println("\n👨‍🏫 All Teachers:");
            List<Teacher> teachers = teacherService.getAllTeachers();
            for (Teacher teacher : teachers) {
                System.out.println("   - " + teacher);
            }
            
            // Demo: List all projects
            System.out.println("\n📁 All Projects:");
            List<Project> projects = projectService.getAllProjects();
            for (Project project : projects) {
                System.out.println("   - " + project);
            }
            
            // Demo: Get pending projects
            System.out.println("\n⏳ Pending Projects:");
            List<Project> pendingProjects = projectService.getPendingProjects();
            for (Project project : pendingProjects) {
                System.out.println("   - " + project.getTitle());
            }
            
            System.out.println("\n========================================");
            System.out.println("   Demo completed successfully!");
            System.out.println("========================================");
        } else {
            System.err.println("\n✗ Failed to connect to database.");
            System.err.println("  Please check your MySQL configuration:");
            System.err.println("  1. Ensure MySQL is running");
            System.err.println("  2. Update credentials in DatabaseConfig.java");
            System.err.println("  3. Run the SQL script: sql/create_database.sql");
        }
        
        // Close connection
        DatabaseConfig.closeConnection();
    }
}
