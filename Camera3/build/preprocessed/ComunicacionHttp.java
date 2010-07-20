
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.*;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

/**
 *
 * @author insitu
 */
public class ComunicacionHttp {

    public static final byte ENVIAR_PERSONA = 0;
    public static final byte TRAER_PERSONAS = 1;
    public static final byte BYE = 2;
    Display pantalla;
    String server = "localhost";
    String port = "8080";
    String aplicacion = "/SimpleMVC/ServletEjemplo";
    //IndicadorProgreso indPr;
    Thread t;

    public ComunicacionHttp(MMSMIDlet eje) {
        pantalla = eje.getDisplay();
        //indPr = new IndicadorProgreso();
        //t = new Thread(indPr);

    }

    //Envio de datos por HTTP
    public void enviarPorHttp(Datos datos) throws IOException {
        Displayable previous = pantalla.getCurrent();
        //t.start();
        //pantalla.setCurrent(indPr);
        //Abrir conexion
        String url = "http://" + server + ":" + port + aplicacion;
        HttpConnection conn = (HttpConnection) Connector.open(url, Connector.READ_WRITE);
        conn.setRequestMethod(HttpConnection.POST);
        //Abrir Streams de salida para envio de peticion

        DataOutputStream dos = conn.openDataOutputStream();

        dos.writeUTF(datos.getNombre());
        dos.writeUTF(datos.getTel());

        // Mandar imagen
//        Image image = datos.getImagen();

//        int height, width;

        // Calculate needed size and allocate buffer area
        byte[] lectura;
        dos.writeInt(datos.getPngImage().length);
        lectura = datos.getPngImage();
        System.out.println("INICIO");
        for (int j = 0; j < datos.getPngImage().length; j++) {
//            System.out.println(lectura[j]);
            dos.writeByte(lectura[j]);
        }

//        height = image.getHeight();
//        width = image.getWidth();
//        int[] imgRgbData = new int[width * height];
//        image.getRGB(imgRgbData, 0, width, 0, 0, width, height);
//        dos.writeInt(width);
//        dos.writeInt(height);
//        dos.writeLong(System.currentTimeMillis());
//        dos.writeInt(imgRgbData.length);
//        //  Serialize the image raw data
//        for (int i = 0; i < imgRgbData.length; i++) {
//            dos.writeInt(imgRgbData[i]);
//            System.out.println(imgRgbData[i]);
//        }

        if (conn.getResponseCode() == HttpConnection.HTTP_OK) {
            //Abrir Streams de entrada para la captura de la respuesta
            InputStream is = conn.openInputStream();
            final int MAX_LENGTH = 1280;
            byte[] buf = new byte[MAX_LENGTH];
            int total = 0;
            while (total < MAX_LENGTH) {
                int count = is.read(buf, total, MAX_LENGTH - total);
                if (count < 0) {
                    break;
                }
                total += count;
            }
            is.close();
            ByteArrayInputStream bin = new ByteArrayInputStream(buf);
            DataInputStream dis = new DataInputStream(bin);
            String conf = dis.readUTF();
            if (conf.equals("ok")) {
                Alert a = new Alert("Info", "Envio exitoso", null, AlertType.CONFIRMATION);
                a.setTimeout(1000);
                pantalla.setCurrent(a, previous);
            }
            //indPr.stop();

        } else {
            System.out.print("ERROR: " + conn.getResponseCode());
        }
    }

    //Envio de datos por HTTP Solicitando el Mapa
    public InputStream requestMap(double platitud, double plongitud) throws IOException {

        InputStream is = null;
        String url = "http://maps.google.com/staticmap?center=" + platitud + "," + plongitud + "&maptype=mobile&zoom=8&size=180x100&key=ABQIAAAABeZfIxNYjCwcXnCyyn6_UxTwM0brOpm-All5BF6PoaKBxRWWERQBRYjiHo1pCHN8JVUB704wwgJFGQ";
        HttpConnection conn = (HttpConnection) Connector.open(url, Connector.READ_WRITE);

        if(conn.getResponseCode() == HttpConnection.HTTP_OK) {
            is = conn.openInputStream();
        } else {
            System.out.print("ERROR CONSULTANDO URL: " + conn.getResponseCode());
        }
        return is;
    }
}
