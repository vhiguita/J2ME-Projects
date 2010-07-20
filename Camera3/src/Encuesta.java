/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Victor
 */
import java.util.Vector;
import javax.microedition.rms.*;
import java.io.*;
import javax.microedition.lcdui.Image;

public class Encuesta {

    private static RecordStore imagesRS = null;
    static byte []lectura;
    static void almacenaEncuesta(Datos d) {
        Image image = d.getImagen();

//        String resourceName = "imagen.png";


        int height, width;
//        if (resourceName == null) {
//            return; // resource name is required
//        }
        // Calculate needed size and allocate buffer area
        height = image.getHeight();
        width = image.getWidth();

        int[] imgRgbData = new int[width * height];

        try {
            image.getRGB(imgRgbData, 0, width, 0, 0, width, height);
            imagesRS = RecordStore.openRecordStore("datos", true);

            //
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(bout);
            //  Serialize the image name
//            dout.writeUTF(resourceName);
            dout.writeUTF(d.getNombre());
            dout.writeUTF(d.getTel());
            dout.writeUTF(d.getDir());
            dout.writeUTF(d.getIntra());
            dout.writeUTF(d.getIntranet());
            dout.writeUTF(d.getAcceso());
            dout.writeInt(width);
            dout.writeInt(height);
            dout.writeLong(System.currentTimeMillis());
            dout.writeInt(imgRgbData.length);
            //  Serialize the image raw data
            for (int i = 0; i < imgRgbData.length; i++) {
                dout.writeInt(imgRgbData[i]);
//                System.out.println(imgRgbData[i]);
            }
            dout.writeInt(d.getPngImage().length);
            lectura=d.getPngImage();
            System.out.println("almacenar:prim:ult:" + lectura[0] + " " + lectura[lectura.length-1]);
            for(int j=0;j<d.getPngImage().length;j++){
//               System.out.println(lectura[j]);
               dout.writeByte(lectura[j]);
            }
           

            dout.flush();
            dout.close();
            byte[] data = bout.toByteArray();
            imagesRS.addRecord(data, 0, data.length);
            bout.reset();
            dout.close();

            bout.close();

            //imagesRS.closeRecordStore();
            log("stored to RMS");
        } catch (Exception e) {
            log("Err in Add to RMS" + e);
        } finally {
            try {
                // Close the Record Store
                if (imagesRS != null) {
                    imagesRS.closeRecordStore();
//                    log("Err in Add to RMS miami" );
                }
            } catch (Exception ignore) {
                // Ignore
                log("Err in Add to RMS sueden");
            }
        }
    }

    static void modificarEncuesta(Datos d, String persona) {
        try {
            imagesRS = RecordStore.openRecordStore("datos", true);
            RecordEnumeration re = imagesRS.enumerateRecords(null, null, true);
            boolean encontro = false;
            while (re.hasNextElement() && encontro == false) {
                int pos = re.nextRecordId();
                byte[] b = imagesRS.getRecord(pos);

                ByteArrayInputStream bais = new ByteArrayInputStream(b);
                DataInputStream dis = new DataInputStream(bais);
                String nombre = dis.readUTF();
                nombre = nombre.trim();
                System.out.println("mi nombre" + nombre);
                System.out.println("POSICION ACTUAL" + pos);
                if (persona.equals(nombre)) {
                    System.out.println("posicion actualizada" + pos);
                    modifEncuesta(d, pos);
                    encontro = true;

                }
                bais.reset();
                dis.reset();
                bais.close();
                dis.close();
            }
            imagesRS.closeRecordStore();
        } catch (Exception e) {
        }
    }

    static void modifEncuesta(Datos d, int pos) {
        Image image = d.getImagen();
//        String resourceName = "imagen.png";
        int height, width;
//        if (resourceName == null) {
//            return; // resource name is required
//        }
        // Calculate needed size and allocate buffer area
        height = image.getHeight();
        width = image.getWidth();
        int[] imgRgbData = new int[width * height];
        try {
            image.getRGB(imgRgbData, 0, width, 0, 0, width, height);
            imagesRS = RecordStore.openRecordStore("datos", true);

            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bas);
            dos.writeUTF(d.getNombre());
            dos.writeUTF(d.getTel());
            dos.writeUTF(d.getDir());
            dos.writeUTF(d.getIntra());
            dos.writeUTF(d.getIntranet());
            dos.writeUTF(d.getAcceso());
            dos.writeInt(width);
            dos.writeInt(height);
            dos.writeLong(System.currentTimeMillis());
            dos.writeInt(imgRgbData.length);
            //  Serialize the image raw data
            for (int i = 0; i < imgRgbData.length; i++) {
                dos.writeInt(imgRgbData[i]);
            }
            dos.flush();
            dos.close();
            byte[] data = bas.toByteArray();
            imagesRS.setRecord(pos, data, 0, data.length);
            bas.reset();
            dos.close();
            bas.close();
            imagesRS.closeRecordStore();
        } catch (Exception e) {
        }

    }

    static Vector leerEncuesta() {

//        RecordStore imagesRS = null;
        Image img  = null;
/*-->*/ Image mapa = null;
        Vector persons = new Vector();
        try {
            Datos datos;
            imagesRS = RecordStore.openRecordStore("datos", true);
            RecordEnumeration re = imagesRS.enumerateRecords(null, null, true);
            int numRecs = re.numRecords();
            System.out.println(numRecs);
            // For each record
            while (re.hasNextElement()) {
                byte[] b = re.nextRecord();
                ByteArrayInputStream bais = new ByteArrayInputStream(b);
                DataInputStream dis = new DataInputStream(bais);
                String nombre = dis.readUTF();
                String tel = dis.readUTF();
                String dir = dis.readUTF();
                String in = dis.readUTF();
                System.out.println(nombre);
                System.out.println(tel);
                String intra = dis.readUTF();
                String acceso = dis.readUTF();
                int width = dis.readInt();
                int height = dis.readInt();
                long timestamp = dis.readLong();
                int length = dis.readInt();

                int[] rawImg = new int[width * height];
                //  Serialize the image raw data
                for (int i = 0; i < length; i++) {
                    rawImg[i] = dis.readInt();
                }
                img  = Image.createRGBImage(rawImg, width, height, false);
/*-->*/         mapa = Image.createRGBImage(rawImg, width, height, false);
                System.out.println(nombre);
                System.out.println(tel);
                System.out.println(img);
                int longitud=dis.readInt();

/*-->PREGUNTA*/ int longMap = dis.readInt();

                byte[] pngImage = new byte[longitud];
                 for (int i = 0; i < longitud; i++) {
                    pngImage[i] = dis.readByte();
                }

/*-->*/         byte[] pngMap = new byte[longMap];
/*-->*/         for (int i = 0; i < longMap; i++) {
/*-->*/             pngMap[i] = dis.readByte();
/*-->*/         }
//                bais.reset();
//                dis.reset();

                datos = new Datos(nombre, tel, dir, in, intra, acceso, img,pngImage, mapa, pngMap);
                persons.addElement(datos);
                bais.close();
                dis.close();
//                imagesRS.closeRecordStore();
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return persons;
    }

//    static Vector leerEncuesta() {
//
//        Image img = null;
//        Vector persons = new Vector();
//        try {
//            Datos datos;
//            imagesRS = RecordStore.openRecordStore("datos", true);
//            RecordEnumeration re = imagesRS.enumerateRecords(null, null, true);
//            int numRecs = re.numRecords();
//
//            // For each record
//            for (int i = 0; i < numRecs; i++) {
//                // Get the next record's ID
//                int recId = re.nextRecordId(); // throws InvalidRecordIDException
//                System.out.println(recId);
//                // Get the record
//                byte[] rec = imagesRS.getRecord(recId);
//                //
//                ByteArrayInputStream bin = new ByteArrayInputStream(rec);
//                DataInputStream din = new DataInputStream(bin);
////                String name = din.readUTF();
//                // If this is the image we are looking for, load it.
////                if (name.equals(resourceName)== false) continue;
//                String nombre = din.readUTF();
//                String tel = din.readUTF();
//                String dir = din.readUTF();
//                String in = din.readUTF();
//                System.out.println(nombre);
//                System.out.println(tel);
////            System.out.println(img);
//                String intra = din.readUTF();
//                String acceso = din.readUTF();
//                int width = din.readInt();
//                int height = din.readInt();
//                long timestamp = din.readLong();
//                int length = din.readInt();
//
//                int[] rawImg = new int[width * height];
//                //  Serialize the image raw data
//                for (i = 0; i < length; i++) {
//                    rawImg[i] = din.readInt();
//                }
//                img = Image.createRGBImage(rawImg, width, height, false);
//                datos = new Datos(nombre, tel, dir, in, intra, acceso, img);
//                persons.addElement(datos);
//                din.close();
//                bin.close();
//            }
//        } catch (InvalidRecordIDException ignore) {
//            // End of enumeration, ignore
//        } catch (Exception e) {
//            // Log the exception
//        } finally {
//            try {
//                // Close the Record Store
//                if (imagesRS != null) {
//                    imagesRS.closeRecordStore();
//                }
//            } catch (Exception ignore) {
//                // Ignore
//            }
//        }
//
//        return persons;
//    }
    public static void delete(String persona) throws Exception {
        try {

            imagesRS = RecordStore.openRecordStore("datos", true);

            RecordEnumeration re = imagesRS.enumerateRecords(null, null, true);

            while (re.hasNextElement()) {
                int pos = re.nextRecordId();
                byte[] b = imagesRS.getRecord(pos);

                ByteArrayInputStream bais = new ByteArrayInputStream(b);
                DataInputStream dis = new DataInputStream(bais);
                String nombre = dis.readUTF();
                nombre = nombre.trim();
                System.out.println("mi nombre" + nombre);
//                System.out.println("su nombre" + persona);
//                String genero = dis.readUTF();
//                int a = dis.readInt();
//                Vector aficiones = new Vector(a);
//                for (int i = 0; i < a; i++) {
//                    aficiones.addElement(dis.readUTF());
//                }
                //int pos=dis.readInt();
                System.out.println("POSICION ACTUAL" + pos);
                if (persona.equals(nombre)) {
                    System.out.println("posicion eliminada" + pos);
                    imagesRS.deleteRecord(pos);

                }
                bais.reset();
                dis.reset();
                bais.close();
                dis.close();
            }
            imagesRS.closeRecordStore();

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public static void log(String msg) {
        System.out.println("Msg: " + msg);
    }
}
