package com.alim.cse.noticebynu.Process;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import com.alim.cse.noticebynu.R;

public class UIProcess {

    Context context;

    public UIProcess(Context context) {
        this.context = context;
    }

    public void ShowAbout() {
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
                Wait(Intent.createChooser(intent, "Send email..."));
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                },250);
            }
        });

        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Wait(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Hacker-0")));
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    Wait(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/100006302621357")));
                } catch (Exception e) {
                    Wait(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/md.thouhed.5")));
                }
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    context.getPackageManager().getPackageInfo("com.twitter.android", 0);
                    Wait(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=sourav_alim")));
                } catch (Exception e) {
                    Wait(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/<place_user_name_here>")));
                }
            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    context.getPackageManager().getPackageInfo("com.instagram.android", 0);
                    Wait(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/alim_sourav")));
                } catch (Exception e) {
                    Wait(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/alim_sourav")));
                }
            }
        });
        dialog.show();
    }

    private void Wait(final Intent intent) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                context.startActivity(intent);
            }
        },250);
    }
}
