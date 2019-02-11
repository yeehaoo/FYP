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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        btnFanOn = findViewById(R.id.btnFanOn);
        tvTemp = findViewById(R.id.tvTemp);
        btnFanOff = findViewById(R.id.btnFanOff);
        btnLightOff = findViewById(R.id.btnLightOff);
        btnLightOn = findViewById(R.id.btnLightOn);
        btnDisconnect = findViewById(R.id.btnDisconnect);

        String clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://m15.cloudmqtt.com:19595",
                        clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setUserName("jaiselhr");
        options.setPassword("eRlY3GGca4OZ".toCharArray());
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                    tvStatus.setText("Connnected");
                    Log.d("connection", "success");

                    int qos = 0;
                    try {
                        IMqttToken subToken = client.subscribe("system/commands/temperature", qos);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // The message was published
                                Toast.makeText(getApplicationContext(), "Subscribed 1", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                // The subscription could not be performed, maybe the user was not
                                // authorized to subscribe on the specified topic e.g. using wildcards

                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                    try {
                        IMqttToken subToken = client.subscribe("system/commands/light", qos);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // The message was published
                                Toast.makeText(getApplicationContext(), "Subscribed 2", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                // The subscription could not be performed, maybe the user was not
                                // authorized to subscribe on the specified topic e.g. using wildcards

                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(getApplicationContext(), "Error Connecting", Toast.LENGTH_SHORT).show();
                    tvStatus.setText("Error Connecting");
                    Log.d("connection", "failure");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }



        btnFanOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = "system/commands/temperature";
                String payload = "tempAbnormal";
                byte[] encodedPayload = new byte[0];
                try {
                    encodedPayload = payload.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    client.publish(topic, message);
                    Toast.makeText(getApplicationContext(), "Published 1", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        btnFanOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = "system/commands/temperature";
                String payload = "tempNormal";
                byte[] encodedPayload = new byte[0];
                try {
                    encodedPayload = payload.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    client.publish(topic, message);
                    Toast.makeText(getApplicationContext(), "Published 2", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | MqttException e) {
                    e.printStackTrace();
                }
            }
        });
        btnLightOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = "system/commands/light";
                String payload = "lightOn";
                byte[] encodedPayload = new byte[0];
                try {
                    encodedPayload = payload.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    client.publish(topic, message);
                    Toast.makeText(getApplicationContext(), "Published 3", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | MqttException e) {
                    e.printStackTrace();
                }
            }
        });
        btnLightOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = "system/commands/light";
                String payload = "lightOff";
                byte[] encodedPayload = new byte[0];
                try {
                    encodedPayload = payload.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    client.publish(topic, message);
                    Toast.makeText(getApplicationContext(), "Published 4", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    IMqttToken disconToken = client.disconnect();
                    disconToken.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // we are now successfully disconnected
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken,
                                              Throwable exception) {
                            // something went wrong, but probably we are disconnected anyway
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        MqttCallback callback = new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                tvStatus.setText("Disconnected");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if(topic.toString().equals("system/commands/light")){
                    tvLight.setText(message.toString());
                }
                else if (topic.toString().equals("system/commands/temperature")) {
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
