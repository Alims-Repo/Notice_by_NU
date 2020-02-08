package com.alim.cse.noticebynu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.alim.cse.noticebynu.R;
import com.alim.cse.noticebynu.ViewerActivity;

import java.io.File;
import java.util.List;

public class Updates extends RecyclerView.Adapter<Updates.MyViewHolder>  {

    private  String From;
    private TextView text;
    private boolean offline;
    private CardView cardView;
    private TextView Type_text;
    private TextView text_date;
    private ImageView Type_image;
    private List<String> mDataset;
    private List<String> mDataDate;
    private List<String> mDataLink;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View view) {
            super(view);
        }
    }

    public Updates(List<String> Data, List<String> Date, List<String> Link
            , boolean offline, String From) {
        this.From = From;
        this.mDataset = Data;
        this.mDataDate = Date;
        this.mDataLink = Link;
        this.offline = offline;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView;
        if (viewType==0)
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.updates_layout_start, parent, false);
        else if (viewType+1==mDataset.size())
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.updates_layout_end, parent, false);
        else
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.updates_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final View rootView = holder.itemView;
        final Context context = holder.itemView.getContext();

        Type_text = rootView.findViewById(R.id.type_text);
        Type_image = rootView.findViewById(R.id.type_image);
        cardView = rootView.findViewById(R.id.card_view);
        text = rootView.findViewById(R.id.text);
        text_date = rootView.findViewById(R.id.text_date);
        text.setText(mDataset.get(position));
        text_date.setText(mDataDate.get(position));
        final String Link = mDataLink.get(position);
        final String Extension = mDataLink.get(position).substring(Link.length()-3);
        if (Extension.equals("zip")) {
            Type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.zip));
            Type_text.setText("ZIP\nfile\nformat");
        } else if (Extension.equals("txt")) {
            Log.println(Log.ASSERT,"TYPE",Extension);
            Type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.text));
            Type_text.setText("TEXT\nfile\nformat");
        } else if (Extension.equals("ocx") | Extension.contains("doc")) {
            Log.println(Log.ASSERT,"TYPE",Extension);
            Type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.text));
            Type_text.setText("DOCX\nfile\nformat");
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Extension.equals("ocx") | Extension.contains("docx")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        File file = new File(mDataLink.get(position));
                        Uri uri = FileProvider.getUriForFile(context, "com.alim.cse.noticebynu.provider", file);
                        Log.println(Log.ASSERT,"FILE",uri.toString());
                        intent.setData(uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        try {
                            context.startActivity(Intent.createChooser(intent, "Open Word document"));
                        } catch (Exception e) {
                            Log.println(Log.ASSERT,"UPDATES",e.toString());
                        }
                    } catch (Exception e) {
                        Log.println(Log.ASSERT,"UPDATES",e.toString());
                    }
                } else {
                    Intent intent = new Intent(context, ViewerActivity.class);
                    intent.putExtra("OFFLINE", offline);
                    intent.putExtra("FROM", "OTHER");
                    intent.putExtra("TYPE", Extension);
                    intent.putExtra("NAME", mDataset.get(position));
                    intent.putExtra("LINK", Link);
                    intent.putExtra("LOCATION", From);
                    Log.println(Log.ASSERT, "TYPE", Link);
                    context.startActivity(intent);
                }
            }
        });

        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}