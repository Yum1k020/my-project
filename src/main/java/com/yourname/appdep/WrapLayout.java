package com.yourname.appdep;

import java.awt.*;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * FlowLayout 的加強版：依容器寬度自動換行，保留固定 hgap/vgap。
 * 取自 Oracle 官方範例。
 */
public class WrapLayout extends FlowLayout {
    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= (getHgap() + 1);
        return minimum;
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int hgap      = getHgap();
            int vgap      = getVgap();
            Insets insets = target.getInsets();
            int maxWidth  = target.getWidth();
            if (maxWidth <= 0) {
                // 初次顯示時，改用捲軸的可視寬度
                JScrollPane sp = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, target);
                maxWidth = (sp != null) ? sp.getViewport().getWidth() : Integer.MAX_VALUE;
            }
            maxWidth -= insets.left + insets.right + hgap * 2;

            Dimension dim = new Dimension(0, 0);
            int rowWidth  = 0, rowHeight = 0;

            for (Component c : target.getComponents()) {
                if (!c.isVisible()) continue;
                Dimension d = preferred ? c.getPreferredSize() : c.getMinimumSize();
                if (rowWidth + d.width > maxWidth) {
                    // 換行
                    dim.width = Math.max(dim.width, rowWidth);
                    dim.height += rowHeight + vgap;
                    rowWidth  = 0;
                    rowHeight = 0;
                }
                rowWidth  += d.width + hgap;
                rowHeight = Math.max(rowHeight, d.height);
            }
            dim.width  = Math.max(dim.width, rowWidth);
            dim.height += rowHeight;
            dim.width  += insets.left + insets.right + hgap * 2;
            dim.height += insets.top + insets.bottom + vgap * 2;
            return dim;
        }
    }
}
