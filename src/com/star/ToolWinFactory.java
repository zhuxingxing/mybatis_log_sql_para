package com.star;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @author: star.zhu
 * @date: 2020-07-07
 */
public class ToolWinFactory implements ToolWindowFactory {
    private JTextArea textArea1;
    private JPanel panel1;
    private JScrollPane scrollPane;
    private ToolWindow toolWindow;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel1, "Control", false);
        toolWindow.getContentManager().addContent(content);
        textArea1.setEditable(false);
        textArea1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
        panel1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
        panel1.setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        textArea1.setOpaque(false);
    }
}
