package com.example.national_bank_of_egypt.Utils;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Utility class for common JavaFX animations and transitions.
 * Provides reusable animation methods for consistent UI effects throughout the application.
 */
public class AnimationUtils {

    // Standard animation durations (in milliseconds)
    public static final Duration QUICK_DURATION = Duration.millis(150);
    public static final Duration STANDARD_DURATION = Duration.millis(300);
    public static final Duration PAGE_TRANSITION_DURATION = Duration.millis(400);
    public static final Duration ENTRANCE_DURATION = Duration.millis(500);

    /**
     * Creates a fade-in animation for a node.
     * @param node The node to animate
     * @param duration Duration of the animation
     * @return The fade transition
     */
    public static FadeTransition fadeIn(Node node, Duration duration) {
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.setInterpolator(Interpolator.EASE_OUT);
        return fade;
    }

    /**
     * Creates a fade-out animation for a node.
     * @param node The node to animate
     * @param duration Duration of the animation
     * @return The fade transition
     */
    public static FadeTransition fadeOut(Node node, Duration duration) {
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setInterpolator(Interpolator.EASE_IN);
        return fade;
    }

    /**
     * Creates a cross-fade transition between two nodes (fade out old, fade in new).
     * @param oldNode The node to fade out
     * @param newNode The node to fade in
     * @param duration Duration of the transition
     * @return A sequential transition
     */
    public static SequentialTransition crossFade(Node oldNode, Node newNode, Duration duration) {
        FadeTransition fadeOut = fadeOut(oldNode, duration);
        FadeTransition fadeIn = fadeIn(newNode, duration);
        fadeIn.setDelay(duration.divide(2)); // Start fade in halfway through
        
        SequentialTransition seq = new SequentialTransition(fadeOut, fadeIn);
        return seq;
    }

    /**
     * Creates a slide-in animation from the right.
     * @param node The node to animate
     * @param distance Distance to slide (in pixels)
     * @param duration Duration of the animation
     * @return The translate transition
     */
    public static TranslateTransition slideInFromRight(Node node, double distance, Duration duration) {
        TranslateTransition slide = new TranslateTransition(duration, node);
        slide.setFromX(distance);
        slide.setToX(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        return slide;
    }

    /**
     * Creates a slide-in animation from the left.
     * @param node The node to animate
     * @param distance Distance to slide (in pixels)
     * @param duration Duration of the animation
     * @return The translate transition
     */
    public static TranslateTransition slideInFromLeft(Node node, double distance, Duration duration) {
        TranslateTransition slide = new TranslateTransition(duration, node);
        slide.setFromX(-distance);
        slide.setToX(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        return slide;
    }

    /**
     * Creates a slide-up animation.
     * @param node The node to animate
     * @param distance Distance to slide (in pixels)
     * @param duration Duration of the animation
     * @return The translate transition
     */
    public static TranslateTransition slideUp(Node node, double distance, Duration duration) {
        TranslateTransition slide = new TranslateTransition(duration, node);
        slide.setFromY(distance);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        return slide;
    }

    /**
     * Creates a combined fade-in and slide-up animation.
     * @param node The node to animate
     * @param slideDistance Distance to slide up (in pixels)
     * @param duration Duration of the animation
     * @return A parallel transition
     */
    public static ParallelTransition fadeInSlideUp(Node node, double slideDistance, Duration duration) {
        FadeTransition fade = fadeIn(node, duration);
        TranslateTransition slide = slideUp(node, slideDistance, duration);
        
        ParallelTransition parallel = new ParallelTransition(fade, slide);
        return parallel;
    }

    /**
     * Creates a scale animation for button hover effect.
     * @param node The node to animate
     * @param scaleFactor Scale factor (e.g., 1.05 for 5% larger)
     * @param duration Duration of the animation
     * @return The scale transition
     */
    public static ScaleTransition scaleHover(Node node, double scaleFactor, Duration duration) {
        ScaleTransition scale = new ScaleTransition(duration, node);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(scaleFactor);
        scale.setToY(scaleFactor);
        scale.setInterpolator(Interpolator.EASE_BOTH);
        return scale;
    }

    /**
     * Creates a scale animation for button press effect.
     * @param node The node to animate
     * @param scaleFactor Scale factor (e.g., 0.98 for 2% smaller)
     * @param duration Duration of the animation
     * @return The scale transition
     */
    public static ScaleTransition scalePress(Node node, double scaleFactor, Duration duration) {
        ScaleTransition scale = new ScaleTransition(duration, node);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(scaleFactor);
        scale.setToY(scaleFactor);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.setInterpolator(Interpolator.EASE_BOTH);
        return scale;
    }

    /**
     * Creates a shake animation (useful for error feedback).
     * @param node The node to animate
     * @param shakeDistance Distance to shake (in pixels)
     * @param duration Duration of the animation
     * @return The translate transition
     */
    public static TranslateTransition shake(Node node, double shakeDistance, Duration duration) {
        TranslateTransition shake = new TranslateTransition(duration.divide(6), node);
        shake.setFromX(0);
        shake.setToX(shakeDistance);
        shake.setAutoReverse(true);
        shake.setCycleCount(6);
        shake.setInterpolator(Interpolator.EASE_BOTH);
        return shake;
    }

    /**
     * Creates a bounce animation for icons or buttons.
     * @param node The node to animate
     * @param bounceDistance Distance to bounce (in pixels)
     * @param duration Duration of the animation
     * @return The translate transition
     */
    public static TranslateTransition bounce(Node node, double bounceDistance, Duration duration) {
        TranslateTransition bounce = new TranslateTransition(duration, node);
        bounce.setFromY(0);
        bounce.setToY(-bounceDistance);
        bounce.setAutoReverse(true);
        bounce.setCycleCount(2);
        bounce.setInterpolator(Interpolator.EASE_OUT);
        return bounce;
    }

    /**
     * Creates a staggered fade-in animation for multiple nodes.
     * @param nodes Array of nodes to animate
     * @param delayBetween Delay between each node (in milliseconds)
     * @param duration Duration of each fade animation
     * @return A parallel transition
     */
    public static ParallelTransition staggeredFadeIn(Node[] nodes, double delayBetween, Duration duration) {
        ParallelTransition parallel = new ParallelTransition();
        
        for (int i = 0; i < nodes.length; i++) {
            FadeTransition fade = fadeIn(nodes[i], duration);
            fade.setDelay(Duration.millis(i * delayBetween));
            parallel.getChildren().add(fade);
        }
        
        return parallel;
    }

    /**
     * Creates a page transition effect (fade out old, fade in new).
     * @param container The container holding the views
     * @param oldView The view to fade out
     * @param newView The view to fade in
     * @param duration Duration of the transition
     */
    public static void transitionPage(Pane container, Node oldView, Node newView, Duration duration) {
        if (oldView != null && oldView.getOpacity() > 0) {
            // Fade out old view
            FadeTransition fadeOut = fadeOut(oldView, duration);
            fadeOut.setOnFinished(e -> {
                container.getChildren().remove(oldView);
                // Add and fade in new view
                newView.setOpacity(0);
                container.getChildren().add(newView);
                fadeIn(newView, duration).play();
            });
            fadeOut.play();
        } else {
            // No old view, just fade in new view
            newView.setOpacity(0);
            container.getChildren().add(newView);
            fadeIn(newView, duration).play();
        }
    }

    /**
     * Creates a window entrance animation (fade + scale).
     * @param node The node to animate (typically the root of a scene)
     * @param duration Duration of the animation
     * @return A parallel transition
     */
    public static ParallelTransition windowEntrance(Node node, Duration duration) {
        FadeTransition fade = fadeIn(node, duration);
        ScaleTransition scale = new ScaleTransition(duration, node);
        scale.setFromX(0.95);
        scale.setFromY(0.95);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.setInterpolator(Interpolator.EASE_OUT);
        
        ParallelTransition parallel = new ParallelTransition(fade, scale);
        return parallel;
    }

    /**
     * Creates a window exit animation (fade + scale).
     * @param node The node to animate
     * @param duration Duration of the animation
     * @return A parallel transition
     */
    public static ParallelTransition windowExit(Node node, Duration duration) {
        FadeTransition fade = fadeOut(node, duration);
        ScaleTransition scale = new ScaleTransition(duration, node);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(0.95);
        scale.setToY(0.95);
        scale.setInterpolator(Interpolator.EASE_IN);
        
        ParallelTransition parallel = new ParallelTransition(fade, scale);
        return parallel;
    }

    /**
     * Creates a shadow effect animation (for hover effects).
     * @param node The node to animate
     * @param shadowRadius Initial shadow radius
     * @param targetRadius Target shadow radius
     * @param duration Duration of the animation
     * @return A timeline animation
     */
    public static Timeline shadowAnimation(Node node, double shadowRadius, double targetRadius, Duration duration) {
        DropShadow shadow = new DropShadow();
        node.setEffect(shadow);
        
        Timeline timeline = new Timeline();
        KeyValue kv1 = new KeyValue(shadow.radiusProperty(), shadowRadius);
        KeyValue kv2 = new KeyValue(shadow.radiusProperty(), targetRadius);
        KeyFrame kf1 = new KeyFrame(Duration.ZERO, kv1);
        KeyFrame kf2 = new KeyFrame(duration, kv2);
        timeline.getKeyFrames().addAll(kf1, kf2);
        
        return timeline;
    }
}

