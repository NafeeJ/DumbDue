package com.kiwicorp.dumbdue.util

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.kiwicorp.dumbdue.R

open class RoundedBottomSheetDialogFragment : BottomSheetDialogFragment() {
    /**
     * Expands the BottomSheetDialog so the entire dialog is shown when the keyboard is first opened
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet: FrameLayout = dialog.findViewById(R.id.design_bottom_sheet)!!
            val bottomSheetBehavior: BottomSheetBehavior<FrameLayout> = BottomSheetBehavior.from(bottomSheet)
            // expand the bottom sheet because otherwise keyboard will not push up entire dialog
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            // redraw background with rounded corners because bottom sheet expanded has full corners
            // by default
            ViewCompat.setBackground(bottomSheet, createMaterialShapeDrawable(bottomSheet))
        }
        return bottomSheetDialog
    }
    /**
     * Used for drawing rounded corners on bottom sheet
     */
    private fun createMaterialShapeDrawable(bottomSheet: View): MaterialShapeDrawable? {
        val shapeAppearanceModel =
            // Create a ShapeAppearanceModel with the same shapeAppearanceOverlay used in the style
            ShapeAppearanceModel.builder(
                context,
                R.style.ShapeAppearance_DumbDue_LargeComponent,
                R.style.ThemeOverlay_DumbDue_BottomSheetDialog)
                .build()

        // Create a new MaterialShapeDrawable (you can't use the original MaterialShapeDrawable in
        // the BottomSheet)
        val currentMaterialShapeDrawable = bottomSheet.background as MaterialShapeDrawable
        val newMaterialShapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)
        // Copy the attributes in the new MaterialShapeDrawable
        newMaterialShapeDrawable.initializeElevationOverlay(context)
        newMaterialShapeDrawable.fillColor = currentMaterialShapeDrawable.fillColor
        newMaterialShapeDrawable.tintList = currentMaterialShapeDrawable.tintList
        newMaterialShapeDrawable.elevation = currentMaterialShapeDrawable.elevation
        newMaterialShapeDrawable.strokeWidth = currentMaterialShapeDrawable.strokeWidth
        newMaterialShapeDrawable.strokeColor = currentMaterialShapeDrawable.strokeColor
        return newMaterialShapeDrawable
    }
}