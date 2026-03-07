package com.tprs.servlet;

import com.tprs.service.StudentService;
import com.tprs.service.TeacherService;
import com.tprs.model.Student;
import com.tprs.model.Teacher;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Authentication Servlet - Handles login and registration
 */
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
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        JsonObject jsonResponse = new JsonObject();
        
        try {
            BufferedReader reader = request.getReader();
            JsonObject data = gson.fromJson(reader, JsonObject.class);
            
            String userType = getJsonString(data, "userType", "");
            int userId = data.get("userId").getAsInt();
            String phone = getJsonString(data, "phone", "");
            
            boolean success = false;
            if ("student".equals(userType)) {
                Student student = studentService.getById(userId);
                if (student != null) {
                    student.setPhone(phone);
                    success = studentService.updateProfile(student);
                }
            } else if ("teacher".equals(userType)) {
                Teacher teacher = teacherService.getById(userId);
                if (teacher != null) {
                    teacher.setPhone(phone);
                    success = teacherService.updateProfile(teacher);
                }
            }
            
            if (success) {
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Phone updated successfully");
            } else {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to update phone");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Server error: " + e.getMessage());
        }
        
        out.print(gson.toJson(jsonResponse));
        out.flush();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        // Get the path info after /api/auth
        String pathInfo = request.getPathInfo();
        JsonObject jsonResponse = new JsonObject();
        
        try {
            // Read request body
            BufferedReader reader = request.getReader();
            JsonObject requestData = gson.fromJson(reader, JsonObject.class);
            
            System.out.println("PathInfo: " + pathInfo);
            
            if ("/login".equals(pathInfo)) {
                handleLogin(requestData, jsonResponse, response);
            } else if ("/register".equals(pathInfo)) {
                handleStudentRegistration(requestData, jsonResponse, response);
            } else if ("/register-teacher".equals(pathInfo)) {
                handleTeacherRegistration(requestData, jsonResponse, response);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Unknown endpoint: " + pathInfo);
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Server error: " + e.getMessage());
            e.printStackTrace();
        }
        
        out.print(gson.toJson(jsonResponse));
        out.flush();
    }
    
    private void handleLogin(JsonObject data, JsonObject jsonResponse, HttpServletResponse response) {
        String email = data.get("email").getAsString();
        String password = data.get("password").getAsString();
        
        // Auto-detect role: try teacher first, then student
        Teacher teacher = teacherService.login(email, password);
        if (teacher != null) {
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Login successful");
            jsonResponse.addProperty("userType", "teacher");
            jsonResponse.addProperty("redirect", "supervisor-dashboard.html");
            jsonResponse.add("user", gson.toJsonTree(teacher));
            return;
        }
        
        Student student = studentService.login(email, password);
        if (student != null) {
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Login successful");
            jsonResponse.addProperty("userType", "student");
            jsonResponse.addProperty("redirect", "home.html");
            jsonResponse.add("user", gson.toJsonTree(student));
            return;
        }
        
        // Neither found - invalid credentials
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        jsonResponse.addProperty("success", false);
        jsonResponse.addProperty("message", "Invalid email or password");
    }
    
    private void handleStudentRegistration(JsonObject data, JsonObject jsonResponse, HttpServletResponse response) {
        try {
            Student student = new Student();
            student.setStudentId(getJsonString(data, "studentId", ""));
            student.setFirstName(getJsonString(data, "firstName", ""));
            student.setLastName(getJsonString(data, "lastName", ""));
            String studentEmail = getJsonString(data, "email", "");
            if (!studentEmail.endsWith("@mbstu.ac.bd")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Email must end with @mbstu.ac.bd");
                return;
            }
            student.setEmail(studentEmail);
            student.setPassword(getJsonString(data, "password", ""));
            student.setDepartment(getJsonString(data, "department", ""));
            // Handle both "semester" and "degreeType" from frontend
            String semester = getJsonString(data, "semester", "");
            if (semester.isEmpty()) {
                semester = getJsonString(data, "degreeType", "");
            }
            student.setSemester(semester);
            student.setSession(getJsonString(data, "session", ""));
            student.setPhone(getJsonString(data, "phone", ""));
            
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
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Invalid registration data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String getJsonString(JsonObject data, String key, String defaultValue) {
        if (data.has(key) && !data.get(key).isJsonNull()) {
            return data.get(key).getAsString();
        }
        return defaultValue;
    }
    
    private void handleTeacherRegistration(JsonObject data, JsonObject jsonResponse, HttpServletResponse response) {
        try {
            Teacher teacher = new Teacher();
            String teacherIdInput = getJsonString(data, "teacherId", "");
            if (teacherIdInput.isEmpty()) {
                // Auto-generate teacher ID
                teacherIdInput = "T" + System.currentTimeMillis();
            }
            teacher.setTeacherId(teacherIdInput);
            teacher.setFirstName(getJsonString(data, "firstName", ""));
            teacher.setLastName(getJsonString(data, "lastName", ""));
            String teacherEmail = getJsonString(data, "email", "");
            if (!teacherEmail.endsWith("@mbstu.ac.bd")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Email must end with @mbstu.ac.bd");
                return;
            }
            teacher.setEmail(teacherEmail);
            teacher.setPassword(getJsonString(data, "password", ""));
            teacher.setDepartment(getJsonString(data, "department", ""));
            teacher.setDesignation(getJsonString(data, "designation", ""));
            teacher.setSpecialization(getJsonString(data, "specialization", ""));
            teacher.setPhone(getJsonString(data, "phone", ""));
            
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
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Invalid registration data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
