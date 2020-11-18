package com.example.ironbank_fb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class transferencia extends AppCompatActivity {
    EditText saldo, cuentadest, valor, hora, fecha;
    Button cerrar, transferir, regresar_tr;
    String NroCtaOrigen, date, time, idCtaDestino, idCtaOrigen;
    Double saldo_tr, saldoDestino, saldoOrigen;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferencia);
        saldo = findViewById(R.id.etsaldo);
        cuentadest = findViewById(R.id.etcuentadest);
        valor = findViewById(R.id.etvalor);
        hora = findViewById(R.id.ethora);
        fecha = findViewById(R.id.etfecha);
        cerrar = findViewById(R.id.btncerrarsesion_tr);
        transferir = findViewById(R.id.btntransferir);
        regresar_tr = findViewById(R.id.btnregresar_tr);

        TimeZone tz = TimeZone.getTimeZone("GMT-05:00");
        date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        fecha.setEnabled(false);
        fecha.setText(date);
        hora.setEnabled(false);
        idCtaOrigen = getIntent().getStringExtra("idCtaOrigen");
        NroCtaOrigen = getIntent().getStringExtra("cuenta_op");
        saldo_tr = getIntent().getDoubleExtra("saldo_op", 0);
        saldo.setEnabled(false);
        saldo.setText(String.valueOf(saldo_tr));

        transferir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mcuentadest = cuentadest.getText().toString();
                String mvalor = valor.getText().toString();

                if (!mcuentadest.isEmpty() && !mvalor.isEmpty())
                {
                    Calendar c = Calendar.getInstance(tz);
                    time = new SimpleDateFormat("HH:mm:ss").format(new Date());
                    //time = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
                    hora.setText(time);
                    tranferir(mcuentadest, mvalor);
                }
                else
                {
                    Toast.makeText(transferencia.this, "Debe ingresar la cuenta de destino y el valor a transferir", Toast.LENGTH_SHORT).show();
                }
            }
        });

        regresar_tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void tranferir(String mcuentadest, String mvalor) {
        double valorTrf = Double.parseDouble(mvalor);
        if (saldo_tr - valorTrf < 10000){
            Toast.makeText(this, "Saldo insuficiente", Toast.LENGTH_SHORT).show();
        }
        else{
            db.collection("cuenta")
                    .whereEqualTo("nroCuenta", mcuentadest)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                if (!task.getResult().isEmpty()) {
                                    saldoOrigen = saldo_tr - valorTrf;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        saldoDestino = document.getDouble("saldo") + valorTrf;
                                        idCtaDestino = document.getId();
                                    }
                                    Map<String, Object> trf = new HashMap<>();
                                    trf.put("cuentaOrigen", NroCtaOrigen);
                                    trf.put("cuentaDestino", mcuentadest);
                                    trf.put("hora", hora.getText().toString());
                                    trf.put("fecha", fecha.getText().toString());
                                    trf.put("valor", Double.parseDouble(mvalor));

                                    // Add a new document with a generated ID
                                    db.collection("transferencia")
                                            .add(trf)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(transferencia.this, "Transferencia realizada", Toast.LENGTH_SHORT).show();
                                                    actualizarCuentas(saldoDestino, saldoOrigen);
                                                    cuentadest.setText("");
                                                    valor.setText("");
                                                    hora.setText("");
                                                    fecha.setText(date);
                                                    saldo.setText(String.valueOf(saldoOrigen));
                                                    cuentadest.requestFocus();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    //Log.w(TAG, "Error adding document", e);
                                                    Toast.makeText(transferencia.this, "La tranferencia no se realizó", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(transferencia.this, "La cuenta de destino no existe", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    });
        }
    }

    private void actualizarCuentas(double saldoDestino, double saldoOrigen) {
        db.collection("cuenta").document(idCtaDestino)
                .update("saldo", saldoDestino)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d("cliente", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w("cliente", "Error writing document", e);
                    }
                });
        db.collection("cuenta").document(idCtaOrigen)
                .update("saldo", saldoOrigen)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d("cliente", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w("cliente", "Error writing document", e);
                    }
                });

    }
}