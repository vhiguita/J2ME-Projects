package com.encuestas;

import java.util.Vector;

public class Datos {

    private int id;
    private String nombre;
    private int tel;
    private String dir;
    private String intra;
    private String intranet;
    private String acceso;
    private String imagen;
    private Vector aficiones;
    private int pos = 0;

    public Datos(String nombre, int tel, String dir, int id) {
        this.nombre = nombre;
        this.tel = tel;
        this.dir = dir;
        this.id = id;
        this.intra = "n/a";
        this.intranet = "n/a";
        this.acceso = "n/a";
        this.imagen="n/a";
    }

    public String getNombre() {
        return nombre;
    }

    public int getTel() {
        return tel;
    }

    public String getDir() {
        return dir;
    }

    public String getIntra() {
        return intra;
    }

    public String getIntranet() {
        return intranet;
    }

    public String getAcceso() {
        return acceso;
    }
    public String getImagen(){
        return imagen;
    }
     public void setImagen(String imagen){
        this.imagen=imagen;
    }
//    public Vector getAficiones() {
//        return aficiones;
//    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTel(int tel) {
        this.tel = tel;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void setIntra(String intra) {
        this.intra = intra;
    }

    public void setIntranet(String intranet) {
        this.intranet = intranet;
    }

    public void setAcceso(String acceso) {
        this.acceso = acceso;
    }
    public void setPos(int pos) {
        this.pos = pos;
    }

    public void setAficiones(Vector aficiones) {
        this.aficiones = aficiones;
    }
    public int getId() {
            return id;
    }

    public String toString() {
            return getNombre() + " -- " + getTel();
    }
}
