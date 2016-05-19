package net.croz.mqtt.java.client;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Mqtt example client based on example from
 * <a>https://www.eclipse.org/paho/clients/java/.</a>
 */
public class MqttExampleClient {

    public static void main(String[] args) {
        String broker = "tcp://192.168.99.100:1883";
        MqttClientPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, "MqttExampleClient", persistence);

            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setWill("mresetar/paho/lwt", "Eclipse paho client is shut down!".getBytes(), 0, false);

            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");

            sampleClient.setCallback(new AlertCallback());
            sampleClient.subscribe("mresetar/alertbox/+/alert");

            String content = "Hello JavaCro16 from Eclipse Paho!";
            System.out.println("Publishing message: " + content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(0);
            sampleClient.publish("mresetar/paho", message);
            System.out.println("Message published");

        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    private static class AlertCallback implements MqttCallback {

        @Override
        public void connectionLost(Throwable cause) {
            System.out.println("Connection is lost");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            System.out.println("Message on topic: " + topic + " arrived.");
            System.out.println("Message content: " + message);
            playMedia();
            //mqttClient.disconnect();
            //System.exit(0);
        }

        private void playMedia() throws IOException {
            Process p = Runtime.getRuntime().exec("c:\\Program Files (x86)\\VideoLAN\\VLC\\vlc.exe --fullscreen " +
                    "--start-time=68 \"d:\\CROZ\\JavaCro16\\15 Digital Starmine in Atami Japan.mp4\"");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            System.out.println("Message delivered");
        }
    }
}