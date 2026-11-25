package upeu.edu.pe

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType
import upeu.edu.pe.CameraLauncher

@Composable
actual fun rememberCameraLauncher(onResult: (ByteArray?) -> Unit): CameraLauncher {
    return remember {
        object : CameraLauncher {
            override fun launch() {
                // Implementación nativa usando UIImagePickerController en iOS
                val imagePicker = UIImagePickerController()
                imagePicker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
                // El delegado procesa la imagen a ByteArray en tiempo de ejecución de iOS
            }
        }
    }
}