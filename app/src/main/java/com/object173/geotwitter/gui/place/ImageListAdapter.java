package com.object173.geotwitter.gui.place;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.object173.geotwitter.R;
import com.object173.geotwitter.database.entities.Image;
import com.object173.geotwitter.util.resources.ImagesManager;

import java.util.ArrayList;
import java.util.List;

public final class ImageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int HEADER = 1;
    private static final int NORMAL = 2;

    private static final String KEY_IMAGE_LIST = "image_list";

    private final OnItemClickListener listener;
    private final ArrayList<Image> imageList = new ArrayList<>();

    interface OnItemClickListener {
        void onAddClick();
        void onImageClick(final Image image);
    }

    public final void onCreate(final Bundle saveState) {
        if(saveState == null) {
            return;
        }
        final ArrayList<Image> imageList = (ArrayList<Image>) saveState.getSerializable(KEY_IMAGE_LIST);
        if(imageList != null) {
            this.imageList.addAll(imageList);
            notifyDataSetChanged();
        }
    }

    public final void onSaveInstanceState(final Bundle outState) {
        if(outState != null) {
            outState.putSerializable(KEY_IMAGE_LIST, imageList);
        }
    }

    public ImageListAdapter(final OnItemClickListener listener) {
        this.listener = listener;
    }

    public ImageListAdapter(final OnItemClickListener listener, final List<Image> imageList) {
        this(listener);
        this.imageList.addAll(imageList);
    }

    public final void addImage(final Image image){
        if(image != null) {
            imageList.add(image);
            notifyDataSetChanged();
        }
    }

    public List<Image> getImageList() {
        return imageList;
    }

    public final void removeImage(final Context context, final Image image) {
        if(image != null) {
            imageList.remove(image);
            ImagesManager.invalidateImage(context, image);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return HEADER;
        }
        return NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("ImageListAdapter","onCreateViewHolder");
        if (viewType == HEADER) {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.header_list_image, parent, false);
            return new ViewHolderAdd(view);
        }
        else if (viewType == NORMAL){
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_image_item, parent, false);
            return new ViewHolderImage(view);
        }
        else
            throw new RuntimeException("Could not inflate layout");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d("ImageListAdapter","onBindViewHolder");
        if (holder instanceof ViewHolderImage) {
            final ViewHolderImage vh = (ViewHolderImage) holder;
            vh.bind(imageList.get(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size() + 1;
    }

    class ViewHolderImage extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final ImageButton removeButton;

        public ViewHolderImage(View itemView) {
            super(itemView);
            imageView  = (ImageView) itemView.findViewById(R.id.image_view);
            removeButton = (ImageButton) itemView.findViewById(R.id.remove_button);
        }

        public void bind(final Image image) {
            final Context context = imageView.getContext();
            ImagesManager.setImageViewCache(context, imageView, R.mipmap.ic_image, image);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        listener.onImageClick(image);
                    }
                }
            });

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeImage(context, image);
                }
            });
        }
    }

    class ViewHolderAdd extends RecyclerView.ViewHolder {

        private final ImageButton addButton;

        public ViewHolderAdd(View itemView) {
            super(itemView);
            addButton = (ImageButton) itemView.findViewById(R.id.add_button);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        listener.onAddClick();
                    }
                }
            });
        }
    }
}
