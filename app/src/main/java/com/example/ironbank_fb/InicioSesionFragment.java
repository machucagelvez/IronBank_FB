package com.example.ironbank_fb;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

public class InicioSesionFragment extends Fragment{

    EditText usuario, contrasena;
    Button iniciar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_inicio_sesion,container,false);
        usuario = vista.findViewById(R.id.etusuario);
        contrasena = vista.findViewById(R.id.etcontrasena);
        iniciar = vista.findViewById(R.id.btniniciarsesion);

        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("cliente")
                        .whereEqualTo("usuario", usuario.getText().toString())
                        .whereEqualTo("contrasena", contrasena.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot document: task.getResult()) {
                                            Intent intlogeado = new Intent(getContext(), opciones.class);
                                            intlogeado.putExtra("nombre_is", document.getString("nombre"));
                                            intlogeado.putExtra("usuario_is", document.getString("usuario"));
                                            intlogeado.putExtra("ident_is", document.getString("ident"));
                                            startActivity(intlogeado);
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(getContext(),"Usuario o contraseña errados",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });
        return vista;
    }

}