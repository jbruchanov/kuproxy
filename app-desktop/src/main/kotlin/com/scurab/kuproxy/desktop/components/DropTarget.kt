package com.scurab.kuproxy.desktop.components

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DropTargetEvent
import java.awt.dnd.DropTargetListener
import java.io.File

@Composable
fun registerAsDropFileTarget(
    onFiles: (List<File>) -> Unit
) {
    val window = LocalAppWindow.current.window
    LaunchedEffect(Unit) {
        DropTarget(
            window,
            object : DropTargetListener {
                override fun dragEnter(dtde: DropTargetDragEvent) {}
                override fun dragOver(dtde: DropTargetDragEvent) {}
                override fun dropActionChanged(dtde: DropTargetDragEvent) {}
                override fun dragExit(dte: DropTargetEvent) {}

                override fun drop(event: DropTargetDropEvent) {
                    event.acceptDrop(DnDConstants.ACTION_LINK)
                    runCatching {
                        event.transferable
                            .let { t -> t.getTransferData(t.transferDataFlavors.firstOrNull { it.isFlavorJavaFileListType }) as List<*> }
                            .filterIsInstance<File>()
                            .toList()
                    }.getOrNull()
                        ?.let {
                            onFiles(it)
                        }

                    event.dropComplete(true)
                }
            }
        )
    }
}
