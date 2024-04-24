package br.com.thiagoodev.bluetoothscanner.services.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import br.com.thiagoodev.bluetoothscanner.basics.extensions.toApp
import br.com.thiagoodev.bluetoothscanner.domain.models.BluetoothDeviceApp
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BluetoothServiceImp(@ApplicationContext private val context: Context) : BluetoothService {
    private val manager: BluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java) }
    private val adapter: BluetoothAdapter by lazy { manager.adapter }

    private val _pairedDevices: MutableStateFlow<List<BluetoothDeviceApp>> = MutableStateFlow(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothDeviceApp>>
        get() = _pairedDevices.asStateFlow()

    private val _scannedDevices: MutableStateFlow<List<BluetoothDeviceApp>> = MutableStateFlow(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDeviceApp>>
        get() = _scannedDevices.asStateFlow()

    private val receiver: BluetoothBroadcastReceiver = BluetoothBroadcastReceiver(::onDevice)

    private fun checkPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED
    }

    override fun updatePairedDevices() {
        if(!checkPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            return
        }
        adapter.bondedDevices.map { mapPairedDevices(it) }
    }

    private fun mapPairedDevices(device: BluetoothDevice) {
        val instance = device.toApp()
        _pairedDevices.update { if(instance !in it) it else it + instance }
    }

    override fun updateScannedDevices() {
        if(!checkPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }

        val intent = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(receiver, intent)
    }

    private fun onDevice(device: BluetoothDevice) {
        if(!checkPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }

        val instance = device.toApp()
        _scannedDevices.update { if(instance !in it) it else it + instance }
    }

    override fun connect(device: BluetoothDeviceApp) {
        val connection = ConnectThread(device)
        connection.start()
    }

    override fun disconnect(device: BluetoothDeviceApp) {
        TODO("Not yet implemented")
    }

    override fun pair(device: BluetoothDeviceApp) {
        TODO("Not yet implemented")
    }

    override fun unpair(device: BluetoothDeviceApp) {
        TODO("Not yet implemented")
    }

    override fun discovery() {
        if(!checkPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }

        adapter.startDiscovery()
    }

    override fun stop() {
        if(!checkPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }

        adapter.cancelDiscovery()
    }

    override fun unregister() {
        context.unregisterReceiver(receiver)
    }

    private inner class ConnectThread(private val device: BluetoothDeviceApp) : Thread() {
        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(device.uuid!!)
        }

        @SuppressLint("MissingPermission")
        override fun run() {
            adapter.cancelDiscovery()
            mmSocket?.connect()
            super.run()
        }

        fun cancel() {
            mmSocket?.close()
        }
    }
}