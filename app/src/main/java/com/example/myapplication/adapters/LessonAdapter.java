package com.example.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.koreanlearning.R;
import com.example.myapplication.models.Lesson;

import java.util.List;

public class LessonAdapter extends ArrayAdapter<Lesson> {

    private Context context;

    public LessonAdapter(@NonNull Context context, int resource, @NonNull List<Lesson> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LessonViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lesson, parent, false);
            holder = new LessonViewHolder();
            holder.lessonTitleTextView = convertView.findViewById(R.id.lesson_title);
            holder.lessonDescriptionTextView = convertView.findViewById(R.id.lesson_description);
            holder.lessonImageView = convertView.findViewById(R.id.lesson_image);
            convertView.setTag(holder);
        } else {
            holder = (LessonViewHolder) convertView.getTag();
        }

        Lesson lesson = getItem(position);

        if (lesson != null) {
            holder.lessonTitleTextView.setText(lesson.getTitle());
            holder.lessonDescriptionTextView.setText(lesson.getKoreanText());  // ou autre champ
            // pour l’image si tu en as une:
            // holder.lessonImageView.setImageResource(lesson.getImageResId());
        }

        return convertView;
    }

    private static class LessonViewHolder {
        TextView lessonTitleTextView;
        TextView lessonDescriptionTextView;
        ImageView lessonImageView;
    }
}
