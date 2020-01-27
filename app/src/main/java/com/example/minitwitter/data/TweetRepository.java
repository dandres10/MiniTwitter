package com.example.minitwitter.data;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.example.minitwitter.common.MyApp;
import com.example.minitwitter.retrofit.AuthTwitterClient;
import com.example.minitwitter.retrofit.AuthTwitterService;
import com.example.minitwitter.retrofit.request.RequestCreateTweet;
import com.example.minitwitter.retrofit.response.Tweet;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TweetRepository {
    AuthTwitterService authTwitterService;
    AuthTwitterClient authTwitterClient;
    MutableLiveData<List<Tweet>> allTweets;


    public TweetRepository() {
        authTwitterClient = AuthTwitterClient.getInstance();
        authTwitterService = authTwitterClient.getAuthTwitterService();
        allTweets = getAllTweets();
    }


    public MutableLiveData<List<Tweet>> getAllTweets() {

        if (allTweets == null) {
            allTweets = new MutableLiveData<>();
        }

        Call<List<Tweet>> call = authTwitterService.getAllTweets();
        call.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {
                if (response.isSuccessful()) {
                    allTweets.setValue(response.body());

                } else {
                    Toast.makeText(MyApp.getContexto(), "Algo a ido mal", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Tweet>> call, Throwable t) {
                Toast.makeText(MyApp.getContexto(), "Error en la conexion.", Toast.LENGTH_LONG).show();
            }
        });

        return allTweets;
    }

    public void createTweet(String mensaje) {
        RequestCreateTweet requestCreateTweet = new RequestCreateTweet(mensaje);
        Call<Tweet> call = authTwitterService.createTweet(requestCreateTweet);
        call.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                if (response.isSuccessful()) {
                    List<Tweet> listaClonada = new ArrayList<>();
                    //añadimos en primer el nuevo tw que llega en el servidor
                    listaClonada.add(response.body());
                    for (int i = 0; i < allTweets.getValue().size(); i++) {
                        listaClonada.add(new Tweet(allTweets.getValue().get(i)));
                    }

                    allTweets.setValue(listaClonada);
                } else {
                    Toast.makeText(MyApp.getContexto(), "Algo a ido mal intentelo de nuevo", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<Tweet> call, Throwable t) {
                Toast.makeText(MyApp.getContexto(), "Error en la conexion.", Toast.LENGTH_LONG).show();
            }
        });
    }


}
