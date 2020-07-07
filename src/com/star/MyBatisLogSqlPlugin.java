package com.star;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Star.zhu
 */
public class MyBatisLogSqlPlugin extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        Project project = e.getProject();
        if (null != editor && null != project) {
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
                }
                showPopupBalloon(editor, stringBuilder.toString());
            }
        }
    }

    private void showPopupBalloon(final Editor editor, final String result) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                JBPopupFactory factory = JBPopupFactory.getInstance();
                factory.createHtmlTextBalloonBuilder(result, null, new JBColor(new Color(186, 238, 186), new Color(73, 117, 73)), null)
                        .setFadeoutTime(15000)
                        .createBalloon()
                        .show(factory.guessBestPopupLocation(editor), Balloon.Position.below);
            }
        });
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
