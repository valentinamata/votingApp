/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;


import view.JavaFacade;
import view.JavaCodedFacadeShitIsDone;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.Users;

public class RestSthis {
    static int port = 3333;
    static String ip = "localhost";
   // static String publicFolder = "src/htmlFiles/";
    static JavaCodedFacadeShitIsDone facade = JavaCodedFacadeShitIsDone.getFacade(true);
    private static final boolean DEVELOPMENT_MODE = true;
    
    public void run() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(ip, port), 0);
    //REST Routes

        //HTTP Server Routes
        server.createContext("/user", new HandlerTheUserByEmail());
        server.createContext("/admin", new HandlerAdmin());
        

        server.start();

    }
    
    public static void main(String[] args) throws Exception {
        if (args.length >= 2) {
            port = Integer.parseInt(args[0]);
            ip = args[1];
        }
        new RestSthis().run();
    }
    
    
    private static class HandlerTheUserByEmail implements HttpHandler {

        public void handle(HttpExchange he) throws IOException {
            String response = "";
            int status = 200;
            String method = he.getRequestMethod().toUpperCase();
        switch (method) {
                case "GET":
                    try {
                        String path = he.getRequestURI().getPath();
                        int lastIndex = path.lastIndexOf("/");
                        String usr = path.substring(lastIndex+1, path.length());
                        if (lastIndex > 0) { 
                            
                            response = facade.getPersonAsJson(usr);
                        } 
                    } 
                    catch (NumberFormatException nfe) {
                        response = "Id is not a string";
                        status = 404;
                    }
                    break;
                case "POST":
                    try {
                        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
                        BufferedReader br = new BufferedReader(isr);
                        String jsonQuery = br.readLine();
                        if (jsonQuery.contains("<") || jsonQuery.contains(">")) {
                            //Simple anti-Martin check :-)
                            throw new IllegalArgumentException("Illegal characters in input");
                        }
                        Users p = facade.addPersonFromGson(jsonQuery);
                        
                        response = new Gson().toJson(p);
                    } catch (IllegalArgumentException iae) {
                        status = 400;
                        response = iae.getMessage();
                    } catch (IOException e) {
                        status = 500;
                        response = "Internal Server Problem";
                    }
                    break;
//               
            }
        he.getResponseHeaders().add("Content-Type", "application/json");
            he.sendResponseHeaders(status, 0);
            try (OutputStream os = he.getResponseBody()) {
                os.write(response.getBytes());
            }
    }
   
    }
    
     private static class HandlerAdmin implements HttpHandler {

        public void handle(HttpExchange he) throws IOException {
            String response = "";
            int status = 200;
            String method = he.getRequestMethod().toUpperCase();
        switch (method) {
                case "GET":
                      try {
                        String path = he.getRequestURI().getPath();
                        int lastIndex = path.lastIndexOf("/");
                        String usr = path.substring(lastIndex+1, path.length());
                        if (lastIndex > 0) { 
                            
                            response = facade.getPersonAsJson(usr);
                        } 
                    } 
                    catch (NumberFormatException nfe) {
                        response = "Id is not a number";
                        status = 404;
                    }
                    break;
                case "POST":
                    try {
                        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
                        BufferedReader br = new BufferedReader(isr);
                        String jsonQuery = br.readLine();
                        if (jsonQuery.contains("<") || jsonQuery.contains(">")) {
                            //Simple anti-Martin check :-)
                            throw new IllegalArgumentException("Illegal characters in input");
                        }
                        Users p = facade.addPersonFromGson(jsonQuery);
                        
                        response = new Gson().toJson(p);
                    } catch (IllegalArgumentException iae) {
                        status = 400;
                        response = iae.getMessage();
                    } catch (IOException e) {
                        status = 500;
                        response = "Internal Server Problem";
                    }
                    break;
               
            
        case "DELETE":
                    try {
                        String path = he.getRequestURI().getPath();
                        int lastIndex = path.lastIndexOf("/");
                        String usr = path.substring(lastIndex+1, path.length());
                        if (lastIndex > 0) { 
                            
                            Users pDeleted = facade.delete(usr);
                            response = new Gson().toJson(pDeleted);
                        } else {
                            status = 400;
                            response = "<h1>Bad Request</h1>No id supplied with request";
                        }
                    } //                    catch (NotFoundException nfe) {
                    //                                            status = 404;
                    //                                            response = nfe.getMessage();
                    // } 
                    catch (NumberFormatException nfe) {
                        response = "Id is not a number";
                        status = 404;
                    }
                    break;
            
    }
        
            he.getResponseHeaders().add("Content-Type", "application/json");
            he.sendResponseHeaders(status, 0);
            try (OutputStream os = he.getResponseBody()) {
                os.write(response.getBytes());
            }
    }
}
     
}