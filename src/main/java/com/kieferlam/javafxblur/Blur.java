package com.kieferlam.javafxblur;

import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * Singleton handler enum class Blur.
 * This class provides global methods to load and apply blur effects to a JavaFX stage.
 */
public enum Blur {
    NONE(0),
    BLUR_BEHIND(3),
    ACRYLIC(4);

    private final int accentState;

    Blur(int accentState) {
        this.accentState = accentState;
    }

    private static final String BLUR_TARGET_PREFIX = "_JFX";
    private static final NativeBlur _extBlur = new NativeBlur();

    /**
     * Loads the required blur library.
     * This should be called at the very start of your main function.
     * The "javafxblur" library file should be added to your library path.
     */
    public static void loadBlurLibrary() {
        // In a jar file; extract .dll with NativeUtils (which also calls System.load).
        if(isJar()) {
            try {
                NativeUtils.loadLibraryFromJar("/resources/Win10/x64/Release/javafxblur.dll");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // We are not in a jar file, which means we can assume the .dll file can be accessed directly (development environment).
        // Load the javafxblur.dll library directory through System.load with the directory pointing to our resources folder.
        else {
            try {
                File dll = new File(Blur.class.getClassLoader().getResource("Win10/x64/Release/javafxblur.dll").toString().replace("file:/", "").replace("%20", " "));
                System.load(dll.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns whether this application is running from a jar file.
     *
     * <p>Used for determining whether to extract .dll files before loading them.
     *
     * @return  whether this application is running from a jar file
     */
    public static boolean isJar() {
        return Blur.class.getResource("Blur.class").toString().startsWith("jar:");
    }

    private static void _extApplyBlur(String target, int accentState){
        _extBlur._extApplyBlur(target, accentState);
    }

    /**
     * Calls the external (native) function to apply the blur effect to a JavaFX stage.
     * The JavaFX stage must be visible before this function is called.
     * If the stage is ever hidden (destroyed, not minimised), this function must be called again once visible.
     *
     * <p>{@link Blur#NONE} represents no blur, {@link Blur#BLUR_BEHIND} represents a slight blur,
     * and {@link Blur#ACRYLIC} represents a strong acrylic blur.
     *
     * @param stage  stage to apply blur effect to
     * @param blur   type of blur to apply to stage
     */
    public static void applyBlur(Stage stage, Blur blur){
        if (!stage.isShowing()) {
            System.err.println("Warning: blur effect was called on a hidden stage!");
        }

        String stageTitle = stage.getTitle();
        String targetTitle = BLUR_TARGET_PREFIX + (System.currentTimeMillis() % 1000);
        stage.setTitle(targetTitle);
        _extApplyBlur(targetTitle, blur.accentState);
        stage.setTitle(stageTitle);
    }
}
