package com.example.user1.mqtt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    TextView tvStatus, tvTemp, tvLight;
    Button btnFanOn, btnFanOff, btnLightOn, btnLightOff, btnDisconnect;
    MqttAndroidClient client;

    private void findViews() {
        tvStatus = findViewById(R.id.tvStatus);
        btnFanOn = findViewById(R.id.btnFanOn);
        tvTemp = findViewById(R.id.tvTemp);
        tvLight = findViewById(R.id.tvLight);
        btnFanOff = findViewById(R.id.btnFanOff);
        btnLightOff = findViewById(R.id.btnLightOff);
        btnLightOn = findViewById(R.id.btnLightOn);
        btnDisconnect = findViewById(R.id.btnDisconnect);
    }

    private boolean connect(MqttConnectOptions options) {
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                    tvStatus.setText("Connnected");
                    Log.d("connection", "success");
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(getApplicationContext(), "Error Connecting", Toast.LENGTH_SHORT).show();
                    tvStatus.setText("Error Connecting");
                    Log.d("connection", "failure");
                }
            });

            if(tvStatus.getText().toString().equals("Connected")) {
                return true;
            }
            else {
                return false;
            }
        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void subscribe(String topic) {
        try {
            IMqttToken subToken = client.subscribe(topic, 1);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getApplicationContext(), "Subscribed.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Toast.makeText(getApplicationContext(), "Subscription Error", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void publish(String topic, String payload, byte[] encodedPayload) {
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
            Toast.makeText(getApplicationContext(), "Publish Success", Toast.LENGTH_SHORT).show();
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    private void disconnect(){
        try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Toast.makeText(getApplicationContext(), "Disconnection Failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        String clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://m15.cloudmqtt.com:19595",
                        clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setUserName("jaiselhr");
        options.setPassword("eRlY3GGca4OZ".toCharArray());

        if(connect(options)) {
            subscribe("system/data/temperature");
            subscribe("system/data/light");
        }

        btnFanOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String topic = "system/commands/temperature";
            String payload = "tempAbnormal";
            byte[] encodedPayload = new byte[0];
            publish(topic, payload, encodedPayload);
            }
        });

        btnFanOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String topic = "system/commands/temperature";
            String payload = "tempNormal";
            byte[] encodedPayload = new byte[0];
            publish(topic, payload, encodedPayload);
            }
        });
        btnLightOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String topic = "system/commands/light";
            String payload = "lightOn";
            byte[] encodedPayload = new byte[0];
            publish(topic, payload, encodedPayload);
            }
        });
        btnLightOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String topic = "system/commands/light";
            String payload = "lightOff";
            byte[] encodedPayload = new byte[0];
            publish(topic, payload, encodedPayload);
            }
        });

        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            disconnect();
            }
        });

        MqttCallback callback = new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                tvStatus.setText("Disconnected");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if(topic.toString().equals("system/data/light")){
                    tvLight.setText(message.toString());
                }
                else if (topic.toString().equals("system/data/temperature")) {
                    tvTemp.setText(message.toString());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        };
        client.setCallback(callback);
    }

}
