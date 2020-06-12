package com.example.verifycounttool;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.verifycounttool.bean.DeviceCountBean;
import com.example.verifycounttool.utils.ExcelUtil;
import com.example.verifycounttool.utils.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.litepal.LitePal;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback;
import cn.com.heaton.blelibrary.ble.callback.BleMtuCallback;
import cn.com.heaton.blelibrary.ble.callback.BleNotiftCallback;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;
import cn.com.heaton.blelibrary.ble.model.ScanRecord;
import io.reactivex.functions.Consumer;

import static cn.com.heaton.blelibrary.ble.Ble.REQUEST_ENABLE_BT;
import static org.litepal.LitePalApplication.getContext;

public class MainActivity extends AppCompatActivity {
    RxPermissions rxPermissions=new RxPermissions(this);
    Context mContext = this;
    String deviceName="HRSTT";
    String TAG ="verifyMainActivity";
    List<String>  mListDeviceName = new ArrayList<String>();
    List<BleDevice>  mListDevice= new ArrayList<BleDevice>();
    private TextView tvName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvName = (TextView) findViewById(R.id.tv_name);
        findViewById(R.id.bt_export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将数据导入乘excel格式
                String filePath = Environment.getExternalStorageDirectory() + "/AndroidDeviceCountExcel"+"/";
                // String filePath = getApplicationContext().getFilesDir().getAbsolutePath()+"/AndroidExcelDemo";
                File file = new File(filePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String excelFileName = "CountExcel.xls";
                String[] title = {"t", "I0t", "I1t", "I2t", "I3t", "I4t", "I5t", "k", "Sg0t", "Sg1t", "Sg2t"};
                    String sheetName = "demoSheetName";
                    List<DeviceCountBean> all1 = LitePal.findAll(DeviceCountBean.class);
                    if(all1.size()==0){
                        ToastUtils.showToast(mContext, "暂无数据" );
                        return;
                    }
                    filePath = filePath + excelFileName;
                    ExcelUtil.initExcel(filePath, title);
                    ExcelUtil.writeObjListToExcel(all1, filePath, getContext());
                    ToastUtils.showToast(mContext, "数据已导出,路径为" + filePath);
            }
        });

        requestPermission();
    }

    //权限选择
    private void requestPermission() {
        rxPermissions.request(Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(
                new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if(aBoolean){
                            initBle();
                            LitePal.getDatabase();
                        }else {
                            Toast.makeText(mContext,"权限获取失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    void initBle(){
        Ble.options().setLogTAG("1234")
                //.setLogBleExceptions(true)//设置是否输出打印蓝牙日志（非正式打包请设置为true，以便于调试）
                .setThrowBleException(true)//设置是否抛出蓝牙异常
                .setAutoConnect(true)//设置是否自动连接
                .setConnectFailedRetryCount(3)//连接失败重试时间
                .setConnectTimeout(10 * 1000)//设置连接超时时长（默认10*1000 ms）
                .setScanPeriod(8 * 1000)//设置扫描时长（默认10*1000 ms）
                .setUuidService(UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb"))//主服务的uuid
                .setUuidWriteCha(UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb"))//可写特征的uuid
                .create(getApplicationContext());
                checkBle();
    }

    private void checkBle() {
            Log.d(TAG, "checkBle");
            // 检查设备是否支持BLE4.0
//        if (!mBle.isSupportBle(getContext())) {
//            Toast.makeText(getActivity(), "不支持蓝牙", Toast.LENGTH_SHORT).show();
//            getActivity().finish();
//            return;
//        }
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            // 初始化蓝牙适配器. API版本必须是18以上, 通过 BluetoothManager 获取到BLE的适配器.

            // 检查当前的蓝牙设别是否支持.
            if (adapter == null) {
                Toast.makeText(this, "不支持Ble蓝牙", Toast.LENGTH_SHORT).show();
                //mActivity.finish();
                return;
            }
            //  if (!mBle.isBleEnable()) {
            if (!adapter.isEnabled()) {
                //Log.d(TAG, "checkBle提示打开蓝牙");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                //isCheckBle = true;//设置为正在check打开蓝牙
            } else {
                //   if (mBle != null) {
                if (Ble.getInstance().getConnetedDevices().size() == 0) {
                    //Ble.getInstance().stopScan();//需要停止搜索后,才能继续搜索,不然库中标志位未重置,不会重复搜索
                    startScan();
                }
            }
    }

    void startScan(){
        Ble.getInstance().startScan(scanCallback);
    }


    /* 将byte数组转换为字符串,用16进制表示 */
    public String bytesToHexString(byte[] src, int len) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || len <= 0) {
            System.out.println("src == null");
            return null;
        }
        for (int i = 0; i < len; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv + ",");
        }
        return stringBuilder.toString();
    }

    //搜索回调
    BleScanCallback<BleDevice> scanCallback = new BleScanCallback<BleDevice>() {
        @Override
        public void onLeScan(BleDevice device, int rssi, byte[] scanRecord) {

            synchronized (MainActivity.class) {
//                ScanRecord scanRecord = device.getScanRecord();
//
//                if (scanRecord != null){
//                    scanRecord.getAdvertiseFlags();
//                    Log.d(TAG, "convert: "+scanRecord.toString());
//                    List<ParcelUuid> serviceUuids = scanRecord.getServiceUuids();
//                    if (serviceUuids != null && !serviceUuids.isEmpty()){
//                        Log.d(TAG,String.format("Service Uuids: %s", TextUtils.join(", ", serviceUuids)));
//                    }
//                    String localName = scanRecord.getDeviceName();
//                    if (TextUtils.isEmpty(localName)){
//                        localName = device.getBleName();
//                    }
//                    Log.d(TAG,"Local Name: "+localName);
//                }


                // SharePrefrencesUtils.setParam(mContext, StringData.deviceName, "Endoc_1908_17C6");
                if (device.getBleName() != null && device.getBleName().contains(deviceName) && !mListDeviceName.contains(device.getBleName())) {
                    Log.d(TAG,"scanRecord"+"==="+bytesToHexString(scanRecord, scanRecord.length));
                    Log.d(TAG, "name=" + device.getBleName());
                    //ToastUtils.showToast(mContext, "绑定设备名称"+ SharePrefrencesUtils.getParam(mContext, StringData.deviceName, ""));
                        mListDevice.add(device);
                        mListDeviceName.add(device.getBleName());
                        tvName.setText("搜到设备 "+device.getBleName());
                        Ble.getInstance().stopScan();
                        // mLoadingDialog.cancel();
                        ToastUtils.showToast(mContext, "已搜到指定设备,正在连接");
                        //mBle.stopScan();
                        //ViseBle.getInstance().stopScan(scanCallback);//此处没有回调函数,此处的停止是在连接回调中去停止的
                        //ViseBle.getInstance().connect(bluetoothLeDevice, connectCallback);
                        Ble.getInstance().connect(device, connectCallback);
                        //mCurrentDeviceName = device.getBleName();//名字记录
                        //mBle.connect(device, connectCallback);
                }
            }
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.d(TAG, "onScanStop");
        }
    };

    BleConnectCallback<BleDevice> connectCallback = new BleConnectCallback<BleDevice>() {
        boolean isConnectStatus = false;//设备上次连接状态

        @Override
        public void onConnectionChanged(BleDevice device) {
            if (device.isConnected()) {

//                Ble.getInstance().setMTU(device.getBleAddress(), 30, new BleMtuCallback<BleDevice>() {
//                    @Override
//                    public void onMtuChanged(BleDevice device, int mtu, int status) {
//                        super.onMtuChanged(device, mtu, status);
//                        if (BluetoothGatt.GATT_SUCCESS == status) {
//                            Ble.getInstance().disconnectAll();
//                            Log.d(TAG,"onMtuChanged success MTU = " + mtu);
//                        } else {
//                            Log.d(TAG,"onMtuChanged fail ");
//                        }
//                    }
//                });
                isConnectStatus = true;//连接成功
                //设备是否连接
                //mDeviceConnect = true;
                // ViseBle.getInstance().stopScan(scanCallback);//此处没有回调函数
                //mDevice = device;
                tvName.setText("连接设备 "+device.getBleName());
                ToastUtils.showToast(mContext, "设备已连接");
                //tvDeviceStatus.setText("设备已连接");
                //tvMac.setText("mac=" + device.getBleAddress() + ",名字=" + device.getBleName());
            } else if (device.isDisconnected()) {//设备断开后，就会调用此方法,无论是主动断开还是被动断开,还是重连是连不上也会调用此方法
                tvName.setText("设备已断开"+device.getBleName());
            }
        }

        //连接失败,除了手动调用api断开,关闭蓝牙也不会调用此方法,其他的都会调用此函数
        @Override
        public void onConnectException(BleDevice device, int errorCode) {
            super.onConnectException(device, errorCode);
            //tvName.setText("设备已断开"+device.getBleName());
            ToastUtils.showToast(mContext, "设备已断开");
        }

        @Override
        public void onReady(final BleDevice device) {
            super.onReady(device);
            Ble.getInstance().setMTU(device.getBleAddress(), 33, new BleMtuCallback<BleDevice>() {
                                    @Override
                    public void onMtuChanged(BleDevice device, int mtu, int status) {
                        super.onMtuChanged(device, mtu, status);
                        if (BluetoothGatt.GATT_SUCCESS == status) {
                            setNotify(device);
                            Log.d(TAG,"onMtuChanged success MTU = " + mtu);
                        } else {
                            Log.d(TAG,"onMtuChanged fail ");
                        }
                    }
                });

        }
    };
    private void setNotify(BleDevice device) {
        Ble.getInstance().enableNotify(device, true, new BleNotiftCallback<BleDevice>() {
            @Override
            public void onChanged(BleDevice device, BluetoothGattCharacteristic characteristic) {
                byte[] characteristicValue = characteristic.getValue();
                if(characteristicValue.length==2){//如果是开始标识
                    if(characteristicValue[0]==0){
                        length++;
                    }else if(characteristicValue[0]==0xff) {
                        if(length==characteristicValue[1]){//长度一致
                            ToastUtils.showToast(mContext,"数据接收成功");
                        }else {
                            //长度不一致
                            LitePal.deleteAll(DeviceCountBean.class);//清除数据库
                        }
                    }
                }else {
                    length++;//接收到血糖数据
                    //解析数据
                    double ad = (double) ((receiveData[9] & 0x00ff) + ((receiveData[10] << 8) & 0x0000ff00)) / 10;
                    bytesToInt();
                }
                /*
                * 收到数据解析
                * */
            Log.d(TAG,bytesToHexString(characteristic.getValue(),characteristic.getValue().length));
            }
        });
    }

    public static int bytesToInt(byte src, byte src2) {
        int value;
        value = (int) ((src & 0xFF)
                | ((src2 & 0xFF) << 8)) & 0x0000ffff;
        return value;
    }
    int length =-1;//-1表示表示还没有接收数据
}


