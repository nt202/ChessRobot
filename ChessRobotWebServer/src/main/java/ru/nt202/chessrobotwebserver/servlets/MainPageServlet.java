package ru.nt202.chessrobotwebserver.servlets;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.IOException;

@WebServlet("/main")
@MultipartConfig
public class MainPageServlet extends HttpServlet {
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(getPage("page.html"));
    }

    // TODO: too complicated, need to simplify
    private String getPage(final String filename) {
        String output = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            is = MainPageServlet.class.getClass().getResourceAsStream("/" + filename);
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            output = IOUtils.toString(br);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
                if (br != null) br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return output;
    }
}
