
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;

// The CameraScreen class shows the live view
// of the camera using the MMAPI and gives
// commands to capture the contents of the camera
class CameraScreen
        extends Canvas
        implements CommandListener {

    private final MMSMIDlet midlet;
    private final Command exitCommand;
    private Player player = null;
    private Command captureCommand = null;
    private Command getImage,  storeImage,  exit;
    private VideoControl videoControl = null;
    private boolean active = false;
    private Form form;
    private Display display = null;
    private RecordStore record = null;
    private Alert alert;

    CameraScreen(MMSMIDlet midlet) {
        this.midlet = midlet;
        alert = new Alert("Image Stored");
        // Builds the user interface
        exitCommand = new Command("Salir", Command.EXIT, 1);
//        exit=new Command("Exit", Command.EXIT, 1);
//        storeImage = new Command("StoreImage", Command.SCREEN, 1);
//        getImage = new Command("GetImage", Command.SCREEN, 1);
        addCommand(exitCommand);
        captureCommand = new Command("Capturar", Command.SCREEN, 1);
        addCommand(captureCommand);
//        form.addCommand(exit);
//        form.addCommand(storeImage);
//        form.addCommand(getImage);
//        form.setCommandListener(this);
        setCommandListener(this);
//        midlet.show(form);
    }

//Paint the background of the Canvas black.

    // Paint the canvas' background in black
    public void paint(Graphics g) {
        // black background
        g.setColor(0x00000000);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

//Create a method for detecting the use of Commands.
    public void commandAction(Command c, Displayable d) {
        if (c == exitCommand) {
            midlet.exitApplication();
        } else if (c == captureCommand) {
            captureImage();
        }
    }

    static void storeImage(Image image) {
        RecordStore imagesRS = null;
        String resourceName = "imagen.png";


        int height, width;
        if (resourceName == null) {
            return; // resource name is required
        }
        // Calculate needed size and allocate buffer area
        height = image.getHeight();
        width = image.getWidth();

        int[] imgRgbData = new int[width * height];

        try {
            image.getRGB(imgRgbData, 0, width, 0, 0, width, height);
            imagesRS = RecordStore.openRecordStore("StoreImage", true);

            //
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(bout);
            //  Serialize the image name
//            dout.writeUTF(resourceName);
            dout.writeInt(width);
            dout.writeInt(height);
            dout.writeLong(System.currentTimeMillis());
            dout.writeInt(imgRgbData.length);
            //  Serialize the image raw data
            for (int i = 0; i < imgRgbData.length; i++) {
                dout.writeInt(imgRgbData[i]);
                System.out.println(imgRgbData[i]);
            }
            dout.flush();
            dout.close();
            byte[] data = bout.toByteArray();
            int recid = imagesRS.addRecord(data, 0, data.length);
            log("Image stored to RMS");
        } catch (Exception e) {
            log("Err in Add Image to RMS" + e);
        } finally {
            try {
                // Close the Record Store
                if (imagesRS != null) {
                    imagesRS.closeRecordStore();
                }
            } catch (Exception ignore) {
                // Ignore
            }
        }
    }

    public RecordStore openRecordStore() {
        try {
            record = RecordStore.openRecordStore("StoreImage", true);
        } catch (Exception e) {
            log("Err in open RS");
        }
        return record;
    }

    public void getImage() {
        try {
            openRecordStore();
            InputStream is = new ByteArrayInputStream(record.getRecord(1));
            Image im = Image.createImage(is);
            System.out.println("Imagen" + im);
        // form.append(im);
//            midlet.show(im);
//            midlet.show(form);
        } catch (Exception e) {
            System.out.println("No ha traido imagen" + e);
        }
    }

    static public Image loadPngFromRMS() {
        RecordStore imagesRS = null;
        Image img = null;
        try {
            imagesRS = RecordStore.openRecordStore("StoreImage", true);
            RecordEnumeration re = imagesRS.enumerateRecords(null, null, true);
            int numRecs = re.numRecords();

            // For each record
            for (int i = 0; i < numRecs; i++) {
                // Get the next record's ID
                int recId = re.nextRecordId(); // throws InvalidRecordIDException
                System.out.println(recId);
                // Get the record
                byte[] rec = imagesRS.getRecord(recId);
                //
                ByteArrayInputStream bin = new ByteArrayInputStream(rec);
                DataInputStream din = new DataInputStream(bin);
//                String name = din.readUTF();
                // If this is the image we are looking for, load it.
//                if (name.equals(resourceName)== false) continue;

                int width = din.readInt();
                int height = din.readInt();
                long timestamp = din.readLong();
                int length = din.readInt();

                int[] rawImg = new int[width * height];
                //  Serialize the image raw data
                for (i = 0; i < length; i++) {
                    rawImg[i] = din.readInt();
                }
                img = Image.createRGBImage(rawImg, width, height, false);
                din.close();
                bin.close();
            }
        } catch (InvalidRecordIDException ignore) {
            // End of enumeration, ignore
        } catch (Exception e) {
            // Log the exception
        } finally {
            try {
                // Close the Record Store
                if (imagesRS != null) {
                    imagesRS.closeRecordStore();
                }
            } catch (Exception ignore) {
                // Ignore
            }
        }
//        openRecordStore();
//        try{
//            InputStream is = new ByteArrayInputStream(record.getRecord(1));
//            img = Image.createImage(is);
//        }catch(Exception e){
//
//        }
        return img;
    }

//Create a method for detecting key presses.
    public void keyPressed(int keyCode) {
        if (getGameAction(keyCode) == FIRE) {
            captureImage();
        }
    }

//Create a method for building and starting the video player.

    // //Creates and starts the video player
    synchronized void start() {
        try {
            player = Manager.createPlayer("capture://video");
            player.realize();

            // Get VideoControl for the viewfinder
            videoControl = (VideoControl) player.getControl("VideoControl");
            if (videoControl == null) {
                discardPlayer();
                midlet.showError("Cannot get the video control.\n" + "Capture may not be supported.");
                player = null;
            } else {
                // Set up the viewfinder on the screen.
                videoControl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO,
                        this);
                int canvasWidth = getWidth();
                int canvasHeight = getHeight();
                int displayWidth = videoControl.getDisplayWidth();
                int displayHeight = videoControl.getDisplayHeight();
                int x = (canvasWidth - displayWidth) / 2;
                int y = (canvasHeight - displayHeight) / 2;
                videoControl.setDisplayLocation(x, y);
                player.start();
                videoControl.setVisible(true);
            }
        } catch (IOException ioe) {
            discardPlayer();
            midlet.showError("IOException: " + ioe.getMessage());
        } catch (MediaException me) {
            midlet.showError("MediaException: " + me.getMessage());
        } catch (SecurityException se) {
            midlet.showError("SecurityException: " + se.getMessage());
        }
    }

    // Stops the video player
    synchronized void stop() {
        if (player != null) {
            try {
                videoControl.setVisible(false);
                player.stop();
            } catch (MediaException me) {
                midlet.showError("MediaException: " + me.getMessage());
            }
            active = false;
        }
    }

    // this method will discard the video player
    private void discardPlayer() {
        if (player != null) {
            player.deallocate();
            player.close();
            player = null;
        }
        videoControl = null;
    }

    // captures the image from the video player
    // in a separate thread
    private void captureImage() {
        if (player != null) {
            // Capture image in a new thread.
            new Thread() {

                public void run() {
                    try {
                        byte[] pngImage = videoControl.getSnapshot("encoding=png");
                        midlet.imageCaptured(pngImage);
                        Image image = Image.createImage(pngImage, 0, pngImage.length);

                        System.out.println(image);
                        System.out.println("run:primero:ultimo: " + pngImage[0] + " " + pngImage[pngImage.length-1]);
//                        for (int i = 0; i < pngImage.length; i++) {
//                            System.out.println(pngImage[i]);
//                        }
                       // midlet.show(image, pngImage);

                        discardPlayer();
                    } catch (MediaException me) {
//                        midlet.showError("MediaException: " + me.getMessage());
                    } catch (SecurityException se) {
//                        midlet.showError("SecurityException: " + se.getMessage());
                    }
                }
            }.start();
        }
    }

    public static void log(String msg) {
        System.out.println("Msg: " + msg);
    }
}



