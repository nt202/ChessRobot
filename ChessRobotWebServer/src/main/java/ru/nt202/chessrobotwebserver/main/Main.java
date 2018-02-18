package ru.nt202.chessrobotwebserver.main;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.nt202.chessrobotwebserver.servlets.MainPageServlet;
import ru.nt202.chessrobotwebserver.servlets.ClassificationServlet;

import javax.servlet.MultipartConfigElement;

public class Main {
    public static void main(String[] args) throws Exception {

        // Servlets:
        MainPageServlet mainPageServlet = new MainPageServlet();
        ClassificationServlet classificationServlet = new ClassificationServlet();

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        ServletHolder classificationServletHolder = new ServletHolder(classificationServlet);
        classificationServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("/result"));
        context.addServlet(classificationServletHolder, "/result");

        context.addServlet(new ServletHolder(mainPageServlet), "/main");

        // Server:
        Server server = new Server(8080);
        server.setHandler(context);

        server.start();
        server.join();
    }
}
