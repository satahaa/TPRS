package com.tprs.servlet;

import com.tprs.service.StudentService;
import com.tprs.service.TeacherService;
import com.tprs.model.Student;
import com.tprs.model.Teacher;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Authentication Servlet - Handles login and registration
 */
@WebServlet(urlPatterns = {"/api/auth/login", "/api/auth/register", "/api/auth/register-teacher"})
public class AuthServlet extends HttpServlet {
    
    private StudentService studentService;
    private TeacherService teacherService;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        studentService = new StudentService();
        teacherService = new TeacherService();
        gson = new Gson();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String path = request.getServletPath();
        JsonObject jsonResponse = new JsonObject();
        
        try {
            // Read request body
            BufferedReader reader = request.getReader();
            JsonObject requestData = gson.fromJson(reader, JsonObject.class);
            
            if ("/api/auth/login".equals(path)) {
                handleLogin(requestData, jsonResponse, response);
            } else if ("/api/auth/register".equals(path)) {
                handleStudentRegistration(requestData, jsonResponse, response);
            } else if ("/api/auth/register-teacher".equals(path)) {
                handleTeacherRegistration(requestData, jsonResponse, response);
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Server error: " + e.getMessage());
        }
        
        out.print(gson.toJson(jsonResponse));
        out.flush();
    }
    
    private void handleLogin(JsonObject data, JsonObject jsonResponse, HttpServletResponse response) {
        String email = data.get("email").getAsString();
        String password = data.get("password").getAsString();
        String userType = data.has("userType") ? data.get("userType").getAsString() : "student";
        
        if ("teacher".equals(userType)) {
            Teacher teacher = teacherService.login(email, password);
            if (teacher != null) {
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Login successful");
                jsonResponse.addProperty("userType", "teacher");
                jsonResponse.add("user", gson.toJsonTree(teacher));
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Invalid email or password");
            }
        } else {
            Student student = studentService.login(email, password);
            if (student != null) {
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Login successful");
                jsonResponse.addProperty("userType", "student");
                jsonResponse.add("user", gson.toJsonTree(student));
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Invalid email or password");
            }
        }
    }
    
    private void handleStudentRegistration(JsonObject data, JsonObject jsonResponse, HttpServletResponse response) {
        Student student = new Student();
        student.setStudentId(data.get("studentId").getAsString());
        student.setFirstName(data.get("firstName").getAsString());
        student.setLastName(data.get("lastName").getAsString());
        student.setEmail(data.get("email").getAsString());
        student.setPassword(data.get("password").getAsString());
        student.setDepartment(data.get("department").getAsString());
        student.setSemester(data.has("semester") ? data.get("semester").getAsString() : "");
        student.setPhone(data.has("phone") ? data.get("phone").getAsString() : "");
        
        boolean success = studentService.register(student);
        
        if (success) {
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Registration successful");
            jsonResponse.addProperty("userId", student.getId());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Email already registered or registration failed");
        }
    }
    
    private void handleTeacherRegistration(JsonObject data, JsonObject jsonResponse, HttpServletResponse response) {
        Teacher teacher = new Teacher();
        teacher.setTeacherId(data.get("teacherId").getAsString());
        teacher.setFirstName(data.get("firstName").getAsString());
        teacher.setLastName(data.get("lastName").getAsString());
        teacher.setEmail(data.get("email").getAsString());
        teacher.setPassword(data.get("password").getAsString());
        teacher.setDepartment(data.get("department").getAsString());
        teacher.setDesignation(data.has("designation") ? data.get("designation").getAsString() : "");
        teacher.setSpecialization(data.has("specialization") ? data.get("specialization").getAsString() : "");
        teacher.setPhone(data.has("phone") ? data.get("phone").getAsString() : "");
        
        boolean success = teacherService.register(teacher);
        
        if (success) {
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Registration successful");
            jsonResponse.addProperty("userId", teacher.getId());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Email already registered or registration failed");
        }
    }
}
