package com.alim.cse.noticebynu.Process;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.alim.cse.noticebynu.R;

public class UIProcess {

    Context context;

    public UIProcess(Context context) {
        this.context = context;
    }

    public void About() {
        final Dialog dialog = new Dialog(context, R.style.AlertDialogLight);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.about_dialog);

        ImageView mail = dialog.findViewById(R.id.mail);
        ImageView close = dialog.findViewById(R.id.close);
        ImageView github = dialog.findViewById(R.id.git_hub);
        ImageView twitter = dialog.findViewById(R.id.twitter);
        ImageView facebook = dialog.findViewById(R.id.facebook);
        ImageView instagram = dialog.findViewById(R.id.instagram);

        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                        "souravalim@outlook.com", null));
                context.startActivity(Intent.createChooser(intent, "Send email..."));


                /*Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);*/

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Hacker-0")));
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/100006302621357")));
                } catch (Exception e) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/md.thouhed.5")));
                }
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    context.getPackageManager().getPackageInfo("com.twitter.android", 0);
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=sourav_alim")));
                } catch (Exception e) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/<place_user_name_here>")));
                }
            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    context.getPackageManager().getPackageInfo("com.instagram.android", 0);
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/alim_sourav")));
                } catch (Exception e) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/alim_sourav")));
                }
            }
        });
        dialog.show();
    }
}
