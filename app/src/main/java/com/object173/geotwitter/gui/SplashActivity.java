package com.object173.geotwitter.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.object173.geotwitter.gui.main.MainActivity;

public final class SplashActivity extends AppCompatActivity {
    //инициализация контента
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //передаем базовому классу состояние
        //создаем намерение на запуск главной активности
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent); //запускаем активность
        finish(); //закрываем текущую
    }
}
