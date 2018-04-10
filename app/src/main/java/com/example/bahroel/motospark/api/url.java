package com.example.bahroel.motospark.api;

/**
 * Created by Bahroel on 25/11/2017.
 */

public class url {
    public static final String REGISTER_URL = "http://192.168.8.100/MotoSpark-Services/api/web/user/create";
    public static final String LOGIN_URL = "http://192.168.8.100/MotoSpark-Services/api/web/user/login";
    public static final String Check_PIN_URL = "http://192.168.8.100/MotoSpark-Services/api/web/user/checkpin";
    public static final String CREATE_PIN_URL ="http://192.168.8.100/MotoSpark-Services/api/web/user/makepin";

    public static final String UPLOAD_URL ="http://192.168.8.100/MotoSpark-Services/api/web/motor/create";
    public static final String DATA_MOTOR_URL ="http://192.168.8.100/MotoSpark-Services/api/web/motor/getmotoruser?id_user=";
    public static final String EDIT_MOTOR_URL ="http://192.168.8.100/MotoSpark-Services/api/web/motor/update?id=";
    public static final String HAPUS_MOTOR_URL  ="http://192.168.8.100/MotoSpark-Services/api/web/motor/delete?id=";
    public static final String SET_POSISI_MOTOR_URL ="http://192.168.8.100/MotoSpark-Services/api/web/posisi-now/create";
}