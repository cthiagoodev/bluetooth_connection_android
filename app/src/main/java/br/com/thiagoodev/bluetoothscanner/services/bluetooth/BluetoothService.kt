package br.com.thiagoodev.bluetoothscanner.services.bluetooth

import br.com.thiagoodev.bluetoothscanner.domain.models.BluetoothDeviceApp
import kotlinx.coroutines.flow.StateFlow

interface BluetoothService {
    val pairedDevices: StateFlow<List<BluetoothDeviceApp>>
    val scannedDevices: StateFlow<List<BluetoothDeviceApp>>

    fun updatePairedDevices()
    fun updateScannedDevices()
    fun connect(device: BluetoothDeviceApp)
    fun disconnect(device: BluetoothDeviceApp)
    fun pair(device: BluetoothDeviceApp)
    fun unpair(device: BluetoothDeviceApp)
    fun discovery()
    fun stop()
    fun unregister()
}