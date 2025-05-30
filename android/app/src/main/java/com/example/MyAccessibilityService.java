package com.example.block_sites_flutter;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.content.Intent;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

public class MyAccessibilityService extends AccessibilityService {

    private final List<String> adultKeywords = Arrays.asList("insta","instagram","facebook","fb","reddit");

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null || getRootInActiveWindow() == null) return;

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        detectAdultContent(rootNode);
    }

    private void detectAdultContent(AccessibilityNodeInfo node) {
        if (node == null) return;

        CharSequence text = node.getText();
        if (text != null) {
            String lowerText = text.toString().toLowerCase();
            for (String keyword : adultKeywords) {
                if (lowerText.contains(keyword)) {
                    Log.d("Site detected", "Blocked: " + lowerText);
                    showBlockScreen();
                    return;
                }
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            detectAdultContent(node.getChild(i));
        }
    }

    private void showBlockScreen() {
        Intent intent = new Intent(this, BlockActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onInterrupt() {}
}
