package com.example.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.koreanlearning.R;
import com.example.myapplication.models.Village;

import java.util.List;

public class VillageAdapter extends RecyclerView.Adapter<VillageAdapter.VillageViewHolder> {

    private List<Village> villages;
    private Context context;
    private OnVillageClickListener listener;

    public interface OnVillageClickListener {
        void onVillageClick(Village village);
    }

    public VillageAdapter(List<Village> villages, Context context, OnVillageClickListener listener) {
        this.villages = villages;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VillageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_village, parent, false);
        return new VillageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VillageViewHolder holder, int position) {
        Village village = villages.get(position);

        holder.villageNameTextView.setText(village.getName());
        holder.villageKoreanTextView.setText(village.getKoreanName());
        holder.villageDescriptionTextView.setText(village.getDescription());
        holder.villageImageView.setImageResource(village.getImageResId());
        holder.progressTextView.setText(village.getCompletedLessons() + " / " + village.getTotalLessons());
        holder.progressBar.setProgress((int) village.getProgressPercentage());

        // Affichage overlay/verrou
        if (village.isUnlocked()) {
            holder.villageOverlay.setVisibility(View.GONE);
            holder.villageLockIcon.setVisibility(View.GONE);
        } else {
            holder.villageOverlay.setVisibility(View.VISIBLE);
            holder.villageLockIcon.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                if (village.isUnlocked()) {
                    listener.onVillageClick(village); // clic fonctionnel
                } else {
                    Toast.makeText(context,
                            "Ce village est verrouillé. Terminez les villages précédents pour le débloquer !",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return villages != null ? villages.size() : 0;
    }

    public void updateVillages(List<Village> newVillages) {
        this.villages = newVillages;
        notifyDataSetChanged();
    }

    static class VillageViewHolder extends RecyclerView.ViewHolder {
        View villageOverlay;
        ImageView villageLockIcon;
        ImageView villageImageView;
        TextView villageNameTextView, villageKoreanTextView, villageDescriptionTextView, progressTextView;
        ProgressBar progressBar;

        public VillageViewHolder(@NonNull View itemView) {
            super(itemView);
            villageImageView = itemView.findViewById(R.id.village_image);
            villageNameTextView = itemView.findViewById(R.id.village_name);
            villageKoreanTextView = itemView.findViewById(R.id.village_korean);
            villageDescriptionTextView = itemView.findViewById(R.id.village_description);
            progressBar = itemView.findViewById(R.id.progress_bar);
            progressTextView = itemView.findViewById(R.id.progress_text);
            villageOverlay = itemView.findViewById(R.id.village_overlay);
            villageLockIcon = itemView.findViewById(R.id.village_lock_icon);
        }
    }
}
