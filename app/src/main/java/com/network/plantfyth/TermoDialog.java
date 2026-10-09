package com.network.plantfyth;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class TermoDialog extends Dialog {
    private Runnable onAceitouTermos;

    public TermoDialog(Context context) {
        super(context);
    }
    public void setOnAceitouTermosListener(Runnable listener){
        this.onAceitouTermos = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_recomendacao);

        setCanceledOnTouchOutside(false);
        setCancelable(false);
        Button btnContinuar = findViewById(R.id.btnContinuar);

        btnContinuar.setOnClickListener(v -> {
            if (onAceitouTermos != null) {
                onAceitouTermos.run();
            }
            dismiss();
        });

        Window window = getWindow();

        if (window != null) {

            window.setBackgroundDrawableResource(
                    android.R.color.transparent
            );

            WindowManager.LayoutParams params =
                    window.getAttributes();

            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;

            window.setAttributes(params);
        }
    }
}