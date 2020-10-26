package com.phoebus.libraries.checkout.example;

import android.content.SharedPreferences;
import android.content.Context;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.TableRow;

import com.example.R;

import br.com.concrete.canarinho.watcher.ValorMonetarioWatcher;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private String email;
    private Long value;
    private String merchantPaymentId;
    private String orderNumber;
    private String merchantToken;
    private EditText edtEmail;
    private EditText edtValue;
    private EditText edtMerchantPaymentId ;
    private EditText edtOrderNumber;
    private EditText edtMerchantToken;
    private String prefMerchantToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edtEmail = findViewById(R.id.edtEmail);

        edtValue = findViewById(R.id.edtValue);
        edtValue.addTextChangedListener(new ValorMonetarioWatcher.Builder()
                .comSimboloReal()
                .build());

        edtMerchantPaymentId = findViewById(R.id.edtMerchantPaymentId);
        edtOrderNumber = findViewById(R.id.edtOrderNumber);
        edtMerchantToken = findViewById(R.id.edtMerchantToken);

        SharedPreferences pref = context.getSharedPreferences("payment_data", Context.MODE_PRIVATE);
        prefMerchantToken = pref.getString("merchant_token", "");

        if(!prefMerchantToken.isEmpty()){
            TableRow tableRow = findViewById(R.id.tableRowMerchantToken);
            tableRow.removeAllViews();
        }

        findViewById(R.id.start_lib).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formData();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!prefMerchantToken.isEmpty()){
            TableRow tableRow = findViewById(R.id.tableRowMerchantToken);
            tableRow.removeAllViews();
        }

    }

    private void formData() {
        email = edtEmail.getText().toString();
        String valueStr = edtValue.getText().toString().replaceAll("[R$]", "").replaceAll("[ ]", "").replaceAll("[,]", "").replaceAll("[.]", "");
        value = valueStr != "" ? Long.parseLong(valueStr) : 0;
        merchantPaymentId = edtMerchantPaymentId.getText().toString();
        orderNumber = edtOrderNumber.getText().toString();

        if(prefMerchantToken.isEmpty()) {
            merchantToken = edtMerchantToken.getText().toString();

            SharedPreferences pref = context.getSharedPreferences("payment_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            editor.putString("merchant_token", merchantToken);
            editor.commit();
            pref = context.getSharedPreferences("payment_data", Context.MODE_PRIVATE);

            prefMerchantToken = pref.getString("merchant_token", "");
            Log.d("merchant_token2", prefMerchantToken);
        }

        isEmptyField(edtMerchantPaymentId, merchantPaymentId);
        isValidValue(edtValue, value);
        isEmail(edtEmail, email);
        isEmptyField(edtOrderNumber, orderNumber);

        boolean valid = !isEmptyField(edtMerchantPaymentId, merchantPaymentId)
                || !isEmptyField(edtOrderNumber, orderNumber)
                || isValidValue(edtValue, value)
                || isEmail(edtEmail, email);

        if (!valid) {
            AlertDialog.Builder emptyField = new AlertDialog.Builder(this);
            emptyField.setTitle("ATENÇÃO: ");
            emptyField.setMessage("Há campos inválidos ou sem preenchimento");
            emptyField.setNeutralButton("OK", null);
            emptyField.show();
        }else{
            startLib();
        }

    }

    private boolean isEmptyField(EditText field, String valor) {
        Boolean result = (TextUtils.isEmpty( valor ) || valor.trim().isEmpty());
        if(result){
            field.setError("Campo obrigatório");
        }
        return result;
    }

    private boolean isValidValue(EditText field, Long valor) {
        Boolean result = valor>0 && valor<=999999999999l;
        if(!result){
            field.setError("Informe um valor maior que 0 e com até 12 dígitos");
        }
        return result;
    }

    private boolean isEmail(EditText field, String email) {
        Boolean result = (!isEmptyField(field, email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if(!result){
            field.setError("Email inválido");
        }
        return result;
    }

    private void startLib(){
        Intent intent = new Intent(this, com.phoebus.libraries.checkout.LibActivity.class);
        Bundle libInitialProps = new Bundle();

        libInitialProps.putString("email", email);
        libInitialProps.putLong("value", value);
        libInitialProps.putString("merchant_payment_id", merchantPaymentId);
        libInitialProps.putString("order_number", orderNumber);
        libInitialProps.putString("merchant_token", prefMerchantToken);

        intent.putExtra("libBundle", libInitialProps);

        startActivity(intent);
    }
}
