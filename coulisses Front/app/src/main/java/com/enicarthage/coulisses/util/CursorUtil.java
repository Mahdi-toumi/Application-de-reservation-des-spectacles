package com.enicarthage.coulisses.util;

import android.widget.EditText;
import android.widget.TextView;
import android.graphics.drawable.Drawable;
import android.content.Context;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Field;

public class CursorUtil {
    public static void setCursorColor(EditText editText, int drawableResId) {
        try {
            // Récupère le champ "mEditor" interne à TextView
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(editText);

            // Récupère le champ "mCursorDrawable" de l'éditeur
            Field fCursorDrawable = editor.getClass().getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);

            // Prépare un tableau avec le drawable personnalisé
            Drawable cursorDrawable = ContextCompat.getDrawable(editText.getContext(), drawableResId);
            Drawable[] drawables = {cursorDrawable, cursorDrawable};

            fCursorDrawable.set(editor, drawables);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
