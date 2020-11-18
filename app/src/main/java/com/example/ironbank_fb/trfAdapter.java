package com.example.ironbank_fb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class trfAdapter extends FirestoreRecyclerAdapter<trfClass, trfAdapter.ViewHolder>{

    public trfAdapter(@NonNull FirestoreRecyclerOptions<trfClass> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull trfAdapter.ViewHolder holder, int position, @NonNull trfClass model) {
        holder.NroCtaDest.setText(model.getCuentaDestino());
        holder.Hora.setText(model.getHora());
        holder.Fecha.setText(model.getFecha());
        holder.Valor.setText(String.valueOf(model.getValor()));
    }

    @NonNull
    @Override
    public trfAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listadotrf,null,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView NroCtaDest, Hora, Fecha, Valor;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            NroCtaDest = itemView.findViewById(R.id.tvcuentadest);
            Hora = itemView.findViewById(R.id.tvhora);
            Fecha = itemView.findViewById(R.id.tvfecha);
            Valor = itemView.findViewById(R.id.tvvalor);
        }
    }
}
