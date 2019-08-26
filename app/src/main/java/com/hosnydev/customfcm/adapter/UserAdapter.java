package com.hosnydev.customfcm.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.hosnydev.customfcm.models.UserModel;
import com.hosnydev.customfcm.R;
import com.hosnydev.customfcm.notification.API;
import com.hosnydev.customfcm.notification.Client;
import com.hosnydev.customfcm.notification.Data;
import com.hosnydev.customfcm.notification.Response;
import com.hosnydev.customfcm.notification.Sender;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewHolder> {

    private Context context;
    private List<UserModel> list;

    // Notification
    API api;

    private FirebaseAuth firebaseAuth;

    public UserAdapter(Context context, List<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_formater, parent, false);

        firebaseAuth = FirebaseAuth.getInstance();
        api = Client.getClient("https://fcm.googleapis.com/").create(API.class);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position) {

        holder.setEmail(list.get(position).getEmail());
        holder.setName(list.get(position).getName());

        holder.sendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog al = new AlertDialog.Builder(context).create();
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View view1 = layoutInflater.inflate(R.layout.send_notification, null);
                al.setView(view1);

                final EditText editTextMessage = view1.findViewById(R.id.alertMessage);
                Button alertButton = view1.findViewById(R.id.alertButton);



                alertButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (editTextMessage.getText().toString().trim().isEmpty()) {
                            editTextMessage.setError("Required message");
                            editTextMessage.requestFocus();
                            return;
                        }

                        al.dismiss();
                        String reciver = list.get(position).getId();
                        String userName = list.get(position).getName();
                        String token = list.get(position).getTokin();
                        String message = editTextMessage.getText().toString().trim();

                        Data data = new Data(
                                firebaseAuth.getCurrentUser().getUid(),
                                message,
                                userName,
                                reciver,
                                R.drawable.common_google_signin_btn_icon_dark_normal_background
                        );
                        Sender sender = new Sender(data, token);
                        api.sendNotification(sender)
                                .enqueue(new Callback<Response>() {
                                    @Override
                                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                        if (response.code() == 200) {
                                            assert response.body() != null;
                                            if (response.body().success != 1) {
                                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                                            } else {

                                                Toast.makeText(context, "Notification Send", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Response> call, Throwable t) {

                                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });

                al.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {

        private TextView email, name;
        private Button sendNotification;

        viewHolder(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.EmailFormat);
            name = itemView.findViewById(R.id.NameFormat);
            sendNotification = itemView.findViewById(R.id.SendNotification);
        }

        private void setEmail(String e) {
            email.setText(e);
        }

        private void setName(String n) {
            name.setText(n);
        }
    }
}
