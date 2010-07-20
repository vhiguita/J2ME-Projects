package com.encuestas;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

import javax.servlet.http.HttpServlet;

public class ServletEjemplo extends HttpServlet {

    public void init() throws ServletException {
        super.init();

    }

    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String salida = null;
        DataInputStream din = null;
        PrintWriter out = null;

        ByteArrayOutputStream bout = null;
        DataOutputStream dos = null;


        try {
            out = response.getWriter();
            ServletInputStream in = request.getInputStream();
            din = new DataInputStream(in);
        } catch (IOException io) {
            System.out.println("Se ha producido una excepcion");
        }

        try {

            // Get Name
            String nombre = din.readUTF();
            System.out.println("Nombre " + nombre);

            // Get Phone
            String telefono = din.readUTF();
            System.out.println("Telefono " + telefono);

            // Get Image
            int length = din.readInt();
            byte[] rawImg = new byte[length];
            for (int j = 0; j < length; j++) {
                rawImg[j] = din.readByte();
            }
            String imgDir = this.getServletContext().getRealPath("/") + "\\images";
            File dir = new File(imgDir);
            String filePathName = null;
            String fileName = null;
            try {
                fileName = "pic" + dir.list().length + ".png";
                filePathName =  imgDir + "\\" + fileName;
                FileOutputStream file = new FileOutputStream(filePathName);
//                for (int i = 0; i < rawImg.length; i++) {
                    file.write(rawImg);
//                }
                file.flush();
                file.close();
            } catch (IOException e) {
                System.out.println("Error--" + e.toString());
            }

            if (nombre != null && telefono != null) {
                ToDoList toDoList = (ToDoList) getServletContext().getAttribute("toDoList");
                toDoList.addItem(nombre, telefono, fileName);
            }


            bout = new ByteArrayOutputStream();
            dos = new DataOutputStream(bout);
            dos.writeUTF("ok");
            byte[] salidaB = bout.toByteArray();
            salida = new String(salidaB);
            out.print(salida);
            out.flush();

            int contentLength = salida.length();

            //System.out.println("ContentLength= " + contentLength);
            response.setContentLength(contentLength);
        } catch (IOException ae) {
            ae.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        out.print(salida);
    }

    //    public boolean guardarPersona(Persona persona) {
//        System.out.println("Guardando Persona...");
//        System.out.println("Nombre:   " + persona.getNombre());
//        System.out.println("Apellido:     " + persona.getApellido());
//
//        return true;
//    }
}