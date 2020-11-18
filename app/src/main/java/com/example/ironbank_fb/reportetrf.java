package com.example.ironbank_fb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class reportetrf extends AppCompatActivity {
    RecyclerView recyclerTrf;
    Button cerrar, regresar;
    trfAdapter madapter;
    FirebaseFirestore mfirestore;
    String cuentaOrigen;
    TextView cuentaor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportetrf);
        cuentaor = findViewById(R.id.tvcuentaor);
        cerrar = findViewById(R.id.btncerrarsesion_rep);
        regresar = findViewById(R.id.btnregresar_rep);
        cuentaOrigen = getIntent().getStringExtra("cuenta_rep");
        recyclerTrf = findViewById(R.id.rvreportetrf);
        recyclerTrf.setLayoutManager(new LinearLayoutManager(this));
        mfirestore = FirebaseFirestore.getInstance();
        Query query = mfirestore.collection("transferencia").whereEqualTo("cuentaOrigen", cuentaOrigen).orderBy("fecha");
        FirestoreRecyclerOptions<trfClass> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<trfClass>()
                .setQuery(query, trfClass.class).build();
        madapter = new trfAdapter(firestoreRecyclerOptions);
        madapter.notifyDataSetChanged();
        //Asignar el adaptador al recyclerview
        recyclerTrf.setAdapter(madapter);

        cuentaor.setText(cuentaOrigen);

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

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        madapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        madapter.stopListening();
    }
}