package com.example.minitwitter.ui.profile;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.data.ProfileViewModel;
import com.example.minitwitter.retrofit.request.RequestUserProfile;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;


public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    ImageView ivAvatar;
    EditText etUsername, etEmail, etPassword, etWebsite, etDescripcion;
    Button btnSave, btnChangePassword;
    boolean loading = true;
    //ProgressDialog progressDialog;
    PermissionListener allPermissionListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileViewModel = ViewModelProviders.of(getActivity()).get(ProfileViewModel.class);
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, container, false);

        //progressDialog = new ProgressDialog(getContext());
        //progressDialog.setMessage("Cargando...");
        //progressDialog.show();

        ivAvatar = v.findViewById(R.id.imageViewAvatar);
        etUsername = v.findViewById(R.id.editTextUsername);
        etEmail = v.findViewById(R.id.editTextEmail);
        etPassword = v.findViewById(R.id.editTextCurrentPassword);
        etWebsite = v.findViewById(R.id.editTextWebsite);
        etDescripcion = v.findViewById(R.id.editTextDescripcion);
        btnSave = v.findViewById(R.id.buttonSave);
        btnChangePassword = v.findViewById(R.id.buttonChangePassword);


        //Eventos

        btnSave.setOnClickListener(view -> {
            String username = etUsername.getText().toString();
            String email = etEmail.getText().toString();
            String descripcion = etDescripcion.getText().toString();
            String website = etWebsite.getText().toString();
            String password = etPassword.getText().toString();

            if (username.isEmpty()) {
                etUsername.setError("El nombre de usuario es requerido");
            } else if (email.isEmpty()) {
                etEmail.setError("El email es requerido");
            } else if (password.isEmpty()) {
                etPassword.setError("La contraseña es requerida");
            } else {
                RequestUserProfile requestUserProfile = new RequestUserProfile(
                        username,
                        email,
                        descripcion,
                        website,
                        password);

                profileViewModel.updateProfile(requestUserProfile);
            }

            Toast.makeText(getActivity(), "Enviando informacion al servidor...", Toast.LENGTH_SHORT).show();
            btnSave.setEnabled(false);

        });

        btnChangePassword.setOnClickListener(view -> {
            Toast.makeText(getActivity(), "Click on password", Toast.LENGTH_LONG).show();
        });


        ivAvatar.setOnClickListener(view ->{
            //Invocar a la seleccion de la fotografia
            //Invocamos al metodo de comprobacion de permiso
             checkPermissions();
        });

        //ViewModel//onChange//funcion flecha
        profileViewModel.userProfile.observe(getActivity(), responseUserProfile -> {
            loading = false;
            etUsername.setText(responseUserProfile.getUsername());
            etEmail.setText(responseUserProfile.getEmail());
            etWebsite.setText(responseUserProfile.getWebsite());
            etDescripcion.setText(responseUserProfile.getDescripcion());


            if (!responseUserProfile.getPhotoUrl().isEmpty()) {
                Glide.with(getActivity())
                        .load(Constantes.API_MINITWITTER_FILES_URL + responseUserProfile.getPhotoUrl())
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
                        .skipMemoryCache(true)
                        .into(ivAvatar);


            } else {
                Glide.with(getActivity())
                        .load(Constantes.FOTO)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
                        .skipMemoryCache(true)
                        .dontAnimate()
                        .into(ivAvatar);

            }


            if (!loading) {
                btnSave.setEnabled(true);
                Toast.makeText(getActivity(), "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
            }
            // progressDialog.dismiss();
        });



        profileViewModel.photoProfile.observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String photo) {
                if (!photo.isEmpty()) {
                    Glide.with(getActivity())
                            .load(Constantes.API_MINITWITTER_FILES_URL + photo)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .centerCrop()
                            .skipMemoryCache(true)
                            .into(ivAvatar);

                }

            }
        });


        return v;
    }

    private void checkPermissions() {
        PermissionListener dialogOnDeniedPermissionListener = DialogOnDeniedPermissionListener
                .Builder
                .withContext(getActivity())
                .withTitle("Permisos ")
                .withMessage("Los permisos son necesarios para poder seleccionar una foto de perfil")
                .withButtonText("Aceptar")
                .withIcon(R.mipmap.ic_launcher)
                .build();

        allPermissionListener = new CompositePermissionListener(
                (PermissionListener) getActivity(),
                dialogOnDeniedPermissionListener);

        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(allPermissionListener)
                .check();
    }


}
