package com.example.ironbank_fb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class opciones extends AppCompatActivity {
    Button cerrar_op, transferencias, reportes;
    TextView cliente;
    String user, idCtaOrigen, cuentaOrigen;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones);
        cerrar_op = findViewById(R.id.btncerrarsesion_op);
        transferencias = findViewById(R.id.btntransferencias);
        reportes = findViewById(R.id.btnreportes);
        cliente = findViewById(R.id.tvcliente);
        user = getIntent().getStringExtra("usuario_is");

        cliente.setText(getIntent().getStringExtra("nombre_is"));

        cerrar_op.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        reportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    db.collection("cuenta")
                            .whereEqualTo("usuario", user)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (!task.getResult().isEmpty()) {
                                            for (QueryDocumentSnapshot document: task.getResult()) {
                                                Intent intreportes = new Intent(getApplicationContext(), reportetrf.class);
                                                intreportes.putExtra("cuenta_rep", document.getString("nroCuenta"));
                                                startActivity(intreportes);
                                            }
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(),"No se encontró cuenta para este usuario",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                }
        });

        transferencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("cuenta")
                        .whereEqualTo("usuario", user)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot document: task.getResult()) {
                                            idCtaOrigen = document.getId();
                                            cuentaOrigen = document.getString("nroCuenta");
                                            Intent inttransf = new Intent(getApplicationContext(), transferencia.class);
                                            inttransf.putExtra("idCtaOrigen", idCtaOrigen);
                                            inttransf.putExtra("cuenta_op", document.getString("nroCuenta"));
                                            inttransf.putExtra("saldo_op", document.getDouble("saldo"));
                                            startActivity(inttransf);
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(),"No se encontró cuenta para este usuario",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });
    }
}