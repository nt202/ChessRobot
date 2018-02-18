package ru.nt202.chessrobotwebserver.servlets;

import ru.nt202.chessrobotwebserver.dl4j.Classification;
import ru.nt202.chessrobotwebserver.imageprocessing.FacadeImageProcessing;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@WebServlet("/result")
@MultipartConfig
public class ClassificationServlet extends HttpServlet {

    private static Classification classification;

    public ClassificationServlet() {
        classification = Classification.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long start = new Date().getTime();

        resp.setContentType("text; charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        try {
            Part filePart = req.getPart("board"); // Retrieves <input type="file" name="board">
            InputStream board = filePart.getInputStream();
            BufferedImage[] squares = new FacadeImageProcessing(board).process();
            resp.getWriter().println(classification.getResult(squares));
        } catch (Exception e) {
            e.printStackTrace();
        }

        long stop = new Date().getTime();

        System.out.println("Duration = " + (stop-start)/1000 + " s");
    }
}
