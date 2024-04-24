package br.com.thiagoodev.bluetoothscanner.domain.models

import android.bluetooth.BluetoothSocket
import java.util.UUID

data class BluetoothDeviceApp(
    val uuid: UUID?,
    val name: String?,
    val address: String,
    val isConnected: Boolean = false,
    val createRfcommSocketToServiceRecord: (uuid: UUID) -> BluetoothSocket
)