package com.github.cosycode.common.ext.swing;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * <b>Description : </b> 拖拽实现监听包装类, 方便swing拖拽组件的使用
 *
 * @author CPF
 * @since 1.0
 * @date 2019/12/13
 **/
public class DropTargetListenerImpl implements DropTargetListener {

    /*
     * 1. 文件: 判断拖拽目标是否支持文件列表数据（即拖拽的是否是文件或文件夹, 支持同时拖拽多个）
     */
    @SuppressWarnings("unchecked")
    public java.util.List<File> getFileList(DropTargetDropEvent dtde) throws IOException, UnsupportedFlavorException {
        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            // 接收拖拽目标数据
            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            // 以文件集合的形式获取数据
            return (java.util.List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
        }
        return Collections.emptyList();
    }

    public String getFilePath(DropTargetDropEvent dtde) throws IOException, UnsupportedFlavorException {
        final List<File> fileList = getFileList(dtde);
        if (fileList == null || fileList.isEmpty()) {
            return null;
        }
        return fileList.get(0).getAbsolutePath();
    }

    /*
     * 2. 文本: 判断拖拽目标是否支持文本数据（即拖拽的是否是文本内容, 或者是否支持以文本的形式获取）
     */
    public String getText(DropTargetDropEvent dtde) throws IOException, UnsupportedFlavorException {
        if (dtde.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            // 接收拖拽目标数据
            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            // 以文本的形式获取数据
            return dtde.getTransferable().getTransferData(DataFlavor.stringFlavor).toString();
        }
        return null;
    }

    /*
     * 3. 图片: 判断拖拽目标是否支持图片数据。注意: 拖拽图片不是指以文件的形式拖拽图片文件,
     *          而是指拖拽一个正在屏幕上显示的并且支持拖拽的图片（例如网页上显示的图片）。
     */
    public Object getPic(DropTargetDropEvent dtde) throws IOException, UnsupportedFlavorException {
        if (dtde.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            // 接收拖拽目标数据
            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            // 以图片的形式获取数据
            return dtde.getTransferable().getTransferData(DataFlavor.imageFlavor);
        }
        return null;
    }

    /**
     * Called while a drag operation is ongoing, when the mouse pointer enters
     * the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     *
     * @param dtde the <code>DropTargetDragEvent</code>
     */
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
    }

    /**
     * Called when a drag operation is ongoing, while the mouse pointer is still
     * over the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     *
     * @param dtde the <code>DropTargetDragEvent</code>
     */
    @Override
    public void dragOver(DropTargetDragEvent dtde) {
    }

    /**
     * Called if the user has modified
     * the current drop gesture.
     * <p>
     *
     * @param dtde the <code>DropTargetDragEvent</code>
     */
    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    /**
     * Called while a drag operation is ongoing, when the mouse pointer has
     * exited the operable part of the drop site for the
     * <code>DropTarget</code> registered with this listener.
     *
     * @param dte the <code>DropTargetEvent</code>
     */
    @Override
    public void dragExit(DropTargetEvent dte) {
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        // 如果此次拖拽的数据是被接受的, 则必须设置拖拽完成（否则可能会看到拖拽目标返回原位置, 造成视觉上以为是不支持拖拽的错误效果）
        dtde.dropComplete(true);
    }
}