package com.example.elipse.ims;

/**
 * Created by hsaidi on 08/03/2015.
 */
public class Connexion {

    private String id_dep;
    private String id_arr;
    private int indice_dep;
    private int indice_arr;
    private String id;
    private int type;
    private int ancre_dep;
    private int ancre_arr;

    public Connexion(String id_dep, String id_arr, int indice_dep, int indice_arr, int ancre_dep, int ancre_arr, String id, int type) {
        this.id_dep = id_dep;
        this.id_arr = id_arr;
        this.indice_dep = indice_dep;
        this.indice_arr = indice_arr;
        this.ancre_arr = ancre_arr;
        this.ancre_dep = ancre_dep;
        this.id = id;
        this.type = type;
    }

    public int getAncre_dep() {
        return ancre_dep;
    }

    public void setAncre_dep(int ancre_dep) {
        this.ancre_dep = ancre_dep;
    }

    public int getAncre_arr() {
        return ancre_arr;
    }

    public void setAncre_arr(int ancre_arr) {
        this.ancre_arr = ancre_arr;
    }

    public String getId_dep() {
        return id_dep;
    }

    public void setId_dep(String id_dep) {
        this.id_dep = id_dep;
    }

    public String getId_arr() {
        return id_arr;
    }

    public void setId_arr(String id_arr) {
        this.id_arr = id_arr;
    }

    public int getIndice_dep() {
        return indice_dep;
    }

    public void setIndice_dep(int indice_dep) {
        this.indice_dep = indice_dep;
    }

    public int getIndice_arr() {
        return indice_arr;
    }

    public void setIndice_arr(int indice_arr) {
        this.indice_arr = indice_arr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
