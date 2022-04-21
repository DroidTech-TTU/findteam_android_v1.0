package com.example.findteam_android_v10.adapters;

import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.findteam_android_v10.R;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class EditTagsAdapter extends RecyclerView.Adapter<EditTagsAdapter.ViewHolder> {

    private List<String> categories;
    private List<String> finalCategories;

    private List<List<String>> tags;

    private final String TAG = "EditTagsAdapter";
    Context context;

    public EditTagsAdapter(Context context,List<String> categories, List<List<String>> tags) {
        this.context = context;
        this.categories = categories;
        finalCategories = categories;
        this.tags = tags;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_edit_tag, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String category = categories.get(position);
        List<String> tag = tags.get(position);

        holder.bind(category, tag, position);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public List<String> getCategories(){
        notifyDataSetChanged();
        return finalCategories;}

    public List<List<String>> getTags(){return tags;}
    public class ViewHolder extends RecyclerView.ViewHolder {

        EditText etCategory, editTagText;
        ImageView deleteTag;
        TagContainerLayout tagContainer;
        Button editTagBtn;

        public ViewHolder(View view) {
            super(view);
            this.etCategory = view.findViewById(R.id.editCategoryTag);
            this.editTagText = view.findViewById(R.id.editTagText);
            this.tagContainer = view.findViewById(R.id.editTags);
            this.editTagBtn = view.findViewById(R.id.addEditTag);
            this.deleteTag = view.findViewById(R.id.removeTag);
        }

        public void bind(String category, List<String> tagsInsert, int positionInView) {

            tagContainer.setTagTypeface(ResourcesCompat.getFont(context, R.font.questrial));

            this.etCategory.setText(category);
            tagContainer.setTags(tagsInsert);

            tagContainer.setOnTagClickListener(new TagView.OnTagClickListener() {
                @Override
                public void onTagClick(int position, String text) {

                }

                @Override
                public void onTagLongClick(int position, String text) {

                }

                @Override
                public void onSelectedTagDrag(int position, String text) {

                }

                @Override
                public void onTagCrossClick(int position) {
                    tagContainer.removeTag(position);
                    tags.get(positionInView).remove(position);
                    notifyItemChanged(positionInView);
                }

            });

            editTagBtn.setOnClickListener(view -> {
                if(!editTagText.getText().toString().equals("")){
                    Log.i(TAG, "positionView in btn is: " + positionInView);
                    tagContainer.addTag(editTagText.getText().toString());
                    tags.get(positionInView).add(editTagText.getText().toString());
                    editTagText.setText("");
                }
            });

            etCategory.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    Log.i(TAG, "positionView is: " + positionInView);
                    finalCategories.set(positionInView, charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            deleteTag.setOnClickListener(view -> {
                categories.remove(positionInView);
                tags.remove(positionInView);
                notifyItemChanged(positionInView);
            });
        }
    }
}
