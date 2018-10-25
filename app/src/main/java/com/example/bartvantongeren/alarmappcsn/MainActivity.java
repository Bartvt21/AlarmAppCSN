package com.example.bartvantongeren.alarmappcsn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttCallback;

public class MainActivity extends AppCompatActivity {


    final String serverUri = "tcp://boezemail.nl:1883";

    String clientId = "AndroidClient";
    final String subscriptionTopic = "/domo";
    final String publishTopic = "/domo";

    MqttAndroidClient MQTTClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MQTTClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
        MQTTClient.setCallback(new MqttCallback() {
            public void connectComplete(boolean reconnect, String serverURI) {
                System.out.println("Connected to MQTT Broker");

            }
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("The Connection was lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println("Incoming message: " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);

        try {
            //addToHistory("Connecting to " + serverUri);
            MQTTClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Connected to MQTT broker!");
                    try {
                        MQTTClient.subscribe(subscriptionTopic, 1);
                    }
                    catch (MqttException ex)
                    {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    System.out.println("Failed to connect to: " + serverUri);
                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }

    public void armAlarm(View v){
        String topic = publishTopic;
        String message = "arm";
        try {
            MQTTClient.publish(topic, message.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        System.out.println("Ik ben armed");
    }

    public void disarmAlarm(View v){
        String topic = publishTopic;
        String message = "disarm";
        try {
            MQTTClient.publish(topic, message.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
