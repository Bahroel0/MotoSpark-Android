package com.example.bahroel.motospark.data;

/**
 * Created by Bahroel on 26/11/2017.
 */

public class DataMotor {
    public String namaMotor;
    public String idPlat;
    public String foto;

    public String getNamaMotor() {
        return namaMotor;
    }

    public void setNamaMotor(String namaMotor) {
        this.namaMotor = namaMotor;
    }

    public String getIdPlat() {
        return idPlat;
    }

    public void setIdPlat(String idPlat) {
        this.idPlat = idPlat;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return "Nama : " + namaMotor + "id_plat : " + idPlat + "foto : " + foto;
    }
}
