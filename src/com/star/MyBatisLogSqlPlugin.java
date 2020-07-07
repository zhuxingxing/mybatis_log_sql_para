package com.star;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Star.zhu
 */
public class MyBatisLogSqlPlugin extends AnAction {

    private JTextArea jTextArea;

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        Project project = e.getProject();
        if (null != editor && null != project) {
            ToolWindow toolWindow = ToolWindowManager.getInstance(e.getProject()).getToolWindow("MyBatisLogSqlTool");
            if (toolWindow != null) {

                // 无论当前状态为关闭/打开，进行强制打开ToolWindow 2017/3/21 16:21
                toolWindow.show(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

                // ToolWindow未初始化时，可能为空 2017/4/4 18:20
                try {
                    jTextArea = (JTextArea) ((JScrollPane) toolWindow.getContentManager().getContent(0)
                            .getComponent().getComponent(0)).getViewport().getComponent(0);
                } catch (Exception e1) {

                }
            }
            SelectionModel model = editor.getSelectionModel();
            String selectedText = model.getSelectedText();
            if ((null != selectedText) && (!"".equals(selectedText))) {
                List<String> sqlList = para(selectedText);
                StringBuilder stringBuilder = new StringBuilder();
                int order = 0;
                for (String sql : sqlList) {
                    order++;
                    stringBuilder.append("MyBatis Sql ");
                    stringBuilder.append(order);
                    stringBuilder.append(" : \n");
                    stringBuilder.append(sql.trim());
                    stringBuilder.append("; \n");
                    stringBuilder.append("\n");
                }
                jTextArea.append(stringBuilder.toString());
            }
        }
    }

    private List<String> para(String text) {
        java.util.Queue<String> queue = new LinkedList<>();
        List<String> list = new ArrayList<>();
        for (String line : text.split("\n")) {
            if (hasText(line)) {
                if (line.contains("Preparing:")) {
                    String sql = line.substring(line.indexOf("Preparing:") + "Preparing:".length());
                    queue.poll();
                    queue.offer(sql);
                } else if (line.contains("Parameters:")) {
                    String parameters = line.substring(line.indexOf("Parameters:") + "Parameters:".length());
                    String sql = queue.poll();
                    if (hasText(sql)) {
                        String[] parameterArray = parameters.split(",");
                        for (String parameter : parameterArray) {
                            String type = null;
                            if (hasText(parameter)) {
                                int last = parameter.lastIndexOf(")");
                                int start = parameter.lastIndexOf("(");
                                if (0 < last && 0 < start) {
                                    type = parameter.substring(start + 1, last).trim();
                                    parameter = parameter.substring(0, start).trim();
                                }
                            }
                            if ("Integer".equals(type) || "Double".equals(type) || "Float".equals(type) || "Long".equals(type) || "BigDecimal".equals(type) || "Boolean".equals(type)) {
                                sql = sql.replaceFirst("\\?", parameter);
                            } else {
                                sql = sql.replaceFirst("\\?", "'" + parameter + "'");
                            }

                        }
                        list.add(sql);
                    }
                }
            }
        }
        return list;
    }

    public boolean hasText(String str) {
        return str != null && !str.isEmpty() && containsText(str);
    }

    private boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

}
