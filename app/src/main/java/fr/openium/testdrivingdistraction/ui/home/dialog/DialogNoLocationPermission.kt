package fr.openium.testdrivingdistraction.ui.home.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import fr.openium.testdrivingdistraction.R

class DialogNoLocationPermission : AppCompatDialogFragment() {

    private var listener: OnPositiveButtonClickedListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Some permissions have not been granted. Do you really want to start a record ?")
            .setPositiveButton(R.string.generic_continue) { _, _ ->
                listener?.onPositiveButtonClicked()
            }
            .setNegativeButton(R.string.generic_cancel) { _, _ ->
                dismiss()
            }
            .create()

    fun setListener(listener: OnPositiveButtonClickedListener?) {
        this.listener = listener
    }

    interface OnPositiveButtonClickedListener {
        fun onPositiveButtonClicked()
    }
}