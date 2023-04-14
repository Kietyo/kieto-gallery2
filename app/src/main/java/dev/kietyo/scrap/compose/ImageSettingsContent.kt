package dev.kietyo.scrap.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import dev.kietyo.scrap.utils.ContentScaleEnum
import dev.kietyo.scrap.utils.alignments
import dev.kietyo.scrap.utils.contentScales
import dev.kietyo.scrap.viewmodels.GalleryViewModel

@Composable
fun ImageSettingsContent(
    galleryViewModel: GalleryViewModel,
    onColumnChange: (Int) -> Unit = {},
    onSaveButtonClick: (String) -> Unit = {},
    onCancelButtonClick: () -> Unit = {}
) {
    Column {
        Stepper(
            "Num columns",
            onMinus = {
                onColumnChange(galleryViewModel.decrementColumns())
            },
            onPlus = {
                onColumnChange(galleryViewModel.incrementColumns())
            },
        )

        val imageContentScaleState = galleryViewModel.imageContentScaleFlow.collectAsState()

        SettingRow(
            "Content scale",
            imageContentScaleState,
            contentScales,
            onDropdownItemSelect = {
                galleryViewModel.updateImageContentScale(it)
            }
        )

        if (imageContentScaleState.value != ContentScaleEnum.FILL_BOUNDS &&
            imageContentScaleState.value != ContentScaleEnum.FILL_HEIGHT &&
            imageContentScaleState.value != ContentScaleEnum.FIT) {
            SettingRow(
                "Alignment",
                galleryViewModel.alignmentFlow.collectAsState(),
                alignments,
                onDropdownItemSelect = {
                    galleryViewModel.updateAlignment(it)
                }
            )
        }


        Row {
            TextButton(
                onClick = { onCancelButtonClick() }) {
                Text(text = "Cancel")
            }
            TextButton(
                onClick = { onSaveButtonClick("") }) {
                Text(text = "Save")
            }
        }

    }
}