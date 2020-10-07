package fr.openium.testdrivingdistraction.ui.detail.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import fr.openium.testdrivingdistraction.R

class DialogTripDelete : AppCompatDialogFragment() {

    private var listener: OnPositiveButtonClickedListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.detail_delete_trip_confirm_message))
            .setPositiveButton(R.string.generic_delete) { _, _ ->
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