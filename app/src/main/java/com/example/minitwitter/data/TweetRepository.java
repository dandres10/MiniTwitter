package com.example.minitwitter.data;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.MyApp;
import com.example.minitwitter.common.SharedPreferencesManager;
import com.example.minitwitter.retrofit.AuthTwitterClient;
import com.example.minitwitter.retrofit.AuthTwitterService;
import com.example.minitwitter.retrofit.request.RequestCreateTweet;
import com.example.minitwitter.retrofit.response.Like;
import com.example.minitwitter.retrofit.response.Tweet;
import com.example.minitwitter.retrofit.response.TweetDeleted;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TweetRepository {
    AuthTwitterService authTwitterService;
    AuthTwitterClient authTwitterClient;
    MutableLiveData<List<Tweet>> allTweets;
    MutableLiveData<List<Tweet>> favTweets;
    String userName;


    public TweetRepository() {
        authTwitterClient = AuthTwitterClient.getInstance();
        authTwitterService = authTwitterClient.getAuthTwitterService();
        allTweets = getAllTweets();
        userName = SharedPreferencesManager.getSomeStringValue(Constantes.PREF_USERNAME);
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

    public MutableLiveData<List<Tweet>> getFavsTweets() {
        if (favTweets == null) {
            favTweets = new MutableLiveData<>();
        }

        List<Tweet> newFavList = new ArrayList<>();
        Iterator itTweets = allTweets.getValue().iterator();

        while (itTweets.hasNext()) {
            Tweet current = (Tweet) itTweets.next();
            Iterator itLikes = current.getLikes().iterator();
            boolean enc = false;
            while (itLikes.hasNext() && !enc) {
                Like like = (Like) itLikes.next();
                if (like.getUsername().equals(userName)) {
                    enc = true;
                    newFavList.add(current);
                }
            }
        }

        favTweets.setValue(newFavList);

        return favTweets;

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

    public void deleteTweet(final int idTweet) {
        Call<TweetDeleted> call = authTwitterService.deleteTweet(idTweet);
        call.enqueue(new Callback<TweetDeleted>() {
            @Override
            public void onResponse(Call<TweetDeleted> call, Response<TweetDeleted> response) {
                if (response.isSuccessful()) {
                    List<Tweet> clonedTweets = new ArrayList<>();
                    for (int i = 0; i < allTweets.getValue().size(); i++) {
                        if (allTweets.getValue().get(i).getId() != idTweet) {
                            clonedTweets.add(new Tweet(allTweets.getValue().get(i)));

                        }
                    }

                    allTweets.setValue(clonedTweets);
                    getFavsTweets();
                } else {
                    Toast.makeText(MyApp.getContexto(), "Algo a ido mal intentelo de nuevo", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<TweetDeleted> call, Throwable t) {
                Toast.makeText(MyApp.getContexto(), "Error en la conexion.", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void likeTweet(final int idTweet) {

        Call<Tweet> call = authTwitterService.likeTweet(idTweet);
        call.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                if (response.isSuccessful()) {
                    List<Tweet> listaClonada = new ArrayList<>();

                    for (int i = 0; i < allTweets.getValue().size(); i++) {

                        if (allTweets.getValue().get(i).getId() == idTweet) {
                            /*
                            si hemos encontrado en la lista original
                            el elemento sobre el que hemos hecho like,
                            introducimos el elemento que nos ha llegado del
                            servidor
                            * */
                            listaClonada.add(response.body());
                        } else {
                            listaClonada.add(new Tweet(allTweets.getValue().get(i)));
                        }
                    }

                    allTweets.setValue(listaClonada);
                    getAllTweets();
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
