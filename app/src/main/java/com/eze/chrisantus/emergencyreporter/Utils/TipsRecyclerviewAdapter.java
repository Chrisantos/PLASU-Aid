package com.eze.chrisantus.emergencyreporter.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eze.chrisantus.emergencyreporter.R;

import java.util.ArrayList;
import java.util.List;

public class TipsRecyclerviewAdapter extends RecyclerView.Adapter<TipsRecyclerviewAdapter.ViewHolder> {
    private Context context;
    private List<ListPOJO> tipsList = new ArrayList<>();
    private ClickListener clickListener;
    private Typeface typeface;

    public TipsRecyclerviewAdapter(Context context,  List<ListPOJO> tipsList, ClickListener clickListener) {
        this.context = context;
        this.tipsList = tipsList;
        this.clickListener = clickListener;

    }

    public void setClickListener(ClickListener itemClickListener){
        this.clickListener = itemClickListener;
    }

    @NonNull
    @Override
    public TipsRecyclerviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TipsRecyclerviewAdapter.ViewHolder holder, int position) {
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        holder.tvShortText.setTypeface(typeface);
        holder.tvAux.setTypeface(typeface);

        ListPOJO listPOJO = tipsList.get(position);
        holder.tvShortText.setText(listPOJO.getInFo());
        holder.tvAux.setText(listPOJO.getAux());
    }

    @Override
    public int getItemCount() {
        return (tipsList == null)? 0 : tipsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvShortText, tvAux;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvShortText = itemView.findViewById(R.id.short_text);
            tvAux = itemView.findViewById(R.id.auxilliary);
            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null){
                clickListener.onClick(view, getAdapterPosition());
            }
        }
    }
}
