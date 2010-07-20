import javax.microedition.location.*;
import javax.microedition.lcdui.*;
/**
 *
 * @author Andres Sierra
 */
public class MapServices extends Thread {
    
    String error    = "";
    double latitud  = 0;
    double longitud = 0;
    private ComunicacionHttp http;
    MMSMIDlet padre = null;
    private java.io.InputStream is = null;
    private final Command exitCommand;

    public MapServices (MMSMIDlet eje){
        http = new ComunicacionHttp(eje);
        padre = eje;
        exitCommand = new Command("Salir", Command.EXIT, 1);
    }
    public void run() {
        try {
            this.localizar();
            is = http.requestMap(latitud, longitud);
            Image im = Image.createImage(is);
            ImageItem map = new ImageItem("", im, ImageItem.LAYOUT_TOP | ImageItem.LAYOUT_RIGHT, null);
            padre.form.insert(0, map);
            padre.form.delete(2);
            Display.getDisplay(padre).setCurrent(padre.form);
            System.out.println("Tamaño del is RECIBIDO: " + is.available());
            is.close();

            


 //NUEVO-->
            java.io.InputStream isTemp =  http.requestMap(latitud, longitud);
            final int MAX_LENGTH = 12000; //isTemp.available();
            byte[] bufMap = new byte[MAX_LENGTH];
            int total = 0;
            while (total < MAX_LENGTH) {
                int count = isTemp.read(bufMap, total, MAX_LENGTH - total);
                if (count < 0) {
                    break;
                }
                total += count;
            }

             System.out.println("Tamaño del BufferMap creado" + bufMap.length);

            isTemp.close();
//HASTA ACA ES LO NUEVO





        } catch (Exception e) {
            e.printStackTrace();
        }
     }

     private void localizar(){
        LocationProvider lp = null;
		javax.microedition.location.Location location = null;
        try{ lp = LocationProvider.getInstance(null);
             location = lp.getLocation(-1); // Timeout
		}catch(LocationException e){ addError(e); }
         catch(InterruptedException e){ addError(e); }

        String res="[RESULTADOS DE LA BUSQUEDA]\n";
		try{
            Coordinates coordinates = location.getQualifiedCoordinates();
			res+="Altitude:"+coordinates.getAltitude()+"\n";
			res+="Latitude:"+coordinates.getLatitude()+"\n";
			res+="Longitude:"+coordinates.getLongitude()+"\n";
            latitud  = coordinates.getLatitude();
            longitud = coordinates.getLongitude();
            System.out.println("RESULTADOS PUNTOS: " + res);
		}catch(Exception e){ addError(e); }
        try {
            http.requestMap(latitud, longitud);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void addError(Exception e){
        e.printStackTrace();
        error+=e.getMessage()+"\n";
    }

    public void commandAction(Command c, Displayable d) {
        if (c == exitCommand) {
            padre.exitApplication();
        }
    }
}
