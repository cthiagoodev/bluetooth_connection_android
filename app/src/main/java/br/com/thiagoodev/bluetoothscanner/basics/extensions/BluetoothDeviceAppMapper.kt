package br.com.thiagoodev.bluetoothscanner.basics.extensions

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import br.com.thiagoodev.bluetoothscanner.domain.models.BluetoothDeviceApp

@SuppressLint("MissingPermission")
fun BluetoothDevice.toApp(): BluetoothDeviceApp {
    return BluetoothDeviceApp(
        uuid = this.uuids.firstOrNull()?.uuid,
        name = this.name,
        address = this.address,
        createRfcommSocketToServiceRecord = this::createRfcommSocketToServiceRecord
    )
}