import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.location.*;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;



public class MMSMIDlet extends MIDlet{

    String error    = "";
    double altitud  = 0;
    double latitud  = 0;
    double longitud = 0;
    private static Display display = null;
    
    protected void startApp() throws MIDletStateChangeException{
        display = Display.getDisplay(this);
		//System.out.println("loading");
        LocationProvider lp=null;
		javax.microedition.location.Location location=null;
        
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
            altitud  = coordinates.getAltitude();
            latitud  = coordinates.getLatitude();
            longitud = coordinates.getLongitude();
		}catch(Exception e){ addError(e); }

        try {
            URL url = new URL("http://maps.google.com/staticmap?size=512x512&maptype=mobile&markers=" + latitud + "," + longitud + ",bluex&key=MAPS_API_KEY");
            URLConnection urlCon = url.openConnection(); // establecemos conexion
            System.out.println(urlCon.getContentType()); // Sacamos por pantalla el tipo de fichero
			InputStream is = urlCon.getInputStream(); // Se obtiene el inputStream de la foto web y se abre el fichero local.

            FileOutputStream fos = new FileOutputStream("e:/foto.jpg");
			// Lectura de la foto de la web y escritura en fichero local
			byte[] array = new byte[1000]; // buffer temporal de lectura.
			int intLeido = is.read(array);
			while (intLeido > 0) {
				fos.write(array, 0, intLeido);
				intLeido = is.read(array);
			}
            is.close();// cierre de conexion y fichero.
			fos.close();
        } catch (Exception e) {}

        
        Form f = new Form("Results");
        f.append(res);
		f.append(error);
        display.setCurrent(f);
    }

    void addError(Exception e){
		e.printStackTrace();
		error+=e.getMessage()+"\n";
    }

    protected void pauseApp(){
    }

    protected void destroyApp(boolean unconditional)
            throws MIDletStateChangeException{
    }

    public static Display getDisplay(){
        return display;
    }
}