package com.tprs.servlet;

import com.tprs.config.DatabaseConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * Dashboard Servlet - Provides dashboard statistics and recent data
 */
public class DashboardServlet extends HttpServlet {
    
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        gson = new Gson();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String pathInfo = request.getPathInfo();
        JsonObject jsonResponse = new JsonObject();
        
        try {
            Connection conn = DatabaseConfig.getConnection();
            
            if (pathInfo == null || "/".equals(pathInfo) || "/stats".equals(pathInfo)) {
                // Get dashboard statistics using stored procedure
                CallableStatement stmt = conn.prepareCall("{CALL GetDashboardStats()}");
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    JsonObject stats = new JsonObject();
                    stats.addProperty("totalStudents", rs.getInt("total_students"));
                    stats.addProperty("totalTeachers", rs.getInt("total_teachers"));
                    stats.addProperty("totalProjects", rs.getInt("total_projects"));
                    stats.addProperty("pendingProjects", rs.getInt("pending_projects"));
                    stats.addProperty("approvedProjects", rs.getInt("approved_projects"));
                    stats.addProperty("inProgressProjects", rs.getInt("in_progress_projects"));
                    stats.addProperty("completedProjects", rs.getInt("completed_projects"));
                    stats.addProperty("rejectedProjects", rs.getInt("rejected_projects"));
                    
                    jsonResponse.addProperty("success", true);
                    jsonResponse.add("stats", stats);
                }
                
                rs.close();
                stmt.close();
                
            } else if ("/recent".equals(pathInfo)) {
                // Get recent projects using stored procedure
                String limitParam = request.getParameter("limit");
                int limit = limitParam != null ? Integer.parseInt(limitParam) : 10;
                
                CallableStatement stmt = conn.prepareCall("{CALL GetRecentProjects(?)}");
                stmt.setInt(1, limit);
                ResultSet rs = stmt.executeQuery();
                
                jsonResponse.addProperty("success", true);
                jsonResponse.add("projects", resultSetToJsonArray(rs));
                
                rs.close();
                stmt.close();
                
            } else if ("/by-department".equals(pathInfo)) {
                // Get projects count by department
                CallableStatement stmt = conn.prepareCall("{CALL GetProjectsCountByDepartment()}");
                ResultSet rs = stmt.executeQuery();
                
                jsonResponse.addProperty("success", true);
                jsonResponse.add("departmentStats", resultSetToJsonArray(rs));
                
                rs.close();
                stmt.close();
                
            } else if ("/by-year".equals(pathInfo)) {
                // Get projects count by year
                CallableStatement stmt = conn.prepareCall("{CALL GetProjectsCountByYear()}");
                ResultSet rs = stmt.executeQuery();
                
                jsonResponse.addProperty("success", true);
                jsonResponse.add("yearStats", resultSetToJsonArray(rs));
                
                rs.close();
                stmt.close();
            }
            
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Database error: " + e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Server error: " + e.getMessage());
        }
        
        out.print(gson.toJson(jsonResponse));
        out.flush();
    }
    
    private com.google.gson.JsonArray resultSetToJsonArray(ResultSet rs) throws SQLException {
        com.google.gson.JsonArray jsonArray = new com.google.gson.JsonArray();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        while (rs.next()) {
            JsonObject row = new JsonObject();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                Object value = rs.getObject(i);
                
                if (value == null) {
                    row.add(columnName, null);
                } else if (value instanceof Integer) {
                    row.addProperty(columnName, (Integer) value);
                } else if (value instanceof Long) {
                    row.addProperty(columnName, (Long) value);
                } else if (value instanceof Double) {
                    row.addProperty(columnName, (Double) value);
                } else if (value instanceof Boolean) {
                    row.addProperty(columnName, (Boolean) value);
                } else {
                    row.addProperty(columnName, value.toString());
                }
            }
            jsonArray.add(row);
        }
        
        return jsonArray;
    }
}
