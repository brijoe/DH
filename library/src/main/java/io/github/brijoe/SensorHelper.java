package io.github.brijoe;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


class SensorHelper implements SensorEventListener {

    private static final int SPEED_THRESHOLD = 5000;
    private static final int UPDATE_INTERVAL_TIME = 50;
    private SensorManager sensorManager;
    private Sensor sensor;

    private float lastX;
    private float lastY;
    private float lastZ;
    private long lastUpdateTime;

    private Context mContext;

    private DHDialog mDialog;

    private final String TAG = "DH";

    private static SensorHelper mInstance = new SensorHelper();

    private SensorHelper() {
        sensorManager = (SensorManager) DH.getContext()
                .getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    public static SensorHelper getInstance() {
        return mInstance;
    }

    protected void inject(Context context) {
        mContext = context;
    }
    // register sensor
    protected void register() {

        if (sensor != null) {
            sensorManager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }
    }

    // unregister sensor
    protected void unregister() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentUpdateTime = System.currentTimeMillis();
        long timeInterval = currentUpdateTime - lastUpdateTime;
        if (timeInterval < UPDATE_INTERVAL_TIME) return;
        lastUpdateTime = currentUpdateTime;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;

        lastX = x;
        lastY = y;
        lastZ = z;
        double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
                * deltaZ)
                / timeInterval * 10000;
        if (speed >= SPEED_THRESHOLD)
            showDebugDialog();
    }

    private void showDebugDialog() {
        Log.d(TAG, "showDialog");
        if (!((Activity) mContext).isFinishing()) {
            if(mDialog!=null&&mDialog.isShowing())
                return;
            mDialog = new DHDialog(mContext);
            mDialog.show();
        }
    }
    protected void destroy() {
        Log.d(TAG, "SensorHelper destroy dialog");
        mDialog=null;
    }
}
